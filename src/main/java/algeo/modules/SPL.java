package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.Determinant;
import algeo.modules.Inverse;
import algeo.modules.SPLResult;
import java.util.Formatter;
public class SPL {
    private static final int DIMENSION_THRESHOLD  = 11;

    /*'********************************************************************'*/
    /*'                                                                    '*/
    /*'           way to solve linear equation (main function)             '*/
    /*'                                                                    '*/
    /*'********************************************************************'*/

    /*
     * return solutions of a linear equation system
     * with Gauss elimination method
     */
    public static SPLResult gauss(Matrix augmented) {
        if (augmented.getRowsCount() > DIMENSION_THRESHOLD) {
            Matrix solution = gaussWithoutSteps(augmented);
            return new SPLResult(solution, "Langkah-langkah tidak ditampilkan pada matriks besar.");
        }

        StringBuilder steps = new StringBuilder("Menyelesaikan SPL dengan Eliminasi Gauss:\n\n");
        SPLResult echelonResult = echelonFormWithSteps(augmented);
        steps.append(echelonResult.steps);
        Matrix m = echelonResult.solution;

        steps.append("\nLANGKAH 2: Melakukan substitusi balik untuk menemukan solusi.\n");

        Matrix a = m.removeLastCol();  // [a | b]
        double[] b = m.getCol(m.getColsCount() - 1);

        if (checkNoSolution(a, b)) {
            steps.append("-> Ditemukan baris [0 0 ... | c] dengan c != 0. SPL tidak memiliki solusi.\n");
            return new SPLResult(null, steps.toString());
        }

        Matrix solution = backSubstitute(a, b);
        steps.append("Solusi akhir yang ditemukan:\n").append(solution);
        return new SPLResult(solution, steps.toString());
    }


    /*
     * return solutions of a linear equation system
     * with Gauss-Jordan elimination method
     */
    public static SPLResult gaussJordan(Matrix augmented){
        if (augmented.getRowsCount() > DIMENSION_THRESHOLD) {
            Matrix solution = gaussJordanWithoutSteps(augmented);
            return new SPLResult(solution, "Langkah-langkah tidak ditampilkan pada matriks besar.");
        }

        StringBuilder steps = new StringBuilder("Menyelesaikan SPL dengan Eliminasi Gauss-Jordan:\n\n");
        SPLResult rrefResult = reducedEchelonFormWithSteps(augmented);
        steps.append(rrefResult.steps);
        Matrix m = rrefResult.solution;

        steps.append("\nAnalisis Solusi dari matriks RREF:\n");
        Matrix a = m.removeLastCol();
        double[] b = m.getCol(m.getColsCount() - 1);

        if(checkNoSolution(a, b)){
            // Jika tidak ada solusi, kembalikan null
//            System.out.println("Tidak ada solusi.");
            steps.append("-> Ditemukan baris [0 0 ... | c] dengan c != 0. SPL tidak memiliki solusi.\n");
            return new SPLResult(null, steps.toString());
        } else if (checkUniqueSolution(a, b)){
            // Jika solusi unik, langsung hitung dengan substitusi balik
//            System.out.println("Solusi unik ditemukan.");
            steps.append("-> Setiap variabel adalah variabel pivot. SPL memiliki solusi unik.\n");
            Matrix solution = backSubstitute(a, b);
            steps.append("Solusi:\n").append(solution);
            return new SPLResult(solution, steps.toString());
        } else {
            // Jika tidak keduanya, berarti solusi banyak.
            // Panggil fungsi untuk membuat matriks parametrik.
//            System.out.println("Terdapat banyak solusi (solusi parametrik).");
            steps.append("-> Terdapat variabel bebas. SPL memiliki banyak solusi (parametrik).\n");
            Matrix solution = getParametricSolution(a, b);
            steps.append("Solusi Parametrik (kolom pertama adalah konstanta, kolom berikutnya adalah parameter):\n").append(solution);
            return new SPLResult(solution, steps.toString());
        }
    }

