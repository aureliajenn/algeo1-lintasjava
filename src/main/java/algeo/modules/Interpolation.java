package algeo.modules;

public class Interpolation {
    public static Matrix polynomialInterpolation(Matrix points) {
        int length = points.getRowsCount();
        Matrix augmented = new Matrix(length, length + 1);

        for (int i = 0; i < length; i++) {
            double xi = points.getElmt(i, 0);
            double yi = points.getElmt(i, 1);
            for (int j = 0; j < length; j++) {
                augmented.setElmt(i, j, Math.pow(xi,j));
            }
            augmented.setElmt(i, length, yi);
        }
        return SPL.gaussJordan(augmented).solution;
    }

    public static Matrix[] interpolasiSplinaBezierKubik(Matrix points) {
        int numPoints = points.getRowsCount();
        if (numPoints < 3) {
            throw new IllegalArgumentException("Dibutuhkan minimal 3 titik untuk interpolasi spline.");
        }
        int systemSize = numPoints - 2;

        Matrix tridiagonal = tridiagonalMatrix(systemSize);
        Matrix xPart = createVectorFromPoints(points, 0);
        Matrix yPart = createVectorFromPoints(points, 1);
        Matrix xSolution = SPL.gauss(Matrix.augment(tridiagonal, xPart)).solution;
        Matrix ySolution = SPL.gauss(Matrix.augment(tridiagonal, yPart)).solution;
        Matrix[] result = new Matrix[xSolution.getRowsCount()];
        for (int i = 0; i < xSolution.getRowsCount(); i++) {
            double mx = xSolution.getElmt(i, 0);
            double my = ySolution.getElmt(i, 0);
            result[i] = new Matrix(new double[][] { { mx, my } });
        }
        return result;
    }

    private static Matrix tridiagonalMatrix(int size) {
        Matrix matrix= new Matrix(size, size);

        for (int i = 0; i < size; i++) {
            matrix.setElmt(i, i, 4);

            if (i < size - 1) {
                matrix.setElmt(i, i + 1, 1);
            }

            if (i > 0) {
                matrix.setElmt(i, i - 1, 1);
            }
        }
        return matrix;
    }

    private static Matrix createVectorFromPoints(Matrix points, int col) {
        int vectorSize = points.getRowsCount() - 2;

        Matrix resultVector = new Matrix(vectorSize, 1);

        for (int i = 0; i < vectorSize; i++) {
            double val = 6 * points.getElmt(i + 1, col);
            resultVector.setElmt(i, 0, val);
        }

        double firstVal = resultVector.getElmt(0, 0) - points.getElmt(0, col);
        resultVector.setElmt(0, 0, firstVal);

        double lastVal = resultVector.getElmt(vectorSize - 1, 0) - points.getElmt(points.getRowsCount() - 1, col);
        resultVector.setElmt(vectorSize - 1, 0, lastVal);

        return resultVector;
    }
}

