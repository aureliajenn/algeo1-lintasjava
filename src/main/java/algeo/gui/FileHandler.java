package algeo.gui;

import algeo.modules.Matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileHandler {
    public static RegressionInput parseRegresi(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        int rowCount = 0;
        int colCount = -1;

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            if (colCount == -1) {
                colCount = parts.length;
            }
            rowCount++;
        }
        sc.close();

        int n = rowCount - 1;
        int k = colCount - 1;

        double[][] X = new double[n][k + 1];
        double[][] y = new double[n][1];
        int derajatPolim;

        sc = new Scanner(new File(filename));
        int row = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");

            if (row == rowCount - 1) {
                derajatPolim = (int) Double.parseDouble(parts[0].replace(',', '.'));
                sc.close();
                return new RegressionInput(new Matrix(X), new Matrix(y), derajatPolim);
            }

            X[row][0] = 1.0;

            for (int j = 0; j < k; j++) {
                X[row][j + 1] = Double.parseDouble(parts[j].replace(',', '.'));
            }

            y[row][0] = Double.parseDouble(parts[k].replace(',', '.'));

            row++;
        }
        sc.close();

        throw new IllegalArgumentException("File input regresi tidak valid");
    }

    private static double parseNumber(String token) {
        token = token.trim().replace(',', '.');
        if (token.contains("/")) {
            String[] frac = token.split("/");
            if (frac.length == 2) {
                double numerator = Double.parseDouble(frac[0]);
                double denominator = Double.parseDouble(frac[1]);
                return numerator / denominator;
            } else {
                throw new NumberFormatException("Format pecahan tidak valid: " + token);
            }
        }
        return Double.parseDouble(token);
    }


    public static Matrix readMatrix(String filename) throws FileNotFoundException {
        File file = new File(filename);

        Scanner sc1 = new Scanner(file);
        int rowCount = 0;
        int colCount = -1;

        while (sc1.hasNextLine()) {
            String line = sc1.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            if (colCount == -1) {
                colCount = parts.length;
            }
            rowCount++;
        }
        sc1.close();

        double[][] arr = new double[rowCount][colCount];

        Scanner sc2 = new Scanner(file);
        int rowIndex = 0;
        while (sc2.hasNextLine()) {
            String line = sc2.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            for (int i = 0; i < colCount; i++) {
                arr[rowIndex][i] = parseNumber(parts[i]);
            }
            rowIndex++;
        }
        sc2.close();

        return new Matrix(arr);
    }

    private static double[] parseRow(String line) {
        String[] parts = line.split("\\s+");
        double[] row = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            row[i] = parseNumber(parts[i]);
        }
        return row;
    }

    public static class RegressionInput {
        public Matrix X;
        public Matrix y;
        public int derajatPolim;

        public RegressionInput(Matrix X, Matrix y, int derajatPolim) {
            this.X = X;
            this.y = y;
            this.derajatPolim = derajatPolim;
        }
    }
}
