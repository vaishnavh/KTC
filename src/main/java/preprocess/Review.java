package preprocess;

import java.util.HashMap;

public class Review implements Comparable<Review>{

	public long time;
	public double rating;
	public String author;
	public String item;
	public String style;
	public double overallRating;
	HashMap<String, Double> aspectRatings=new HashMap<String,Double>();
	public String text;
	//HashMap<String, String> metaData;
	
	public int compareTo(Review o) {

		return (int) (time-o.time);
	}
}

