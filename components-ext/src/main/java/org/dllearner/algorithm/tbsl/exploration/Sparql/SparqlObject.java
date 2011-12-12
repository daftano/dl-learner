package org.dllearner.algorithm.tbsl.exploration.Sparql;
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;

import org.dllearner.algorithm.tbsl.exploration.sax.ParseXmlHtml;
import org.dllearner.algorithm.tbsl.nlp.WordNet;
import org.dllearner.algorithm.tbsl.sparql.BasicQueryTemplate;
import org.dllearner.algorithm.tbsl.sparql.Path;
import org.dllearner.algorithm.tbsl.sparql.SPARQL_Filter;
import org.dllearner.algorithm.tbsl.sparql.SPARQL_Term;
import org.dllearner.algorithm.tbsl.sparql.Slot;
import org.dllearner.algorithm.tbsl.sparql.Template;
import org.dllearner.algorithm.tbsl.templator.BasicTemplator;
import org.dllearner.algorithm.tbsl.templator.Templator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class SparqlObject {
	//global Variable dict
	
	//start counting with 0
	static int explorationdepthwordnet=2;
	static int iterationdepth =0;
	static int numberofanswers=1;
	static double LevenstheinMin = 0.8;
	static WordNet wordnet;
	BasicTemplator btemplator;
	Templator templator;
	private static mySQLDictionary myindex; 
	
	//Konstruktor
	public SparqlObject() throws MalformedURLException, ClassNotFoundException, SQLException{
		wordnet = new WordNet();
		System.out.println("Loading SPARQL Templator");
		//
    	btemplator = new BasicTemplator();
    	//wenn ich das nehme, dann gebe ich dem parser den ideal....
    	//btemplator.UNTAGGED_INPUT = false;
    	templator = new Templator();
    	System.out.println("Loading SPARQL Templator Done\n");
    	System.out.println("Start Indexing");
    	myindex = new mySQLDictionary();
    	
    	System.out.println("Done:Indexing");
    	
    	//normaly 1
    	setExplorationdepthwordnet(1);
    	//eigentlich immer mit 0 initialisieren
    	setIterationdepth(1);
    	setNumberofanswers(1);
	}
	
	/*
	 * #####################################
	 * Getter and Setter Methods
	 */
	
	public int getExplorationdepthwordnet() {
		return explorationdepthwordnet;
	}


	public void setExplorationdepthwordnet(int explorationdepthwordnet) {
		SparqlObject.explorationdepthwordnet = explorationdepthwordnet;
	}


	public int getIterationdepth() {
		return iterationdepth;
	}


	public void setIterationdepth(int iterationdepth) {
		SparqlObject.iterationdepth = iterationdepth;
	}


	public int getNumberofanswers() {
		return numberofanswers;
	}


	public void setNumberofanswers(int numberofanswers) {
		SparqlObject.numberofanswers = numberofanswers;
	}
	

	/*
	 * ##############################
	 *
	 */
	/*
	 * "Main" Method of this Class.
	 * 
	 */
	 public void create_Sparql_query(String question) throws JWNLException, IOException, SQLException{
		 	//create_Sparql_query_new(string);
			
		 ArrayList<ArrayList<String>> lstquery = new ArrayList<ArrayList<String>>();
		long startParsingTime = System.currentTimeMillis();
		lstquery=getQuery(question);
		long endParsingTime = System.currentTimeMillis();
		System.out.println("The Questionparsing took "+ (endParsingTime-startParsingTime)+ " ms");
		ArrayList<String> final_answer = new ArrayList<String>();
		
		if(lstquery.isEmpty()){
			saveNotParsedQuestions(question);
		}

			for(ArrayList<String> querylist : lstquery){
				
				boolean startIterating=true;
				String query="";
				if(querylist.get(0).contains("ERROR"))startIterating=false;
				else query=querylist.get(0).toString();
				
				//TODO: Somewhere is an error, because sometimes there is an double _  a __ and thats not allowed.
				//fixing it now with an replace of "__" to ""
				query=query.replace("__", "");
				
				if(getIterationdepth()==-1&&startIterating==true){
				    String tmp = new String();
				    String s = null;
				    BufferedReader in = null;
	
				    // Liest Textzeilen aus der Datei in einen Vector:
				    try {
				      in = new BufferedReader(
				                          new InputStreamReader(
				                          new FileInputStream( "/tmp/testresult.txt" ) ) );
				      while( null != (s = in.readLine()) ) {
				        tmp+="\n"+s;
				      }
				    } catch( FileNotFoundException ex ) {
				    } catch( Exception ex ) {
				      System.out.println( ex );
				    } finally {
				      if( in != null )
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    
				    String out=null;
				    if (query=="" || query==" "||query.length()==0) query="Could not parse";
				    out=tmp + "\n" + question + ":\n"+query+"\n";
				    
				    BufferedWriter outfile = new BufferedWriter(
	                          new OutputStreamWriter(
	                          new FileOutputStream( "/tmp/testresult.txt" ) ) );
	    
				    outfile.write(out);
				    outfile.close();
					
				}
		
				/*
				 * #################################################################################################
				 */				
				//Iteration 0
				if(getIterationdepth()==0&&startIterating==true){
				    String tmp = new String();
				    String s = null;
				    BufferedReader in = null;
	
				    // Lies Textzeilen aus der Datei in einen Vector:
				    try {
				      in = new BufferedReader(
				                          new InputStreamReader(
				                          new FileInputStream( "/tmp/answer.txt" ) ) );
				      while( null != (s = in.readLine()) ) {
				        tmp+="\n"+s;
				      }
				    } catch( FileNotFoundException ex ) {
				    } catch( Exception ex ) {
				      System.out.println( ex );
				    } finally {
				      if( in != null )
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }					
					String answer;
					answer=sendServerQuestionRequest(query);
					final_answer.add("Begin:\n"+query +"\n"+answer+" \n End");
					
				}
				/*
				 * #################################################################################################
				 */				
				//Iterration 1
				/*
				 * Only Levensthein!!!
				 */
				if(getIterationdepth()==1&&startIterating==true){
				/*	
					//4, because of query + three conditions for the simple case
					if(querylist.size()==4)final_answer=simpleIteration1Case(querylist, query);
					
					//if we have more conditions, we need to change the way of replacing the uris got from wordnet etc
					if(querylist.size()>4)final_answer=complexeIteration1Case(querylist, query);
					*/
					
					
					if(querylist.size()==4)final_answer=simpleLevinstheinIteration(querylist, query);
					if(querylist.size()>4)final_answer=complexeLevinstheinIteration(querylist, query);
					
				}
				/*
				 * #################################################################################################
				 */				
				//Iterration 2
				/*
				 * Only Wordnet!!!
				 */
				if(getIterationdepth()==2&&startIterating==true){
					if(querylist.size()==4)final_answer=simpleWordnetIteration(querylist, query);
					if(querylist.size()>4)final_answer=complexWordnetIteration(querylist, query);
				}
				
				
			}
			
			BufferedReader in = null;
			
		    String tmp="";
			// Lies Textzeilen aus der Datei in einen Vector:
		    try {
		      in = new BufferedReader(
		                          new InputStreamReader(
		                          new FileInputStream( "/tmp/answer" ) ) );
		      String s;
			while( null != (s = in.readLine()) ) {
		        tmp+="\n"+s;
		      }
		    } catch( FileNotFoundException ex ) {
		    } catch( Exception ex ) {
		      System.out.println( ex );
		    } finally {
		      if( in != null )
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }	
		    
		    String out="";
			for(String answer : final_answer){
				//only answered question
				//if(!answer.contains("Error in searching Wordnet with word") && !answer.contains("EmtyAnswer")&& !answer.contains("Error in getting Properties"))out=out+ "\n"+answer+"\n";
			    
				/*
				//only questions with wordnet error
				if(answer.contains("Error in searching Wordnet with word"))out=out+ "\n"+answer+"\n";
				
				//only questions with emty answers
				if(answer.contains("EmtyAnswer"))out=out+ "\n"+answer+"\n";
*/				
				//only questions with Error in Properties
			//	if(answer.contains("Error in getting Properties"))out=out+ "\n"+answer+"\n";

				out+= "\n"+answer+"\n";

			}
		    System.out.println(question);
		    System.out.println(out);
		    BufferedWriter outfile = new BufferedWriter(
                      new OutputStreamWriter(
                      new FileOutputStream( "/tmp/answer" ) ) );

		    outfile.write(tmp+"\n"+question+" :\n"+out);
		    outfile.close();
		}

	 
	 private ArrayList<String> simpleLevinstheinIteration(ArrayList<String> querylist, String query) throws SQLException,
		JWNLException {

		ArrayList<String> final_answer=new ArrayList<String>();
		String resource="";
		String property_to_compare_with="";
		String sideOfProperty="LEFT";

		
		int tmpcounter=0;
		for(String s : querylist){
			//we dont need the first one, because thats the query itself
			tmpcounter=tmpcounter+1;
			if(tmpcounter>=1){
				if(s.contains("LEFT")){
					sideOfProperty="LEFT";
					resource=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfProperty="RIGHT";
					resource=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_with=s.replace("PROPERTY","");
				}
				
			}
		}
		System.out.println("Property to compare:: "+ property_to_compare_with);
		System.out.println("Resource: "+ resource);
		
		
		 HashMap<String,String> properties = new HashMap<String, String>();
		 GetRessourcePropertys property = new GetRessourcePropertys();
		 Boolean goOnAfterProperty = true;
		 
		 System.out.println("URI from Resource "+ resource +": "+getUriFromIndex(resource.toLowerCase(),0));
		 
		 //gets Propertys left or right from the resource!
		 try {
			 properties=property.getPropertys(getUriFromIndex(resource.toLowerCase(),0),sideOfProperty);
			if (properties==null){
				
				final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
				goOnAfterProperty=false;
			}
			
			System.out.println("Properties from Resource "+resource+": "+properties);
			
		} catch (IOException e) {
			
			final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
			goOnAfterProperty=false;
			
		}
		if(goOnAfterProperty==true){
			 ArrayList<String> new_queries= new ArrayList<String>();
			 //iterate over properties
			 for (Entry<String, String> entry : properties.entrySet()) {
				 String key = entry.getKey();
				 String value = entry.getValue();
				 //compare property gotten from the resource with the property from the original query
				 double nld=Levenshtein.nld(property_to_compare_with.toLowerCase(), key);
				 
				 //check if nld is greater than Levensthein
			     if(nld>=LevenstheinMin){
					 //if its so, replace old uri with the new one
					 String querynew=query;
					 String replacement = getUriFromIndex(property_to_compare_with.toLowerCase(),1);
					 querynew=querynew.replace(replacement,value);
					 System.out.println("Simple Levensthein Query: "+ querynew);
					 new_queries.add(querynew);
				 }
			 }

				
			 	//iterate over all Queries and get answer from Server
				for(String anfrage : new_queries){
					String answer_tmp;
					answer_tmp=sendServerQuestionRequest(anfrage);
					System.out.println("Antwort vom Server: "+answer_tmp);
					final_answer.add("Begin:\n"+anfrage +"\n"+answer_tmp+" \n End");
				}
			 }
		
	
	return final_answer;
}
	 
	 private ArrayList<String> complexeLevinstheinIteration(ArrayList<String> querylist, String query) throws SQLException,
		JWNLException {

		ArrayList<String> final_answer=new ArrayList<String>();
		String resourceOne="";
		String property_to_compare_withOne="";
		String resourceTwo="";
		String property_to_compare_withTwo="";
		String sideOfPropertyOne="LEFT";
		String sideOfPropertyTwo="LEFT";

		
		int tmpcounter=0;
		for(String s : querylist){
			//we dont need the first one, because thats the query itself
			
			//for condition One
			tmpcounter=tmpcounter+1;
			if(tmpcounter>=1&&tmpcounter<=4){
				if(s.contains("LEFT")){
					sideOfPropertyOne="LEFT";
					resourceOne=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfPropertyOne="RIGHT";
					resourceOne=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_withOne=s.replace("PROPERTY","");
				}
				
			}
			
			//for condition Two
			if(tmpcounter>4){
				if(s.contains("LEFT")){
					sideOfPropertyTwo="LEFT";
					resourceTwo=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfPropertyTwo="RIGHT";
					resourceTwo=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_withTwo=s.replace("PROPERTY","");
				}
				
			}
		}
		 HashMap<String,String> propertiesOne = new HashMap<String, String>();
		 HashMap<String,String> propertiesTwo = new HashMap<String, String>();
		 GetRessourcePropertys property = new GetRessourcePropertys();
		 Boolean goOnAfterProperty = true;
		 
		 //Get Properties for Resource in condition One and Two from Server
		 try {

			 propertiesOne=property.getPropertys(getUriFromIndex(resourceOne.toLowerCase(),0),sideOfPropertyOne);
			 propertiesTwo=property.getPropertys(getUriFromIndex(resourceTwo.toLowerCase(),0),sideOfPropertyTwo);
			 
			if (propertiesOne==null){
				final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
				goOnAfterProperty=false;
			}
			
		} catch (IOException e) {
			
			final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
			goOnAfterProperty=false;
			
		}
		 
		 
		if(goOnAfterProperty==true){

			 ArrayList<String> new_queries= new ArrayList<String>();
			 
			 //Iterate over property from resource one
			 for (Entry<String, String> entryOne : propertiesOne.entrySet()) {
				 
				 String queryOne=query;
				 String keyOne = entryOne.getKey();
				 String valueOne = entryOne.getValue();
				 
				 
			     double levnstheinDistanzeOne=Levenshtein.nld(property_to_compare_withOne.toLowerCase(), keyOne);
				
			     /*if distance is higher or equals LevenstheinMin, replace old uri with new uri 
			      * and use that new query, for the property of the second resource
				 */
				 if(levnstheinDistanzeOne>=LevenstheinMin){
					 String test = getUriFromIndex(property_to_compare_withOne.toLowerCase(),1);
					 queryOne=queryOne.replace(test,valueOne);
				 }
			     
				 /*
				  * Iterate now over the second set of properties, but this time not using the original query in which
				  * to replace the old uri with the new one, but using queryOne from the first step. 
				  */
				 for (Entry<String, String> entryTwo : propertiesTwo.entrySet()) {
					    String keyTwo = entryTwo.getKey();
					    String valueTwo = entryTwo.getValue();
					
					    //again calculate the nld with the property from the second condition and the property from the propertyset
					    double levnstheinDistanzeTwo=Levenshtein.nld(property_to_compare_withTwo.toLowerCase(), keyTwo);
						 
						 if(levnstheinDistanzeTwo>LevenstheinMin){
							 String queryTwo=queryOne;
							 String replacement = getUriFromIndex(property_to_compare_withTwo.toLowerCase(),1);
							 queryTwo=queryTwo.replace(replacement,valueTwo);
							 System.out.println("Complex Levensthein Query: "+ queryTwo);
							 new_queries.add(queryTwo);
						 }
					     
				 }
			 }
			 
			 
			//iterate over all Queries and get answer from Server
				for(String anfrage : new_queries){
					String answer_tmp;
					answer_tmp=sendServerQuestionRequest(anfrage);
					System.out.println("Antwort vom Server: "+answer_tmp);
					final_answer.add("Begin:\n"+anfrage +"\n"+answer_tmp+" \n End");
				}
			 
		}
	
	return final_answer;
}
	 
	 private ArrayList<String> simpleWordnetIteration(ArrayList<String> querylist, String query) throws SQLException,
		JWNLException {
	ArrayList<String> final_answer=new ArrayList<String>();
	
	System.out.println("In simpleWordnetIteration");
	
		String resource="";
		String property_to_compare_with="";
		String sideOfProperty="LEFT";

		
		int tmpcounter=0;
		for(String s : querylist){
			//we dont need the first one, because thats the query itself
			tmpcounter=tmpcounter+1;
			if(tmpcounter>=1){
				if(s.contains("LEFT")){
					sideOfProperty="LEFT";
					resource=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfProperty="RIGHT";
					resource=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_with=s.replace("PROPERTY","");
				}
				
			}
		}
		System.out.println("Property to compare:: "+ property_to_compare_with);
		System.out.println("Resource: "+ resource);

		
		 HashMap<String,String> properties = new HashMap<String, String>();
		 GetRessourcePropertys property = new GetRessourcePropertys();
		 Boolean goOnAfterProperty = true;
		 try {
			 properties=property.getPropertys(getUriFromIndex(resource.toLowerCase(),0),sideOfProperty);
			if (properties==null){

				final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
				goOnAfterProperty=false;
			}
			
		} catch (IOException e) {
			
			final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
			goOnAfterProperty=false;
			
		}
		if(goOnAfterProperty==true){
			
			 ArrayList<String> new_queries= new ArrayList<String>();

			 System.out.println("Start Iterating Wordnet with "+property_to_compare_with+" and deept of "+explorationdepthwordnet);
			 ArrayList<String> semantics=new ArrayList<String>();
			 ArrayList<String> tmp_semantics=new ArrayList<String>();
			 ArrayList<String> result_SemanticsMatchProperties=new ArrayList<String>();
			 semantics.add(property_to_compare_with);
			 System.out.println("Semantics: "+ semantics);
			 
			 //first check, if there is a singular form in the wordnet dictionary.. eg children -> child
			 String _temp_=myindex.getWordnetHelp(property_to_compare_with);
			 if(_temp_==null){
				 tmp_semantics=semantics;
			 }
			 else{
				 semantics.clear();
				 semantics.add(_temp_);
				 tmp_semantics=semantics;
			 }
			 
			 System.out.println("tmp_semantics: "+ tmp_semantics);
			 Boolean goOnAfterWordnet = true;
			 
			 
			 System.out.println("##########################");
			 System.out.println("properties for "+getUriFromIndex(resource.toLowerCase(),0)+": "+properties);
			 System.out.println("Property to compare with: "+property_to_compare_with);
			 System.out.println("Semantics: "+semantics);
			 System.out.println("##########################");
			 for(int i=0;i<=explorationdepthwordnet;i++){

				 try {
					tmp_semantics=getSemantics(tmp_semantics);
					System.out.println("tmp_semantics in Iteration: "+ tmp_semantics);
					if (tmp_semantics==null){
						goOnAfterWordnet=false;
						final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semantics+" \n End");

					}
					else{
					//each word only one time
					 for(String k : tmp_semantics){
						 if(!semantics.contains(k)) semantics.add(k);
					 }
					}
					
				} catch (IOException e) {
					
					goOnAfterWordnet=false;
					final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semantics+" \n End");
					
				}
						 
			 }
			
			 if(goOnAfterWordnet==true){
				
				 for (Entry<String, String> entry : properties.entrySet()) {
					    String key = entry.getKey();
					    String value = entry.getValue();
					    
					for(String b : semantics){
						if(key.contains(b.toLowerCase())){
							System.out.println("Hey, Iam in too!!!!!!!!!!!");
							//to check, if no property is used twice...
							if(!result_SemanticsMatchProperties.contains(key)){
								//create new query
							 System.out.println("Hey, Iam in!!!!!!!!!!!");
							 result_SemanticsMatchProperties.add(key);
							 String query_tmp=query;
							 String test = getUriFromIndex(property_to_compare_with.toLowerCase(),1);
							 query_tmp=query_tmp.replace(test,value);
							 System.out.println("Simple Wordnet Query: "+ query_tmp);
							 new_queries.add(query_tmp);
							}
						}
					}
				}
				 
				//iterate over all Queries and get answer from Server
				for(String bla : new_queries){
					String answer_tmp;
					answer_tmp=sendServerQuestionRequest(bla);
					System.out.println("Antwort vom Server: "+answer_tmp);
					final_answer.add("Begin:\n"+bla +"\n"+answer_tmp+" \n End");
				}
			 }
		}
	
	
	return final_answer;
}
	 
	 
	 private ArrayList<String> complexWordnetIteration(ArrayList<String> querylist, String query) throws SQLException,
		JWNLException {
	ArrayList<String> final_answer=new ArrayList<String>();
	
		String resourceOne="";
		String property_to_compare_withOne="";
		String resourceTwo="";
		String property_to_compare_withTwo="";
		String sideOfPropertyOne="LEFT";
		String sideOfPropertyTwo="LEFT";

		
		int tmpcounter=0;
		for(String s : querylist){
			//we dont need the first one, because thats the query itself
			tmpcounter=tmpcounter+1;
			//get resource and property from the first condtion
			if(tmpcounter>=1&&tmpcounter<=4){
				if(s.contains("LEFT")){
					sideOfPropertyOne="LEFT";
					resourceOne=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfPropertyOne="RIGHT";
					resourceOne=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_withOne=s.replace("PROPERTY","");
				}
				
			}
			//get resource and property from the second condtion
			if(tmpcounter>4){
				if(s.contains("LEFT")){
					sideOfPropertyTwo="LEFT";
					resourceTwo=s.replace("LEFT","");
				}
				if(s.contains("RIGHT")){
					sideOfPropertyTwo="RIGHT";
					resourceTwo=s.replace("RIGHT","");
				}
				if(s.contains("PROPERTY")){
					property_to_compare_withTwo=s.replace("PROPERTY","");
				}
				
			}
		}
		System.out.println("Property to compare:: "+ property_to_compare_withOne);
		System.out.println("Resource: "+ resourceOne);
		
		 HashMap<String,String> propertiesOne = new HashMap<String, String>();
		 HashMap<String,String> propertiesTwo = new HashMap<String, String>();
		 GetRessourcePropertys property = new GetRessourcePropertys();
		 Boolean goOnAfterProperty = true;
		 
		 //gets the properties for both conditions
		 try {
			 propertiesOne=property.getPropertys(getUriFromIndex(resourceOne.toLowerCase(),0),sideOfPropertyOne);
			 propertiesTwo=property.getPropertys(getUriFromIndex(resourceTwo.toLowerCase(),0),sideOfPropertyTwo);
			if (propertiesOne==null){
				
				final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
				goOnAfterProperty=false;
			}

		} catch (IOException e) {

			final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
			goOnAfterProperty=false;
			
		}
		if(goOnAfterProperty==true){
			
			/*
			 * #################################### Semantics One#############################################
			 */
			
			 ArrayList<String> new_queries= new ArrayList<String>();

			 //System.out.println("Start Iterating Wordnet with "+property_to_compare_withOne+" and deept of "+explorationdepthwordnet);
			 ArrayList<String> semanticsOne=new ArrayList<String>();
			 ArrayList<String> tmp_semanticsOne=new ArrayList<String>();
			 ArrayList<String> result_SemanticsMatchPropertiesOne=new ArrayList<String>();
			 semanticsOne.add(property_to_compare_withOne);
			 
			 //first check, if there is a singular form in the wordnet dictionary.. eg children -> child
			 String _temp_One=myindex.getWordnetHelp(property_to_compare_withOne);
			 if(_temp_One==null){
				 tmp_semanticsOne=semanticsOne;
			 }
			 else{
				 semanticsOne.clear();
				 semanticsOne.add(_temp_One);
				 tmp_semanticsOne=semanticsOne;
			 }
			 
			 //get the "semantics" from wordnet. Iterate as long as the explorationdepthwordnet is reached
			 Boolean goOnAfterWordnet = true;
			 for(int i=0;i<=explorationdepthwordnet;i++){

				 try {
					tmp_semanticsOne=getSemantics(tmp_semanticsOne);
					if (tmp_semanticsOne==null){
						goOnAfterWordnet=false;
						final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semanticsOne+" \n End");

					}
					else{
					//each word only one time
					 for(String k : tmp_semanticsOne){
						 if(!semanticsOne.contains(k)) semanticsOne.add(k);
					 }
					}
					
				} catch (IOException e) {

					goOnAfterWordnet=false;
					final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semanticsOne+" \n End");
					
				}
				 
						 
			 }
			 /*
				 * #################################### Semantics Two#############################################
			*/

			 System.out.println("Start Iterating Wordnet with "+property_to_compare_withOne+" and deept of "+explorationdepthwordnet);
			 ArrayList<String> semanticsTwo=new ArrayList<String>();
			 ArrayList<String> tmp_semanticsTwo=new ArrayList<String>();
			 ArrayList<String> result_SemanticsMatchPropertiesTwo=new ArrayList<String>();
			 semanticsTwo.add(property_to_compare_withTwo);
			 
			 //first check, if there is a singular form in the wordnet dictionary.. eg children -> child
			 String _temp_Two=myindex.getWordnetHelp(property_to_compare_withTwo);
			 if(_temp_Two==null){
				 tmp_semanticsOne=semanticsTwo;
			 }
			 else{
				 semanticsTwo.clear();
				 semanticsTwo.add(_temp_Two);
				 tmp_semanticsTwo=semanticsTwo;
			 }
			 
			//get the "semantics" from wordnet. Iterate as long as the explorationdepthwordnet is reached
			 for(int i=0;i<=explorationdepthwordnet;i++){

				 try {
					tmp_semanticsTwo=getSemantics(tmp_semanticsTwo);
					if (tmp_semanticsTwo==null){
						goOnAfterWordnet=false;
						final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semanticsTwo+" \n End");

					}
					else{
					//each word only one time
					 for(String k : tmp_semanticsTwo){
						 if(!semanticsTwo.contains(k)) semanticsTwo.add(k);
					 }
					}
					
				} catch (IOException e) {
					
					goOnAfterWordnet=false;
					final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semanticsTwo+" \n End");
					
				}
				 
						 
			 }
			 
			
			 if(goOnAfterWordnet==true){
				
				 
				 //start iterating over the propery sets
				 for (Entry<String, String> entryOne : propertiesOne.entrySet()) {
					 String keyOne = entryOne.getKey();
					 String valueOne = entryOne.getValue();
					 String queryOne=query;
					 
					 for(String b : semanticsOne){
							if(keyOne.contains(b.toLowerCase())){
								if(!result_SemanticsMatchPropertiesOne.contains(keyOne)){
									//create new query
								result_SemanticsMatchPropertiesOne.add(keyOne);
								 String replacementOne = getUriFromIndex(property_to_compare_withOne.toLowerCase(),1);
								 queryOne=queryOne.replace(replacementOne,valueOne);
								 
								 for (Entry<String, String> entryTwo : propertiesTwo.entrySet()) {
									    String keyTwo = entryTwo.getKey();
									    String valueTwo = entryTwo.getValue();
									    
									for(String z : semanticsTwo){
										if(keyTwo.contains(z.toLowerCase())){
											if(!result_SemanticsMatchPropertiesTwo.contains(keyTwo)){
												//create new query
											result_SemanticsMatchPropertiesTwo.add(keyTwo);
											 String queryTwo=queryOne;
											 String replacementTwo = getUriFromIndex(property_to_compare_withTwo.toLowerCase(),1);
											 queryTwo=queryTwo.replace(replacementTwo,valueTwo);
											 System.out.println("Complexe Wordnet Query: "+ queryTwo);
											 new_queries.add(queryTwo);
											}
										}
									}
								}
								 
								}
							}
						}
					 
					 
					 
				 }
				 
				 
				 
				//iterate over all Queries and get answer from Server
				for(String bla : new_queries){
					String answer_tmp;
					answer_tmp=sendServerQuestionRequest(bla);
					System.out.println("Antwort vom Server: "+answer_tmp);
					final_answer.add("Begin:\n"+bla +"\n"+answer_tmp+" \n End");
				}
			 }
		}
	
	return final_answer;
}
	 
	 
	
	
	
	
	
	
	
	

	 /**
	  * Iterates thru the conditions and returns an array, where one can see, if the Property is left or right from the resource
	  * @param query
	  * @return returns an array, where one can see, if the Property is left or right from the resource
	  */
	 private static ArrayList<String> createLeftAndRightPropertyArray(String query){
		 query=query.replace("  ", " ");
		 	Pattern p = Pattern.compile (".*\\{(.*\\<http.*)\\}.*");
		    Matcher m = p.matcher (query);
		    ArrayList<String> lstquery = new ArrayList<String>();
		  	while (m.find()) {
		  		String tmp= m.group(1);
		  		tmp=tmp.replace("http://dbpedia.org/resource/","").replace("http://dbpedia.org/property/", "").replace("http://dbpedia.org/ontology/", "");
		  		
		  		//split on . for sign for end of conditions
		  		String[] firstArray=tmp.split("\\.");
		  		for(String i : firstArray){
		  			
		  			String[] secondArray=i.split(" ");
		  			//always in three counts
		  			int counter=0;
		  			for(String j : secondArray){
		  				counter=counter+1;
		  				//only one condition
		  				if(secondArray.length%3==0){
		  					if(counter==1&&j.contains("<")){
		  						//position of Property is right
		  						lstquery.add("RIGHT"+j.replace("<", "").replace(">",""));
		  					}
		  					else if(counter==3&&j.contains("<")){
		  						//position of Property is left
		  						lstquery.add("RIGHT"+j.replace("<", "").replace(">",""));
		  					}
		  					else if(counter==2){
		  						lstquery.add("PROPERTY"+j.replace("<", "").replace(">",""));
		  					}
		  					
		  					else if(j.contains("?")) lstquery.add("VARIABLE");
		  				}
		  				if(counter==0)counter=0;
		  				
		  				
		  			}
		  		}
		  	}
		  	
		  	return lstquery;
	 }
	 
	 /**
	* Method gets a String and takes the information from the templator to creat a Sparql query.
	* @param question question in natural language
	* @return ArrayList of Sparql queries.
	 * @throws SQLException 
	*/
	private ArrayList<ArrayList<String>> getQuery(String question) throws SQLException {
		ArrayList<ArrayList<String>> lstquery = new ArrayList<ArrayList<String>>();
	    Set<BasicQueryTemplate> querytemps = btemplator.buildBasicQueries(question);
	     	for (BasicQueryTemplate temp : querytemps) {
	     		
	     		ArrayList<String> lstquerynew = new ArrayList<String>();
	     		ArrayList<String> lstquerupsidedown = new ArrayList<String>();
	     		String query;
	     		String selTerms ="";
	     		
	     		boolean addQuery=true;
	     		//sometimes there isnt an Selectterm, so dont use this query
	     		try{
	     			for(SPARQL_Term terms :temp.getSelTerms()) selTerms=selTerms+(terms.toString())+" ";
	     		}
	     		catch (Exception e){
	     			selTerms="";
	     			addQuery=false;
	     		}
	     		
	     		
	     		String conditions = "";
	     		try{
	     			for(Path condition: temp.getConditions()) conditions=conditions+(condition.toString())+".";
	     		}
	     		catch (Exception e){
	     			conditions="";
	     			addQuery=false;
	     		}
	     		
	     		String filters="";
	     		try{
	     			for(SPARQL_Filter tmp : temp.getFilters()) filters=filters+tmp+" ";
	     		}
	     		catch(Exception e){
	     			filters="";
	     			addQuery=false;
	     		}
	     		if(addQuery==true){
		        	query="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+temp.getQt().toString()+" "+selTerms+" WHERE {"+  conditions.replace("--","") + filters+"}";
		        	
		        	String conditions_new = "";
		     		for(Path condition: temp.getConditions()){
			     		//make conditions up-side-down
			     		String[] tmp_upside = condition.toString().split(" -- ");
			     		String tmp_conditions_new="";
			     		for(String con : tmp_upside) tmp_conditions_new = con +" "+tmp_conditions_new;
			     		//remove all dots befor end
			     		tmp_conditions_new=tmp_conditions_new.replace(".", "");
			     		//at the end ein .
			     		tmp_conditions_new = tmp_conditions_new + ".";
			     		
			     		//conditions_new=tmp_conditions_new;
			     		
		     			conditions_new=conditions_new + tmp_conditions_new;
		     		}
		     		
	
		     		
		     		System.out.println("Conditions: " + conditions);
		     		System.out.println("Conditions_new: " + conditions_new);
		     		
		     		
		        	String query_upside_down = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+temp.getQt().toString()+" "+selTerms+" WHERE {"+  conditions_new.replace("--","") +filters+ "}";
		        	String[] slots= null;
		        	int slotcounter=1;
		    		for(Slot slot : temp.getSlots()){
		    			
		    			//see below
		    			slotcounter=slotcounter+1;
		    			
		    			
		    			String slotstring=slot.toString().replace("UNSPEC","").replace("RESOURCE","").replace("{","").replace("}","");
		    			slotstring=slotstring.replace("  ","");
		    			//System.out.println(tmp);
		    			//damit auch wirklich nur ?y und nicht ?y0 ersetzt wird, einfach nach "?y " suchen.
		    			String[] array = slotstring.split(":");
		    			String replace;
		    			if(array[0].length()<2)replace = "?"+array[0]+" ";
		    			else replace="?"+array[0];
	
		    			
		    			//TODO: Hotfix: get rid of " PROPERTY "
		    			String _ThingGettingURIfor_=array[1];
		    			_ThingGettingURIfor_=_ThingGettingURIfor_.replace(" PROPERTY ","").toLowerCase();
		    			String hm_result=getUriFromIndex(_ThingGettingURIfor_,0);
		    		      try
		    		      {
		    		    	  if(hm_result.contains("Category:")) hm_result=hm_result.replace("Category:","");
		    		        }
		    		      catch ( Exception e )
		    		      {
	
		    		      }
		    			
		    		      /*always the middle slot is the property
		    		       * so count and always take the second of third to become a property
		    		       */
		    		      if(slotcounter%2==0){
		    		    	  hm_result=getUriFromIndex(_ThingGettingURIfor_,1);
		    		      }
		    		      //set back to 0 to start new
		    		      if(slotcounter==3) slotcounter=0;
		    			query=query.replace(replace, "<"+hm_result+">");
		    			query_upside_down=query_upside_down.replace(replace, "<"+hm_result+">");
		    			
		    		}
		    		
		    		lstquerupsidedown.add(query_upside_down);
		    		lstquerynew.add(query);
		    		
		    		
		    		
		    		ArrayList<String> lsttmp=createLeftAndRightPropertyArray(query);
		    		//if its lower than three, we dont have any conditions and dont need to check it.
		    		//also if the size%3 isnt 0, than something else is wrong and we dont need to test the query
		    		if(lsttmp.size()>=3&&lsttmp.size()%3==0)for(String i : lsttmp) lstquerynew.add(i);
		    		else{
		    			lstquerynew.clear();
		    			lstquerynew.add("ERROR");
		    			addQuery=false;
		    		}
		    		
		    		lsttmp.clear();
		    		lsttmp=createLeftAndRightPropertyArray(query_upside_down);
		    		if(lsttmp.size()>=3&&lsttmp.size()%3==0)for(String i : lsttmp) lstquerupsidedown.add(i);
		    		else{
		    			lstquerupsidedown.clear();
		    			lstquerupsidedown.add("ERROR");
		    			addQuery=false;
		    		}
		    		
		    		if(addQuery==true){
		    			lstquery.add(lstquerynew);
		    			lstquery.add(lstquerupsidedown);
		    		}
	     		}
	     	}
	     		
	     	
	     	return lstquery;
		}
	
	 
	 
	private void saveNotParsedQuestions(String question) throws IOException{
		BufferedReader in = null;
		
	    String tmp="";
		// Lies Textzeilen aus der Datei in einen Vector:
	    try {
	      in = new BufferedReader(
	                          new InputStreamReader(
	                          new FileInputStream( "/tmp/notParsedQuestions" ) ) );
	      String s;
		while( null != (s = in.readLine()) ) {
	        tmp=tmp+"\n"+s;
	      }
	    } catch( FileNotFoundException ex ) {
	    } catch( Exception ex ) {
	      System.out.println( ex );
	    } finally {
	      if( in != null )
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	
	    
	    String out="";
	    BufferedWriter outfile = new BufferedWriter(
                  new OutputStreamWriter(
                  new FileOutputStream( "/tmp/notParsedQuestions" ) ) );

	    outfile.write(tmp+"\n"+question);
	    outfile.close();
	}
	
	/**
	 *  
	 * @param string
	 * @param fall 1 Property 0 no Property
	 * @return
	 * @throws SQLException 
	 */
	private String getUriFromIndex(String string, int fall) throws SQLException{
		String result=null;
		String tmp1=null;
		String tmp2 = null;
		//just to be sure its only 0 or 1
		if(fall!=0 && fall!=1) fall=0;
		if(fall==0){
			
			//first try: take always the ontology if existing and not the Resource
			tmp1=myindex.getResourceURI(string.toLowerCase());
			tmp2=myindex.getontologyClassURI(string.toLowerCase());
			System.out.println("URI from resource: "+tmp1);
			System.out.println("URI from ontologyClass: "+tmp2);
			
			
			System.out.println("value from http://dbpedia.org/resource/WikiLeaks : "+ myindex.getResourceURI("http://dbpedia.org/resource/WikiLeaks"));
			System.out.println("value from author : "+ myindex.getResourceURI("author"));
			if(tmp1!=null && tmp2!=null) result=tmp2;
			if(tmp1!=null && tmp2==null) result=tmp1;
			if(tmp1==null && tmp2!=null) result=tmp2;
			//result=myindex.getResourceURI(string.toLowerCase());
			if(result==null)result=myindex.getPropertyURI(string.toLowerCase());
		}
		if(fall==1){
			tmp1=myindex.getPropertyURI(string.toLowerCase());
			tmp2=myindex.getontologyURI(string.toLowerCase());
			if(tmp1!=null && tmp2!=null) result=tmp2;
			if(tmp1!=null && tmp2==null) result=tmp1;
			if(tmp1==null && tmp2!=null) result=tmp2;
			if(result==null){
				result=myindex.getResourceURI(string.toLowerCase());
				if(result!=null) result=result.replace("resource", "property");
			}
			
		}
		String tmp="";
		tmp=string.toLowerCase();
		tmp=tmp.replace("property","");
		tmp=tmp.replace(" ", "_");
		if(result==null) {
			if(fall==1)return "http://dbpedia.org/property/"+tmp;
			if(fall==0)return "http://dbpedia.org/resource/"+tmp;
		else{
			System.out.println("return result: "+result);
				return result;
		}
		}
		else return result;
	}


	
	
	/*
	 * TODO: if for example title,name,label is given, replace , and get for each thing the semantics
	 * 
	 */
		private static ArrayList<String> getSemantics (ArrayList<String> semanticsOrig) throws IOException, JWNLException {
			ArrayList<String> result = new ArrayList<String>();
			
			//System.out.println("in function get Semantics!");
			
			ArrayList<String> semantics = new ArrayList<String>();
			semantics=semanticsOrig;
			/*//check out, if in the semantics are still terms, with _ or ,
			//if so, split on _ and , and add them to the semantic list
			for(String id :semanticsOrig){
				if(id.contains("_")){
					System.out.println("in _");
					String[] tmp=id.split("_");
					for(String i: tmp) if(!semantics.contains(i))semantics.add(i);
					
					//and also add a term without _
					if(!semantics.contains(id.replace("_"," ")))semantics.add(id.replace("_"," "));
					//remove old id
					//semantics.remove(id);
				}
				if(id.contains(",")){
					System.out.println("in ,");
					String[] tmp=id.split(",");
					for(String i: tmp) if(!semantics.contains(i))semantics.add(i);
					//semantics.remove(id);
				}
			}*/
			
			for(String id :semantics){
				//System.out.println("in String id : semantics");
				//System.out.println("ID :"+id);
				
				//add id also to the result, if its not already in there
				if(!result.contains(id))result.add(id);
				List<String> array_relatedNouns=null;
				List<String> array_bestsynonyms=null;
				
				System.out.println("Wordnet Word: "+id);
				try{
					array_relatedNouns =wordnet.getRelatedNouns(id);
				}
				catch(Exception e){
					//array_relatedNouns.clear();
				}
				System.out.println("array_relatedNouns: "+ array_relatedNouns);
				//System.out.println("after relatedNouns");

				try{
					array_bestsynonyms=wordnet.getBestSynonyms(POS.NOUN, id);
					System.out.println("array_bestsynonyms: "+ array_bestsynonyms);
				}
				catch(Exception e){
					//
				}

					
					
				if(array_relatedNouns!=null){
					for(String i:array_relatedNouns){
						if(!result.contains(i))result.add(i);
					}
				}
				if(array_bestsynonyms!=null){
					for(String i:array_bestsynonyms){
						if(!result.contains(i))result.add(i);
					}
				}
				
				
					
			}

			if(!result.isEmpty()) return result;
			else{
				//System.out.println("Didnt find ")
				/*this is the case, if the first time nothing was found.
				 * but sometimes wordnet doesnt find anything e.g. die place... bzt you have also die and place
				 * so we try to find the seperate words and test them as well
				 */
				try{
					for(String id :semantics){
						//System.out.println("in String id : semantics TWO");
						String[] tmp_array=id.split(" ");
						//System.out.println("ID TWO:"+id);
						if(tmp_array.length>=2){
							for(String advanced_id : tmp_array){
								List<String> array_relatedNouns=null;
								List<String> array_bestsynonyms=null;
								//add id also to the result, if its not already in there
								if(!result.contains(advanced_id))result.add(advanced_id);
								
								try{
									array_relatedNouns =wordnet.getRelatedNouns(advanced_id);
								}
								catch(Exception e){
									//array_relatedNouns.clear();
								}
								System.out.println("array_relatedNouns: "+ array_relatedNouns);
								//System.out.println("after relatedNouns");

								try{
									array_bestsynonyms=wordnet.getBestSynonyms(POS.NOUN, advanced_id);
									System.out.println("array_bestsynonyms: "+ array_bestsynonyms);
								}
								catch(Exception e){
									//
								}
									
								if(array_relatedNouns!=null){
									for(String i:array_relatedNouns){
										if(!result.contains(i))result.add(i);
									}
								}
								if(array_bestsynonyms!=null){
									for(String i:array_bestsynonyms){
										if(!result.contains(i))result.add(i);
									}
								}

								
								}
						}
							
					}
					}
					catch(Exception e){
						if(result.isEmpty()) return semanticsOrig;
					}
				
			}
			
			if(!result.isEmpty()) return result;
			else return null;
		//	else{ return result;}
		}
		
		
		
		
	private String sendServerQuestionRequest(String query){
		//SPARQL-Endpoint of Semantic Computing Group
		String tmp="http://greententacle.techfak.uni-bielefeld.de:5171/sparql?default-graph-uri=&query="+createServerRequest(query)+"&format=text%2Fhtml&debug=on&timeout=";
		System.out.println(tmp);
		URL url;
	    InputStream is;
	    InputStreamReader isr;
	    BufferedReader r;
	    String str="";
	    String result="";

	    try {
	      url = new URL(tmp);
	      is = url.openStream();
	      isr = new InputStreamReader(is);
	      r = new BufferedReader(isr);
	      int counter=0;
	      do {
	        str = r.readLine();
	        if (str != null){
	        	result=result.concat(str);
	        		counter=counter+1;}
	      } while (str != null);

	    } catch (MalformedURLException e) {
	      System.out.println("Must enter a valid URL");
	    } catch (IOException e) {
	      System.out.println("Can not connect");
	    }
	    
	    
	    
		return createAnswer(result);
	}
	
	
	private String createAnswer(String string){
		//<td>Klaus Wowereit</td>
		
		//get with regex all between <td> </td>
		
		Pattern p = Pattern.compile (".*<td>(.*)</td>.*");
	    Matcher m = p.matcher (string);
	    String result="";

	  	while (m.find()) {
	  		if(m.group(1)!=null) 
	  		result = result+" "+ m.group(1);
	  	}
	  		
		if (result.length()==0) result="EmtyAnswer";

		return result;

	}
	
	
	private String createServerRequest(String query){
		String anfrage=null;
		anfrage=removeSpecialKeys(query);
	    anfrage=anfrage.replace("&lt;","<");
	    anfrage=anfrage.replace("%gt;",">");
	    anfrage=anfrage.replace("&amp;","&");
	    //anfrage=anfrage.replaceAll("#>","%23%3E%0D%0A%");
	    anfrage=anfrage.replace("#","%23");
	    anfrage=anfrage.replace(" ","+");
	    anfrage=anfrage.replace("/","%2F");
	    anfrage=anfrage.replace(":","%3A");
	    anfrage=anfrage.replace("?","%3F");
	    anfrage=anfrage.replace("$","%24");
	    //anfrage=anfrage.replaceAll("F&gt;+","F%3E%0D%0A");
	    anfrage=anfrage.replace(">","%3E");
	    anfrage=anfrage.replace("<","%3C");
	    anfrage=anfrage.replace("\"","%22");
	    anfrage=anfrage.replace("\n","%0D%0A%09");
	    anfrage=anfrage.replace("%%0D%0A%09","%09");
	    anfrage=anfrage.replace("=","%3D");
	    anfrage=anfrage.replace("@","%40");
	    anfrage=anfrage.replace("&","%26");
	    anfrage=anfrage.replace("(","%28");
	    anfrage=anfrage.replace(")","%29");
	    anfrage=anfrage.replace("%3E%0D%0A%25","%3E");
	    //anfrage=anfrage.replaceAll("\n",".%0D%0A%09");
		return anfrage;
	}
	
	private String removeSpecialKeys(String query){
		query=query.replace("\\","");
	    //query=query.replaceAll("\a","");
	    query=query.replace("\b","");
	    query=query.replace("\f","");
	    query=query.replace("\r","");
	    query=query.replace("\t","");
	   // query=query.replaceAll("\v","");
	    return query;
	}
	
	
	
	
	
}



