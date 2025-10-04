package algeo.modules;
import algeo.modules.Matrix;
import algeo.modules.MatrixOperator;
import algeo.modules.SPL;

public class Inverse {
    public static Matrix inverseAugment(Matrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matriks harus persegi");
        }

        double determinant = Determinant.detCofactor(a);
        if (determinant == 0) {
            throw new IllegalArgumentException("Matrix Singular, Invers tidak terdefinisi");
        }
        int n = a.getRowsCount();

        // buat matriks [A | I]
        Matrix I = Matrix.identity(n);
        Matrix augmented = Matrix.augment(a.copyMatrix(), I);

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

        return MatrixOperator.scalarDivision(cofactorMatrix(a).transpose(), determinant);
    }

    /*
     * membentuk matriks kofaktor dari matriks a
     */
    public static Matrix cofactorMatrix(Matrix a){
        if (!a.isSquare()){
            throw new IllegalStateException("Matriks kofaktor hanya bisa dibentuk dari matriks persegi");
        }
        int n = a.getRowsCount();
        Matrix result = new Matrix(n,n);
        for (int i = 0; i < n;i++){
            for (int j = 0; j < a.getColsCount(); j++){
                Matrix r = a.removeRowColMatrix(i,j);
                double cofactor = Math.pow(-1 , (i + j)) * Determinant.detCofactor(r);
                result.setElmt(i,j,cofactor);
            }
        }
        return result;
    }
}