    /*
     * return solutions of a linear equation system using cramer method
     * Ax = b
     *
     */
    public static SPLResult cramer(Matrix coeffMatrix, Matrix constMatrix) {
        double coeffMatDet = Determinant.detReduksiBaris(coeffMatrix).value;
        if (coeffMatDet == 0) {
            throw new IllegalArgumentException("Det == 0, cramer's method couldn't be applied");
        }

        if (coeffMatrix.getRowsCount() > DIMENSION_THRESHOLD) {
            // apply cramer's rule
            int constMatrixRow = constMatrix.getRowsCount();
            Matrix determinants = new Matrix(constMatrixRow, 1);
            for (int i = 0; i < constMatrixRow; i++) {
                Matrix replacedCol = coeffMatrix.replaceCol(i, constMatrix);
                determinants.setElmt(i, 0, Determinant.detReduksiBaris(replacedCol).value);
            }
            // look for each
            Matrix solution =  MatrixOperator.scalarDivision(determinants, coeffMatDet);
            return new SPLResult(solution, "Langkah-langkah tidak ditampilkan untuk Kaidah Cramer pada matriks besar.");
        }

        // Versi lengkap dengan langkah-langkah untuk matriks kecil
        StringBuilder steps = new StringBuilder("Menyelesaikan SPL dengan Kaidah Cramer: xi = det(Ai) / det(A)\n\n");
        steps.append("LANGKAH 1: Menghitung determinan matriks koefisien (A)...\n");
        steps.append("Hasil: det(A) = ").append(String.format("%.4f", coeffMatDet)).append("\n\n");


        // apply cramer's rule
        int constMatrixRow = constMatrix.getRowsCount();
        Matrix determinants = new Matrix(constMatrixRow, 1);
        for (int i = 0; i < constMatrixRow; i++) {
            steps.append("LANGKAH ").append(i + 2).append(": Hitung det(A").append(i + 1).append(").\n");
            Matrix replacedCol = coeffMatrix.replaceCol(i, constMatrix);
            steps.append("Matriks A").append(i + 1).append(" (kolom ").append(i + 1).append(" diganti):\n").append(replacedCol).append("\n");

            double determinant = Determinant.detReduksiBaris(replacedCol).value; // Langsung ambil nilainya
            steps.append("Hasil: det(A").append(i + 1).append(") = ").append(String.format("%.4f", determinant)).append("\n\n");
            determinants.setElmt(i, 0, determinant);
        }
        // look for each
        Matrix solution =  MatrixOperator.scalarDivision(determinants, coeffMatDet);
        steps.append("Solusi akhir:\n").append(solution);
        return new SPLResult(solution, steps.toString());
    }

    public static SPLResult inverseMethod(Matrix coef, Matrix constantM) {
        if (coef.getRowsCount() != coef.getColsCount()) {
            throw new IllegalArgumentException("Matrix A harus persegi");
        }

        try {
            if (coef.getRowsCount() > DIMENSION_THRESHOLD) {
                InverseResult inverseRes = Inverse.inverseAugment(coef);
                Matrix solution =  MatrixOperator.matrixMultiplication(inverseRes.matrix, constantM);
                return new SPLResult(solution, "Langkah-langkah tidak ditampilkan untuk Metode Invers pada matriks besar.");
            }

            StringBuilder steps = new StringBuilder("Menyelesaikan SPL dengan Metode Matriks Balikan: X = A⁻¹ * B\n\n");
            steps.append("LANGKAH 1: Cari matriks balikan (A⁻¹).\n");

            InverseResult inverseResult = Inverse.inverseAugment(coef);
            steps.append(inverseResult.steps);
            Matrix inverseRes = inverseResult.matrix;

            steps.append("\nLANGKAH 2: Kalikan A⁻¹ dengan B untuk mendapatkan X.\n");
            steps.append("X = A⁻¹ * B\n").append(inverseRes).append("\n   *\n").append(constantM).append("\n");

            Matrix solution = MatrixOperator.matrixMultiplication(inverseRes, constantM);
            steps.append("   =\n").append(solution);
            return new SPLResult(solution, steps.toString());

        } catch (IllegalArgumentException error) {
            return new SPLResult(null, error.getMessage());
        }
    }
    /*'***********************************************************************'*/
    /*'                                                                       '*/
    /*                               helpers                                   */
    /*'                                                                       '*/
    /*'***********************************************************************'*/
    public static SPLResult echelonFormWithSteps(Matrix augmented) {
        StringBuilder steps = new StringBuilder();
        Matrix m = augmented.copyMatrix();
        int rowCount = m.getRowsCount();
        int colCount = m.getColsCount();
        int pivotRow = 0;
        for (int pivotCol = 0; pivotCol < colCount - 1 && pivotRow < rowCount; pivotCol++) {
            int nonZero = pivotRow;
            while (nonZero < rowCount && Math.abs(m.getElmt(nonZero, pivotCol)) < 1e-9) nonZero++;
            if (nonZero == rowCount) continue;
            if (nonZero != pivotRow) {
                steps.append(String.format("-> Tukar B%d dengan B%d\n", pivotRow + 1, nonZero + 1));
                m.swapRow(pivotRow, nonZero);
            }
            double pivotVal = m.getElmt(pivotRow, pivotCol);
            if (Math.abs(pivotVal - 1.0) > 1e-9) {
                steps.append(String.format("-> B%d = B%d / %.4f\n", pivotRow + 1, pivotRow + 1, pivotVal));
                m.scaleRow(pivotRow, 1.0 / pivotVal);
            }
            for (int i = pivotRow + 1; i < rowCount; i++) {
                double factor = m.getElmt(i, pivotCol);
                if (Math.abs(factor) > 1e-9) {
                    steps.append(String.format("-> B%d = B%d - (%.4f * B%d)\n", i + 1, i + 1, factor, pivotRow + 1));
                    m.addRowMultiple(i, pivotRow, -factor);
                }
            }
            steps.append("Matriks setelah proses di kolom pivot ").append(pivotCol + 1).append(":\n").append(m).append("\n\n");
            pivotRow++;
        }
        steps.append("Bentuk Eselon Baris tercapai.\n");
        return new SPLResult(m, steps.toString());
    }

