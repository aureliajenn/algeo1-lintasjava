package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;

public class SPL {
    /*
     * return solutions of a linear equation
     * Ax = b
     *
     */
    public static Matrix cramer(Matrix coeffMatrix, Matrix constMatrix) {
        // calculate the determinant of coeffMatrix
        double coeffMatDet = MatrixOperator.detCofactor(coeffMatrix);
        if (coeffMatDet != 0) {
            throw new IllegalArgumentException("Det != 0, cramer's method couldn't be applied");
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
}
