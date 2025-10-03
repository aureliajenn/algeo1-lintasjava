package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.Inverse;
import algeo.modules.Determinant;

public class Regression {

    public static Matrix multiRegression(Matrix X, Matrix y) {
        // B = (X^T . X)^-1  . X^T . y
        // Hasil B berupa matrix
        Matrix Xt = X.transpose();
        Matrix XtX = MatrixOperator.matrixMultiplication(Xt, X);

        Matrix invM = Inverse.inverseAugment(XtX);
        Matrix Xty = MatrixOperator.matrixMultiplication(Xt, y);

        Matrix hasil = MatrixOperator.matrixMultiplication(invM, Xty);
        return hasil;
    }
}
