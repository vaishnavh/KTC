
import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import kernel_utils.PreferenceKernel;



/**
 * Kernels
 *
 * @author Kijung
 */
public class Kernel {

    /**
     * Return identity matrix with given size
     * @param modeLength length of row and column
     * @param constantFactor constant factor multiplied by the identity matrix
     * @return
     */
    public static CSRMatrix identity(int modeLength, double constantFactor) {

        DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(modeLength, modeLength);
        for(int i=0; i<modeLength; i++) {
            matrix.setQuick(i, i, constantFactor);
        }
        return new CSRMatrix(matrix);
    }

    /**
     * Return the inverse of the RBF kernel with given size
     * @param modeLength length of row and column
     * @param sigma
     * @return
     */
    public static CSRMatrix RBFKernel(int modeLength, double sigma) {
        double denominator = sigma * sigma * 2;
        double[][] kernelMatrix = new double[modeLength][modeLength];
        for(int i=0; i<modeLength; i++){
            for(int j=0; j<modeLength; j++) {
                kernelMatrix[i][j] = Math.exp(-Math.pow(i-j, 2)) / denominator;
            }
        }
        return new CSRMatrix(DenseDoubleAlgebra.DEFAULT.inverse(DoubleFactory2D.dense.make(kernelMatrix)));
    }

    /**
     * Return the inverse of the kernel for image
     * @param modeLength length of row and column
     * @param width
     * @param gamma
     * @return
     */
    public static CSRMatrix imageKernel(int modeLength, double width, double gamma) {
        DoubleMatrix2D kernel = new SparseDoubleMatrix2D(modeLength, modeLength);
        for(int i=0; i<modeLength; i++) {
            for(int j=1; j<=width; j++) {
                if(i+j < modeLength) {
                    kernel.set(i, i + j, 1);
                }
                if(i-j >= 0) {
                    kernel.set(i, i - j, 1);
                }
            }
        }
        return Laplacian(kernel, gamma);
    }
    
    /**
     * Return the Symmetric Kullback-Leibler Divergence for user similarity.
     * @param filePath containing user_id and text of the tweet
     * @return CSRMatrix
     */
    
    public static CSRMatrix SymmetricKLD(String filePath, int modeLength) throws Exception{
    	return SymmetricKLD_Matrix(filePath, modeLength);
    }

    /**
     * Return the user-preference matrix for user similarity.
     * @param filePath containing user_aspect ratings and overall rating
     * @return the preferences of users. 
     */

    public static CSRMatrix Preference_Matrix(String filePath,int modeLength) throws Exception{
    	return PreferenceMatrixSimilarity(filePath,modeLength);
    }
    /**
     * Return the inverse of the commute time kernel with given size
     * @param modeLength length of row and column
     * @return
     */
    public static CSRMatrix CTKernel(String filePath, double gamma, int modeLength) throws IOException {
        return Laplacian(importNetworkFromFile(filePath, ",", modeLength), gamma);
    }

    /**
     * Return the inverse of the regularized laplacian kerenl with given size
     * @param modeLength length of row and column
     * @return
     */
    public static CSRMatrix RLKernel(String filePath, double gamma, int modeLength) throws IOException {
        return regularizedLaplacian(importNetworkFromFile(filePath, ",", modeLength), gamma);
    }
    
    /**
    * Compute the preference similarity matrix.
    *
    */
    private static CSRMatrix PreferenceMatrixSimilarity(String filePath,int modeLength) throws IOException{
    	DoubleMatrix2D preferences = PreferenceKernel.GetPreferenceVector(filePath,modeLength);
    	return new CSRMatrix(preferences);
    }
    
    /**
     * Computes the symmetric KLD for the given text file. 
     */
    
    private static CSRMatrix SymmetricKLD_Matrix(String filePath,int modeLength) throws Exception{
    	DoubleMatrix2D symm_kld = DoubleFactory2D.sparse.make(modeLength, modeLength);
    	symm_kld = importKernelFromFile(filePath, ",", modeLength);
    	return new CSRMatrix(symm_kld);
    }
    
    /**
     * Compute the Laplacian matrix of the given adjacency matrix
     * @param adjacency
     * @param gamma
     * @return
     */
	private static CSRMatrix Laplacian(DoubleMatrix2D adjacency, final double gamma) {
        final DoubleMatrix2D laplacian = adjacency.copy();
	    adjacency.forEachNonZero(new IntIntDoubleFunction() {
	        public double apply(int src, int trg, double value) {
	            laplacian.setQuick(src, trg, -1 * gamma);
	            laplacian.setQuick(src, src, laplacian.get(src, src) + gamma);
	            return value;
	        }
	    });
	    return new CSRMatrix(laplacian);
	}