/*
 * Backup original Iteration function
 * 
 */

/*
 * 
 *  
	  // Is the function for the Case, you are in Iteration one and have only one triple of condition (s,p,o). 
	  // @param querylist
	  // @param query
	  // @return a list with answers from the Server
	  // @throws SQLException
	  // @throws JWNLException
	  
	private ArrayList<String> simpleIteration1Case(ArrayList<String> querylist, String query) throws SQLException,
			JWNLException {
		//asking server
		String answer;
		ArrayList<String> final_answer=new ArrayList<String>();
		
		//First try the original query on the server. If that doesnt work, try it with Iteration
		 
		answer=sendServerQuestionRequest(query);

		if(answer.contains("EmtyAnswer")){
		
			String resource="";
			String property_to_compare_with="";
			String sideOfProperty="LEFT";

			
			int tmpcounter=0;
			for(String s : querylist){
				//we dont need the first one, because thats the query itself
				tmpcounter=tmpcounter+1;
				if(tmpcounter>=1){
					if(s.contains("LEFT")){
						sideOfProperty="LEFT";
						resource=s.replace("LEFT","");
					}
					if(s.contains("RIGHT")){
						sideOfProperty="RIGHT";
						resource=s.replace("RIGHT","");
					}
					if(s.contains("PROPERTY")){
						property_to_compare_with=s.replace("PROPERTY","");
					}
					
				}
			}
			System.out.println("Property to compare:: "+ property_to_compare_with);
			System.out.println("Resource: "+ resource);
			//contains uri AND string, every second is the string
			 HashMap<String,String> properties = new HashMap<String, String>();
			 GetRessourcePropertys property = new GetRessourcePropertys();
			 Boolean goOnAfterProperty = true;
			 try {
				 properties=property.getPropertys(getUriFromIndex(resource.toLowerCase(),0),sideOfProperty);
				if (properties==null){
					//final_answer.add("Error in getting Properties\n");
					
					final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
					goOnAfterProperty=false;
				}
				//System.out.println(properties);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				
				final_answer.add("Begin:\n"+query +"\nError in getting Properties \n End");
				goOnAfterProperty=false;
				
			}
			if(goOnAfterProperty==true){
				 //property_to_compare_with mit der Liste der propertys vergleichen, und wenn der normalisierte Wert >= LvenstheinMin ist, einbauen und neue query erzeugen.
				 ArrayList<String> new_queries= new ArrayList<String>();
				 for (Entry<String, String> entry : properties.entrySet()) {
					    String key = entry.getKey();
					    String value = entry.getValue();
					 double tmp=Levenshtein.computeLevenshteinDistance(property_to_compare_with.toLowerCase(), key);
					 
					 
					   //TODO: Implement Normalised levensthein
					  
				     if(tmp<=3.0){
						 //alte property uri mit neuer ersetzen:
						 String query_tmp=query;
						 String test = getUriFromIndex(property_to_compare_with.toLowerCase(),1);
						 //query_tmp=query_tmp.replace(test,properties.get(i-1));
						 query_tmp=query_tmp.replace(test,value);
						 new_queries.add(query_tmp);
					 }
					 
				 }
				 
				 System.out.println("Start Iterating Wordnet with "+property_to_compare_with+" and deept of "+explorationdepthwordnet);
				 ArrayList<String> semantics=new ArrayList<String>();
				 ArrayList<String> tmp_semantics=new ArrayList<String>();
				 ArrayList<String> result_SemanticsMatchProperties=new ArrayList<String>();
				 semantics.add(property_to_compare_with);
				 
				 //first check, if there is a singular form in the wordnet dictionary.. eg children -> child
				 String _temp_=myindex.getWordnetHelp(property_to_compare_with);
				 if(_temp_==null){
					 tmp_semantics=semantics;
				 }
				 else{
					 semantics.clear();
					 semantics.add(_temp_);
					 tmp_semantics=semantics;
				 }
				 Boolean goOnAfterWordnet = true;
				 for(int i=0;i<=explorationdepthwordnet;i++){

					 try {
						tmp_semantics=getSemantics(tmp_semantics);
						if (tmp_semantics==null){
							goOnAfterWordnet=false;
							final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semantics+" \n End");

						}
						else{
						//each word only one time
						 for(String k : tmp_semantics){
							 if(!semantics.contains(k)) semantics.add(k);
						 }
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						goOnAfterWordnet=false;
						final_answer.add("Begin:\n"+query +"\n Error in searching Wordnet with word "+semantics+" \n End");
						
					}
							 
				 }
				
				 if(goOnAfterWordnet==true){
					
					 for (Entry<String, String> entry : properties.entrySet()) {
						    String key = entry.getKey();
						    String value = entry.getValue();
						    
						for(String b : semantics){
							if(key.contains(b.toLowerCase())){
								if(!result_SemanticsMatchProperties.contains(key)){
									//create new query
								result_SemanticsMatchProperties.add(key);
								 String query_tmp=query;
								 String test = getUriFromIndex(property_to_compare_with.toLowerCase(),1);
								 query_tmp=query_tmp.replace(test,value);
								 System.out.println("New query after wordnet: "+ query_tmp);
								 new_queries.add(query_tmp);
								}
							}
						}
					}
					 
					for(String bla : new_queries){
						String answer_tmp;
						answer_tmp=sendServerQuestionRequest(bla);
						System.out.println("Antwort vom Server: "+answer_tmp);
						final_answer.add("Begin:\n"+bla +"\n"+answer_tmp+" \n End");
					}
				 }
			}
		}
		
		return final_answer;
	}
		 
	*/
 


