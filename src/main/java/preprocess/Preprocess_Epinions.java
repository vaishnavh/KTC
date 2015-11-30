package preprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Preprocess_Epinions {

	public static int[] Get_MinMax(String in_path) throws Exception{
		int[] arr=new int[2];
		int min_ts=Integer.MAX_VALUE;
		int max_ts=Integer.MIN_VALUE;
		
		BufferedReader brd=new BufferedReader(new FileReader(in_path));
		String strLine="";
		
		while((strLine=brd.readLine())!=null){
			strLine=strLine.trim();
			strLine=strLine.replace("  "," ");
			String[] split=strLine.split(" ");
			
			int user_id=Double.valueOf(split[0]).intValue();
			int prod_id=Double.valueOf(split[1]).intValue();
			double rating=Double.valueOf(split[3]);
			long timestamp=Double.valueOf(split[5]).longValue();
			Date date=new Date(timestamp*1000);
			int monthyear = (date.getYear()-70)*12+date.getMonth();
			
			if (monthyear<min_ts)
				min_ts=monthyear;
			if(monthyear>max_ts)
				max_ts=monthyear;
				
		}
		
		arr[0]=min_ts;
		arr[1]=max_ts;
		
		return arr;
	}
	
	public static void preprocess_ratings_file(String in_path,String map_users_file,String out_path) throws Exception{
		Hashtable<Integer,Integer> map_users= new Hashtable<Integer,Integer>();
		int ctr_users=0;
		
		Hashtable<Integer,Integer> map_movies = new Hashtable<Integer,Integer>();
		int ctr_movie=0;
		
		int[] arr=Get_MinMax(in_path);
		int min_ts=arr[0];
		int max_ts=arr[1];
		System.out.println("Min Ts="+min_ts);
		System.out.println("Max Ts="+max_ts);
		System.out.println("Diff="+(max_ts-min_ts));
		
		FileWriter fwd_map=new FileWriter(map_users_file);
		BufferedReader brd=new BufferedReader(new FileReader(in_path));
		FileWriter fwd=new FileWriter(out_path);
		
		String strLine="";
		
		while((strLine=brd.readLine())!=null){
			strLine=strLine.trim();
			strLine=strLine.replace("  "," ");
			String[] split=strLine.split(" ");
			
			int user_id=Double.valueOf(split[0]).intValue();
			int prod_id=Double.valueOf(split[1]).intValue();
			double rating=Double.valueOf(split[3]);
			long timestamp=Double.valueOf(split[5]).longValue();
			Date date=new Date(timestamp*1000);
			int monthyear = (date.getYear()-70)*12+date.getMonth();
			
			int mod_user_id=-1;
			int mod_prod_id=-1;
			int mod_ts=-1;
			
			if(map_users.containsKey(user_id)){
				mod_user_id=map_users.get(user_id);
			}
			else{
				mod_user_id=ctr_users;
				map_users.put(user_id, ctr_users);
				fwd_map.write(user_id+"\t"+ctr_users+"\n");
				ctr_users+=1;
			}
			
			if(map_movies.containsKey(prod_id)){
				mod_prod_id=map_movies.get(prod_id);
			}
			else{
				mod_prod_id=ctr_movie;
				map_movies.put(prod_id, ctr_movie);
				ctr_movie+=1;
			}
			
			mod_ts=monthyear-min_ts;
			
			fwd.write(mod_user_id+","+mod_prod_id+","+mod_ts+","+rating+"\n");
		}
		fwd_map.close();
		brd.close();
		fwd.close();
	}
	
	public static void preprocess_network_file(String in_path,String map_file,String out_path) throws IOException{
		Hashtable<Integer,Integer> map_users=new Hashtable<Integer,Integer>();
		BufferedReader map=new BufferedReader(new FileReader(map_file));
		String strLine="";
		while((strLine=map.readLine())!=null){
			String[] split=strLine.split("\t");
			int user_id=Integer.parseInt(split[0]);
			int mod_id=Integer.parseInt(split[1]);
			map_users.put(user_id,mod_id);
		}
		map.close();
		int error=0;
		
		BufferedReader brd=new BufferedReader(new FileReader(in_path));
		FileWriter fwd=new FileWriter(out_path);
		String[] split;
		while((strLine=brd.readLine())!=null){
			strLine=strLine.trim();
			strLine=strLine.replace("  "," ");
			split=strLine.split(" ");
			int user1=Double.valueOf(split[0]).intValue();
			int user2=Double.valueOf(split[1]).intValue();
			
			if(map_users.containsKey(user1) && map_users.containsKey(user2)){
				int mod_u1=map_users.get(user1);
				int mod_u2=map_users.get(user2);
				fwd.write(mod_u1+","+mod_u2+"\n");
			}
			else{
				error+=1;
			}
		}
		fwd.close();
		brd.close();
		System.out.println(error);
	}
	
	public static void Count_Data(String in_path) throws IOException{
		HashSet<Integer> users=new HashSet<Integer>();
		HashSet<Integer> movies=new HashSet<Integer>();
		HashSet<Integer> ts=new HashSet<Integer>();
		
		BufferedReader brd=new BufferedReader(new FileReader(in_path));
		String strLine="";
		
		
		while((strLine=brd.readLine())!=null){
			String[] split=strLine.split(",");
			int user_id=Integer.parseInt(split[0]);
			int movie_id=Integer.parseInt(split[1]);
			int timestamp=Integer.parseInt(split[2]);
			double rating=Double.parseDouble(split[3]);
			
			users.add(user_id);
			movies.add(movie_id);
			ts.add(timestamp);
			
			
		}
		
		System.out.println("#Distinct Users="+users.size());
		System.out.println("#Distinct Movies="+movies.size());
		System.out.println("#Distinct TS="+ts.size());
	}
	
	public static void Split_Train_Test(String in_file,String train_file,String test_file) throws IOException{
		double test_set_percentage=0.1;
		BufferedReader brd=new BufferedReader(new FileReader(in_file));
		FileWriter fwd_train=new FileWriter(train_file);
		FileWriter fwd_test=new FileWriter(test_file);
		
		String strLine="";
		while((strLine=brd.readLine())!=null){
			double rand_value=Math.random();
			if(rand_value<test_set_percentage)
				fwd_test.write(strLine+"\n");
			else
				fwd_train.write(strLine+"\n");
		}
		brd.close();
		fwd_train.close();
		fwd_test.close();
	}

	public static void Average(String in_path, String out_path) throws IOException{
		
		BufferedReader brd= new BufferedReader(new FileReader(in_path));
		String strLine="";
		String[] split;
		
		double sum=0;
		int ctr=0;
		
		while((strLine=brd.readLine())!=null){
			split=strLine.split(",");
			double rating=Double.parseDouble(split[3]);
			sum+=rating;
			ctr+=1;
		}
		brd.close();
		
		double average = (double)sum/ctr;
		
		FileWriter fwd = new FileWriter(out_path);
		
		brd=new BufferedReader(new FileReader(in_path));
		while((strLine=brd.readLine())!=null){
			split=strLine.split(",");
			double rating=Double.parseDouble(split[3]);
			double value = rating - average;
			
			fwd.write(split[0]+","+split[1]+","+split[2]+","+value+"\n");
		}
		fwd.close();
		brd.close();
	}
	
	public static void average_ratings_file(String training_path, String test_path,
						String out_train_path,String out_test_path) throws IOException{
		
		Average(training_path,out_train_path);
		Average(test_path,out_test_path);
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println(System.getProperty("user.dir"));
		//preprocess_ratings_file("./data/epinions/ratings.txt","./data/epinions/map_users.txt","./data/epinions/preprocessed_ratings.txt");
		//preprocess_network_file("./data/epinions/network.txt","./data/epinions/map_users.txt","./data/epinions/preprocessed_network.txt");
		
		//Count_Data("./data/epinions/preprocessed_ratings.txt");
		//Count_Data("./data/flixster/flixster/rating.train");
		String in_file="./data/epinions/preprocessed_ratings.txt";
		String train_file="./data/epinions/ratings.train";
		String test_file="./data/epinions/ratings.test";
		//Split_Train_Test(in_file, train_file, test_file);
		
		String center_train_file="./data/epinions/rating_centered_time.train";
		String center_test_file="./data/epinions/rating_centered_time.test";
		
		average_ratings_file(train_file, test_file, center_train_file, center_test_file);
		//Count_Data(train_file);
		//Count_Data(test_file);
	}
}
