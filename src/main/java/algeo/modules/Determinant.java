package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.SPL;

public class Determinant {
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
            return det;
        }
    }

    /*
     * hitung determinan dari matriks a dengan metode reduksi baris
     */
    public static double detReduksiBaris(Matrix a) {

        if (!a.isSquare()) {
            throw new IllegalStateException("Determinan hanya bisa dihitung dari matriks persegi.");
        }

        Matrix m = a.copyMatrix();
        int n = m.getRowsCount();
        double determinant = 1.0;
        int swapCount = 0;

        for (int i = 0; i < n; i++) {
            int pivotRow = i;
            while (pivotRow < n && m.getElmt(pivotRow, i) == 0) {
                pivotRow++;
            }

            if (pivotRow == n) {
                return 0;
            }

            if (pivotRow != i) {
                m.swapRow(pivotRow, i);
                swapCount++;
            }
            double pivotVal = m.getElmt(i, i);
            determinant *= pivotVal;

            for (int j = i + 1; j < n; j++) {
                double factor = m.getElmt(j, i) / pivotVal;
                m.addRowMultiple(j, i, -factor);

            }
        }
        if (swapCount % 2 == 1) {
            determinant *= -1;
        }
        return determinant;
    }
}