/**
 * Cluster function
 */
/*
int length=array.length;
int [] result_array= new int[length];
for(int p =0;p<length;p++){
	 result_array[p]=0;
}
int zaehler=1;

//looking for max 3 word as one index
for(int z=length-1;z>=0;z=z-1){
	 if(z-2>=0){
		 String tmp1 = array[z];
		 String tmp2 = array[z-1];
		 String tmp3 = array[z-2]; 
		 
		 String tmpstring3=(((tmp3.concat(" ")).concat(tmp2)).concat(" ")).concat(tmp1);
		 String tmpstring2=(tmp2.concat(" ")).concat(tmp1);
		 String tmpstring1=tmp1;
		 
		 //always looking for the "biggest" match
		 if(hm.get(tmpstring3)!=null){
			 result_array[z]=zaehler;
			 result_array[z-1]=zaehler;
			 result_array[z-2]=zaehler;
			 zaehler++;
		 }
		 else{
			 if(hm.get(tmpstring2)!=null){
				 result_array[z]=zaehler;
				 result_array[z-1]=zaehler;
				 zaehler++;
			 }
			 else{
				 if(hm.get(tmpstring1)!=null){
					 result_array[z]=zaehler;
					 zaehler++;
				 }
			 }
		 }
		 
	 }
	 else{
		 if(z-1>=0){
			 String tmp1 = array[z];
			 String tmp2 = array[z-1];
			 
			 String tmpstring2=(tmp2.concat(" ")).concat(tmp1);
			 String tmpstring1=tmp1;
			 
			 //always looking for the "biggest" match

			 if(hm.get(tmpstring2)!=null){
				 result_array[z]=zaehler;
				result_array[z-1]=zaehler;
				zaehler++;
			}
			else{
				if(hm.get(tmpstring1)!=null){
					result_array[z]=zaehler;
					zaehler++;
				}
			}
		   }
		 if(z==0){
			 if(hm.get(array[z])!=null){
				result_array[z]=zaehler;
				zaehler++; 
			 }
		 }
	   }
	 }

System.out.println("###### Cluster ######");
for(int p =0;p<length;p++){
	 System.out.println(result_array[p]);
}
System.out.println("######");
*/

