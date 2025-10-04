package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.Determinant;
import algeo.modules.Inverse;

public class SPL {

    /*'********************************************************************'*/
    /*'                                                                    '*/
    /*'           way to solve linear equation (main function)             '*/
    /*'                                                                    '*/
    /*'********************************************************************'*/

    /*
     * return solutions of a linear equation system
     * with Gauss elimination method
     */
    public static Matrix gauss(Matrix augmented) {
        Matrix m = augmented.copyMatrix();

        int rowCount = m.getRowsCount();
        int colCount = m.getColsCount();
        int pivotRow = 0;

        for (int pivotCol = 0; pivotCol < colCount - 1 && pivotRow < rowCount; pivotCol++) {
            int nonZero = pivotRow;
            while (nonZero < rowCount && m.getElmt(nonZero, pivotCol) == 0) {
                nonZero++;  // cek bawah
            }
            if (nonZero == rowCount) {
                continue;
            }

            if (nonZero != pivotRow) {
                m.swapRow(pivotRow, nonZero);
            }

            double pivotVal = m.getElmt(pivotRow, pivotCol);
            if (pivotVal != 1 && pivotVal != 0) {
                m.scaleRow(pivotRow, 1.0 / pivotVal);
            }

            m = zeroBelowPivot(m, pivotRow, pivotCol);

            pivotRow++;
        }

        Matrix A = m.removeLastCol();  // [A | b]
        double[] b = m.getCol(colCount - 1);

        return backSubstitute(A, b);
    }


    /*
     * return solutions of a linear equation system
     * with Gauss-Jordan elimination method
     */
    public static Matrix gaussJordan(Matrix augmented){
        Matrix m = augmented.copyMatrix();
        m = reducedEchelonForm(m);

        Matrix a = m.removeLastCol();
        double[] b = m.getCol(m.getColsCount() - 1);

        if(checkNoSolution(a, b)){
            // Jika tidak ada solusi, kembalikan null
            System.out.println("Tidak ada solusi.");
            return null;
        } else if (checkUniqueSolution(a, b)){
            // Jika solusi unik, langsung hitung dengan substitusi balik
            System.out.println("Solusi unik ditemukan.");
            return backSubstitute(a,b);
        } else {
            // Jika tidak keduanya, berarti solusi banyak.
            // Panggil fungsi untuk membuat matriks parametrik.
            System.out.println("Terdapat banyak solusi (solusi parametrik).");
            return getParametricSolution(a, b);
        }
    }

    /*
     * return solutions of a linear equation system using cramer method
     * Ax = b
     *
     */
    public static Matrix cramer(Matrix coeffMatrix, Matrix constMatrix) {
        // calculate the determinant of coeffMatrix
//        double coeffMatDet = MatrixOperator.detCofactor(coeffMatrix);
        double coeffMatDet = Determinant.detCofactor(coeffMatrix);
        if (coeffMatDet == 0) {
            throw new IllegalArgumentException("Det == 0, cramer's method couldn't be applied");
        }

        // apply cramer's rule
        int constMatrixRow = constMatrix.getRowsCount();
        Matrix determinants = new Matrix(constMatrixRow, 1);
        for (int i = 0; i < constMatrixRow; i++) {
            Matrix replacedCol = coeffMatrix.replaceCol(i, constMatrix);
//            determinants.setElmt(i, 0, MatrixOperator.detCofactor(replacedCol));
            determinants.setElmt(i, 0, Determinant.detCofactor(replacedCol));
        }
        // look for each
        return MatrixOperator.scalarDivision(determinants, coeffMatDet);
    }

    public static Matrix inverseMethod(Matrix coef, Matrix constantM) {
        if (coef.getRowsCount() != coef.getColsCount()) {
            throw new IllegalArgumentException("Matrix A harus persegi");
        }

//        double det = MatrixOperator.detCofactor(coef);
        double det = Determinant.detCofactor(coef);
        if (det == 0) {
            throw new IllegalArgumentException("Matrix A tidak ada invers (det = 0)");
        }

//        Matrix inverseA = Matrix.inverse(coef);
        Matrix inverseA = Inverse.inverseAugment(coef);

        return MatrixOperator.matrixMultiplication(inverseA, constantM);
    }
    /*'***********************************************************************'*/
    /*'                                                                       '*/
    /*                               helpers                                   */
    /*'                                                                       '*/
    /*'***********************************************************************'*/

