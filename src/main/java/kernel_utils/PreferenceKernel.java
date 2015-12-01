package kernel_utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

public class PreferenceKernel {

	public static DoubleMatrix2D GetPreferenceVector(String filePath,int modeLength) throws IOException{
		BufferedReader brd = new BufferedReader(new FileReader(filePath));
		String strLine="";
		String[] split;
		DoubleMatrix2D result=null;
		
		Hashtable<Integer,Vector<Double[]>> user_to_ratings = new Hashtable<Integer,Vector<Double[]>>();
		while((strLine=brd.readLine())!=null){
			split=strLine.split("\t");
			int user_id = Integer.parseInt(split[0]);
			int prod_id = Integer.parseInt(split[1]);
			
			Double[] arr_ratings = new Double[5];
			arr_ratings[0] = Double.parseDouble(split[3]);
			arr_ratings[1] = Double.parseDouble(split[4]);
			arr_ratings[2] = Double.parseDouble(split[5]);
			arr_ratings[3] = Double.parseDouble(split[6]);
			arr_ratings[4] = Double.parseDouble(split[7]);
			
			Vector<Double[]> this_usr_rating=new Vector<Double[]>();
			if(user_to_ratings.containsKey(user_id)){
				this_usr_rating=user_to_ratings.get(user_id);
			}
			this_usr_rating.add(arr_ratings);
			
			
			
			
			
		}
		return result;
	}
}