    public static SPLResult reducedEchelonFormWithSteps(Matrix m){
        StringBuilder steps = new StringBuilder("Mengubah ke Bentuk Eselon Baris Tereduksi (RREF):\n");
        Matrix m1 = m.copyMatrix();
        steps.append("Matriks Awal:\n").append(m1).append("\n\n");
        int rowCount = m1.getRowsCount();
        int colCount = m1.getColsCount();

        int lead = 0;
        for(int r = 0; r < rowCount; r++){
            if (lead >= colCount){
                break;
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
                steps.append(String.format("-> Tukar B%d dengan B%d\n", r + 1, pivotRow + 1));
                m1.swapRow(r, pivotRow);
            }
            // normalisasi pivot jadi 1
            double pivotVal = m1.getElmt(r, lead);
            if (pivotVal != 0 && pivotVal != 1){
                steps.append(String.format("-> B%d = B%d / %.4f\n", r + 1, r + 1, pivotVal));
                m1.scaleRow(r, 1.0/pivotVal);
            }

            for (int i = r+1; i < rowCount; i++){
                double factor = m1.getElmt(i, lead);
                if (factor != 0){
                    steps.append(String.format("-> B%d = B%d - (%.4f * B%d)\n", i + 1, i + 1, factor, r + 1));
                    m1.addRowMultiple(i, r, -1 * factor);
                }
            }
            steps.append("Matriks setelah proses di kolom pivot ").append(lead + 1).append(":\n").append(m1).append("\n\n");
            lead++;
        }

        steps.append("\nMemulai fase eliminasi ke atas (Back Substitution):\n");
        for (int i = rowCount - 1; i >= 0; i--) {
            lead = -1;
            for (int j = 0; j < colCount; j++) {
                if (Math.abs(m1.getElmt(i, j)) > 1e-9) {
                    lead = j;
                    break;
                }
            }

            if (lead != -1) {
                for (int r = i - 1; r >= 0; r--) {
                    double factor = m1.getElmt(r, lead);
                    if (Math.abs(factor) > 1e-9) {
                        steps.append(String.format("-> B%d = B%d - (%.4f * B%d)\n", r + 1, r + 1, factor, i + 1));
                        m1.addRowMultiple(r, i, -factor);
                    }
                }
            }
        }
        steps.append("Matriks setelah eliminasi ke atas:\n").append(m1).append("\n\n");
        steps.append("Bentuk Eselon Baris Tereduksi (RREF) akhir tercapai.\n");
        return new SPLResult(m1, steps.toString());
    }
    public static Matrix gaussWithoutSteps(Matrix augmented) {
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

    private static Matrix gaussJordanWithoutSteps(Matrix augmented){
        Matrix m = augmented.copyMatrix();
        m = reducedEchelonFormWithoutSteps(m);

        Matrix a = m.removeLastCol();
        double[] b = m.getCol(m.getColsCount() - 1);

        if(checkNoSolution(a, b)){
            // Jika tidak ada solusi, kembalikan null
//            System.out.println("Tidak ada solusi.");
            return null;
        } else if (checkUniqueSolution(a, b)){
            // Jika solusi unik, langsung hitung dengan substitusi balik
//            System.out.println("Solusi unik ditemukan.");
            return backSubstitute(a,b);
        } else {
            // Jika tidak keduanya, berarti solusi banyak.
            // Panggil fungsi untuk membuat matriks parametrik.
//            System.out.println("Terdapat banyak solusi (solusi parametrik).");
            return getParametricSolution(a, b);
        }
    }

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
    public static Matrix reducedEchelonFormWithoutSteps(Matrix m){
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
