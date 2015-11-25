import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.SparseDoubleAlgebra;

import java.util.*;

/**
 * Social Recommend
 *
 * @author Kijung
 */
public class SocialRec {

    /**
     * main function to run Social Recommend
     * @param args	[training] [output] [network] [N] [I1] [I2] ... [IN] [test] [query]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        run(args);
    }

    /**
     * run Social Recommend
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
			 * run Social Recommend
			 */
            String name = "Social Recommend";

            System.out.println("=============");
            System.out.println("Start "+name+"...");
            System.out.println("=============");

            SocialRec method = new SocialRec();

            CSRMatrix adjacencyMat = new CSRMatrix(Kernel.importNetworkFromFile(networkPath, ",", modeSizes[0]));

            CSRMatrix trainingMat = new CSRMatrix(Kernel.importBipartiteFromFile(training, ",", 1, 0, 3, modeSizes[1], modeSizes[0]));
            CSRMatrix testMat = null;
            if(test!=null){
                testMat = new CSRMatrix(Kernel.importBipartiteFromFile(test, ",", 1, 0, 3, modeSizes[1], modeSizes[0]));
            }

            CSRMatrix queryMat = null;
            if(query!=null){
                queryMat = new CSRMatrix(Kernel.importBipartiteFromFile(test, ",", 1, 0, 3, modeSizes[1], modeSizes[0]));
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
     * @param adjacencyMat	adjacency matrix of social graph
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

        for(int item=0; item<modeSizes[1]; item++) {

            int[] trainUsers = training.getColumns(item);
            float[] trainValues = training.getValues(item);
            //ArrayMethods.shuffle(trainValues, random); //debug (random friends)

            Map<Integer, Float> candidateToValue = new HashMap();
            float sum = 0; // debug

            for(int i=0; i<trainUsers.length; i++) {
                int user = trainUsers[i];
                float value = trainValues[i];
                candidateToValue.put(user, value);
                sum += value; // debug
            }

            // float average = trainUsers.length == 0 ? 0 : sum / trainUsers.length; // debug

            for(int i=0; i<trainUsers.length; i++) {
                int user = trainUsers[i];
                double valueSum = 0;
                int neighborNum = 0;
                int[] neighbors = adjacencyMat.getColumns(user);
                for (int neighbor : neighbors) {
                    if(candidateToValue.containsKey(neighbor)) { //friend
                        valueSum += candidateToValue.get(neighbor);
                        neighborNum++;
                    }
                }
                double predict = neighborNum < 50 ? 0 : valueSum/neighborNum;
                trainLoss += Math.pow((predict - trainValues[i]), 2);
                trainingCount++;
            }

            if(test!= null) {
                int[] testUsers = test.getColumns(item);
                float[] testValues = test.getValues(item);

                for (int i = 0; i < testUsers.length; i++) {
                    int user = testUsers[i];
                    double valueSum = 0;
                    int neighborNum = 0;
                    int[] neighbors = adjacencyMat.getColumns(user);
                    for (int neighbor : neighbors) {
                        if(candidateToValue.containsKey(neighbor)) { //friend
                            valueSum += candidateToValue.get(neighbor);
                            neighborNum++;
                        }
                    }
                    double predict = neighborNum < 50 ? 0 : valueSum / neighborNum;
                    testLoss += Math.pow((predict - testValues[i]), 2);
                    testCount++;
                }
            }

            if(query!= null) {
                int[] queryUsers = query.getColumns(item);
                float[] queryValues = query.getValues(item);

                for (int i = 0; i < queryUsers.length; i++) {
                    int user = queryUsers[i];
                    double valueSum = 0;
                    int neighborNum = 0;
                    int[] neighbors = adjacencyMat.getColumns(user);
                    for (int neighbor : neighbors) {
                        if(candidateToValue.containsKey(neighbor)) { //friend
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