/*
 * 	private void create_Sparql_query_old(String string) throws JWNLException {
		String[] array= string.split(" ");

		 
		 //look, if the first word is a who!
		 if(array[0].contains("who")){
			 int position=0;
			 for(int i=0;i<array.length;i++){
				 if (array[i].contains("of")){
					 position=i;
					 break;
				 }
			 }
			 String vor_of=array[position-1];
			 String nach_of="";
			 //if there is only one element after of
			 if(array.length-position-1==1){
				 nach_of=array[position+1];
			 }
			 else{
				 for(int i=position+1; i<array.length;i++){
					 nach_of=(nach_of.concat(array[i])).concat(" ");
				 }
				 
				 //delete last emty space
				 nach_of = nach_of.substring(0, nach_of.length()-1);
			 }
			 String uri_vor_of=" ";
			 String uri_nach_of=" ";
			 
			 uri_vor_of=hm.get(vor_of);
			 uri_nach_of=hm.get(nach_of);
			 if(uri_vor_of!=null && uri_nach_of!=null){
				 uri_nach_of=uri_nach_of.replace("Category:", "");
				 uri_nach_of=uri_nach_of.replace("category:", "");
				 
				 String anfrage;
				 anfrage="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>select ?x  where { <"+uri_nach_of+"> <"+uri_vor_of+"> ?x.}";
				 
				 //if there is no answer from the server, start searching with wordnet
				 String result="";
				 result=sendServerQuestionRequest(anfrage);
				 if(result!="noanswer"){
					 System.out.println(result); 
				 }
				 else{
					 long startTime = System.currentTimeMillis();

					 System.out.println("Get Propertys of "+nach_of);
					 
					 //contains uri AND string, every second is the string
					 ArrayList<String> properties = new ArrayList<String>();
					 GetRessourcePropertys property = new GetRessourcePropertys();
					 try {
						 //using uri now, not the string
						properties=property.getPropertys(hm.get(nach_of));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 
					 System.out.println("Start Iterating Wordnet with "+vor_of+" and deept of "+iteration_deept);
					 ArrayList<String> semantics=new ArrayList<String>();
					 ArrayList<String> tmp_semantics=new ArrayList<String>();
					 ArrayList<String> result_SemanticsMatchProperties=new ArrayList<String>();
					 semantics.add(vor_of);
					 tmp_semantics=semantics;
					 for(int i=0;i<=iteration_deept;i++){

						 try {
							tmp_semantics=getSemantics(tmp_semantics);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
						}
						 //each word only one time
						 for(String k : tmp_semantics){
							 if(!semantics.contains(k)) semantics.add(k);
						 }
								 
					 }
					 long endTime = System.currentTimeMillis();
					System.out.println("Getting Properties and Semantics took "+(endTime-startTime) +" ms\n");
					
					//TODO: Try, if it works, if you use only one loop: (b.lowerCase).contains(properties.get(h))
					for(int h=1;h<properties.size()-2;h=h+2){
						for(String b : semantics){
							//System.out.println(properties.get(h));
							//System.out.println(b);
							if(properties.get(h).contains(b.toLowerCase())){
								if(!result_SemanticsMatchProperties.contains(properties.get(h)))
								result_SemanticsMatchProperties.add(properties.get(h));
							}
						}
					}
					for(String b : result_SemanticsMatchProperties){
						vor_of=b.toLowerCase();
						uri_vor_of=hm.get(vor_of);
						if(uri_vor_of!=null){
							 anfrage="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>select ?x  where { <"+uri_nach_of+"> <"+uri_vor_of+"> ?x.}";
							 System.out.println("Answer with the property \" " + b + "\" :\n"+sendServerQuestionRequest(anfrage));
						}
					}
					long endTime2 = System.currentTimeMillis();
					System.out.println("Getting Properties, Semantics and Answer from server took "+(endTime2-startTime) +" ms");
				 }
			 }
			 
		 }
	}
*/