    /*
     * Membangun matriks yang merepresentasikan solusi parametrik untuk sistem
     * dengan banyak solusi.
     * Kolom pertama matriks hasil adalah solusi khusus (particular solution).
     * Kolom-kolom berikutnya adalah vektor yang berkorespondensi dengan setiap variabel bebas (parameter).
     */
    public static Matrix getParametricSolution(Matrix a, double[] b) {
        int n = a.getColsCount();
        boolean[] isPivot = new boolean[n];
        int[] pivotRows = new int[n];
        java.util.Arrays.fill(pivotRows, -1);

        // Identifikasi variabel pivot dan bebas
        for (int i = 0; i < a.getRowsCount(); i++) {
            for (int j = 0; j < n; j++) {
                if (a.getElmt(i, j) == 1) {
                    isPivot[j] = true;
                    pivotRows[j] = i;
                    break;
                }
            }
        }

        java.util.ArrayList<Integer> freeVarIndices = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!isPivot[i]) {
                freeVarIndices.add(i);
            }
        }

        // Buat matriks hasil
        // Jumlah kolom = 1 (untuk solusi khusus) + jumlah variabel bebas
        int resultCols = 1 + freeVarIndices.size();
        Matrix result = new Matrix(n, resultCols);

        // Isi kolom pertama (solusi khusus)
        // Variabel pivot diisi dengan nilai dari vektor b
        // Variabel bebas bernilai 0
        for (int i = 0; i < n; i++) {
            if (isPivot[i]) {
                result.setElmt(i, 0, b[pivotRows[i]]);
            }
        }

        // Isi kolom untuk setiap variabel bebas/parameter
        for (int k = 0; k < freeVarIndices.size(); k++) {
            int freeVarIndex = freeVarIndices.get(k);
            int resultCol = k + 1;

            // Nilai variabel bebas itu sendiri adalah 1 (misal z = 1*t)
            result.setElmt(freeVarIndex, resultCol, 1.0);

            // Nilai variabel pivot bergantung pada koefisien variabel bebas
            for (int i = 0; i < n; i++) {
                if (isPivot[i]) {
                    int row = pivotRows[i];
                    // Nilainya adalah negatif dari koefisien variabel bebas di baris pivot tsb
                    result.setElmt(i, resultCol, -a.getElmt(row, freeVarIndex));
                }
            }
        }

        return result;
    }

    public static Matrix zeroBelowPivot(Matrix m, int pivotRow, int pivotCol){
        Matrix m1 = m.copyMatrix();
        for(int i = pivotRow + 1; i < m1.getRowsCount(); i++){
            double factor = m1.getElmt(i, pivotCol);
            if (factor != 0){
                m1.addRowMultiple(i, pivotRow, -1 * factor);
            }
        }
        return m1;
    }

    public static Matrix zeroAbovePivot(Matrix m, int pivotRow, int pivotCol){
        Matrix m1 = m.copyMatrix();
        for(int i = pivotRow - 1; i >= 0; i--){
            double factor = m1.getElmt(i, pivotCol);
            if (factor != 0){
                m1.addRowMultiple(i, pivotRow, -1 * factor);
            }
        }
        return m1;
    }

    /*
     * transforms the given matrix into its Reduced Row Echelon Form
     *  [ 1 2 -1]    [1 0 -3]
     *  [ 2 3  0] -> [0 1  2]
     *  [ 3 5  1]    [0 0  0]
     */
    public static Matrix reducedEchelonForm(Matrix m){
        Matrix m1 = m.copyMatrix();
        int rowCount = m1.getRowsCount();
        int colCount = m1.getColsCount();

        int lead = 0;
        for(int r = 0; r < rowCount; r++){
            if (lead >= colCount){
                return m1;
            }
            int pivotRow = r;

            while (pivotRow < rowCount && m1.getElmt(pivotRow, lead) == 0){
                pivotRow++;
            }
            if (pivotRow == rowCount){
                lead++;
                r--;
                continue;
            }
            if (pivotRow != r){
                m1.swapRow(r, pivotRow);
            }
            // normalisasi pivot jadi 1
            double pivotVal = m1.getElmt(r, lead);
            if (pivotVal != 0 && pivotVal != 1){
                m1.scaleRow(r, 1.0/pivotVal);
            }

            for (int i = r+1; i < rowCount; i++){
                double factor = m1.getElmt(i, lead);
                if (factor != 0){
                    m1.addRowMultiple(i, r, -1 * factor);
                }
            }
            lead++;
        }

        for(int i = rowCount-1; i >= 0; i--){
            int leadingOneCol = -1;
            for(int j = 0; j < colCount; j++){
                if (m1.getElmt(i,j) == 1){
                    leadingOneCol = j;
                    break;
                } else if (m1.getElmt(i,j) != 0){
                    break;
                }
            }
            if (leadingOneCol != -1){
                for(int r = i-1; r >= 0; r--){
                    double factor = m1.getElmt(r, leadingOneCol);
                    if (factor != 0){
                        m1.addRowMultiple(r, i, -1 * factor);
                    }
                }
            }
        }
        return m1;
    }

    /*
     * return the first index of a non-zero element in a row
     */
    private static int idxNotZero(double[] row) {
        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0) return i;
        }
        return -1;
    }

    /*
     * arrange the rows so that the leading one arrangement
     * is in accordance with the requirements of the echelon matrix
     */
    private static Matrix adjustLeadingOneRow(Matrix m){
        Matrix m1 = m.copyMatrix();
        for(int i = m1.getRowsCount()-1; i >= 0; i--){
            int idx = idxNotZero(m1.getRow(i));
            if(idx == -1){
                continue;
            }
            if (i != idx){
                m1.swapRow(i,idx);
            }
        }
        return m1;
    }

    /*
     * Checks if the linear equation system has no solution.
     */
    public static boolean checkNoSolution(Matrix a, double[] b) {
        for (int i = 0; i < a.getRowsCount(); i++) {
            boolean isRowAllZero = true;
            // Check if the entire row in the coefficient matrix is zero
            for (int j = 0; j < a.getColsCount(); j++) {
                if (a.getElmt(i, j) != 0) {
                    isRowAllZero = false;
                    break;
                }
            }
            // check if 0 = non-zero value
            if (isRowAllZero && b[i] != 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * Checks if the linear equation system has infinitely many solutions.
     */
    public static boolean checkManySolution(Matrix a, double[] b) {
        if (checkNoSolution(a, b)) {
            return false;
        }

        // The rank of a matrix in row echelon form is its number of non-zero rows.
        int rank = 0;
        for (int i = 0; i < a.getRowsCount(); i++) {
            for (int j = 0; j < a.getColsCount(); j++) {
                if (a.getElmt(i, j) != 0) {
                    rank++;
                    break;
                }
            }
        }

        int n = a.getColsCount();
        return rank < n;
    }

    /*
     * Checks if the linear equation system has a unique solution.
     * This occurs if the system is consistent (has no contradictions) and the rank
     * of the coefficient matrix is equal to the number of variables. This means there
     * are no free variables.
     */
    public static boolean checkUniqueSolution(Matrix a, double[] b) {
        if (checkNoSolution(a, b) || checkManySolution(a, b)) {
            return false;
        }

        // The rank of a matrix in row echelon form is its number of non-zero rows.
        int rank = 0;
        for (int i = 0; i < a.getRowsCount(); i++) {
            for (int j = 0; j < a.getColsCount(); j++) {
                if (a.getElmt(i, j) != 0) {
                    rank++;
                    break;
                }
            }
        }

        int n = a.getColsCount();
        // A unique solution exists if the system is consistent and rank equals the number of variables.
        return rank == n;
    }

    /*
     * do the backward substitution to get the result of the linear equation
     * in gauss method
     */
    private static Matrix backSubstitute(Matrix A, double[] B) {
        int n = A.getColsCount();
        Matrix solution = new Matrix(n, 1);

        for (int i = A.getRowsCount() - 1; i >= 0; i--) {
            int idx = idxNotZero(A.getRow(i));
            if (idx == -1) {
                continue;
            }
            double sum = 0;
            for (int j = idx + 1; j < n; j++) {
                sum += A.getElmt(i, j) * solution.getElmt(j, 0);
            }
            double xi = (B[i] - sum) / A.getElmt(i, idx);
            solution.setElmt(idx, 0, xi);
        }
        return solution;
    }

}
