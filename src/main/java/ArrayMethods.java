import java.util.Random;

/**
 * Methods for handling arrays
 * <P>
 * @author Kijung
 */
public class ArrayMethods {
	
	/**
	 * deep copy
	 * @param input	array to copy
	 * @return	copied array
	 */
	public static int[][] copy(int[][] input){
		int[][] result = new int[input.length][];
		for(int i=0; i<input.length; i++){
			result[i] = input[i].clone();
		}
		return result;
	}
	
	/**
	 * deep copy
	 * @param input	array to copy
	 * @return	copied array
	 */
	public static float[][] copy(float[][] input){
		float[][] result = new float[input.length][];
		for(int i=0; i<input.length; i++){
			result[i] = input[i].clone();
		}
		return result;
	}
	
	/**
	 * deep copy
	 * @param input	array to copy
	 * @return	copied array
	 */
	public static double[][] copy(double[][] input){
		double[][] result = new double[input.length][];
		for(int i=0; i<input.length; i++){
			result[i] = input[i].clone();
		}
		return result;
	}
	
	/**
	 * deep copy
	 * @param input	array to copy
	 * @return	copied array
	 */
	public static float[][][] copy(float[][][] input){
		float[][][] result = new float[input.length][][];
		for(int i=0; i<input.length; i++){
			result[i] = copy(input[i]);
		}
		return result;
	}
	
	/**
	 * create random matrix
	 * @param m	row number of created matrix
	 * @param n	column number of created matrix
	 * @param scalarFactor Scalar factor multiplied to each element
	 * @return	created matrix
	 */
	public static float[][] createUniformRandomMatrix(int m, int n, float scalarFactor, Random random){
		float[][] matrix = new float[m][n];
		for(int i=0; i<m; i++){
			for(int j=0; j<n; j++){
				while(matrix[i][j]==0) {
					matrix[i][j] = (random.nextFloat()) * scalarFactor;
				}
			}
		}
		return matrix;
	}

	/**
	 * create a length N sequential list
	 * @param N	length of the list
	 * @return	created sequence
	 */
	public static int[] createSequnce(int N){
		int[] result = new int[N];
		for(int i=0; i<N; i++){
			result[i] = i;
		}
		return result;
	}

	/**
	 * shuffle given array
	 * @param array
	 * @param random
	 */
	public static void shuffle(int[] array, Random random){
		int n = array.length;
		for(int i = n; i>=1; i--) {
			int index = random.nextInt(i);
			int temp = array[i-1];
			array[i-1] = array[index];
			array[index] = temp;
		}
	}

	/**
	 * shuffle given array
	 * @param array
	 * @param random
	 */
	public static void shuffle(float[] array, Random random){
		int n = array.length;
		for(int i = n; i>=1; i--) {
			int index = random.nextInt(i);
			float temp = array[i-1];
			array[i-1] = array[index];
			array[index] = temp;
		}
	}

}
