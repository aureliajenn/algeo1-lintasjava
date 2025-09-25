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

    public Matrix zeroBelowPivot(Matrix m, int pivotRow, int pivotCol){
        Matrix m1 = m.copyMatrix();
        for(int i = pivotRow + 1; i < m1.getRowsCount(); i++){
            double factor = m1.getElmt(i, pivotCol);
            if (factor != 0){
                m1.addRowMultiple(i, pivotRow, -factor);
            }
        }
        return m1;
    }

    public Matrix zeroAbovePivot(Matrix m, int pivotRow, int pivotCol){
        Matrix m1 = m.copyMatrix();
        for(int i = pivotRow - 1; i >= 0; i--){
            double factor = m1.getElmt(i, pivotCol);
            if (factor != 0){
                m1.addRowMultiple(i, pivotRow, -factor);
            }
        }
        return m1;
    }

    public Matrix reducedEchelonForm(Matrix m){
        Matrix m1 = m.copyMatrix();
        int rowCount = m1.getRowsCount();
        int colCount = m1.getColsCount();
        int leadRow = 0;

        // Fase Maju
        for(int j = 0; j < colCount; j++){
            if (leadRow >= rowCount){
                break;
            }
            int pivotRow = -1;
            for(int k = leadRow; k < rowCount; k++){
                if (m1.getElmt(k,j) != 0){
                    pivotRow = k;
                    break;
                }
            }
            if (pivotRow == -1){
                continue;
            }
            if (pivotRow != leadRow){
                m1.swapRow(leadRow, pivotRow);
            }

            double pivotVal = m1.getElmt(leadRow,j);
            if (pivotVal != 1){
                m1.scaleRow(leadRow,1.0/pivotVal);
            }

            for (int r = leadRow + 1; r < rowCount; r++){
                double factor = m1.getElmt(r,j);
                if (factor != 0){
                    m1.addRowMultiple(r, leadRow, -factor);
                }
            }
            leadRow++;
        }

        //Fase Mundur
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
                for(int r = i - 1; r >= 0; r--){
                    double factor = m1.getElmt(r,leadingOneCol);
                    if (factor != 0){
                        m1.addRowMultiple(r, i, -factor);
                    }
                }
            }
        }
        return m1;
    }
}
