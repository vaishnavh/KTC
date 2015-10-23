import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Methods for handling tensors
 * <P>
 * @author Kijung
 */
public class TensorMethods {
	
	/**
	 * import tensor from file
	 * @param path
	 * @param delim
	 * @param modeLengths	mode length of each mode (n -> I_{n}) 
	 * @param modesIdx	column index of each mode in the input file
	 * @param valIdx	column index of entry value in the input file
	 * @param permutedIdx reordered indices as a result of greedy row assignment
	 * @return imported tensor
	 * @throws IOException 
	 */
	public static Tensor importSparseTensor(String path, String delim, int[] modeLengths, int[] modesIdx, int valIdx, int[][] permutedIdx) throws IOException{
		
		int omega=0; // number of observable entries
		BufferedReader br = new BufferedReader(new FileReader(path));
		while(true){
			String line = br.readLine();
			if(line==null)
				break;
			omega++;
		}
		br.close();
		return importSparseTensor(path, delim, omega, modeLengths, modesIdx, valIdx, permutedIdx);
	}
	
	/**
	 * import tensor from file
	 * @param path
	 * @param delim
	 * @param omega	number of observable entries
	 * @param modeLengths	mode length of each mode (n -> I_{n}) 
	 * @param modesIdx	column index of each mode in the input file
	 * @param valIdx	column index of entry value in the input file
	 * @param permutedIdx reordered indices as a result of greedy row assignment
	 * @return imported tensor
	 * @throws IOException 
	 */
	public static Tensor importSparseTensor(String path, String delim, int omega, int[] modeLengths, int[] modesIdx, int valIdx, int[][] permutedIdx) throws IOException{
		
		int N = modesIdx.length;
		int[][] indices = new int[N][omega];
		float[] values = new float[omega];

		
		BufferedReader br = new BufferedReader(new FileReader(path));
		float sum = 0;
		
		int i = 0;
		while(true){
			String line = br.readLine();
			if(line==null)
				break;
			String[] tokens = line.split(delim);
			if(permutedIdx!=null){
				for(int n=0; n<N; n++){
					indices[n][i] = permutedIdx[n][Integer.valueOf(tokens[modesIdx[n]])];
				}
			}
			else {
				for(int n=0; n<N; n++){
					indices[n][i] = Integer.valueOf(tokens[modesIdx[n]]);
				}
			}
			values[i] = Float.valueOf(tokens[valIdx]);
			sum += values[i];
			i++;
		}
			
		br.close();
		
		return new Tensor(N, modeLengths, omega, indices, values, sum);
	}
	
	/**
	 * calculate number of observable entries in each fiber
	 * (n, i_{n}) -> |\Omega^{(n)}_{i_{n}}|
	 * @param tensor
	 * @return (n, i_{n}) -> |\Omega^{(n)}_{i_{n}}|
	 */
	public static int[][] cardinality(Tensor tensor){
		
		int dimension = tensor.N;
		int[] modeSizes = tensor.modeLengths;
		int[][] cardinality = new int[dimension][];
		for(int dim=0; dim<dimension; dim++){
			cardinality[dim] = new int[modeSizes[dim]];
		}
		
		for(int i=0; i<tensor.omega; i++){
			for(int dim=0; dim<dimension; dim++){
				cardinality[dim][tensor.indices[dim][i]]++;
			}
		}
		
		return cardinality;
	}
	
}
