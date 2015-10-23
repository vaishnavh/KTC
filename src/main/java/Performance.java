/**
 * Common functions related to measure performance
 * <P>
 * @author Kijung
 */
public class Performance {
	
	/**
	 * Compute RMSE
	 * @param tensor	tensor
	 * @param params	factor matrices	(n, i_{n}, k) -> a^{(n)}_{i_{n}k}
	 * @param N	dimension
	 * @param K	rank
	 * @return	RMSE
	 */
	public static double computeRMSE(final Tensor tensor, final float[][][] params, final int N, final int K){

		double loss = 0;

		for(int elemIdx =0; elemIdx < tensor.omega; elemIdx++){

			//estimated value
			float predict = 0;
			for(int k=0; k<K; k++){
				float product = 1;
				for(int n=0; n<N; n++){
					product *= params[n][tensor.indices[n][elemIdx]][k];
				}
				predict += product;
			}
			loss += Math.pow((predict - tensor.values[elemIdx]), 2);
		}

		return Math.sqrt(loss/tensor.omega);
	}
}
