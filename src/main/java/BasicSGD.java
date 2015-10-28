import java.util.Random;

/**
 * Basic SGD
 * <P>
 * @author Kijung
 */
public class BasicSGD {
	
	/**
	 * main function to run BasicSGD
	 * @param args	[training] [output] [T] [N] [K] [eta] [lambda] [I1] [I2] ... [IN] [test] [query]
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		run(args);
	}
	
	/**
	 * run BasicSGD
	 * @param args	[training] [output] [T] [N] [K] [eta] [lambda] [I1] [I2] ... [IN] [test] [query]
	 * @throws Exception
	 */
	public static void run(String[] args) throws Exception{
		
		boolean inputError = true;
		
		try {
		
			/*
			 * parameter check
			 */
			System.out.println("========================");
			System.out.println("Start parameter check...");
			System.out.println("========================");
			
			String training = args[0];
			System.out.println("-training: "+training);
			String outputDir = args[1];
			System.out.println("-output: " + outputDir);
			int T = Integer.valueOf(args[2]);
			System.out.println("-T: "+T); // number of iterations
			int N = Integer.valueOf(args[3]);
			System.out.println("-N: "+N); // dimension
			int K = Integer.valueOf(args[4]);
			System.out.println("-K: "+K); // rank
			float eta = Float.valueOf(args[5]);
			System.out.println("-eta: "+eta); // learning rate
			float lambda = Float.valueOf(args[6]);
			System.out.println("-lambda: "+lambda); // regularization
			int[] modeSizes = new int[N];
			for(int dim=0; dim<N; dim++){
				modeSizes[dim] = Integer.valueOf(args[7+dim]);
				System.out.println("-I"+(dim+1)+": "+modeSizes[dim]);
			}
			String test = null;
			if(args.length > 7+N ){
				test = args[7+N];
				System.out.println("-test: "+test);
			}
			String query = null;
			if(args.length > 8+N ){
				query = args[8+N];
				System.out.println("-query: "+query);
			}
			
			
			inputError = false;
							
			double[][] result = null;
			
			/*
			 * run BasicSGD
			 */
			String name = "Basic SGD";
			
			System.out.println("=============");
			System.out.println("Start "+name+"...");
			System.out.println("=============");
			
			BasicSGD method = new BasicSGD();

            int[] modesIdx = ArrayMethods.createSequnce(N);

			Tensor trainingTensor = TensorMethods.importSparseTensor(training, ",", modeSizes, modesIdx, N, null);
			if(test!=null){
				Tensor testTensor = TensorMethods.importSparseTensor(test, ",", modeSizes, modesIdx, N, null);
				method.setTest(testTensor);
			}
			
			result = method.run(trainingTensor, K, T, eta, lambda, true);
			
			/*
			 * write output
			 */
			System.out.println("=======================");
			System.out.println("Start writing output...");
			System.out.println("=======================");
			
			Output.writePerformance(outputDir, result, T);
			Output.writeFactorMatrices(outputDir, method.params);
			
			if(query!=null){
				Tensor queryTensor = TensorMethods.importSparseTensor(query, ",", modeSizes, modesIdx, 0, null);
                Output.calculateEstimate(queryTensor, method.params, N, K);
				Output.writeEstimate(outputDir, queryTensor, N);
			}
			
			System.out.println("===========");
			System.out.println("Complete!!!");
			System.out.println("===========");
		
		} catch(Exception e){
			if(inputError){
				String fileName = "run_basic_sgd.sh";
				System.err.println("Usage: "+fileName+" [training] [output] [T] [N] [K] [eta] [lambda] [I1] [I2] ... [IN] [test] [query]");
				e.printStackTrace();
			}
			else {
				throw e;
			}
		}
	}
	
	/**
	 * test data
	 */
	private Tensor test;
	
	/**
	 * factor matrices (n, i_{n}, k) -> a^{(n)}_{i_{n}k}
	 */
	private float[][][] params;
	
	/**
	 * set test data (optional)
	 * @param test	test data (tensor)
	 */
	private void setTest(Tensor test){
		this.test = test;
	}
	
