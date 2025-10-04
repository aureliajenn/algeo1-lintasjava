//import algeo.modules.ModuleContoh;

//public class App {
//    public static void main(String[] args) {
//        ModuleContoh m = new ModuleContoh();
//
//        System.out.println("Hai");
//        m.halo();
//    }
//}

// ====== MAIN GUI EXAMPLE ======
// Be sure to uncoment the section below and follow the steps written in README

// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.stage.Stage;
// public class App extends Application {
//
//     @Override
//     public void start(Stage stage) {
//         ModuleContoh m = new ModuleContoh();
//
//         Label label = new Label("Hello JavaFX from Algeo 1!");
//         Scene scene = new Scene(label, 300, 200);
//
//         stage.setTitle("Matrix Calculator");
//         stage.setScene(scene);
//         stage.show();
//     }
//
//     public static void main(String[] args) {
//         launch(args);
//     }
// }
package algeo;

import algeo.modules.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

public class App extends Application {

    private Stage primaryStage;
    private final FileChooser fileChooser = new FileChooser();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Lintasjava Calculator");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        showMainMenu();
        primaryStage.show();
    }

    // ==================== SCENE UTAMA & MENU ====================

    private void showMainMenu() {
        VBox layout = createMenuLayout("Lintasjava Calculator", "Menu");
        Button btnSPL = createMenuButton("Sistem Persamaan Linear");
        Button btnDet = createMenuButton("Determinan Matriks");
        Button btnInv = createMenuButton("Invers Matriks");
        Button btnReg = createMenuButton("Regresi Linier Berganda");
        Button btnInt = createMenuButton("Interpolasi");
        Button btnExit = new Button("Keluar");
        btnExit.setMaxWidth(Double.MAX_VALUE);
        btnExit.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        layout.getChildren().addAll(btnSPL, btnDet, btnInv, btnReg, btnInt, btnExit);
        btnSPL.setOnAction(e -> showSplMenu());
        btnDet.setOnAction(e -> showDeterminantMenu());
        btnInv.setOnAction(e -> showInverseMenu());
        btnReg.setOnAction(e -> showRegressionUI());
        btnInt.setOnAction(e -> showInterpolationUI());
        btnExit.setOnAction(e -> Platform.exit());
        primaryStage.setScene(new Scene(layout, 500, 450));
    }

    private void showSplMenu() {
        VBox layout = createMenuLayout("Sistem Persamaan Linear", "Pilih metode yang ingin digunakan:");
        Button btnGauss = createMenuButton("Metode Eliminasi Gauss");
        Button btnGaussJordan = createMenuButton("Metode Eliminasi Gauss-Jordan");
        Button btnInverse = createMenuButton("Metode Matriks Balikan");
        Button btnCramer = createMenuButton("Metode Kaidah Cramer");
        Button btnBack = createBackButton(this::showMainMenu);
        Button btnExit = createExitButton();
        layout.getChildren().addAll(btnGauss, btnGaussJordan, btnInverse, btnCramer, btnBack, btnExit);
        btnGauss.setOnAction(e -> showCalculationScene("SPL", "Eliminasi Gauss"));
        btnGaussJordan.setOnAction(e -> showCalculationScene("SPL", "Eliminasi Gauss-Jordan"));
        btnInverse.setOnAction(e -> showCalculationScene("SPL", "Metode Matriks Balikan"));
        btnCramer.setOnAction(e -> showCalculationScene("SPL", "Kaidah Cramer"));
        primaryStage.setScene(new Scene(layout, 500, 450));
    }

    private void showDeterminantMenu() {
        VBox layout = createMenuLayout("Determinan Matriks", "Pilih metode yang ingin digunakan:");
        Button btnKofaktor = createMenuButton("Metode Ekspansi Kofaktor");
        Button btnReduksi = createMenuButton("Metode Reduksi Baris");
        Button btnBack = createBackButton(this::showMainMenu);
        Button btnExit = createExitButton();
        layout.getChildren().addAll(btnKofaktor, btnReduksi, btnBack, btnExit);
        btnKofaktor.setOnAction(e -> showCalculationScene("Determinan", "Ekspansi Kofaktor"));
        btnReduksi.setOnAction(e -> showCalculationScene("Determinan", "Reduksi Baris"));
        primaryStage.setScene(new Scene(layout, 500, 400));
    }

    private void showInverseMenu() {
        VBox layout = createMenuLayout("Invers Matriks", "Pilih metode yang ingin digunakan:");
        Button btnAugment = createMenuButton("Metode Augment (Gauss-Jordan)");
        Button btnAdjoin = createMenuButton("Metode Adjoint");
        Button btnBack = createBackButton(this::showMainMenu);
        Button btnExit = createExitButton();
        layout.getChildren().addAll(btnAugment, btnAdjoin, btnBack, btnExit);
        btnAugment.setOnAction(e -> showCalculationScene("Invers", "Metode Augment"));
        btnAdjoin.setOnAction(e -> showCalculationScene("Invers", "Metode Adjoint"));
        primaryStage.setScene(new Scene(layout, 500, 400));
    }


    // ==================== SCENE PERHITUNGAN UMUM (SPL, DET, INV) ====================

    private void showCalculationScene(String type, String method) {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        Label header = new Label(type + " - " + method);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        layout.setTop(header);

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Masukkan matriks di sini atau gunakan tombol 'Load File'.");
        VBox centerBox = new VBox(10, new Label("Input:"), inputArea);
        layout.setCenter(centerBox);

        TextArea outputArea = createOutputArea("Hasil akan ditampilkan di sini.");
        TextArea stepsArea = createOutputArea("Langkah-langkah akan ditampilkan di sini.");

        Button btnLoad = new Button("Load File (.txt)");
        Button btnCalc = new Button("Hitung");
        Button btnSave = new Button("Simpan Hasil (.txt)");
        Runnable backAction = this::showMainMenu;
        if (type.equals("SPL")) backAction = this::showSplMenu;
        else if (type.equals("Determinan")) backAction = this::showDeterminantMenu;
        else if (type.equals("Invers")) backAction = this::showInverseMenu;
        Button btnBack = createBackButton(backAction);

        HBox buttonBar = new HBox(10, btnLoad, btnCalc, btnSave, btnBack);
        buttonBar.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(10, buttonBar, new Label("Output:"), outputArea, new Label("Langkah-langkah:"), stepsArea);
        layout.setBottom(bottomBox);

        btnLoad.setOnAction(e -> loadFileToTextArea(inputArea));

        btnCalc.setOnAction(e -> {
            try {
                Matrix inputMatrix = parseMatrixFromTextArea(inputArea.getText());
                String resultText = "";
                String stepsText = "Maaf, fitur langkah-langkah belum diimplementasikan di backend.";

                switch (type) {
                    case "SPL":
                        resultText = solveSPL(inputMatrix, method);
                        break;
                    case "Determinan":
                        resultText = solveDeterminant(inputMatrix, method);
                        break;
                    case "Invers":
                        resultText = solveInverse(inputMatrix, method);
                        break;
                }
                outputArea.setText(resultText);
                stepsArea.setText(stepsText);

            } catch (Exception ex) {
                showErrorDialog("Error Perhitungan", "Terjadi kesalahan: " + ex.getMessage());
            }
        });

        btnSave.setOnAction(e -> {
            String contentToSave = "Metode: " + method + "\n\n" +
                    "Input:\n" + inputArea.getText() + "\n\n" +
                    "Hasil:\n" + outputArea.getText();
            saveTextToFile(contentToSave);
        });

        primaryStage.setScene(new Scene(layout, 700, 600));
    }

    // ==================== UI SPESIFIK: INTERPOLASI & REGRESI ====================

    private void showInterpolationUI() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));
        Label header = new Label("Interpolasi Polinomial");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        layout.setTop(header);

        TextArea inputArea = new TextArea();
        inputArea.setPromptText("Masukkan titik-titik (x y) per baris.");
        TextField estimateInput = new TextField();
        estimateInput.setPromptText("Masukkan nilai x yang ingin ditaksir");
        HBox estimateBox = new HBox(10, new Label("Taksir f(x) untuk x:"), estimateInput);
        estimateBox.setAlignment(Pos.CENTER_LEFT);

        VBox centerBox = new VBox(10, new Label("Input Titik:"), inputArea, estimateBox);
        layout.setCenter(centerBox);

        TextArea outputArea = createOutputArea("Hasil akan ditampilkan di sini.");
        Button btnLoad = new Button("Load File");
        Button btnCalc = new Button("Hitung");
        Button btnSave = new Button("Simpan Hasil");
        Button btnBack = createBackButton(this::showMainMenu);
        HBox buttonBar = new HBox(10, btnLoad, btnCalc, btnSave, btnBack);

        VBox bottomBox = new VBox(10, buttonBar, new Label("Output:"), outputArea);
        layout.setBottom(bottomBox);

        btnLoad.setOnAction(e -> loadFileToTextArea(inputArea));
        btnCalc.setOnAction(e -> {
            try {
                Matrix points = parseMatrixFromTextArea(inputArea.getText());
                int n = points.getRowsCount();
                Matrix augmented = new Matrix(n, n + 1);
                for (int i = 0; i < n; i++) {
                    double x = points.getElmt(i, 0);
                    double y = points.getElmt(i, 1);
                    for (int j = 0; j < n; j++) {
                        augmented.setElmt(i, j, Math.pow(x, j));
                    }
                    augmented.setElmt(i, n, y);
                }

                Matrix solutionRREF = SPL.gaussJordan(augmented);

                Matrix coeffs = new Matrix(n, 1);
                for (int i = 0; i < n; i++) {
                    coeffs.setElmt(i, 0, solutionRREF.getElmt(i, n));
                }

                String polynomial = buildPolynomialString(coeffs);
                String result = "Persamaan Polinomial:\n" + polynomial;

                if (!estimateInput.getText().trim().isEmpty()) {
                    double xVal = Double.parseDouble(estimateInput.getText());
                    double yVal = predictPolynomial(coeffs, xVal);
                    result += "\n\nHasil Taksiran:\nf(" + xVal + ") = " + String.format("%.4f", yVal);
                }
                outputArea.setText(result);

            } catch(Exception ex) {
                showErrorDialog("Error", "Gagal melakukan interpolasi: " + ex.getMessage());
            }
        });
        btnSave.setOnAction(e -> {
            String content = "Metode: Interpolasi Polinomial\n\nInput:\n" + inputArea.getText() + "\n\n" + outputArea.getText();
            saveTextToFile(content);
        });

        primaryStage.setScene(new Scene(layout, 700, 600));
    }

    private void showRegressionUI() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));
        Label header = new Label("Regresi Linier Berganda");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        layout.setTop(header);

        TextArea infoArea = createOutputArea("Silakan muat file data (.txt).");
        TextField estimateInput = new TextField();
        estimateInput.setPromptText("Masukkan x1, x2, ... dipisah spasi");
        HBox estimateBox = new HBox(10, new Label("Taksir y untuk:"), estimateInput);

        VBox centerBox = new VBox(10, new Label("Info Data:"), infoArea, estimateBox);
        layout.setCenter(centerBox);

        TextArea outputArea = createOutputArea("Hasil akan ditampilkan di sini.");
        Button btnLoad = new Button("Load File");
        Button btnCalc = new Button("Taksir Nilai");
        Button btnSave = new Button("Simpan Hasil");
        Button btnBack = createBackButton(this::showMainMenu);
        HBox buttonBar = new HBox(10, btnLoad, btnCalc, btnSave, btnBack);

        VBox bottomBox = new VBox(10, buttonBar, new Label("Output:"), outputArea);
        layout.setBottom(bottomBox);

        final Matrix[] coefficients = new Matrix[1];

        btnLoad.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Controller.RegressionInput regInput = Controller.parseRegresi(file.getAbsolutePath());
                    coefficients[0] = Regression.multiRegression(regInput.X, regInput.y);

                    String equation = "Persamaan Regresi:\n" + buildRegressionString(coefficients[0]);
                    infoArea.setText("Data dari '" + file.getName() + "' berhasil dimuat.");
                    outputArea.setText(equation);
                } catch (Exception ex) {
                    showErrorDialog("Error File", "Gagal memproses file: " + ex.getMessage());
                }
            }
        });

        btnCalc.setOnAction(e -> {
            if (coefficients[0] == null) {
                showErrorDialog("Info", "Harap muat file data regresi terlebih dahulu.");
                return;
            }
            try {
                String[] parts = estimateInput.getText().trim().split("\\s+");
                double[] xValues = new double[parts.length];
                for (int i = 0; i < parts.length; i++) xValues[i] = Double.parseDouble(parts[i]);

                double prediction = predictRegression(coefficients[0], xValues);
                String currentText = outputArea.getText().split("\n\nHasil Taksiran:")[0];
                outputArea.setText(currentText + "\n\nHasil Taksiran:\ny = " + String.format("%.4f", prediction));

            } catch (Exception ex) {
                showErrorDialog("Error", "Input taksiran tidak valid: " + ex.getMessage());
            }
        });

        btnSave.setOnAction(e -> {
            String content = "Input File: (dari file yang dipilih)\n\n" + outputArea.getText();
            saveTextToFile(content);
        });

        primaryStage.setScene(new Scene(layout, 700, 600));
    }

    // ==================== LOGIKA PERHITUNGAN ====================

    private String solveSPL(Matrix augmentedMatrix, String method) {
        try {
            switch (method) {
                case "Eliminasi Gauss":
                case "Eliminasi Gauss-Jordan":
                    Matrix solutionMatrix = SPL.gaussJordan(augmentedMatrix.copyMatrix());
                    return formatSolutionResult(solutionMatrix);

                case "Metode Matriks Balikan":
                case "Kaidah Cramer":
                    int rows = augmentedMatrix.getRowsCount();
                    int cols = augmentedMatrix.getColsCount();
                    int numVars = cols - 1;

                    if (rows != numVars) {
                        return "Error: Untuk metode ini, matriks koefisien (A) harus persegi.";
                    }

                    // Memisahkan A dan b
                    Matrix A = new Matrix(rows, numVars);
                    Matrix b = new Matrix(rows, 1);
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < numVars; j++) {
                            A.setElmt(i, j, augmentedMatrix.getElmt(i, j));
                        }
                        b.setElmt(i, 0, augmentedMatrix.getElmt(i, numVars));
                    }

                    Matrix uniqueSolution;
                    if (method.equals("Metode Matriks Balikan")) {
                        uniqueSolution = SPL.inverseMethod(A, b);
                    } else { // Kaidah Cramer
                        uniqueSolution = SPL.cramer(A, b);
                    }
                    return formatSolutionResult(uniqueSolution);

                default:
                    return "Metode SPL tidak dikenal.";
            }
        } catch (IllegalArgumentException e) {
            // Menangkap error "det=0" dari metode Cramer/Inverse
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Memformat matriks hasil solusi SPL menjadi String yang mudah dibaca.
     * Fungsi ini bisa menangani kasus tidak ada solusi, solusi unik, dan solusi parametrik.
     *
     * @param solutionMatrix Matriks hasil dari fungsi SPL.solve...
     * @return String representasi dari solusi.
     */
    private String formatSolutionResult(Matrix solutionMatrix) {
        // kasus 1: Tidak ada solusi
        if (solutionMatrix == null) {
            return "Sistem Persamaan Linier tidak memiliki solusi.";
        }

        int numVariables = solutionMatrix.getRowsCount();
        int numCols = solutionMatrix.getColsCount();
        StringBuilder sb = new StringBuilder();

        // kasus 2: Solusi unik (matriks hasil hanya punya 1 kolom)
        if (numCols == 1) {
            sb.append("Solusi unik ditemukan:\n");
            for (int i = 0; i < numVariables; i++) {
                // Format: x1 = 3.0, x2 = 4.5, dst.
                sb.append(String.format("x%d = %.4f\n", i + 1, solutionMatrix.getElmt(i, 0)));
            }
            return sb.toString();
        }

        // kasus 3: Solusi banyak / parametrik (matriks hasil > 1 kolom)
        else {
            sb.append("Terdapat banyak solusi (solusi parametrik):\n");
            String[] params = {"t", "s", "r", "p", "q"}; // Nama parameter

            for (int i = 0; i < numVariables; i++) { // Untuk setiap variabel (x1, x2, ...)
                sb.append(String.format("x%d = ", i + 1));

                // Ambil bagian konstanta (dari kolom pertama)
                double constant = solutionMatrix.getElmt(i, 0);
                boolean isFirstTerm = true;

                // Tampilkan konstanta jika tidak nol atau jika itu satu-satunya suku
                if (constant != 0 || numCols == 1) {
                    sb.append(String.format("%.4f", constant));
                    isFirstTerm = false;
                }

                // Tambahkan bagian parametrik (dari kolom-kolom berikutnya)
                for (int j = 1; j < numCols; j++) {
                    double coeff = solutionMatrix.getElmt(i, j);
                    if (Math.abs(coeff) > 1e-9) { // Cek jika koefisien tidak nol
                        String paramName = (j - 1 < params.length) ? params[j - 1] : "t" + (j);

                        // Tentukan tanda (+ atau -)
                        if (!isFirstTerm) {
                            sb.append(coeff > 0 ? " + " : " - ");
                        } else if(coeff < 0) {
                            sb.append("-");
                        }

                        isFirstTerm = false;

                        double absCoeff = Math.abs(coeff);
                        // Jangan tampilkan angka 1 jika tidak perlu (misal: "t" bukan "1.00t")
                        if (Math.abs(absCoeff - 1.0) > 1e-9) {
                            sb.append(String.format("%.4f", absCoeff));
                        }
                        sb.append(paramName);
                    }
                }
                // Jika semua 0, berarti xi = 0
                if (isFirstTerm) {
                    sb.append("0.0000");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    private String solveDeterminant(Matrix matrix, String method) {
        double determinant;
        if ("Reduksi Baris".equals(method)) {
            determinant = Determinant.detReduksiBaris(matrix);
        } else {
            determinant = Determinant.detCofactor(matrix);
        }
        return "Nilai Determinan: " + String.format("%.4f", determinant);
    }

    private String solveInverse(Matrix matrix, String method) {
        Matrix inverse;
        if ("Metode Adjoint".equals(method)) {
            inverse = Inverse.inverseAdjoin(matrix);
        } else {
            inverse = Inverse.inverseAugment(matrix);
        }
        return "Matriks Invers:\n" + matrixToString(inverse);
    }


    // ==================== HELPER & UTILITY ====================

    private VBox createMenuLayout(String title, String subtitle) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.TOP_CENTER);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 0 10 0;");
        layout.getChildren().addAll(titleLabel, subtitleLabel);
        return layout;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(40);
        return btn;
    }

    private Button createBackButton(Runnable action) {
        Button btn = new Button("Kembali ke Menu");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Button createExitButton() {
        Button btn = new Button("Keluar");
        btn.setOnAction(e -> Platform.exit());
        btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        return btn;
    }

    private TextArea createOutputArea(String prompt) {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        area.setPromptText(prompt);
        area.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 14px;");
        return area;
    }

    private void loadFileToTextArea(TextArea textArea) {
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                Matrix m = Controller.readMatrix(file.getAbsolutePath());
                textArea.setText(matrixToString(m));
            } catch (FileNotFoundException ex) {
                showErrorDialog("File Tidak Ditemukan", "File yang Anda pilih tidak dapat ditemukan.");
            } catch (Exception ex) {
                showErrorDialog("Error Membaca File", "Terjadi kesalahan saat membaca file: " + ex.getMessage());
            }
        }
    }

    private void saveTextToFile(String content) {
        fileChooser.setTitle("Simpan Hasil Ke File");
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(content);
            } catch (FileNotFoundException e) {
                showErrorDialog("Error Menyimpan File", "Gagal menyimpan file: " + e.getMessage());
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Matrix parseMatrixFromTextArea(String text) {
        String[] lines = text.trim().split("\\n");
        if (lines.length == 0 || lines[0].trim().isEmpty()) throw new IllegalArgumentException("Input matriks tidak boleh kosong.");
        String[] firstLineParts = lines[0].trim().split("\\s+");
        int cols = firstLineParts.length;
        double[][] data = new double[lines.length][cols];
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].trim().split("\\s+");
            if (parts.length != cols) throw new IllegalArgumentException("Jumlah kolom tidak konsisten pada baris " + (i + 1));
            for (int j = 0; j < cols; j++) {
                data[i][j] = Double.parseDouble(parts[j].replace(',', '.'));
            }
        }
        return new Matrix(data);
    }

    private String matrixToString(Matrix m) {
        if (m == null) return "Matriks null.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.getRowsCount(); i++) {
            for (int j = 0; j < m.getColsCount(); j++) {
                sb.append(String.format("%12.4f", m.getElmt(i, j)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatSolutionVector(Matrix solution) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < solution.getRowsCount(); i++) {
            sb.append(String.format("x%d = %.4f\n", (i + 1), solution.getElmt(i, 0)));
        }
        return sb.toString();
    }

    private String formatSplSolution(Matrix rref) {
        int rows = rref.getRowsCount();
        int cols = rref.getColsCount();
        int numVars = cols - 1;

        for (int i = 0; i < rows; i++) {
            boolean allZero = true;
            for (int j = 0; j < numVars; j++) {
                if (Math.abs(rref.getElmt(i, j)) > 1e-9) {
                    allZero = false;
                    break;
                }
            }
            if (allZero && Math.abs(rref.getElmt(i, numVars)) > 1e-9) {
                return "SPL tidak memiliki solusi.";
            }
        }

        boolean[] isPivotCol = new boolean[numVars];
        int[] pivotRow = new int[numVars];
        Arrays.fill(pivotRow, -1);
        int pivotCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < numVars; j++) {
                if (Math.abs(rref.getElmt(i, j) - 1) < 1e-9) {
                    boolean isPivot = true;
                    for (int k=0; k < rows; k++){
                        if (k != i && Math.abs(rref.getElmt(k,j)) > 1e-9){
                            isPivot = false;
                            break;
                        }
                    }
                    if (isPivot) {
                        isPivotCol[j] = true;
                        pivotRow[j] = i;
                        pivotCount++;
                        break;
                    }
                }
            }
        }

        if (pivotCount == numVars) {
            Matrix solution = new Matrix(numVars, 1);
            for(int i = 0; i < numVars; i++){
                solution.setElmt(i,0, rref.getElmt(i, numVars));
            }
            return "Solusi tunggal:\n" + formatSolutionVector(solution);
        } else {
            StringBuilder sb = new StringBuilder("Solusi tak hingga (parametrik):\n");
            String[] params = {"t", "s", "r", "q", "p"};
            int paramIndex = 0;
            String[] solutions = new String[numVars];
            for (int j = 0; j < numVars; j++) {
                if (!isPivotCol[j]) {
                    solutions[j] = params[paramIndex++];
                }
            }
            for (int j = 0; j < numVars; j++){
                if (isPivotCol[j]){
                    int row = pivotRow[j];
                    StringBuilder expr = new StringBuilder(String.format("%.4f", rref.getElmt(row, numVars)));
                    for (int k = j + 1; k < numVars; k++) {
                        if (!isPivotCol[k]) {
                            double coeff = rref.getElmt(row, k);
                            if (Math.abs(coeff) > 1e-9) {
                                if (coeff > 0) expr.append(" - "); else expr.append(" + ");
                                expr.append(String.format("%.4f", Math.abs(coeff))).append(solutions[k]);
                            }
                        }
                    }
                    solutions[j] = expr.toString();
                }
            }
            for(int i=0; i<numVars; i++){
                sb.append("x").append(i + 1).append(" = ").append(solutions[i]).append("\n");
            }
            return sb.toString();
        }
    }

    private String buildPolynomialString(Matrix coeffs) {
        StringBuilder sb = new StringBuilder("y(x) = ");
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            double c = coeffs.getElmt(i, 0);
            if (i > 0) {
                if (c >= 0) sb.append(" + "); else sb.append(" - ");
                sb.append(String.format("%.4f", Math.abs(c)));
            } else {
                sb.append(String.format("%.4f", c));
            }
            if (i == 1) sb.append("x");
            else if (i > 1) sb.append("x^").append(i);
        }
        return sb.toString();
    }

    private double predictPolynomial(Matrix coeffs, double x) {
        double y = 0;
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            y += coeffs.getElmt(i, 0) * Math.pow(x, i);
        }
        return y;
    }

    private String buildRegressionString(Matrix coeffs) {
        StringBuilder sb = new StringBuilder("y = ");
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            double c = coeffs.getElmt(i, 0);
            if (i > 0) {
                if (c >= 0) sb.append(" + "); else sb.append(" - ");
                sb.append(String.format("%.4f", Math.abs(c)));
            } else {
                sb.append(String.format("%.4f", c));
            }
            if (i > 0) sb.append("x").append(i);
        }
        return sb.toString();
    }

    private double predictRegression(Matrix coeffs, double[] xValues) {
        double y = coeffs.getElmt(0, 0); // b0
        for (int i = 0; i < xValues.length; i++) {
            y += coeffs.getElmt(i + 1, 0) * xValues[i];
        }
        return y;
    }
}