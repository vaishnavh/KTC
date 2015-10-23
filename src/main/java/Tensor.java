/**
 * Data structure to save tensor data
 * <P>
 * @author Kijung
 */
public class Tensor {

    public int N; //dimension
    public int[] modeLengths; // mode length of each mode (n -> I_{n})
    public int omega; // number of observable entries
    public int[][] indices; // (n, i) -> nth mode index of ith entry of the tensor
    public float[] values; // i -> entry value of ith entry of the tensor

    /**
     *
     * @param N	//dimension
     * @param modeLengths	// mode length of each mode (n -> I_{n})
     * @param omega	// number of observable entries
     * @param indices	(i, n) -> nth mode index of ith entry of the tensor
     * @param values	// i -> entry value of ith entry of the tensor
     */
    public Tensor(int N, int[] modeLengths, int omega, int[][] indices, float[] values, float sum){
        this.N = N;
        this.modeLengths = modeLengths;
        this.omega = omega;
        this.indices = indices;
        this.values = values;
    }

    /**
     * copy
     * @return	copied tensor
     */
    public Tensor copy(){
        return new Tensor(this);
    }

    private Tensor(Tensor target){
        this.N = target.N;
        this.modeLengths = target.modeLengths.clone();
        this.omega = target.omega;
        this.indices = ArrayMethods.copy(target.indices);
        this.values = target.values.clone();
    }

}

