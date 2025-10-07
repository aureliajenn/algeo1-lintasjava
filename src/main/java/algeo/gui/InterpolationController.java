package algeo.gui;

import algeo.modules.Interpolation;
import algeo.modules.Matrix;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/*
 * Controller yang bertanggung jawab untuk menangani semua aksi
 * dari file InterpolationUI.fxml.
 */
public class InterpolationController {

    @FXML private ToggleGroup methodGroup;
    @FXML private TextArea inputArea;
    @FXML private TextField estimateField;
    @FXML private TextArea outputArea;

    private UIController uiController;

    /*
     * Metode ini akan dipanggil oleh UIController untuk memberikan
     * referensi dirinya sendiri ke controller ini.
     */
    public void setUiController(UIController uiController) {
        this.uiController = uiController;
    }

    @FXML
    void handleCalculate(ActionEvent event) {
        try {
            RadioButton selectedRadioButton = (RadioButton) methodGroup.getSelectedToggle();
            String method = selectedRadioButton.getText();

            Matrix points = MatrixParser.parseMatrix(inputArea.getText());
            if (points.getColsCount() != 2) {
                uiController.showErrorDialog("Error Input", "Setiap baris input harus berisi 2 nilai: x dan y.");
                return;
            }

            if ("Polinomial".equals(method)) {
                Matrix coeffs = Interpolation.polynomialInterpolation(points);
                String polynomial = FormatResult.buildPolynomialString(coeffs);
                String result = "Persamaan Polinomial:\n" + polynomial;

                if (estimateField.getText() != null && !estimateField.getText().trim().isEmpty()) {
                    double xVal = Double.parseDouble(estimateField.getText().replace(',', '.'));
                    double yVal = predictPolynomial(coeffs, xVal);
                    result += "\n\nHasil Taksiran:\nf(" + xVal + ") = " + String.format("%.4f", yVal);
                }
                outputArea.setText(result);

            } else { // Splina Bezier
                Matrix[] segments = Interpolation.interpolasiSplinaBezierKubik(points);
                outputArea.setText(formatBezierResult(segments));
            }
        } catch(Exception ex) {
            uiController.showErrorDialog("Error Interpolasi", "Gagal melakukan interpolasi: " + ex.getMessage());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        uiController.showMainMenu();
    }

    // ========== Helper ==========

    private double predictPolynomial(Matrix coeffs, double x) {
        double y = 0;
        for (int i = 0; i < coeffs.getRowsCount(); i++) {
            y += coeffs.getElmt(i, 0) * Math.pow(x, i);
        }
        return y;
    }

    private String formatBezierResult(Matrix[] segments) {
        StringBuilder sb = new StringBuilder("Titik-titik Kontrol Kurva Splina Bezier Kubik:\n");
        sb.append("------------------------------------------\n");

        int pointCtr = 1;

        // Iterasi melalui setiap segmen
        for (Matrix controlPoints : segments) {
            for (int j = 0; j < controlPoints.getRowsCount(); j++) {
                sb.append(String.format("  Titik Kontrol %d: (%.4f, %.4f)\n", pointCtr,
                        controlPoints.getElmt(j, 0), controlPoints.getElmt(j, 1)));
                pointCtr++;
            }
        }

        sb.append("------------------------------------------\n");
        return sb.toString();
    }
}