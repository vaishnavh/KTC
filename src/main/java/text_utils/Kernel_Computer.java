package text_utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import edu.stanford.nlp.stats.*;
import edu.stanford.nlp.util.Generics;

public class Kernel_Computer {

	public static HashSet<String> Get_Vocab(String file) throws IOException{
		HashSet<String> vocab=new HashSet<String>();
		BufferedReader brd=new BufferedReader(new FileReader(file));
		String strLine="";
		String[] split;
		while((strLine=brd.readLine())!=null){
			split=strLine.split("\t");
			for(int i=1;i<split.length;i++){
				String[] split2=split[i].split(" ");
				for(int j=0;j<split2.length;j++){
					vocab.add(split2[j]);
				}
			}
		}
		return vocab;
	}
	
	public static HashMap<Integer,Distribution<String>> Load_User_Texts(String file) throws IOException{
		HashSet<String> vocab=Get_Vocab(file);
		
		HashMap<Integer,Distribution<String>> user_distributions=new HashMap<Integer,Distribution<String>>();
		BufferedReader brd = new BufferedReader(new FileReader(file));
		String strLine="";
		String[] split;
		
		int prev_user_id=0;
	    ClassicCounter<String> c = new ClassicCounter();
	    Distribution<String> user_distribution;
 	    
		while((strLine=brd.readLine())!=null){
			split=strLine.split("\t");
			int user_id=Integer.parseInt(split[0]);
			if(user_id==prev_user_id){
				for(int i=1;i<split.length;i++){
					String[] split2=split[i].split(" ");
					for(int j=0;j<split2.length;j++){
						c.incrementCount(split2[j]);
					}
				}
			}
			else{
				System.out.println("Read:"+prev_user_id+".Now Reading:"+user_id);
				//Create Distribution Here;
				user_distribution=Distribution.goodTuringSmoothedCounter(c,vocab.size());
				user_distributions.put(prev_user_id, user_distribution);
				//ReInitialize ClassicCounter
				c = new ClassicCounter();
				//Fill Up counter with this line's text.
				for(int i=1;i<split.length;i++){
					String[] split2=split[i].split(" ");
					for(int j=0;j<split2.length;j++){
						c.incrementCount(split2[j]);
					}
				}
				prev_user_id=user_id;
			}
		}
		user_distribution=Distribution.goodTuringSmoothedCounter(c,vocab.size());
		user_distributions.put(prev_user_id, user_distribution);
		
		return user_distributions;
	}

	protected static <K> Set<K> getSetOfAllKeys(Distribution<K> d1, Distribution<K> d2) {
	    /*
		if (d1.getNumberOfKeys() != d2.getNumberOfKeys()){
	      throw new RuntimeException("Tried to compare two Distribution<K> objects but d1.numberOfKeys != d2.numberOfKeys");
	    }
		*/
	    Set<K> allKeys = Generics.newHashSet(d1.getCounter().keySet());
	    allKeys.addAll(d2.getCounter().keySet());
	    /*
	    if (allKeys.size() > d1.getNumberOfKeys()){
	      throw new RuntimeException("Tried to compare two Distribution<K> objects but d1.counter intersect d2.counter > numberOfKeys");
	    }
	    */
	    return allKeys;
	  }

