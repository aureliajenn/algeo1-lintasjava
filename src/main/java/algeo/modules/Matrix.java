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
                System.out.printf("%8.2f", this.data[i][j]);
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

    public void swapRow(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

}
