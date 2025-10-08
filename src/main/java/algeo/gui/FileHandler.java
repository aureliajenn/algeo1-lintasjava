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
        int derajatPolim = 0; // Inisialisasi

        sc = new Scanner(new File(filename));
        int row = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");

            if (row == rowCount - 1) {
                derajatPolim = (int) Double.parseDouble(parts[0].replace(',', '.'));
                break; // Keluar dari loop setelah mendapatkan derajatPolim
            }

            X[row][0] = 1.0;
            for (int j = 0; j < k; j++) {
                X[row][j + 1] = Double.parseDouble(parts[j].replace(',', '.'));
            }
            y[row][0] = Double.parseDouble(parts[k].replace(',', '.'));
            row++;
        }
        sc.close();

        return new RegressionInput(new Matrix(X), new Matrix(y), derajatPolim);
    }

    /*
     * Membaca seluruh isi file menjadi satu String,
     * lalu menyerahkan proses parsing sepenuhnya ke MatrixParser.
     */
    public static Matrix readMatrix(String filename) throws FileNotFoundException {
        File file = new File(filename);
        StringBuilder fileContent = new StringBuilder();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContent.append(scanner.nextLine()).append("\n");
            }
        }

        return MatrixParser.parseMatrix(fileContent.toString());
    }

    private static double[] parseRow(String line) {
        String[] parts = line.split("\\s+");
        double[] row = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            row[i] = Double.parseDouble(parts[i].replace(',', '.'));
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