	public static <K> double klDivergence(Distribution<K> from, Distribution<K> to) {
	    Set<K> allKeys = getSetOfAllKeys(from, to);
	    int numKeysRemaining = from.getNumberOfKeys();
	    double result = 0.0;
	    double assignedMass1 = 0.0;
	    double assignedMass2 = 0.0;
	    double log2 = Math.log(2.0);
	    double p1, p2;
	    double epsilon = 1e-10;

	    for (K key : allKeys){
	      p1 = from.probabilityOf(key);
	      p2 = to.probabilityOf(key);
	      numKeysRemaining--;
	      assignedMass1 += p1;
	      assignedMass2 += p2;
	      if (p1 < epsilon) {
	        continue;
	      }
	      double logFract = Math.log((p1 / p2));
	      
	      if (logFract == Double.POSITIVE_INFINITY) {
	        System.out.println("Distributions.kldivergence returning +inf: p1=" + p1 + ", p2=" +p2);
	        System.out.flush();
	        return Double.POSITIVE_INFINITY; // can't recover
	      }
	      
	      result += p1 * (logFract / log2); // express it in log base 2
	    }

	    if (numKeysRemaining != 0){
	      p1 = (1.0 - assignedMass1) / numKeysRemaining;
	      if (p1 > epsilon){
	        p2 = (1.0 - assignedMass2) / numKeysRemaining;
	        double logFract = Math.log(1+(p1 / p2));
	        if (logFract == Double.POSITIVE_INFINITY) {
	          System.out.println("Distributions.klDivergence (remaining mass) returning +inf: p1=" + p1 + ", p2=" +p2);
	          System.out.flush();
	          return Double.POSITIVE_INFINITY; // can't recover
	        }
	        result += numKeysRemaining * p1 * (logFract / log2); // express it in log base 2
	      }
	    }
	    return result;
	  }

	public static void compute_KLD(HashMap<Integer, Distribution<String>> user_texts,
			int modeLengths,String out_path) throws IOException{

		FileWriter fwd=new FileWriter(out_path);
		//final DoubleMatrix2D symm_kld = DoubleFactory2D.sparse.make(modeLengths, modeLengths);
    	
		for(int i=0;i<modeLengths;i++){
			for(int j=i;j<modeLengths;j++){
				double score=0;
				Distribution<String> dist1=null;
				Distribution<String> dist2=null;
				
				if(user_texts.containsKey(i))
					dist1=user_texts.get(i);
				else
					System.out.println("Exit.Key="+i);
				
				if(user_texts.containsKey(j))
					dist2=user_texts.get(j);
				else
					System.out.println("Exit.Key="+j);
				
				System.out.println("Computing between:"+i+" and "+j);
				double kl_u_v=klDivergence(dist1,dist2);
				double kl_v_u=klDivergence(dist2,dist1);
				
				score=(kl_u_v+kl_v_u)/2;
				
				score=1/score;
				
				fwd.write(i+","+j+","+score+"\n");
			}
		}
		fwd.close();
	}

	public static void inverse_and_write(String in_path, String out_path,int modeLength) throws Exception{
		DoubleMatrix2D kernel = DoubleFactory2D.sparse.make(modeLength, modeLength);
        BufferedReader br = new BufferedReader(new FileReader(in_path));
        while(true) {
            String line = br.readLine();
            if (line == null)
                break;
            String[] tokens = line.split(",");
            int src = Integer.valueOf(tokens[0]);
            int trg = Integer.valueOf(tokens[1]);
            double value=Double.valueOf(tokens[2]);
            if(src!=trg) {
                kernel.setQuick(src, trg, value);
                kernel.setQuick(trg, src, value);
            }
        }
        br.close();
        
        System.out.println("Now Inversing");
        
        DoubleMatrix2D inv_kernel = cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra.class.newInstance().inverse(kernel);
        FileWriter fwd = new FileWriter(out_path);
        for(int i=0;i<modeLength;i++){
        	for(int j=i;j<modeLength;j++){
        		fwd.write(i+","+j+","+inv_kernel.get(i, j)+"\n");
        	}
        }
        fwd.close();
	}
	
	
	public static void main(String[] args) throws Exception{
		String in_file="./data/beer_advocate/Beeradvocate_UserDetails.txt";
		String out_file="./data/beer_advocate/UserTextKernel.kernel";
		HashMap<Integer,Distribution<String>> user_distributions=Load_User_Texts(in_file);
		compute_KLD(user_distributions,3553,out_file);
		String out_file2="./data/beer_advocate/UserInvTextKernel.kernel";
		inverse_and_write(out_file, out_file2,3553);
	}
}
