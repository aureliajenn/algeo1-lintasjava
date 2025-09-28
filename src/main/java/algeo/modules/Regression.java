package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;

public class Regression {

    public static Matrix multiRegression(Matrix X, Matrix y) {
        // B = (X^T . X)^-1  . X^T . y
        // Hasil B berupa matrix
        Matrix Xt = Matrix.transpose(X);
        Matrix XtX = MatrixOperator.matrixMultiplication(Xt, X);
        Matrix invM = Matrix.inverse(XtX);
        Matrix Xty = MatrixOperator.matrixMultiplication(Xt, y);

        Matrix result = MatrixOperator.matrixMultiplication(invM, Xty);
        return result;
    }
}
