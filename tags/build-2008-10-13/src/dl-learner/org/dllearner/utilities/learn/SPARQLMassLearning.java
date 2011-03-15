/**
 * Copyright (C) 2007-2008, Jens Lehmann
 *
 * This file is part of DL-Learner.
 * 
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.dllearner.utilities.learn;

import java.net.URLEncoder;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.dllearner.kb.sparql.Cache;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.kb.sparql.SparqlQuery;
import org.dllearner.utilities.statistics.SimpleClock;
import org.dllearner.utilities.statistics.Statistics;

import com.hp.hpl.jena.query.ResultSet;

public class SPARQLMassLearning {

	
	static Cache c;
	static SparqlEndpoint se;
	private static Logger logger = Logger.getRootLogger();
	
	static String standardSettings="";
	static String algorithm="refexamples";
	static String standardSettingsRefexamples = 
		"refexamples.minExecutionTimeInSeconds = 30;\n" +
		"refexamples.maxExecutionTimeInSeconds = 30;\n" +
		"//refexamples.guaranteeXgoodDescriptions = 10;\n" +
		"refexamples.logLevel=\"TRACE\";\n" +
		"refexamples.noisePercentage = 0.10;\n" +
		"refexamples.writeSearchTree = false;\n" +
		"refexamples.searchTreeFile = \"searchTree.txt\";\n" +
		"refexamples.replaceSearchTree = true;\n\n" ;
	
	static String standardSettingsRefinement = 
		"refinement.minExecutionTimeInSeconds = 30;\n" +
		"refinement.maxExecutionTimeInSeconds = 30;\n" +
		"//refinement.guaranteeXgoodDescriptions = 10;\n" +
		"refinement.logLevel=\"TRACE\";\n" +
		"refinement.writeSearchTree = false;\n" +
		"refinement.searchTreeFile = \"searchTree.txt\";\n" +
		"refinement.replaceSearchTree = true;\n\n" ;
	
	
	
	static String standardDBpedia="" +
			"sparql.recursionDepth = 1;\n" +
			"sparql.predefinedFilter = \"YAGO\";\n" + 
			"sparql.predefinedEndpoint = \"DBPEDIA\";\n";
			//"sparql.logLevel = \"INFO\";\n";
	
	
	//vars
	static boolean useRelated = false;
	static boolean useSuperClasses = false;
	static boolean useParallelClasses = true;
	static int poslimit = 10;
	static int neglimit = 20;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		//logger.setLevel(Level.TRACE);
		Logger.getLogger(SparqlKnowledgeSource.class).setLevel(Level.INFO);
		//System.out.println(Logger.getLogger(SparqlQuery.class).getLevel());
		SimpleClock sc=new SimpleClock();
		
		standardSettings=standardSettingsRefexamples+standardDBpedia;
		//standardSettings=standardSettingsRefinement+standardDBpedia;
		
		DBpedia();
		//algorithm="refinement";
		//roles();
		
		/*System.out.println(Level.DEBUG.getClass());
			System.out.println(Level.toLevel("INFO"));
			System.out.println(Level.INFO);*/
			//System.exit(0);
			
	
		
			sc.printAndSet("Finished");

	}
	
	
	
	
	static void roles(){
		
		se = SparqlEndpoint.getEndpointDBpedia();
		//se = SparqlEndpoint.EndpointUSCensus();
		SortedSet<String> roles = new TreeSet<String>();
		roles.add("http://dbpedia.org/property/birthPlace");
		//roles.add("http://www.rdfabout.com/rdf/schema/census/landArea");
		standardSettings+=algorithm+".ignoredRoles = {\""+roles.first()+"\"};\n";
		
		SortedSet<String> posExamples = new TreeSet<String>();
		SortedSet<String> negExamples = new TreeSet<String>();
		String url = "http://dbpedia.openlinksw.com:8890/sparql";
		//HashMap<String, ResultSet> result = new HashMap<String, ResultSet>();
		//HashMap<String, String> result2 = new HashMap<String, String>();
		//System.out.println(concepts.first());
		//logger.setLevel(Level.TRACE);
		//AutomaticExampleFinderRolesSPARQL ae= new AutomaticExampleFinderRolesSPARQL( se);
		
		//ae.initDomainRange(roles.first(), poslimit, neglimit);
	
		//posExamples = ae.getPosExamples();
		//negExamples = ae.getNegExamples();
		
		System.out.println(posExamples);
		System.out.println(negExamples);
		//System.exit(0);
		String tmp = roles.first().replace("http://dbpedia.org/property/", "").replace("\"","");
		String confname1 = "";
		String confname2 = "";
		try{
			confname1 = URLEncoder.encode(tmp, "UTF-8")+"_domain.conf";
			confname2 = URLEncoder.encode(tmp, "UTF-8")+"_range.conf";
		}catch (Exception e) {e.printStackTrace();}
		//
		ConfWriter cf=new ConfWriter();
		cf.addToStats("relearned role: "+roles.first());
		
		//System.exit(0);
		//"relearned concept: ";
		cf.writeSPARQL(confname1,  negExamples,posExamples, url, new TreeSet<String>(),standardSettings,algorithm);
		
		cf.writeSPARQL(confname2, posExamples, negExamples, url, new TreeSet<String>(),standardSettings,algorithm);
		//new LearnSparql().learn(posExamples, negExamples, "http://dbpedia.openlinksw.com:8890/sparql", new TreeSet<String>());
	
		
	}
	
	static void DBpedia(){
		se = SparqlEndpoint.getEndpointLOCALDBpedia();
		//concepts.add("(EXISTS \"monarch\".TOP AND EXISTS \"predecessor\".(\"Knight\" OR \"Secretary\"))");
		
		SortedSet<String> concepts = new TreeSet<String>();
		SortedSet<String> tmpSet=selectDBpediaConcepts(20);
		System.out.println(concepts.size());
		for (String string : tmpSet) {
			concepts.add("\""+string+"\"");
		}
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		concepts.remove(concepts.first());
		//concepts.remove(concepts.first());
		//concepts.add("(\"http://dbpedia.org/class/yago/HeadOfState110164747\" AND (\"http://dbpedia.org/class/yago/Negotiator110351874\" AND \"http://dbpedia.org/class/yago/Representative110522035\"))");
		//concepts.add("\"http://dbpedia.org/class/yago/Person100007846\"");
		//concepts.add("\"http://dbpedia.org/class/yago/FieldMarshal110086821\"");
		//concepts.add("http://dbpedia.org/resource/Category:Prime_Ministers_of_the_United_Kingdom");
		//concepts.add("http://dbpedia.org/resource/Category:Grammy_Award_winners");
		//concepts.add("EXISTS \"http://dbpedia.org/property/grammyawards\".TOP");
		
		SortedSet<String> posExamples = new TreeSet<String>();
		SortedSet<String> negExamples = new TreeSet<String>();
		String url = "http://dbpedia.openlinksw.com:8890/sparql";
		url = "http://139.18.2.37:8890/sparql";
		//HashMap<String, ResultSet> result = new HashMap<String, ResultSet>();
		//HashMap<String, String> result2 = new HashMap<String, String>();
		//System.out.println(concepts.first());
		//logger.setLevel(Level.TRACE);
		
		//String concept=concepts.first();
		//int i=0;
		Statistics.setCurrentLabel("0");
		int recursiondepth=0;
		boolean closeAfterRecursion=false;
		//int numberOfTriples = 0;
		for (String oneConcept : concepts) {
			//AutomaticExampleFinderSPARQLold ae= new AutomaticExampleFinderSPARQLold( se);	
			useRelated = true;
			useSuperClasses = true;
			useParallelClasses = false;
			
			poslimit=10;
			neglimit=10;
			//ae.initDBpedia(concept, useRelated, useSuperClasses,useParallelClasses, poslimit, neglimit);
			//posExamples = ae.getPosExamples();
			//negExamples = ae.getNegExamples();
		
			
			/*String tmp = concepts.first().replace("http://dbpedia.org/resource/Category:", "").replace("\"","");
			tmp = tmp.replace("http://dbpedia.org/class/yago/", "");
			tmp = tmp.replace("http://dbpedia.org/property/", "");
			String confname = "";
			try{
				confname = URLEncoder.encode(tmp, "UTF-8")+".conf";
			}catch (Exception e) {e.printStackTrace();}*/
			//
			//ConfWriter cf=new ConfWriter();
			//cf.addToStats("relearned concept: "+concepts.first());
			//System.out.println(confname);
			LearnSparqlold ls = new LearnSparqlold();
			TreeSet<String> igno = new TreeSet<String>();
			System.out.println(oneConcept);
			//igno.add(oneConcept.replaceAll("\"", ""));
			
			ls.learnDBpedia(posExamples, negExamples, url,igno,recursiondepth, closeAfterRecursion,false);
			
			//System.out.println("AAAAAAAA");
			//System.exit(0);
			//"relearned concept: ";
			//cf.writeSPARQL(confname, posExamples, negExamples, url, new TreeSet<String>(),standardSettings,algorithm);
			//
	
		}
		//Statistics.print();
	}
	

	

	

	

	public static void init() {
		
		SimpleLayout layout = new SimpleLayout();
		// create logger (a simple logger which outputs
		// its messages to the console)
		FileAppender fileAppender =null; ;
		try{
			fileAppender = new FileAppender(layout,"the_log.txt",false);
		}catch (Exception e) {e.printStackTrace();}

		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.removeAllAppenders();
		logger.addAppender(consoleAppender);
		logger.addAppender(fileAppender);
		logger.setLevel(Level.DEBUG);
		c = new Cache("cachetemp");
		

	}
	
	public static SortedSet<String> selectDBpediaConcepts(int number){
		String query = "SELECT DISTINCT ?concept WHERE { \n" + 
		"[] a ?concept .FILTER (regex(str(?concept),'yago'))" +
		" \n}  \n"; //LIMIT "+number+"
		

		String JSON = (c.executeSparqlQuery(new SparqlQuery(query, se)));
		ResultSet rs =SparqlQuery.convertJSONtoResultSet(JSON);
		if(rs==null);
		//JenaResultSetConvenience rsc = new JenaResultSetConvenience(rs);
		//return SetManipulation.fuzzyShrink(rsc.getStringListForVariable("concept"),number);
		return null;
	}
	
	

}