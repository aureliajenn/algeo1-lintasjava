package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.SPL;

public class Interpolation {
    public static Matrix polynomialInterpolation(Matrix points) {
        int length = points.getRowsCount() + 1;
        Matrix augmented = new Matrix(length, length + 1);

        for (int i = 0; i < length; i++) {
            double xi = points.getElmt(i, 0);
            double yi = points.getElmt(i, 1);
            for (int j = 0; j < length; j++) {
                augmented.setElmt(i, j, Math.pow(xi,j));
            }
            augmented.setElmt(i, length, yi);
        }
        return SPL.gauss(augmented);
    }

    public static Matrix[] interpolasiSplinaBezierKubik(Matrix points) {
        int n = points.getRowsCount() - 1;
        double[] X = points.getCol(0);
        double[] Y = points.getCol(1);

        // Matriks koefisien n+1 x n+1
        Matrix A = new Matrix(n+1, n+1);
        double[] Rx = new double[n+1];
        double[] Ry = new double[n+1];

        // kiri: 2*P1₀ + P1₁ = P₀ + 2 * P₁
        A.setElmt(0, 0, 2);
        A.setElmt(0, 1, 1);
        Rx[0] = X[0] + 2 * X[1];
        Ry[0] = Y[0] + 2 * Y[1];

        // kanan: P1ₙ₋₁ + 2*P1ₙ = 2*Pₙ + Pₙ₋₁
        A.setElmt(n, n-1, 1);
        A.setElmt(n, n, 2);
        Rx[n] = 2 * X[n] + X[n-1];
        Ry[n] = 2 * Y[n] + Y[n-1];

        // tengah
        for (int i = 1; i < n; i++) {
            A.setElmt(i, i - 1, 1);
            A.setElmt(i, i, 4);
            A.setElmt(i, i + 1, 1);
            Rx[i] = 4 * X[i] + 2 * X[i+1];
            Ry[i] = 4 * Y[i] + 2 * Y[i+1];
        }

        // solve P1
        // Create augmented matrices properly
        Matrix Abx = new Matrix(n+1, n+2);
        Matrix Aby = new Matrix(n+1, n+2);

        for (int i = 0; i < n+1; i++) {
            for (int j = 0; j < n + 1; j++) {
                Abx.setElmt(i, j, A.getElmt(i, j));
                Aby.setElmt(i, j, A.getElmt(i, j));
            }
            Abx.setElmt(i, n + 1, Rx[i]);
            Aby.setElmt(i, n + 1, Ry[i]);
        }

        // solve spl pakai gauss
        Matrix Dx = SPL.gauss(Abx);
        Matrix Dy = SPL.gauss(Aby);

        // bezier segments
        Matrix[] result = new Matrix[n];

        for (int i = 0; i < n; i++) {
            double P0x = X[i];
            double P0y = Y[i];
            double P3x = X[i + 1];
            double P3y = Y[i + 1];
            double P1x = Dx.getElmt(i, 0);
            double P1y = Dy.getElmt(i, 0);

            double P2x;
            double P2y;
            if (i < n - 1) {
                P2x = 2 * X[i + 1] - Dx.getElmt(i + 1, 0);
                P2y = 2 * Y[i + 1] - Dy.getElmt(i + 1, 0);
            } else {
                P2x = (X[n] + Dx.getElmt(n, 0)) / 2;
                P2y = (Y[n] + Dy.getElmt(n, 0)) / 2;
            }

            double[][] controlPoints = {
                    {P0x, P0y}, {P1x, P1y},
                    {P2x, P2y}, {P3x, P3y}
            };
            result[i] = new Matrix(controlPoints);
        }

        return result;
    }
}
