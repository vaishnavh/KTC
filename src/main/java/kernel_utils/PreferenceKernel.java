package kernel_utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

public class PreferenceKernel {

	public static DoubleMatrix2D GetPreferenceVector(String filePath,int modeLength) throws IOException{
		BufferedReader brd = new BufferedReader(new FileReader(filePath));
		String strLine="";
		String[] split;
		DoubleMatrix2D result=null;
		
		Hashtable<Integer,double[][]> user_to_ratings = new Hashtable<Integer,double[][]>();
		while((strLine=brd.readLine())!=null){
			split=strLine.split("\t");
			int user_id = Integer.parseInt(split[0]);
			double r1 = Double.parseDouble(split[1]);
			double r2 = Double.parseDouble(split[2]);
			double r3 = Double.parseDouble(split[3]);
			double r4 = Double.parseDouble(split[4]);
			double ov = Double.parseDouble(split[5]);

			
		}
		return result;
	}
}
