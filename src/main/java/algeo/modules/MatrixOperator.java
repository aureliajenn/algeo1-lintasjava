/*
public static Matrix addition(Matrix a, Matrix b)
public static Matrix subtraction(Matrix a, Matrix b)
public static Matrix scalarMultiplication(Matrix a, double scalar)
public static Matrix matrixMultiplication(Matrix a, Matrix b)
public static double detCofactor(Matrix a)
public static Matrix cofactorMatrix(Matrix a)
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
        if (aColLength != b.getRowsCount()) {
            throw new IllegalArgumentException("a's column must be equal to b's row");
        }
        Matrix result = new Matrix(resultRowLength, resultColLength);
        for (int i = 0; i < resultRowLength; i++) {
            for (int j = 0; j < resultColLength; j++) {
                double sum = 0;
                for (int k = 0; k < aColLength; k++) {
                    sum += a.getElmt(i, k) * b.getElmt(k, j);
                }
                result.setElmt(i, j, sum);
            }
        }
        return result;
    }

    /*
     * hitung determinan dari matriks a dengan metode ekspansi kofaktor
     */
    public static double detCofactor(Matrix a) {

        if (!a.isSquare()) {
            throw new IllegalStateException("Determinan hanya bisa dihitung dari matriks persegi.");
        }
        int n = a.getRowsCount();

        if (n == 1) {
            return a.getElmt(0,0);
        } else if (n == 2) {
            return a.getElmt(0,0) * a.getElmt(1,1) - a.getElmt(0,1) * a.getElmt(1,0);
        } else{
            double det = 0.0;
            for (int j = 0; j < n; j++) {
                Matrix r = a.removeRowColMatrix(0, j);
                double cofactor = Math.pow(-1, j) * a.getElmt(0,j) * detCofactor(r);
                det += cofactor;
            }
        }

        return det;
    }
    /*
     * membentuk matriks kofaktor dari matriks a
     */
    public static Matrix cofactorMatrix(Matrix a){
        if (!a.isSquare()){
            throw new IllegalStateException("Matriks kofaktor hanya bisa dibentuk dari matriks persegi");
        }
        int n = a.getRowsCount();
        Matrix result = new Matrix(n,n);
        for (int i = 0; i < a.getRow();i++){
            for (int j = 0; j < a.getCol(); j++){
                Matrix r = a.removeRowColMatrix(i,j);
                double cofactor = Math.pow(-1 , (i + j)) * detCofactor(r);
                result.setElmt(i,j,cofactor);
            }
        }
        return result;
    }
}
