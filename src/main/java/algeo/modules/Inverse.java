package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.SPL;

public class Inverse {
    public Matrix inverseAugment() {
        if (!isSquare()) {
            throw new IllegalArgumentException("Matriks harus persegi");
        }

        int n = getRowsCount();

        // buat matriks [A | I]
        Matrix I = Matrix.identity(n);
        Matrix augmented = Matrix.augment(copyMatrix(), I);

        Matrix reduced = SPL.reducedEchelonForm(augmented);

        Matrix inverse = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse.setElmt(i, j, reduced.getElmt(i, j + n));
            }
        }

        return inverse;
    }

    public static Matrix inverseAdjoin(Matrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Invers tidak terdefinisi");
        }

        double determinant = Determinant.detCofactor(a);
        if (determinant == 0) {
            throw new IllegalArgumentException("Matrix Singular, Invers tidak terdefinisi");
        }

        return Matrix.scalarDivision(Determinant.cofactorMatrix(a).transpose(), determinant);
    }
}