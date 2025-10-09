package algeo.modules;

public class Regression {

    public static Matrix multiRegression(Matrix X, Matrix y) {
        // B = (X^T . X)^-1  . X^T . y
        Matrix Xt = X.transpose();
        Matrix XtX = MatrixOperator.matrixMultiplication(Xt, X);

        Matrix invM = Inverse.inverseAugment(XtX).matrix;
        Matrix Xty = MatrixOperator.matrixMultiplication(Xt, y);

        Matrix hasil = MatrixOperator.matrixMultiplication(invM, Xty);
        return hasil;
    }

    public static Matrix expandPolynomialMatrix(Matrix Xawal, int derajat) {
        if (derajat <= 1) return Xawal;

        int n = Xawal.getRowsCount();
        int k = Xawal.getColsCount() - 1; // kolom pertama = bias

        // Hitung total kombinasi (pakai kombinatorial)
        int totalFeatures = 1; // bias
        for (int d = 1; d <= derajat; d++) {
            totalFeatures += combination(k + d - 1, d);
        }

        double[][] newX = new double[n][totalFeatures];

        for (int i = 0; i < n; i++) {
            int idx = 0;
            newX[i][idx++] = 1.0; // bias

            int[] pangkat = new int[k];
            generatePolynomialMatrix(Xawal, newX, i, k, derajat, 0, pangkat, idxHolder(new int[]{idx}));
        }

        return new Matrix(newX);
    }

    private static void generatePolynomialMatrix(Matrix X, double[][] newX, int row, int k, int maxDeg, int pos, int[] pangkat, int[] idx) {
        if (pos == k) {
            int sum = 0;
            for (int i = 0; i < pangkat.length; i++) {
                sum += pangkat[i];
            }
            if (sum == 0 || sum > maxDeg) return;

            double val = 1.0;
            for (int j = 0; j < k; j++) {
                for (int t = 0; t < pangkat[j]; t++) {
                    val *= X.getElmt(row, j + 1);
                }
            }
            newX[row][idx[0]++] = val;
            return;
        }

        for (int p = 0; p <= maxDeg; p++) {
            pangkat[pos] = p;
            generatePolynomialMatrix(X, newX, row, k, maxDeg, pos + 1, pangkat, idx);
        }
    }

    private static int combination(int n, int r) {
        if (r == 0 || r == n) return 1;
        return combination(n - 1, r - 1) + combination(n - 1, r);
    }

    private static int[] idxHolder(int[] arr) {
        return arr;
    }


    // ============================================================
    // 🔹 Nama-nama fitur untuk format hasil regresi
    // ============================================================

    public static String[] generateFeatureNames(int k, int degree) {
        if (degree <= 1) {
            String[] names = new String[k + 1];
            names[0] = "";
            for (int i = 1; i <= k; i++) {
                names[i] = "x" + i;
            }
            return names;
        }

        int total = 1 + k + (k * (k + 1)) / 2;
        String[] names = new String[total];
        int idx = 0;
        names[idx++] = ""; // bias

        // linear terms
        for (int i = 1; i <= k; i++) {
            names[idx++] = "x" + i;
        }

        // quadratic & interaction terms
        for (int a = 1; a <= k; a++) {
            for (int b = a; b <= k; b++) {
                if (a == b) names[idx++] = "x" + a + "^2";
                else names[idx++] = "x" + a + "x" + b;
            }
        }

        return names;
    }
}
