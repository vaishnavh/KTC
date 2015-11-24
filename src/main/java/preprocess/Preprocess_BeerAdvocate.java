package preprocess;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Preprocess_BeerAdvocate {
	HashMap<String, TreeSet<Review>> userReviews;
	static int reviewThresholdBeer = 50;
	int reviewCount = 0;
	StopWordRemover stop = new StopWordRemover();
	Lemmatizer lem = new Lemmatizer("./lemmatiser/");

	public void LoadBeerData(String file) throws Exception{
		System.out.println("Loading data from:"+file);
		BufferedReader br = new BufferedReader(new FileReader(file));
		userReviews = new HashMap<String, TreeSet<Review>>();

		String strLine="";
		String[] split;

		HashMap<String, Double> aspectRatings = null;
		HashMap<String, String> metaData = null;
		Review review = null;

		while((strLine=br.readLine())!=null){
			split=strLine.split(":");

			if(split.length<2)
				continue;

			split[0]=split[0].trim().toLowerCase();
			split[1]=split[1].trim().toLowerCase();

			if(split.length>2){
				for(int i=1;i<split.length-1;i++){
					split[1]=split[1]+":"+split[i];
				}
				split[1]=split[1].trim().toLowerCase();
			}

			if(strLine.startsWith("beer/name")){
				if(review!=null){
					TreeSet<Review> reviews = new TreeSet<Review>();
					if (userReviews.containsKey(review.author))
						reviews = userReviews.get(review.author);
					reviews.add(review);
					userReviews.put(review.author, reviews);
					reviewCount++;
				}
				review = new Review();
			}

			if (split[0].contains("time")) {
				review.time = Integer.parseInt(split[1]);
				continue;
			}

			if (split[0].contains("text")) {			
				//review.text = split[1];
				review.text = processText(split[1]);
				continue;
			}

			if (split[0].contains("profilename")) {
				review.author = split[1];
				if(review.author.length()==0)
				{
					review.author =  "ZEROCHAR_UNNAMED";
				}
				continue;
			}

			if (split[0].contains("overall")) {
				review.rating = Double.parseDouble(split[1].split("/")[0]);
				continue;
			}

			if (split[0].startsWith("beer")) {
				if (split[0].contains("beerid"))
					review.item= split[1];
			}

			if (split[0].startsWith("review")) {
				review.aspectRatings.put(split[0], Double.parseDouble(split[1].split("/")[0]));
			}
		}
		br.close();
		System.out.println("Data Loaded");
		//combine all users with less than threshold reviews to a background "anon" user
		TreeSet<Review> anonReviews = new TreeSet<Review>();	
		Iterator<Map.Entry<String, TreeSet<Review>>> userReviewsIt = userReviews.entrySet().iterator();

		while (userReviewsIt.hasNext()) {
			Map.Entry<String, TreeSet<Review>> entry = userReviewsIt.next();
			TreeSet<Review> reviews = entry.getValue();	
			if (reviews.size() < reviewThresholdBeer) {
				anonReviews.addAll(reviews);
				userReviewsIt.remove();
			}
		}
		//userReviews.put("anon", anonReviews);
	}

	public String processText(String text) {

		text = text.replaceAll("[,$()\"~`^*/\\.'-\\?:;,!]", "");
		text = text.replaceAll("\t", " ");
		String tok[] = text.split(" ");
		String buf="";
		for (int i=0; i<tok.length; i++) {
			//System.out.println(tok[i]);
			if (stop.stopwords.contains(tok[i] + " ") || tok[i].charAt(0)<='Z')
				continue;
			//System.out.println(tok[i]);
			buf += tok[i]+" ";
		}
		//text = buf.trim().replace("\t", " ");
		//text = lem.lemmatize(text);
		return text;
	}

	public int[] Get_MinMax(){
		int[] arr=new int[2];
		int min_ts=Integer.MAX_VALUE;
		int max_ts=Integer.MIN_VALUE;
		
		Set<String> user_set = userReviews.keySet();
		Iterator<String> user_iterator = user_set.iterator();
		while(user_iterator.hasNext())
		{
			String user=user_iterator.next();
			System.out.println("Reading:"+user);
			TreeSet<Review> reviews = userReviews.get(user);
			for(Review review:reviews){
				long ts=review.time;
				Date date=new Date(ts*1000);
				int monthyear = (date.getYear()-70)*12+date.getMonth();
				
				if(monthyear<min_ts)
					min_ts=monthyear;
				if(monthyear>max_ts)
					max_ts=monthyear;
			}
		}
		arr[0]=min_ts;
		arr[1]=max_ts;
		
		return arr;
	}
	
	public void WriteToAFile(String user_map_file,String beer_map_file, 
			String user_details_file, String out_file) throws IOException{
		FileWriter fwd = new FileWriter(out_file);
		FileWriter fwd_beer = new FileWriter(beer_map_file);
		FileWriter fwd_user = new FileWriter(user_map_file);
		FileWriter fwd_user_details = new FileWriter(user_details_file);

		HashMap<String, Integer> user_map = new HashMap<String,Integer>();
		HashMap<String, Integer> beer_map = new HashMap<String,Integer>();
		
		int ctr_users=0;
		int ctr_products=0;
		
		int[] ts_minmax=Get_MinMax();
		int min_ts=ts_minmax[0];
		int max_ts=ts_minmax[1];
		
		Set<String> user_set = userReviews.keySet();
		Iterator<String> user_iterator = user_set.iterator();
		while(user_iterator.hasNext())
		{
			String user=user_iterator.next();
			System.out.println("Reading:"+user);
			TreeSet<Review> reviews = userReviews.get(user);
			for(Review review:reviews){

				String user_name=review.author;
				String beer_name=review.item;
				double rating = review.rating;

				long ts=review.time;
				Date date=new Date(ts*1000);
				int monthyear = (date.getYear()-70)*12+date.getMonth();

				int mod_user_id=-1;
				int mod_prod_id=-1;
				int mod_ts=-1;

				if(user_name!="ZEROCHAR_UNNAMED"){
					if(user_map.containsKey(user_name)){
						mod_user_id=user_map.get(user_name);
					}
					else{
						fwd_user.write(user_name+"\t"+ctr_users+"\n");
						user_map.put(user_name, ctr_users);
						mod_user_id=ctr_users;
						ctr_users+=1;
					}


					if(beer_map.containsKey(beer_name)){
						mod_prod_id=beer_map.get(beer_name);
					}
					else{
						fwd_beer.write(beer_name+"\t"+ctr_products+"\n");
						beer_map.put(beer_name,ctr_products);
						mod_prod_id=ctr_products;
						ctr_products+=1;
					}

					mod_ts=monthyear-min_ts;
					
					if(mod_user_id!=-1 && mod_prod_id!=-1){
						fwd.write(mod_user_id+","+mod_prod_id+","+mod_ts+","+rating+"\n");
						fwd_user_details.write(mod_user_id+"\t");
						fwd_user_details.write(review.aspectRatings.get("review/taste")+"\t");
						fwd_user_details.write(review.aspectRatings.get("review/aroma")+"\t");
						fwd_user_details.write(review.aspectRatings.get("review/appearance")+"\t");
						fwd_user_details.write(review.aspectRatings.get("review/palate")+"\t");
						fwd_user_details.write(review.text+"\n");
					}
				}
			}
		}
		fwd_user.close();
		fwd_beer.close();
		fwd_user_details.close();
		fwd.close();
	}

	public static void Count_Data(String in_path) throws IOException{
		HashSet<Integer> users=new HashSet<Integer>();
		HashSet<Integer> movies=new HashSet<Integer>();
		HashSet<Integer> ts=new HashSet<Integer>();

		BufferedReader brd=new BufferedReader(new FileReader(in_path));
		String strLine="";

		int max_uid=Integer.MIN_VALUE;
		int max_pid=Integer.MIN_VALUE;
		int max_tsid=Integer.MIN_VALUE;

		while((strLine=brd.readLine())!=null){
			String[] split=strLine.split(",");
			int user_id=Integer.parseInt(split[0]);
			int movie_id=Integer.parseInt(split[1]);
			int timestamp=Integer.parseInt(split[2]);
			double rating=Double.parseDouble(split[3]);

			users.add(user_id);
			movies.add(movie_id);
			ts.add(timestamp);

			if(user_id>max_uid)
				max_uid=user_id;
			if(movie_id>max_pid)
				max_pid=movie_id;
			if(timestamp>max_tsid)
				max_tsid=timestamp;
		}

		System.out.println("#Distinct Users="+users.size());
		System.out.println("#Distinct Movies="+movies.size());
		System.out.println("#Distinct TS="+ts.size());

		System.out.println(max_uid);
		System.out.println(max_pid);
		System.out.println(max_tsid);
		
		System.out.println(ts);
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

	public static void main(String[] args) throws Exception{
		String in_file="./data/beer_advocate/Beeradvocate.txt";
		String out_reviews_file="./data/beer_advocate/Beeradvocate_filtered.txt";
		String umap_file="./data/beer_advocate/Beeradvocate_Users.txt";
		String pmap_file="./data/beer_advocate/Beeradvocate_Products.txt";
		String udetails_file="./data/beer_advocate/Beeradvocate_UserDetails.txt";
		Preprocess_BeerAdvocate pba = new Preprocess_BeerAdvocate();
		pba.LoadBeerData(in_file);
		pba.WriteToAFile(umap_file, pmap_file, udetails_file, out_reviews_file);

		Preprocess_BeerAdvocate.Count_Data(out_reviews_file);


		String train_file="./data/beer_advocate/ratings.train";
		String test_file="./data/beer_advocate/ratings.test";
		Preprocess_BeerAdvocate.Split_Train_Test(out_reviews_file, train_file, test_file);
	}
}
