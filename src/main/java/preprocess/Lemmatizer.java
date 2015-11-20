package preprocess;
import java.util.ArrayList;
import java.util.List;

import dragon.nlp.tool.lemmatiser.EngLemmatiser;

class Lemmatizer extends EngLemmatiser {

	public Lemmatizer(String lemmatizerDictionaryFile) {
		super(lemmatizerDictionaryFile, false, true);
	}

	public final List<String> lemmatizeTerms(List<String> terms) {
		List<String> lemmatizedTerms = new ArrayList<String>(terms.size());
		for (String term : terms) {
			lemmatizedTerms.add(lemmatize(term));
		}
		return lemmatizedTerms;
	}

	public List<String> lemmatizeWords(List<String> terms) {
		List<String> lemmatizedTerms = new ArrayList<String>(terms.size());
		for (String term : terms) {
			lemmatizedTerms.add(lemmatizeWords(term));
		}
		return lemmatizedTerms;
	}

	public String lemmatizeWords(String text) {
		if (!text.contains(" ")) {
			return lemmatize(text);
		}
		String[] tokens = text.split("\\s+");
		StringBuilder retText = new StringBuilder();
		for (String token : tokens) {
			retText.append(" ").append(lemmatize(token));
		}
		return retText.substring(1);
	}
	
	public static void main (String []args) {
		
		//Lemmatizer lem = new Lemmatizer(ApplicationProperty.projectDataPath+"/lemmatiser/");
		//System.out.println(lem.lemmatize("relaxed"));
	}
}