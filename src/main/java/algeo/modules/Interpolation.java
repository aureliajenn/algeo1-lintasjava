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
}
