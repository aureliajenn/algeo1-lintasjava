package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.SPL;

public class Inverse {
    public static class InverseResult {
        public Matrix inverse;
        public String steps;

        public InverseResult(Matrix inverse, String steps) {
            this.inverse = inverse;
            this.steps = steps;
        }
    }

    public static InverseResult inverseAugment(Matrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matriks harus persegi");
        }

        int n = a.getRowsCount();
        StringBuilder sb = new StringBuilder();

        // buat matriks [A | I]
        Matrix I = Matrix.identity(n);
        Matrix augmented = Matrix.augment(a.copyMatrix(), I);
        sb.append("1: Membentuk matriks augmented [A | I]\n");
        sb.append(augmented.toString()).append("\n\n");

        Matrix reduced = SPL.reducedEchelonForm(augmented);
        sb.append("2. Eliminasi Gauss-Jordan:\n");
        sb.append(reduced.toString()).append("\n\n");

        Matrix inverse = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse.setElmt(i, j, reduced.getElmt(i, j + n));
            }
        }

        sb.append("3. Sisi kanan sebagai matriks invers\n");
        sb.append(inverse.toString());

        return new InverseResult(inverse, sb.toString());
    }

    public static InverseResult inverseAdjoin(Matrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matriks harus persegi");
        }
        // TODO: implementasi langkah inversAdjoin
        double determinant = Determinant.detCofactor(a);
        if (determinant == 0) {
            throw new IllegalArgumentException("Matrix Singular, Invers tidak terdefinisi");
        }

        return MatrixOperator.scalarDivision(Determinant.cofactorMatrix(a).transpose(), determinant);
    }
}