	/**
     * Compute the regularized Laplacian matrix of the given adjacency matrix
     * @param adjacency
     * @param gamma
     * @return
     */
    private static CSRMatrix regularizedLaplacian(DoubleMatrix2D adjacency, final double gamma) {
        final int[] degree = new int[adjacency.rows()];
        adjacency.forEachNonZero(new IntIntDoubleFunction() {
            public double apply(int src, int trg, double value) {
                degree[src] += 1;
                return value;
            }
        });
        final DoubleMatrix2D laplacian = adjacency.copy();
        adjacency.forEachNonZero(new IntIntDoubleFunction() {
            public double apply(int src, int trg, double value) {
                laplacian.setQuick(src, trg, - gamma / Math.sqrt(degree[src] * degree[trg]));
                return value;
            }
        });
        for(int i=0; i<adjacency.rows(); i++) {
            laplacian.setQuick(i, i, 1 + gamma);
        }
        return new CSRMatrix(laplacian);
    }

    /**
     * import network from given path
     * @param filePath path of the network file
     * @param delim delimeter
     * @param modeLength number of nodes in a graph
     * @return adjacency matrix
     * @throws IOException
     */
    public static DoubleMatrix2D importNetworkFromFile(String filePath, String delim, int modeLength) throws IOException {
        DoubleMatrix2D adjacency = DoubleFactory2D.sparse.make(modeLength, modeLength);
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while(true) {
            String line = br.readLine();
            if (line == null)
                break;
            String[] tokens = line.split(delim);
            int src = Integer.valueOf(tokens[0]);
            int trg = Integer.valueOf(tokens[1]);
            if(src!=trg) {
                adjacency.setQuick(src, trg, 1);
                adjacency.setQuick(trg, src, 1);
            }
        }
        return adjacency;
    }

    /**
     * import network from given path
     * @param filePath path of the network file
     * @param delim delimeter
     * @param srcIdx column index of source index
     * @param trgIdx column index of target index
     * @param valueIdx column index of value
     * @param srcModeLength number of source nodes
     * @param trgModeLength number of target nodes
     * @return adjacency matrix
     * @throws IOException
     */
    public static DoubleMatrix2D importBipartiteFromFile(String filePath, String delim, int srcIdx, int trgIdx, int valueIdx, int srcModeLength, int trgModeLength) throws IOException {
        DoubleMatrix2D adjacency = DoubleFactory2D.sparse.make(srcModeLength, trgModeLength);
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while(true) {
            String line = br.readLine();
            if (line == null)
                break;
            String[] tokens = line.split(delim);
            int src = Integer.valueOf(tokens[srcIdx]);
            int trg = Integer.valueOf(tokens[trgIdx]);
            if(src!=trg) {
                adjacency.setQuick(src, trg, Double.valueOf(tokens[valueIdx]));
            }
        }
        return adjacency;
    }


    /**
     * import network from given path
     * @param filePath path of the network file
     * @param delim delimiter
     * @param modeLength number of nodes in a graph
     * @return similarity matrix
     * @throws IOException
     */
    public static DoubleMatrix2D importKernelFromFile(String filePath,  String delim, int modeLength) throws IOException {
        DoubleMatrix2D kernel = DoubleFactory2D.sparse.make(modeLength, modeLength);
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while(true) {
            String line = br.readLine();
            if (line == null)
                break;
            String[] tokens = line.split(delim);
            int src = Integer.valueOf(tokens[0]);
            int trg = Integer.valueOf(tokens[1]);
            double value=Double.valueOf(tokens[2]);
            if(src!=trg) {
                kernel.setQuick(src, trg, value);
                kernel.setQuick(trg, src, value);
            }
        }
        return kernel;
    }

    
    public static void main(String[] ar) throws Exception {
    	/*
    	DoubleMatrix2D adjacency = importNetworkFromFile("data/kernel.test", ",", 4);
        System.out.println(adjacency);
        System.out.println(Laplacian(adjacency));
        System.out.println(regularizedLaplacian(adjacency, 0.5));
        System.out.println(RBFKernel(4, 1));
        */
    	
    	String filePath="./data/beer_advocate/Beeradvocate_UserDetails.txt";
    	int modeLength=3553;
    	CSRMatrix symm_kld=SymmetricKLD_Matrix(filePath,modeLength);
    	System.out.println(symm_kld);
    }

}
