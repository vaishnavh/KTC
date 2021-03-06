import java.util.Random;

/**
 * KPTF SGD
 *
 * @author Kijung
 */
public class KPTFSGD {

    /**
     * main function to run KPTF SGD
     * @param args	[training] [output] [T] [N] [K] [eta] [sigma] [I1] [I2] ... [IN] [Kernel1] [Kernel2] ... [KernelN] [test] [query]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        run(args);
    }

    /**
     * run KPTF SGD
     * @param args	[training] [output] [T] [N] [K] [eta] [sigma] [I1] [I2] ... [IN] [Kernel1] [Kernel2] ... [KernelN] [test] [query]
     * @throws Exception
     */
    public static double[][] run(String[] args) throws Exception{
        boolean inputError = true;
        double[][] result = null;
        
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
            float sigma = Float.valueOf(args[6]);
            System.out.println("-sigma: "+sigma); // regularization
            int[] modeSizes = new int[N];
            for(int dim=0; dim<N; dim++){
                modeSizes[dim] = Integer.valueOf(args[7+dim]);
                System.out.println("-I"+(dim+1)+": "+modeSizes[dim]);
            }
            CSRMatrix[] invKernels = new CSRMatrix[N];
            for(int dim=0; dim<N; dim++){
                final String kernel = args[7+N+dim];
                String[] tokens = kernel.split(":");
                if(tokens[0].startsWith("None")) { //use identity matrix as the inverse of the kernel matrix
                    double stdev = Double.valueOf(tokens[1]);
                    invKernels[dim] = Kernel.identity(modeSizes[dim], stdev * stdev);
                } else if(tokens[0].startsWith("CT")) {
                    String path = tokens[1];
                    double gamma = Double.valueOf(tokens[2]);
                    invKernels[dim] = Kernel.CTKernel(path, gamma, modeSizes[dim]);
                } else if(tokens[0].startsWith("RL")) {
                    String path = tokens[1];
                    double gamma = Double.valueOf(tokens[2]);
                    invKernels[dim] = Kernel.RLKernel(path, gamma, modeSizes[dim]);
                } else if(tokens[0].startsWith("RBF")) {
                    double rbfSigma = Double.valueOf(tokens[1]);
                    invKernels[dim] = Kernel.RBFKernel(modeSizes[dim], rbfSigma);
                } else if(tokens[0].startsWith("SKLD")){
                    String path=tokens[1];
                    invKernels[dim] = Kernel.SymmetricKLD(path, modeSizes[dim]);
                } else if(tokens[0].startsWith("IMG")){
                    double width = Double.valueOf(tokens[1]);
                    double gamma = Double.valueOf(tokens[2]);
                    invKernels[dim] = Kernel.imageKernel(modeSizes[dim], width, gamma);
                }
                else {
                    throw new Exception("Unknown Kernel Function :"+ kernel);
                }
                System.out.println("-Kernel"+(dim+1)+": "+kernel);
            }
            String test = null;
            if(args.length > 7+2*N ){
                test = args[7+2*N];
                System.out.println("-test: "+test);
            }
            String query = null;
            if(args.length > 8+2*N ){
                query = args[8+2*N];
                System.out.println("-query: "+query);
            }

            inputError = false;

            result = null;
			
			/*
			 * run KPTF SGD
			 */
            String name = "KPTF SGD";

            System.out.println("=============");
            System.out.println("Start "+name+"...");
            System.out.println("=============");

            KPTFSGD method = new KPTFSGD();

            int[] modesIdx = ArrayMethods.createSequnce(N);

            Tensor trainingTensor = TensorMethods.importSparseTensor(training, ",", modeSizes, modesIdx, N, null);
            if(test!=null){
                Tensor testTensor = TensorMethods.importSparseTensor(test, ",", modeSizes, modesIdx, N, null);
                method.setTest(testTensor);
            }

            result = method.run(trainingTensor, K, T, eta, sigma, invKernels, modeSizes, true);
			
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
                String fileName = "run_kptf_sgd.sh";
                System.err.println("Usage: "+fileName+" [training] [output] [T] [N] [K] [eta] [sigma] [I1] [I2] ... [IN] [Kernel1] [Kernel2] ... [kernelN] [test] [query]");
                e.printStackTrace();
            }
            else {
                throw e;
            }
        }
        return result;
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
     * @param sigma	standard deviation
     * @param invKernels n -> S_{n}, inverted kernel matrix for each mode
     * @param modeSizes n -> I_{n}, length of each mode
     * @param printLog
     * @return	[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
     */
    private double[][] run(final Tensor training, final int K, int Tout, float eta0, final float sigma, CSRMatrix[] invKernels, int[] modeSizes, boolean printLog){

        Random random = new Random(0);

        if(printLog){
            System.out.println("iteration,elapsed_time,training_rmse,test_rmse,test_rmse(cold start),learning_rate");
        }

        final int N = training.N;  // dimension
        final boolean useTest = test!=null;

        final int[] modeLengths = training.modeLengths; // n -> I_{n}
        params = new float[N][][]; //factor matrices (n, i_{n}, k) -> a^{(n)}_{i_{n}k}
        for(int dim=0; dim<N; dim++){
            params[dim] = ArrayMethods.createUniformRandomMatrix(modeLengths[dim], K, 1.0f/K, random);
        }

        /**
         * number of observable entries in each fiber (n, i_{n}) -> |\Omega^{(n)}_{i_{n}|
         */
        final int[][] nnzFiber = TensorMethods.cardinality(training);

        for(int n=0; n<N; n++) {
            for(int index = 0; index < modeSizes[n]; index++) {
                if(nnzFiber[n][index]==0) {
                    for(int k=0; k<K; k++) {
                        params[n][index][k] = 0;
                    }
                }
            }
        }

        double[][] result = new double[Tout][4]; //[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
        long start = System.currentTimeMillis();

        float eta = eta0;

        for(int outIter=0; outIter<Tout; outIter++){

            update(training, params, K, sigma, eta, nnzFiber, invKernels, modeSizes, random);

            //compute RMSE
            double trainingRMSE = Performance.computeRMSE(training, params, N, K);
            double testRMSE = 0;
            double testRMSEColdStart = 0;
            if(useTest){
                testRMSE = Performance.computeRMSE(test, params, N, K);
                testRMSEColdStart = Performance.computeRMSEColdStart(test, params, N, K, nnzFiber);
            }

            // check overflow
            if(Double.isNaN(trainingRMSE)||Double.isNaN(testRMSE)){
                System.out.println("Error: NaN");
                break;
            }

            long elapsedTime = System.currentTimeMillis()-start;

            if(printLog){
                System.out.printf("%d,%d,%f,%f,%.10f,%f\n",outIter, elapsedTime, trainingRMSE, testRMSE, testRMSEColdStart, eta);
            }

            result[outIter] = new double[]{(outIter+1), elapsedTime, trainingRMSE, testRMSE};

            //adjust learning rate
            eta = eta0 / ((outIter+2)*0.5f);
            //eta = eta0 / ((outIter+2)*0.5f);
        }

        return result;

    }

    /**
     * update factor matrices and biases
     * @param training	training data
     * @param params	factor matrices (n, i_{n}, k) -> a^{(n)}_{i_{n}k}
     * @param K	rank
     * @param sigma standard deviation
     * @param eta	learning rate
     * @param nnzFiber	(n, i_{n}) -> |\Omega^{(n)}_{i_{n}|, number of observable entries in each fiber
     * @param invKernels n -> S_{n}, inverted kernel matrix for each mode
     * @param modeSizes n -> I_{n}, length of each mode
     * @param random
     */
    public void update(Tensor training, float[][][] params, int K, float sigma, float eta, int[][] nnzFiber, CSRMatrix[] invKernels, int[] modeSizes, Random random){

        int[] sequence = ArrayMethods.createSequnce(training.omega);
        ArrayMethods.shuffle(sequence, random);

        int N = params.length;
        float sigmaSquare = sigma * sigma;
        boolean[][] isUpdated = new boolean[N][];
        for(int n=0; n<N; n++) {
            isUpdated[n] = new boolean[modeSizes[n]];
        }

        for(int _idx=0; _idx<training.omega; _idx++){

            int idx = sequence[_idx];
            int[] indices = new int[N];
            float value = training.values[idx];
            float predict = 0;
            for(int n=0; n<N; n++){
                indices[n] = training.indices[n][idx];
                isUpdated[n][indices[n]] = true;
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


            int[][] kernelColumns = new int[N][];
            float[][] kernelValues = new float[N][];
            for(int n=0; n<N; n++){
                kernelColumns[n] = invKernels[n].getColumns(indices[n]);
                kernelValues[n] = invKernels[n].getValues(indices[n]);
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
                    float kernelTerm = 0;
                    for(int j=0; j<kernelColumns[n].length; j++) {
                        int col = kernelColumns[n][j];
                        float kernelVal = kernelValues[n][j];
                        kernelTerm += kernelVal * params[n][col][k];
                        if(col==indices[n]) {
                            kernelTerm += kernelVal * params[n][col][k];
                        }
                    }
                    params[n][indices[n]][k] = oldValue - eta * ( -2 / sigmaSquare * (value - predict) * productOfRest + kernelTerm / nnzFiber[n][indices[n]]); //update rule
                }
            }
        }

        for(int n=0; n<N; n++) {
            for(int index = 0; index < modeSizes[n]; index++) {
                if(!isUpdated[n][index]) {
                    //System.out.println(n+","+index);
                    int[] kernelColumns = invKernels[n].getColumns(index);
                    float[] kernelValues = invKernels[n].getValues(index);
                    for(int k=0; k<K; k++) {
                        float kernelTerm = 0;
                        for(int j=0; j<kernelColumns.length; j++) {
                            int col = kernelColumns[j];
                            float kernelVal = kernelValues[j];
                            kernelTerm += kernelVal * params[n][col][k];
                        }
                        params[n][index][k] = params[n][index][k] - eta * kernelTerm;
                    }
                }
            }
        }
    }
}
