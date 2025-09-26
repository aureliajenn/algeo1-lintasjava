/*
public Matrix(int rows, int cols)
public Matrix(double[][] val)

public int getRowsCount()
public int getColsCount()

public double getElmt(int i, int j)
public double[] getRow(int r)
public double[] getCol(int c)

public void setElmt(int i, int j, double val)
public void setRow(int r, double[] values)
public void setCol(int c, double[] values)

public void displayMatrix()
public Matrix copyMatrix()

public void swapRow(int i, int j)
public void scaleRow(int row, double constant)
public void addRowMultiple(int targetRow, int rowHelper, double constant)

public boolean isSquare()

public Matrix transpose()
public static Matrix identity(int dimension)
public Matrix removeRowColMatrix(int row, int col)
 */

package algeo.modules;

import java.util.Arrays;

public class Matrix {
    private final int rows;
    private final int cols;
    private final double[][] data;


    // constructor
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];
    }

    public Matrix(double[][] val) {  // used for copyMatrix, create matrix based on 2Ds array
        rows = val.length;
        cols = val[0].length;
        data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (val[i].length != cols) {
                throw new IllegalArgumentException("Panjang baris harus sama.");
            }
            data[i] = Arrays.copyOf(val[i], cols);
        }
    }

    // getter
    public int getRowsCount() {
        return rows;
    }

    public int getColsCount() {
        return cols;
    }

    public double getElmt(int i, int j) {
        return data[i][j];
    }

    public double[] getRow(int r) {
        if (r < 0 || r >= rows) {
            throw new IndexOutOfBoundsException("Index row salah.");
        }
        return Arrays.copyOf(data[r], cols);
    }

    public double[] getCol(int c) {
        if (c < 0 || c >= cols) {
            throw new IndexOutOfBoundsException("Index col salah.");
        }
        double[] col = new double[rows];
        for (int i = 0; i < rows; i++) {
            col[i] = data[i][c];
        }
        return col;
    }

    // setter
    public void setElmt(int i, int j, double val) {
        data[i][j] = val;
    }

    public void setRow(int r, double[] values) {
        if (values.length != cols) {
            throw new IllegalArgumentException("Panjang array tidak sama dengan kolom");
        }
        data[r] = Arrays.copyOf(values, cols);
    }

    public void setCol(int c, double[] values) {
        if (values.length != rows) {
            throw new IllegalArgumentException("Panjang array tidak sama dengan baris");
        }
        for (int i = 0; i < rows; i++) {
            data[i][c] = values[i];
        }
    }

    // utility
    public void displayMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%8.3f", data[i][j]);
                if (j != cols - 1) {
                    System.out.print(" ");
                }
            }
            if (i != rows - 1) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public Matrix copyMatrix() {
        return new Matrix(data);
    }

    public void swapRow(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // scale the specified row with a non 0 constant
    public void scaleRow(int row, double constant) { 
        if (constant == 0) {
            throw new IllegalArgumentException("Konstan tidak boleh nol");
        }
        for (int i = 0; i < cols; i++) {
            data[row][i] *= constant;
        }
    }

    // return targetRow = targetRow + (constant * rowHelper)
    public void addRowMultiple(int targetRow, int rowHelper, double constant) {
        if (constant == 0) {
            throw new IllegalArgumentException("Konstan tidak boleh nol");
        }
        for (int i = 0; i < cols; i++) {
            data[targetRow][i] += constant * data[rowHelper][i];
        }
    }

    
    // replace all element in the specified column with values from a 1-column matrix
    public Matrix replaceCol(int targetCol, Matrix newValue) {
        if (rows != newValue.getRowsCount()) {
            throw new IllegalArgumentException("Both matrix must have equal row length");
        } else if (newValue.getColsCount() != 1) {
            throw new IllegalArgumentException("newValue matrix must be 1D-row matrix");
        }

        Matrix result = new Matrix(data);

        for (int i = 0; i < rows; i++) {
            result.setElmt(i, targetCol, newValue.getElmt(i, 0));
        }
        return result;
    }

    public boolean isSquare() {
        return rows == cols;
    }

    public Matrix transpose() {
        Matrix transposed = new Matrix(cols, rows);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                transposed.setElmt(i, j, getElmt(j,i));
            }
        }
        return transposed;
    }

    // return matrix identitas
    public static Matrix identity(int dimension) {
        Matrix identityMatrix = new Matrix(dimension, dimension);
        for (int i = 0; i < dimension; i++) {
            identityMatrix.setElmt(i, i, 1);
        }
        return identityMatrix;
    }

    public Matrix removeRowColMatrix(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException("Index baris dan/atau kolom salah.");
        }

        Matrix removedRowColMatrix = new Matrix(rows-1, cols-1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == row || j == col){
                    continue;
                } else if (i < row && j < col){
                    removedRowColMatrix.setElmt(i,j,data[i][j]);
                } else if (i < row && j > col){
                    removedRowColMatrix.setElmt(i,j-1, data[i][j]);
                } else if (i > row && j < col){
                    removedRowColMatrix.setElmt(i-1,j, data[i][j]);
                } else if (i > row && j > col){
                    removedRowColMatrix.setElmt(i-1,j-1, data[i][j]);
                }
            }
        }
        return removedRowColMatrix;
    }
}