	/**
	 * run BasicSGD
	 * @param training	training data
	 * @param K	rank
	 * @param Tout	number of outer iterations
	 * @param eta0	initial learning rate
	 * @param lambda	regularization parameter
	 * @param printLog
	 * @return	[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
	 */
	private double[][] run(final Tensor training, final int K, int Tout, float eta0, final float lambda, boolean printLog){

		Random random = new Random(0);
		
		if(printLog){
			System.out.println("iteration,elapsed_time,training_rmse,test_rmse,learning_rate");
		}
		
		final int N = training.N;  // dimension
		final boolean useTest = test!=null;
		
		final int[] modeLengths = training.modeLengths; // n -> I_{n}
		params = new float[N][][]; //factor matrices (n, i_{n}, k) -> a^{(n)}_{i_{n}k}
		for(int dim=0; dim<N; dim++){
			params[dim] = ArrayMethods.createUniformRandomMatrix(modeLengths[dim], K, 1, random);
		}
		
		/**
		 * number of observable entries in each fiber (n, i_{n}) -> |\Omega^{(n)}_{i_{n}|
		 */
		final int[][] nnzFiber = TensorMethods.cardinality(training);
		
		double[][] result = new double[Tout][4]; //[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
		long start = System.currentTimeMillis();

		float eta = eta0;

		for(int outIter=0; outIter<Tout; outIter++){

            update(training, params, K, lambda, eta, nnzFiber, random);
			
			//compute RMSE
			double trainingRMSE = Performance.computeRMSE(training, params, N, K);
			double testRMSE = 0;
			if(useTest){
				testRMSE = Performance.computeRMSE(test, params, N, K);
			}
			
			// check overflow
			if(Double.isNaN(trainingRMSE)||Double.isNaN(testRMSE)){
				System.out.println("Error: NaN");
				break;
			}
			
			long elapsedTime = System.currentTimeMillis()-start;
			
			if(printLog){
				System.out.printf("%d,%d,%f,%f,%f\n",outIter, elapsedTime, trainingRMSE, testRMSE, eta);
			}
			
			result[outIter] = new double[]{(outIter+1), elapsedTime, trainingRMSE, testRMSE}; 
			
			//adjust learning rate
            eta = eta0 / ((outIter+2)*0.5f);
		}
		
		return result;
		
	}
	
	/**
	 * update factor matrices and biases
	 * @param training	training data
	 * @param params	factor matrices (n, i_{n}, k) -> a^{(n)}_{i_{n}k}
	 * @param K	rank
	 * @param lambda	regularization parameters
	 * @param eta	learning rate
	 * @param nnzFiber	(n, i_{n}) -> |\Omega^{(n)}_{i_{n}|, number of observable entries in each fiber
	 */
	public void update(Tensor training, float[][][] params, int K, float lambda, float eta, int[][] nnzFiber, Random random){

		int[] sequence = ArrayMethods.createSequnce(training.omega);
		ArrayMethods.shuffle(sequence, random);

		int N = params.length;
		
		for(int _idx=0; _idx<training.omega; _idx++){

			int idx = sequence[_idx];
			int[] indices = new int[N];
			float value = training.values[idx];
			float predict = 0;
			for(int n=0; n<N; n++){
				indices[n] = training.indices[n][idx];
			}

			float[] products = new float[K];
			for(int k=0; k<K; k++){
				float product = 1;
				for(int n=0; n<N; n++){
					product *= params[n][indices[n]][k];
				}
				predict += product;
				products[k] = product;
			}

			for(int k=0; k<K; k++){
				float product = products[k];
				for(int n=0; n<N; n++){
					float oldValue = params[n][indices[n]][k];
					float productOfRest = 0;
					if(oldValue==0){
						 productOfRest = 1;
						 for(int _n=0; _n<N; _n++){
							 if(_n!=n){
								 productOfRest *= params[_n][indices[_n]][k];
							 }
						 }
					}
					else{
						productOfRest = product/oldValue;
					}
					params[n][indices[n]][k] = oldValue - eta * (-2 * (value - predict) * productOfRest + 2 * lambda * oldValue / nnzFiber[n][indices[n]]); //update rule
				}
			}
		}
	}
}
