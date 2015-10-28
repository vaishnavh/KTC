import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * Data Structure for Sparse Matrix
 * <P>
 * @author Kijung
 */
public class CSRMatrix {

    private int[][] columns;
    private float[][] values;

    public CSRMatrix(DoubleMatrix2D matrix) {

        int n = matrix.rows();
        final int[] degree = new int[n];
        matrix.forEachNonZero(new IntIntDoubleFunction() {
            public double apply(int src, int trg, double value) {
                degree[src] += 1;
                return value;
            }
        });

        final int[] indices = new int[n];
        columns = new int[n][];
        values = new float[n][];

        for(int i=0; i<n; i++) {
            columns[i] = new int[degree[i]];
            values[i] = new float[degree[i]];
        }

        matrix.forEachNonZero(new IntIntDoubleFunction() {
            public double apply(int src, int trg, double value) {
                columns[src][indices[src]] = trg;
                values[src][indices[src]] = (float) value;
                indices[src]++;
                return value;
            }
        });
    }

    public int[] getColumns(int row) {
        return columns[row];
    }

    public float[] getValues(int row) {
        return values[row];
    }


    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<columns.length; i++){
            for(int j=0; j< columns[i].length; j++){
                buffer.append(i+","+columns[i][j]+","+values[i][j]+"\n");
            }
        }
        return buffer.toString();
    }
}
