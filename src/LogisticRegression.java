import java.io.*;
import java.util.*;


public class LogReg {
	HashMap<String,Integer> vocabulary = new HashMap<>(); 
	HashMap<String,Double> weights=new HashMap<>();
	
	double rate=0.001;
	double lambda=0.3;
	
	HashMap<String,HashMap<String,Integer>> countWithClass = new HashMap<>();
	HashMap<String, properties> wordsList=new HashMap<>();
	double spamCt=0,hamCt=0,wordCt=0,vWordCt=0;
	double probHam=0;
	double probSpam=0;
	HashSet<String> stopWords = new HashSet<String>();
	public void createVocabulary(String inputPath) 
	{
		try{
			File[] folderList = new File(inputPath).listFiles();
			String classifier="";
			for (File folder : folderList) {
				File[] fileList = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				classifier=folder.getName();
				System.out.println(classifier);
				//System.out.println("classifier -- "+classifier+" Count of Files: "+fileList.length);
				int totalClassifierWords=0;
				for (File inputFile : fileList) {
					BufferedReader reader = new BufferedReader(new FileReader(inputFile));
					String line= "";
					while ((line = reader.readLine()) != null) {
						line=line.toLowerCase();
						String[] words = line.split("\\s+");
						
						for (String word : words) {
							totalClassifierWords++;
							if(!vocabulary.containsKey(word)){
								vocabulary.put(word, 0);
							}
						}
					}
				}
				for(String word: vocabulary.keySet()){
					weights.put(word, 0.0);
				}
				
			}
		}catch(Exception e){
			System.out.println("Error occured in Logistic Regression\n ");
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Integer> countWords(File inputFile){
		HashMap<String, Integer> count = new HashMap<>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line ="";
			while((line=br.readLine())!=null){
				line=line.toLowerCase();
				String[] words = line.split("\\s+");
				for (String word : words) {
					/*if(stopWords.contains(word)){
						continue;
					}*/
					if(count.containsKey(word)){
						count.put(word, vocabulary.get(word)+1);
					}
					else{
						count.put(word, 1);
					}
				
				}
			}
		}
		catch(Exception e){
			System.out.println("Exception occured");
		}
		return count;
	}
	
	public double sigmoid(double x){
		return 1.0/(1.0+Math.exp(-x));
	}
	
	public void processDocument(File inputFile, double classifier){
		HashMap<String, Integer> wordCount = countWords(inputFile);
		double totalVal=0.0;
		for(String word: wordCount.keySet()){
			totalVal+=wordCount.get(word)*vocabulary.get(word);
		}
		double predictedVal = sigmoid(totalVal);
		for(String word: wordCount.keySet()){
			double weight=rate*(classifier-predictedVal)*wordCount.get(word);
			weights.put(word,weights.get(word)+weight);
		}
	}
	
	public void findClass(File inputFile){
		double totalVal=0.0;
		HashMap<String, Integer> tCount = new HashMap<>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line ="";
			while((line=br.readLine())!=null){
				line=line.toLowerCase();
				String[] words = line.split("\\s+");
				for (String word : words) {
					/*if(stopWords.contains(word)){
						continue;
					}*/
					if(tCount.containsKey(word)){
						tCount.put(word, tCount.get(word)+1);
					}
					else{
						tCount.put(word, 1);
					}
				
				}
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(String word: tCount.keySet()){
			if(vocabulary.get(word)!=null)
			totalVal+=tCount.get(word)*vocabulary.get(word);
		}
		double result = sigmoid(totalVal);
		if(result < 0.5)
			System.out.println(" Ham");
		else
			System.out.println(" Spam");
	}
	
	public void test(String inputPath){
		try{
			File[] folderList = new File(inputPath).listFiles();
			
			for (File folder : folderList) {
				File[] fileList = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				
				//System.out.println("classifier -- "+" Count of Files: "+fileList.length);
				
				for(File input: fileList){
					findClass(input);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void train(String inputPath){
		try{
			File[] folderList = new File(inputPath).listFiles();
			String classifier="";
			double cla;
			for (File folder : folderList) {
				File[] fileList = folder.listFiles();
				HashMap<String,Integer> classCount = new HashMap<>();
				classifier=folder.getName();
				if(classifier=="spam"){
					cla=0.0;
				}
				else{
					cla=1.0;
				}
				//System.out.println("classifier -- "+" Count of Files: "+fileList.length);
				int totalClassifierWords=0;
				for(File input: fileList){
					processDocument(input, cla);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void modify(){
		for(String word: vocabulary.keySet()){
			double actualWeight=vocabulary.get(word);
			double pre = weights.get(word);
			double modified=actualWeight+pre-(rate*lambda*actualWeight);
			weights.put(word, modified+weights.get(word));
		}	
	}
	
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
			 System.out.println("Error occured in NaiveBayes: addStopWords method \n ");
				e.printStackTrace();
		 }
	 }

	public static void main(String[] args) {
		LogReg driver = new LogReg();
		public static void main(String[] args) {
			String train = "", test = "";
			if(args.length<2)
			System.out.println("No enough arguments to run the program");
			else{
			training=args[0];
			test=args[1];
			}
		driver.createVocabulary(train);
		
		for(int i=0;i<100;i++){
			driver.train(test);
			driver.modify();
		}
		
		driver.test(test);
		
	}
}

