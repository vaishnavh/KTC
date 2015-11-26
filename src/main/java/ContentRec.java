import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Content-Based Recommend
 *
 * @author Kijung
 */
public class ContentRec {

    /**
     * main function to run Content-Based Recommend
     * @param args	[training] [output] [network] [N] [I1] [I2] ... [IN] [test] [query]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        run(args);
    }

    /**
     * run Content-Based Recommend
     * @param args	[training] [output] [network] [N] [I1] [I2] ... [IN] [test] [query]
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
            String networkPath = args[2];
            System.out.println("-network: " + networkPath);

            int N = Integer.valueOf(args[3]);
            System.out.println("-N: "+N); // dimension
            int[] modeSizes = new int[N];
            for(int dim=0; dim<N; dim++){
                modeSizes[dim] = Integer.valueOf(args[4+dim]);
                System.out.println("-I"+(dim+1)+": "+modeSizes[dim]);
            }
            String test = null;
            if(args.length > 4+N ){
                test = args[4+N];
                System.out.println("-test: "+test);
            }
            String query = null;
            if(args.length > 5+N ){
                query = args[5+N];
                System.out.println("-query: "+query);
            }

            inputError = false;

            double[][] result = null;
			
			/*
			 * run Content-Based Recommend
			 */
            String name = "Content-Based Recommend";

            System.out.println("=============");
            System.out.println("Start "+name+"...");
            System.out.println("=============");

            ContentRec method = new ContentRec();

            CSRMatrix adjacencyMat = new CSRMatrix(Kernel.importNetworkFromFile(networkPath, ",", modeSizes[1]));

            CSRMatrix trainingMat = new CSRMatrix(Kernel.importBipartiteFromFile(training, ",", 0, 1, 3, modeSizes[0], modeSizes[1]));
            CSRMatrix testMat = null;
            if(test!=null){
                testMat = new CSRMatrix(Kernel.importBipartiteFromFile(test, ",", 0, 1, 3, modeSizes[0], modeSizes[1]));
            }

            CSRMatrix queryMat = null;
            if(query!=null){
                queryMat = new CSRMatrix(Kernel.importBipartiteFromFile(test, ",", 0, 1, 3, modeSizes[0], modeSizes[1]));
            }

            result = method.run(trainingMat, testMat, queryMat, adjacencyMat, modeSizes, true);
			/*
			 * write output
			 */
            System.out.println("=======================");
            System.out.println("Start writing output...");
            System.out.println("=======================");
            Output.writePerformance(outputDir, result, 1);
            if(query!=null) {
                Output.writeEstimate(outputDir, queryMat, modeSizes[1]);
            }

            System.out.println("===========");
            System.out.println("Complete!!!");
            System.out.println("===========");

        } catch(Exception e){
            if(inputError){
                String fileName = "run_social_rec.sh";
                System.err.println("Usage: "+fileName+" [training] [output] [network] [N] [I1] [I2] ... [IN] [test] [query]");
                e.printStackTrace();
            }
            else {
                throw e;
            }
        }
    }

    /**
     * run SocialRec
     * @param training	training data
     * @param test	test data
     * @param query	query data
     * @param adjacencyMat	adjacency matrix of movie network
     * @param modeSizes n -> I_{n}, length of each mode
     * @param printLog
     * @return	[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
     */
    private double[][] run(final CSRMatrix training, final CSRMatrix test, final CSRMatrix query, CSRMatrix adjacencyMat, int[] modeSizes, boolean printLog){

        double[][] result = new double[1][4]; //[(iteration, elapsed time, trainingRMSE, testRMSE), ...]
        long start = System.currentTimeMillis();
        long elapsedTime = System.currentTimeMillis()-start;

        double trainLoss = 0;
        int trainingCount =0;
        double testLoss = 0;
        int testCount =0;

        Random random = new Random(0);

        for(int user=0; user<modeSizes[0]; user++) {

            int[] trainMovies = training.getColumns(user);
            float[] trainValues = training.getValues(user);
            //ArrayMethods.shuffle(trainValues, random); //debug (random friends)

            Map<Integer, Float> candidateToValue = new HashMap();
            float sum = 0; // debug

            for(int i=0; i<trainMovies.length; i++) {
                int item = trainMovies[i];
                float value = trainValues[i];
                candidateToValue.put(item, value);
                sum += value; // debug
            }

            // float average = trainUsers.length == 0 ? 0 : sum / trainUsers.length; // debug

            for(int i=0; i<trainMovies.length; i++) {
                int item = trainMovies[i];
                double valueSum = 0;
                int neighborNum = 0;
                int[] neighbors = adjacencyMat.getColumns(item);
                for (int neighbor : neighbors) {
                    if(candidateToValue.containsKey(neighbor)) { //friend
                        valueSum += candidateToValue.get(neighbor);
                        neighborNum++;
                    }
                }
                double predict = neighborNum == 0 ? 0 : valueSum/neighborNum;
                trainLoss += Math.pow((predict - trainValues[i]), 2);
                trainingCount++;
            }

            if(test!= null) {
                int[] testItems = test.getColumns(user);
                float[] testValues = test.getValues(user);

                for (int i = 0; i < testItems.length; i++) {
                    int item = testItems[i];
                    double valueSum = 0;
                    int neighborNum = 0;
                    int[] neighbors = adjacencyMat.getColumns(item);
                    for (int neighbor : neighbors) {
                        if(candidateToValue.containsKey(neighbor)) { //neighbor items
                            valueSum += candidateToValue.get(neighbor);
                            neighborNum++;
                        }
                    }
                    double predict = neighborNum == 0 ? 0 : valueSum / neighborNum;
                    testLoss += Math.pow((predict - testValues[i]), 2);
                    testCount++;
                }
            }

            if(query!= null) {
                int[] queryItems = query.getColumns(user);
                float[] queryValues = query.getValues(user);

                for (int i = 0; i < queryItems.length; i++) {
                    int item = queryItems[i];
                    double valueSum = 0;
                    int neighborNum = 0;
                    int[] neighbors = adjacencyMat.getColumns(item);
                    for (int neighbor : neighbors) {
                        if(candidateToValue.containsKey(neighbor)) { //neighbor items
                            valueSum += candidateToValue.get(neighbor);
                            neighborNum++;
                        }
                    }
                    double predict = neighborNum == 0 ? 0 : valueSum / neighborNum;
                    queryValues[i] = (float) predict;
                }
            }
        }

        double trainingRMSE = Math.sqrt(trainLoss/trainingCount);
        double testRMSE = testCount == 0 ? 0 :Math.sqrt(testLoss/testCount);

        if(printLog){
            System.out.printf("%d,%d,%f,%f\n", 0, elapsedTime, trainingRMSE, testRMSE);
        }
        result[0] = new double[]{(1), elapsedTime, trainingRMSE, testRMSE};
        return result;

    }
}
