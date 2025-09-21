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
public void replaceRow(int rowReplaced, int rowHelper, double constant)

public boolean isSquare()

public Matrix transpose()
public static Matrix identity(int dimension)
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
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                System.out.printf("%8.3f", this.data[i][j]);
                if (j != this.cols - 1) {
                    System.out.print(" ");
                }
            }
            if (i != this.rows - 1) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public Matrix copyMatrix() {
        return new Matrix(data);
    }

    /*
     * swap the i row with j row
     */
    public void swapRow(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    /*
     * scale the specified row with a non 0 constant
     */
    public void scaleRow(int row, double constant) {
        if (constant == 0) {
            throw new IllegalArgumentException("Constant must be not 0");
        }
        for (int i = 0; i < this.cols; i++) {
            this.data[row][i] *= constant;
        }
    }

    /*
     *
     */
    public void replaceRow(int rowReplaced, int rowHelper, double constant) {
        if (constant == 0) {
            throw new IllegalArgumentException("Constant must be not 0");
        }
        for (int i = 0; i < this.cols; i++) {
            this.data[rowReplaced][i] += constant * this.data[rowHelper][i];
        }
    }

    /*
     * return true if it's a square matrix
     */
    public boolean isSquare() {
        return this.rows == this.cols;
    }

    /*
     * return transpose of a matrix
     */
    public Matrix transpose() {
        Matrix transposed = new Matrix(this.cols, this.rows);
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                transposed.setElmt(i, j, this.getElmt(j,i));
            }
        }
        return transposed;
    }

    /*
     * return dimension * dimension identity matrix
     */
    public static Matrix identity(int dimension) {
        Matrix identityMatrix = new Matrix(dimension, dimension);
        for (int i = 0; i < dimension; i++) {
            identityMatrix.setElmt(i, i, 1);
        }
        return identityMatrix;
    }

}
