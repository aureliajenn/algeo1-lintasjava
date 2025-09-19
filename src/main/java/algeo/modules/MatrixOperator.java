/*
public static Matrix addition(Matrix a, Matrix b)
public static Matrix subtraction(Matrix a, Matrix b)
public static Matrix scalarMultiplication(Matrix a, double scalar)
public static Matrix matrixMultiplication(Matrix a, Matrix b)
*/
package algeo.modules;

public class MatrixOperator {
    /*
     * return a matrix as a result of a + b
     */
    public static Matrix addition(Matrix a, Matrix b) {
        int rowLength = a.getRowsCount();
        int colLength = a.getColsCount();
        Matrix result = new Matrix(rowLength, colLength);
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                result.setElmt(i, j, a.getElmt(i, j) + b.getElmt(i, j));
            }
        }
        return result;
    }

    /*
     * return a matrix result from a - b
     */
    public static Matrix subtraction(Matrix a, Matrix b) {
        int rowLength = a.getRowsCount();
        int colLength = a.getColsCount();
        Matrix result = new Matrix(rowLength, colLength);
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                result.setElmt(i, j, a.getElmt(i, j) - b.getElmt(i, j));
            }
        }
        return result;
    }

    /*
     * return a matrix result of a scalar times a matrix
     */
    public static Matrix scalarMultiplication(Matrix a, double scalar) {
        int rowLength = a.getRowsCount();
        int colLength = a.getColsCount();
        Matrix result = new Matrix(rowLength, colLength);
        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < colLength; j++) {
                result.setElmt(i, j, scalar * a.getElmt(i, j));
            }
        }
        return result;
    }

    /*
     * return a matrix result from a * b
     */
    public static Matrix matrixMultiplication(Matrix a, Matrix b) {
        int resultRowLength = a.getRowsCount();
        int resultColLength = b.getColsCount();
        int aColLength = a.getColsCount();
        Matrix result = new Matrix(resultRowLength, resultColLength);
        for (int i = 0; i < resultRowLength; i++) {
            for (int j = 0; j < resultColLength; j++) {
                for (int k = 0; k < aColLength; k++) {
                    result.setElmt(i, j, a.getElmt(i, k) * b.getElmt(k, j));
                }
            }
        }
        return result;
    }

}
