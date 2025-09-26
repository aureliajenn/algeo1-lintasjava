package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;

public class SPL {

    /*'********************************************************************'*/
    /*'                                                                    '*/
    /*'           way to solve linear equation (main function)             '*/
    /*'                                                                    '*/
    /*'********************************************************************'*/

    /*
     * return solutions of a linear equation system
     * with Gauss-Jordan elimination method
     */
    public static Matrix gaussJordan(Matrix augmented){
        Matrix m = augmented.copyMatrix();
        m = reducedEchelonForm(m);

        Matrix a = m.removeRowColMatrix(0, m.getColsCount() - 1);
        double[] b = m.getCol(m.getColsCount() - 1);

        if(!doesSolutionExist(a, b)){
            return null;
        }
        if (m.getRowsCount() < m.getColsCount() - 1){
            int diff = (m.getColsCount() - 1) - m.getRowsCount();
            Matrix extended = new Matrix(m.getRowsCount() + diff, m.getColsCount());
            for(int i = 0; i < m.getRowsCount(); i++){
                extended.setRow(i, m.getRow(i));
            }
            m = extended;
        } else if (m.getRowsCount() > m.getColsCount() - 1){
            int newRow = m.getColsCount() - 1;
            Matrix trimmed = new Matrix(newRow, m.getColsCount());
            for(int i = 0; i < newRow; i++){
                trimmed.setRow(i,m.getRow(i));
            }
            m = trimmed;
        }

        m = adjustLeadingOneRow(m);

        a = m.removeRowColMatrix(0,m.getColsCount()-1);
        b = m.getCol(m.getColsCount()-1);
        return backSubstitute(a,b);
    }

    /*
     * return solutions of a linear equation system using cramer method
     * Ax = b
     *
     */
    public static Matrix cramer(Matrix coeffMatrix, Matrix constMatrix) {
        // calculate the determinant of coeffMatrix
        double coeffMatDet = MatrixOperator.detCofactor(coeffMatrix);
        if (coeffMatDet == 0) {
            throw new IllegalArgumentException("Det == 0, cramer's method couldn't be applied");
        }

        // apply cramer's rule
        int constMatrixRow = constMatrix.getRowsCount();
        Matrix determinants = new Matrix(constMatrixRow, 1);
        for (int i = 0; i < constMatrixRow; i++) {
            Matrix replacedCol = coeffMatrix.replaceCol(i, constMatrix);
            determinants.setElmt(i, 0, MatrixOperator.detCofactor(replacedCol));
        }
        // look for each
        return MatrixOperator.scalarDivision(determinants, coeffMatDet);
    }

    /*'***********************************************************************'*/
    /*'                                                                       '*/
    /*                               helpers                                   */
    /*'                                                                       '*/
    /*'***********************************************************************'*/

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
    private static Matrix reducedEchelonForm(Matrix m){
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
     * check if the linear equation system has a solution
     */
    private static boolean doesSolutionExist(Matrix a, double[] b){
        for(int i = a.getRowsCount()-1;i >= 0; i--){
            boolean isAllZero = true;
            for(int j = 0; j < a.getColsCount(); j++){
                if (a.getElmt(i,j) != 0){
                    isAllZero = false;
                    break;
                }
            }
            if (isAllZero && (b[i] != 0)){
                return false;
            }
        }
        return true;
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
