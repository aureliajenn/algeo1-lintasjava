package algeo.modules;

import algeo.modules.Matrix;
public class CofactorResult {
    public final Matrix matrix;
    public final String steps;

    public CofactorResult(Matrix matrix, String steps) {
        this.matrix = matrix;
        this.steps = steps;
    }
}
