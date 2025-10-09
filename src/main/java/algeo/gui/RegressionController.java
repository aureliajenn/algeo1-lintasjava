package algeo.gui;

import algeo.modules.Matrix;
import algeo.modules.Regression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/*
 * Controller yang bertanggung jawab untuk menangani semua aksi
 * dari file RegressionUI.fxml.
 */
public class RegressionController {

    @FXML private TextArea infoArea;
    @FXML private TextField estimateField;
    @FXML private TextArea outputArea;

    private UIController uiController;
    private Stage primaryStage;

    // Menyimpan hasil koefisien untuk taksiran
    private Matrix coefficients;

    /*
     * Metode ini akan dipanggil oleh UIController untuk memberikan
     * referensi yang diperlukan oleh controller ini.
     */
    public void setUiController(UIController uiController, Stage primaryStage) {
        this.uiController = uiController;
        this.primaryStage = primaryStage;
    }

    @FXML
    void handleLoadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                FileHandler.RegressionInput regInput = FileHandler.parseRegresi(file.getAbsolutePath());
                this.coefficients = Regression.multiRegression(regInput.X, regInput.y);

                String equation = "Persamaan Regresi:\n" + formatRegressionString(coefficients);
                infoArea.setText("Data dari '" + file.getName() + "' berhasil dimuat.\n\n" + equation);
                outputArea.clear(); // clear out hasil taksiran sebelumnya
            } catch (Exception ex) {
                uiController.showErrorDialog("Error File Regresi", "Gagal memproses file: " + ex.getMessage());
            }
        }
    }

    @FXML
    void handleEstimate(ActionEvent event) {
        if (this.coefficients == null) {
            uiController.showErrorDialog("Info", "Harap muat file data regresi terlebih dahulu.");
            return;
        }
        try {
            String[] parts = estimateField.getText().trim().split("\\s+");
            if (parts[0].isEmpty()){ // Cek jika input kosong
                throw new NumberFormatException("Input taksiran tidak boleh kosong.");
            }
            if (parts.length != this.coefficients.getRowsCount() - 1) {
                String msg = String.format("Error: Harap masukkan %d nilai x.", this.coefficients.getRowsCount() - 1);
                uiController.showErrorDialog("Input Tidak Sesuai", msg);
                return;
            }

            double[] xValues = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                xValues[i] = Double.parseDouble(parts[i].replace(',', '.'));
            }

            double prediction = predictRegression(xValues);
            outputArea.setText("Hasil Taksiran:\ny = " + String.format("%.3f", prediction));

        } catch (Exception ex) {
            uiController.showErrorDialog("Error Taksiran", "Input taksiran tidak valid: " + ex.getMessage());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        uiController.showMainMenu();
    }

    // ========== Helper ==========

    private double predictRegression(double[] xValues) {
        double y = coefficients.getElmt(0, 0); // b0
        for (int i = 0; i < xValues.length; i++) {
            y += coefficients.getElmt(i + 1, 0) * xValues[i];
        }
        return y;
    }

    private String formatRegressionString(Matrix coeffs) {
        StringBuilder sb = new StringBuilder("y = ");
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            double c = coeffs.getElmt(i, 0);
            if (i > 0) {
                sb.append(c >= 0 ? " + " : " - ");
                sb.append(String.format("%.3f", Math.abs(c)));
            } else {
                sb.append(String.format("%.3f", c));
            }
            if (i > 0) sb.append("x").append(i);
        }
        return sb.toString();
    }
}
