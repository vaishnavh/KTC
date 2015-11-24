package preprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

public class Preprocess_Epinions {

	public static void preprocess_ratings_file(String in_path,String map_users_file,String out_path) throws IOException{
		Hashtable<Integer,Integer> map_users= new Hashtable<Integer,Integer>();
		int ctr_users=0;
		Hashtable<Integer,Integer> map_movies = new Hashtable<Integer,Integer>();
		int ctr_movie=0;
		Hashtable<Integer,Integer> map_ts=new Hashtable<Integer,Integer>();
		int ctr_ts=0;
		
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
			
			if(map_ts.containsKey(monthyear)){
				mod_ts=map_ts.get(monthyear);
			}
			else{
				mod_ts=ctr_ts;
				map_ts.put(monthyear,ctr_ts);
				ctr_ts+=1;
			}
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
	
	public static void main(String[] args) throws IOException{
		
		//System.out.println(System.getProperty("user.dir"));
		//preprocess_ratings_file("./data/epinions/ratings.txt","./data/epinions/map_users.txt","./data/epinions/preprocessed_ratings.txt");
		
		Count_Data("./data/epinions/preprocessed_ratings.txt");
		//Count_Data("./data/flixster/flixster/rating.train");
		String in_file="./data/epinions/preprocessed_ratings.txt";
		String train_file="./data/epinions/ratings.train";
		String test_file="./data/epinions/ratings.test";
		//Split_Train_Test(in_file, train_file, test_file);
		
		//Count_Data(train_file);
		//Count_Data(test_file);
		preprocess_network_file("./data/epinions/network.txt","./data/epinions/map_users.txt","./data/epinions/preprocessed_network.txt");
	}
}
