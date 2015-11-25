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

    /**
     * Compute RMSE for Cold Start Entries
     * @param tensor
     * @param params
     * @param N
     * @param K
     * @param nnzFiber
     * @return
     */
	public static double computeRMSEColdStart(final Tensor tensor, final float[][][] params, final int N, final int K,
									 final int[][] nnzFiber){

        double loss = 0;
        int count = 0;
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
            for(int n=0; n<N; n++) {
                if (nnzFiber[n][tensor.indices[n][elemIdx]] == 0) {
                    loss += Math.pow((predict - tensor.values[elemIdx]), 2);
                    count += 1;
                    break;
                }
            }
		}

		return Math.sqrt(loss / count);
	}
}
