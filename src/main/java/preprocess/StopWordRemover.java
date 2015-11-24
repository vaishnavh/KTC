package preprocess;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * Removes the stop word passed as a token
 * @author hemank lamba
 */
public class StopWordRemover {

    public String stopwords;

    public StopWordRemover() {
        this.stopwords = new String();
        populateStopWords("./data/stopwords.txt");
    }

    /**
     *@param word the given token
     *@return if the word is a stopword, an empty string will be returned; or else the word itself will be returned
     */
    public String removeIfStopWord(String word) {
        if (this.stopwords.contains(word + " ")) {
            return "";
        } else {
            return word;
        }


    }

    /**
     *@param the given sentence
     *@return the sentence with all stopwords removed
     */
    public String removeAllStopWords(String text) {
    	
    	String tok[] = text.split(" ");
    	String buf="";
    	for (int i=0; i<tok.length; i++) {
    		if (this.stopwords.contains(tok[i] + " "))
    			continue;
    		buf += tok[i]+" ";
    	}
    	return buf.trim();
    }
    	
    /**
     *
     * Populate the stop words
     * @param sourceFile file containing stop words
     */
    public void populateStopWords(String sourceFile) {
        try {

            //System.out.println("POPULATING STOP WORD REMOVER");
            BufferedReader in = new BufferedReader(new FileReader(sourceFile));
            String line = "";
            while ((line = in.readLine()) != null) {
                this.stopwords += line + " ";


            }
            in.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
}
