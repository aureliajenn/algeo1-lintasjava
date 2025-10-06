package algeo.gui;

import algeo.modules.Matrix;

public class FormatResult {

    public static String matrixToString(Matrix m) {
        if (m == null) return "Matriks null atau tidak valid.";
        return m.toString();
    }

    public static String formatSolutionResult(Matrix solutionMatrix) {
        if (solutionMatrix == null) {
            return "Sistem Persamaan Linier tidak memiliki solusi.";
        }
        int numVariables = solutionMatrix.getRowsCount();
        int numCols = solutionMatrix.getColsCount();
        StringBuilder sb = new StringBuilder();

        if (numCols == 1) {
            // Solusi unik/tunggal
            sb.append("Solusi unik ditemukan:\n");
            for (int i = 0; i < numVariables; i++) {
                sb.append(String.format("x%d = %.4f\n", i + 1, solutionMatrix.getElmt(i, 0)));
            }
        } else {
            // Solusi banyak (parametrik)
            sb.append("Terdapat banyak solusi (solusi parametrik):\n");
            String[] params = {"t", "s", "r", "p", "q"};
            for (int i = 0; i < numVariables; i++) {
                sb.append(String.format("x%d = ", i + 1));
                double constant = solutionMatrix.getElmt(i, 0);
                boolean isFirstTerm = true;
                if (Math.abs(constant) > 1e-9 || numCols == 1) {
                    sb.append(String.format("%.4f", constant));
                    isFirstTerm = false;
                }
                for (int j = 1; j < numCols; j++) {
                    double coeff = solutionMatrix.getElmt(i, j);
                    if (Math.abs(coeff) > 1e-9) {
                        String paramName = (j - 1 < params.length) ? params[j - 1] : "t" + (j);
                        if (!isFirstTerm) {
                            sb.append(coeff > 0 ? " + " : " - ");
                        } else if (coeff < 0) {
                            sb.append("-");
                        }
                        isFirstTerm = false;
                        double absCoeff = Math.abs(coeff);
                        if (Math.abs(absCoeff - 1.0) > 1e-9) {
                            sb.append(String.format("%.4f", absCoeff));
                        }
                        sb.append(paramName);
                    }
                }
                if (isFirstTerm) sb.append("0.0000");
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String buildPolynomialString(Matrix coeffs) {
        StringBuilder sb = new StringBuilder("y(x) = ");
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            double c = coeffs.getElmt(i, 0);
            if (i > 0) {
                sb.append(c >= 0 ? " + " : " - ");
                sb.append(String.format("%.4f", Math.abs(c)));
            } else {
                sb.append(String.format("%.4f", c));
            }
            if (i == 1) sb.append("x");
            else if (i > 1) sb.append("x^").append(i);
        }
        return sb.toString();
    }
}