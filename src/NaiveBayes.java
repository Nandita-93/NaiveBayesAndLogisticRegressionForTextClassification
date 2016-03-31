import java.io.*;
import java.util.*;


	
public class NaiveBayes {
	HashMap<String,Integer> vocabulary = new HashMap<>(); 
	HashMap<String,HashMap<String,Integer>> countWithClass = new HashMap<>();
	double wordCt=0,vocCt=0;;
	HashMap<String,properties> wordsList=new HashMap<>();
	double spamCt=0,hamCt=0;
	double probHam=0;
	double probSpam=0;

	HashSet<String> stopWords = new HashSet<String>();
	public void addStopWords(String inputFile){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line= "";
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("\\s+");
				for (String word : words) {
					if(!stopWords.contains(word)){
						stopWords.add(word);
					}
				}
				
			}
		
	 }catch(Exception e){
			e.printStackTrace();
	 }
 }

	public double getCount(String category){
		if(category.equals("spam")){
			return spamCt;
		}
		if(category.equals("total")){
			return wordCt;
		}
		if(category.equals("ham")){
			return hamCt;
		}
		return 0;
	}
	
	public double getProb(String category){
		if(category.equals("spam")){
			return probSpam;
		}
		if(category.equals("ham")){
			return probHam;
		}
		return 0;
	}
	
	public void createVocabulary(String inputPath) {
		try{
			File[] folderList = new File(inputPath).listFiles();
			String classifier="";
			for (File folder : folderList) {
				File[] fileList = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				classifier=folder.getName();
				//System.out.println("classifier -- "+classifier+" Count of Files: "+fileList.length);
				int totalClassifierWords=0;
				for (File inputFile : fileList) {
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					String line= "";
					while ((line = reader.readLine()) != null) {
						line=line.toLowerCase();
						String[] words = line.split("\\s+");
						
						for (String word : words) {
							/*if(stopWords.contains(word)){
								continue;
							}*/
							totalClassifierWords++;
							if(vocabulary.containsKey(word)){
								vocabulary.put(word, vocabulary.get(word)+1);
							}
							else{
								vocabulary.put(word, 1);
							}
							if(classCount.containsKey(word)){
								classCount.put(word, classCount.get(word)+1);
							}else{
								classCount.put(word, 1);
							}
						}
						
						
					}
				}
				if(classifier.equals("spam")){
					spamCt=totalClassifierWords;
					countWithClass.put("spam", classCount);
				}
				else{
					hamCt=totalClassifierWords;
					countWithClass.put("ham", classCount);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void findProbabilityOfWords(){
		try{
			wordsList=new HashMap<>();
			for (String word : vocabulary.keySet()) {
				properties wordObject = new properties(word);
				if(countWithClass.get("spam").containsKey(word)){
					wordObject.spamCount=countWithClass.get("spam").get(word);
					vocCt+=wordObject.spamCount;
				}
				else{
					wordObject.spamCount=0;
				}
				
				if(countWithClass.get("ham").containsKey(word)){
					wordObject.hamCount=countWithClass.get("ham").get(word);
					vocCt+=wordObject.hamCount;
				}
				else{
					wordObject.hamCount=0;
				}
				wordObject.spamProb=(wordObject.spamCount+1)/(spamCt+vocabulary.size());
				wordObject.hamProb=(wordObject.hamCount+1)/(hamCt+vocabulary.size());
				wordsList.put(word, wordObject);
				//System.out.println(wordObject);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void validateFiles(String TestPath){
		try{
			File[] folderList = new File(TestPath).listFiles();
			String classifier="";
			for (File folder : folderList) {
				File[] fileList = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				classifier=folder.getName();
				double filecount=0,successCount=0;
				for (File inputFile : fileList) {
					String line;
					String predictedClassifier="";
					filecount++;
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					double hamProb=0,spamProb=0;
					double nologhamProb=1,nologspamProb=1;
					while ((line = reader.readLine()) != null) {
						line=line.toLowerCase();
						String[] words = line.split("\\s+");
						for (String word : words) {
							if(wordsList.containsKey(word)){
								hamProb+=Math.log10(wordsList.get(word).hamProb);
								spamProb+=Math.log10(wordsList.get(word).spamProb);
								//System.out.print(wordsList.get(word).hamProbability+" -- ");
							/*	nologhamProb*=wordsList.get(word).hamProbability;
								nologspamProb*=wordsList.get(word).spamProbability;*/
							}
						}
					}
					/*System.out.print(" "+totalHamProbabilty);
					System.out.println();*/
					hamProb+=Math.log10(probHam);
					spamProb+=Math.log10(probSpam);
					
					
/*					nologhamProb*=totalHamProbabilty;
					nologspamProb*=totalSpamProbability;*/
					
					/*System.err.println("Ham proba "+nologhamProb+" \n Spam proba"+nologspamProb);
					System.err.println("Ham proba1 "+hamProb+" \n Spam proba1"+spamProb);
					*/if(hamProb>spamProb){
						predictedClassifier="ham";
					}else{
						predictedClassifier="spam";
					}
					
					if(predictedClassifier.equals(classifier)){
						successCount++;
					}
				}
				System.out.println("Probability of "+classifier+":"+(successCount/filecount));
				System.out.println("Number of "+classifier+" files:"+(successCount));	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void printStatus(String methodName,String status){
	//	System.out.println(""+methodName+":"+status+"");
	}
	
	public static void main(String[] args) {
		NB driver = new NB();	
		String train = "", test = "";
		if(args.length<2)
		System.out.println("No enough arguments to run the program");
		else{
		training=args[0];
		test=args[1];
		}
		driver.addStopWords("C:/eclipse/ML Assign 2/src/stopwords.txt");
		driver.createVocabulary(train);
		driver.wordCt=driver.hamCt+driver.spamCt;
		System.out.println("Count of Ham Words   :"+(driver.spamCt));
		System.out.println("Count of Spam Words  :"+(driver.hamCt));
		System.out.println("Count of Total Words :"+(driver.wordCt));
		System.out.println("Total Vocabulary Size  :"+(driver.vocabulary.size()));
		driver.probHam=(3.0/4.0);
		driver.probSpam=(1.0/4.0);
		System.out.println("Ham Probability  "+driver.probHam);
		System.out.println("Spam Probability  "+driver.probSpam);
		driver.findProbabilityOfWords();
		System.err.println("Ham Probability  "+driver.probHam);
		System.err.println("Spam Probability  "+driver.probSpam);
		driver.validateFiles(test);;
		//printStatus("validateFiles","ended");	
	}
}

