package org.dllearner.scripts.tiger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dllearner.algorithms.refinement2.ROLComponent2;
import org.dllearner.algorithms.refinement2.ROLearner2;
import org.dllearner.core.ComponentPool;
import org.dllearner.core.EvaluatedDescription;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.LearningAlgorithm;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.configurators.ComponentFactory;
import org.dllearner.kb.OWLFile;
import org.dllearner.kb.sparql.Cache;
import org.dllearner.kb.sparql.SPARQLTasks;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.kb.sparql.SparqlQuery;
import org.dllearner.kb.sparql.SparqlQueryDescriptionConvertVisitor;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.utilities.Files;
import org.dllearner.utilities.Helper;
import org.dllearner.utilities.JamonMonitorLogger;
import org.dllearner.utilities.examples.ExMakerCrossFolds;
import org.dllearner.utilities.examples.ExMakerFixedSize;
import org.dllearner.utilities.examples.ExMakerRandomizer;
import org.dllearner.utilities.examples.ExampleDataCollector;
import org.dllearner.utilities.examples.Examples;
import org.dllearner.utilities.examples.ExperimentCollector;

import com.jamonapi.Monitor;

public class TestIterativeLearning {
	private static final Logger logger = Logger.getLogger(TestIterativeLearning.class);

	static DecimalFormat df = new DecimalFormat("00.###%");

//	static String backgroundXML = "files/tiger.noSchema.noImports.rdf";
	static String backgroundXML = "files/tiger_trimmed_toPOS.rdf";
	static String propertiesXML = "files/propertiesOnly.rdf";
	static String sentenceXMLFolder = "files/tiger/";
	static String sentenceprefix = "http://nlp2rdf.org/ontology/s";
	static String prefix = "http://nlp2rdf.org/ontology/";

	static String active = "files/active_all_sentenceNumbers.txt";
	static String passiveNoZU = "files/passive_noZuInf_sentenceNumbers.txt";
	static String passiveWithZu = "files/passive_zuInf_sentenceNumbers.txt";
	static String test_has_pos = "files/test_has_pos.txt";
	static String test_has_neg = "files/test_has_neg.txt";

	static SparqlEndpoint sparqlEndpoint;
	static SPARQLTasks sparqlTasks;

	static String sparqlEndpointURL = "http://db0.aksw.org:8893/sparql";
	static String graph = "http://nlp2rdf.org/tiger";
	static String rulegraph = "http://nlp2rdf.org/schema/rules1";
	
	public static DecimalFormat dftime = new DecimalFormat("#####.#");
	
	
	
	
	static int iterations = 5;
	static int folds = 6;
	static int printSentences = 3;
	//no randomization in examples

	public static void main(String[] args) {
		LogHelper.initLoggers();
		Logger.getLogger(Cache.class).setLevel(Level.INFO);
		Logger.getLogger(ComponentPool.class).setLevel(Level.INFO);
		Logger.getLogger(ROLearner2.class).setLevel(Level.INFO);
		Logger.getLogger(RhoDRDown.class).setLevel(Level.INFO);
		Logger.getLogger(SparqlQuery.class).setLevel(Level.INFO);

		try {
			sparqlEndpoint = new SparqlEndpoint(new URL(sparqlEndpointURL), new ArrayList<String>(Arrays
					.asList(new String[] { graph })), new ArrayList<String>());
			sparqlTasks = new SPARQLTasks(Cache.getDefaultCache(), sparqlEndpoint);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		boolean debug = true;
//		if(debug){
//			folds = 1;
//			iterations = 1;
//		}
		
		passiveNoZU();
//		passiveWithZu();

		logger.warn("finished");
		JamonMonitorLogger.writeHTMLReport("log/tiger.html");

	}
	
	public static void passiveNoZU(){
			ExperimentCollector eColl_passiveNoZU = new ExperimentCollector("passiveNoZU");
		
			SortedSet<String> positives =  read(passiveNoZU);
			SortedSet<String> negatives = read(active);
			
			//removing overlap
			positives.removeAll(negatives);
			negatives.removeAll(positives);
			
			Examples allExamples = new Examples();
			allExamples.addPosTrain(positives);
			allExamples.addNegTrain(negatives);
			
			logger.debug("All examples \n"+allExamples);
			
			List<Examples> folds =  new ExMakerCrossFolds(allExamples).split(TestIterativeLearning.folds, 0.1d);
//			ExMakerCrossFolds.printFolds(folds);
			List<ExperimentConfig> configs = getConfigs();
			for (ExperimentConfig experimentConfig : configs) {
				logger.warn("next: passiveNoZU."+experimentConfig.label);
				int i = 1;
				for (Examples examples : folds) {
					
					logger.warn("beginning fold: "+(i++));
					conductExperiment( examples, experimentConfig);
					
				}
				eColl_passiveNoZU.addExperimentConfig(experimentConfig);
				logger.info(experimentConfig);
			}
			eColl_passiveNoZU.write(iterations);
		
	}
	
	public static void passiveWithZu(){
		ExperimentCollector eColl_passiveWithZu = new ExperimentCollector("passiveWithZu");
			SortedSet<String> positives = read(passiveWithZu);
			SortedSet<String> negatives = read(active);
			
			//removing overlap
			positives.removeAll(negatives);
			negatives.removeAll(positives);
			
			Examples allExamples = new Examples();
			allExamples.addPosTrain(positives);
			allExamples.addNegTrain(negatives);
			
			logger.debug("All examples \n"+allExamples);
			
			List<Examples> runs = new ArrayList<Examples>();
			runs.add(new ExMakerRandomizer(allExamples).split(0.7d));
			runs.add(new ExMakerRandomizer(allExamples).split(0.7d));
			runs.add(new ExMakerRandomizer(allExamples).split(0.7d));
			runs.add(new ExMakerRandomizer(allExamples).split(0.7d));
			runs.add(new ExMakerRandomizer(allExamples).split(0.7d));

			List<ExperimentConfig> configs = getConfigs();
			for (ExperimentConfig experimentConfig : configs) {
				logger.warn("next: passiveWithZu."+experimentConfig.label);
				int i=1;
				for (Examples examples : runs) {
					logger.warn("beginning run: "+(i++));
					conductExperiment( examples, experimentConfig);
					
				}
				eColl_passiveWithZu.addExperimentConfig(experimentConfig);
				
				logger.info(experimentConfig);
			}
			eColl_passiveWithZu.write(iterations);
		
	}
	
	public static List<ExperimentConfig> getConfigs(){
		
		List<ExperimentConfig> l = new ArrayList<ExperimentConfig>();
		ExperimentConfig baseline = new ExperimentConfig(iterations, "baseline_5_5");
		
		
		ExperimentConfig reducedExamples = new ExperimentConfig(iterations, "reducedExamples_2_2");
		reducedExamples.initialsplits = 2;
		reducedExamples.splits = 2;
		
		
		ExperimentConfig fixRuntime = new ExperimentConfig(iterations, "fixRuntime_20s");
		fixRuntime.adaptMaxRuntime=false;
		fixRuntime.maxExecutionTime = 20;
		
		
		ExperimentConfig useLemma = new ExperimentConfig(iterations, "useLemma_false");
		useLemma.useDataHasValue=false;
		
		
		l.add(baseline);
//		l.add(reducedExamples);
//		l.add(fixRuntime);
//		l.add(useLemma);
		

		return l;
	}
	
	public static void conductExperiment(Examples allExamples, ExperimentConfig config){
		Examples tmp = new Examples();
		tmp.addPosTrain(allExamples.getPosTrain());
		tmp.addNegTrain(allExamples.getNegTrain());
		
		ExMakerFixedSize fs = new ExMakerFixedSize(tmp);
		Examples learn = fs.select(config.initialsplits, config.initialsplits);
		logger.debug("Total set \n"+allExamples);
		logger.debug("Initial training set \n"+learn);
		
		SortedSet<String> posAsPos = new TreeSet<String>();
		SortedSet<String> retrieved = new TreeSet<String>();
		
		String lastConcept="";
		
		for(int i = 0 ; config.stopCondition(i, learn, posAsPos,  retrieved, allExamples, lastConcept) ;i++ ) {
			Monitor iterationTime = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "iterationTime").start();
			/*LEARNING*/
			EvaluatedDescription ed = learn(learn, config);
			lastConcept = PrefixMap.toKBSyntaxString(ed.getDescription());
			logger.debug("USING CONCEPT: "+lastConcept);
			
			/*RETRIEVING*/
			retrieved = getSentences(ed, config.resultLimit, learn);
			//remove all that are not to be tested
			retrieved =	Helper.intersection(allExamples.getTestExamples(), retrieved );
			logger.debug("Retrieved "+retrieved.size()+" sentences");
			
			
			/*MASHING*/
			//Menge aller positiven geschn. mit den gefundenen
			posAsPos = Helper.intersection(retrieved, allExamples.getPosTest());
			logger.debug("Number of retrieved positives: "+posAsPos.size());
			logger.debug("Number of total positives: "+allExamples.getPosTest().size());
			results(posAsPos, retrieved, allExamples);
			
			//Menge aller positiven geschn. mit den gefundenen
			SortedSet<String> negAsPos = Helper.intersection(retrieved, allExamples.getNegTest());
			logger.debug("Number of retrieved negatives: "+negAsPos.size());
			logger.debug("Number of total negatives: "+allExamples.getNegTest().size());
			logger.debug("Total: "+posAsPos.size()+" + "+negAsPos.size() +" = "+retrieved.size());
			
			
			Examples newlyFound = new Examples();
			SortedSet<String> discoveredPosInStore = Helper.intersection(retrieved, allExamples.getPosTest());
			SortedSet<String> misclassifiedNegInStore = Helper.intersection(retrieved, allExamples.getNegTest());
			newlyFound.addPosTrain(discoveredPosInStore);
			newlyFound.addNegTrain(misclassifiedNegInStore);

			SortedSet<String> posAsNeg = Helper.difference(allExamples.getPositiveExamples(), retrieved);
			
			logger.info("Discovered: "+discoveredPosInStore.size()+" positive sentences in store (printing "+printSentences+"):");
			_getLabels(discoveredPosInStore, printSentences);
			logger.info("Misclassified: "+misclassifiedNegInStore.size()+" negative sentences in store (printing "+printSentences+"):");
			_getLabels(misclassifiedNegInStore, printSentences);
			logger.info("Not found positives: "+posAsNeg.size()+" positive sentences in store (printing "+printSentences+"):");
			_getLabels(posAsNeg, printSentences);
			
			
			
			fs = new ExMakerFixedSize(newlyFound);
			newlyFound = fs.select(config.splits, config.splits);
			
			learn.addPosTrain(newlyFound.getPosTrain());
			learn.addNegTrain(newlyFound.getNegTrain());
			logger.debug("Next training set \n"+learn);
			iterationTime.stop();
			Monitor learningTime = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "learningTime");
			logger.warn("finished iteration "+(i+1)+" needed on  avg: "+dftime.format(iterationTime.getAvg()));
			logger.warn("for learning: "+dftime.format(learningTime.getLastValue())+"  avg: "+dftime.format(learningTime.getAvg()));
		}
		
		
		
		
		
	}
	
	private static void results(SortedSet<String> posAsPos, SortedSet<String> retrieved, Examples allExamples) {
		double precision = precision( posAsPos.size(), retrieved.size());
		double recall = recall( posAsPos.size(),allExamples.getPosTest().size());
		double fmeasure =  (2*precision*recall)/(precision+recall);
		logger.info("F-Measure: "+df.format(   fmeasure  ));
		
	}

	public static double precision( int posAsPos, int retrieved){
		double precision = (retrieved==0)?0.0d:((double)posAsPos)/((double)retrieved);
		logger.info("Precision: "+df.format(precision));
		return precision;
	}
	public static double recall( int posAsPos, int allPositives){
		double recall = ((double)posAsPos)/((double)allPositives);
		logger.info("Recall: "+df.format(recall));
		return recall;
		
	}

	private static Set<KnowledgeSource> _getOWL(Examples ex) throws Exception{
		Set<KnowledgeSource> tmp = new HashSet<KnowledgeSource>();
		List<URL> urls = new ArrayList<URL>();
		urls.add(new File(backgroundXML).toURI().toURL());
		urls.addAll(ExampleDataCollector.convert(sentenceXMLFolder, ex.getPosTrain()));
		urls.addAll(ExampleDataCollector.convert(sentenceXMLFolder, ex.getNegTrain()));
		
		for (URL u : urls) {
			OWLFile ks = ComponentFactory.getOWLFile(u);
			tmp.add(ks);
		}
		return tmp;
	}
	@SuppressWarnings("unused")
	private static Set<KnowledgeSource> _getSPARQL(Examples ex) throws Exception{
		Set<KnowledgeSource> tmp = new HashSet<KnowledgeSource>();

		Set<String> examples = new TreeSet<String>();
		examples.addAll(ex.getPosTrain());
		examples.addAll(ex.getNegTrain());
		SparqlKnowledgeSource ks = ComponentFactory.getSparqlKnowledgeSource(new URL(sparqlEndpointURL), examples);
		ks.getConfigurator().setUrl(new URL(sparqlEndpointURL));
		ks.getConfigurator().setDefaultGraphURIs(new HashSet<String>(Arrays.asList(new String[]{graph})));
		ks.getConfigurator().setInstances(examples);
		ks.getConfigurator().setDissolveBlankNodes(false);
		ks.getConfigurator().setRecursionDepth(2);
		ks.getConfigurator().setDissolveBlankNodes(false);
		ks.getConfigurator().setCloseAfterRecursion(true);
		ks.getConfigurator().setGetAllSuperClasses(true);
		ks.getConfigurator().setGetPropertyInformation(false);
		ks.getConfigurator().setUseLits(true);
//		ks.getConfigurator().
		OWLFile ks2 = ComponentFactory.getOWLFile(new File(propertiesXML).toURI().toURL());
		tmp.add(ks);
		tmp.add(ks2);
		
		return tmp;
	}
	
	//test if virtuoso is correct
//	public static void validate(Description d, Examples newlyFound){
//		try {
//		ExMakerFixedSize fs = new ExMakerFixedSize(newlyFound);
//		Examples tmp = fs.select(100, 100);
//		FastInstanceChecker fc = _getFastInstanceChecker(tmp);
//		@SuppressWarnings("unused")
//		SortedSet<Individual> inds = fc.getIndividuals(d);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public static FastInstanceChecker _getFastInstanceChecker(Examples ex)throws Exception{
			Set<KnowledgeSource> tmp = _getOWL(ex);
//			Set<KnowledgeSource> tmp = _getSPARQL(ex);
			

			FastInstanceChecker rc = ComponentFactory.getFastInstanceChecker(tmp);
			for (KnowledgeSource ks : tmp) {
				ks.init();
			}
			rc.init();
			return rc;
	}
	
	public static EvaluatedDescription learn(Examples ex, ExperimentConfig config) {
		Monitor initTimeKBandReasoner = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "initTimeKBandReasoner").start();
		

		EvaluatedDescription result = null;
		
		try {
			FastInstanceChecker rc = _getFastInstanceChecker(ex);
			PosNegLPStandard lp = ComponentFactory
					.getPosNegLPStandard(rc, ex.getPosTrain(), ex.getNegTrain());
			LearningAlgorithm la = _getROLLearner(lp, rc, config, ex);
			lp.init();
			la.init();
			initTimeKBandReasoner.stop();
			Monitor learningTime = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "learningTime").start();
			la.start();
			learningTime.stop();
			
			result = la.getCurrentlyBestEvaluatedDescription();
			logger.trace(PrefixMap.toKBSyntaxString(result.getDescription()));
			logger.trace(PrefixMap.toManchesterSyntaxString(result.getDescription()));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return result;
	}

	public static SortedSet<String> getSentences(EvaluatedDescription ed, int resultLimit, Examples justforFindingTheBug) {
		Monitor m = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "getSentences").start();
		SortedSet<String> result = new TreeSet<String>();
		SparqlQueryDescriptionConvertVisitor visit = new SparqlQueryDescriptionConvertVisitor();
		visit.setDistinct(true);
		visit.setLabels(false);
		visit.setLimit(resultLimit);
		String sparqlQueryGood = "";
		String sparqlQueryBad = "";
		try {
			sparqlQueryGood = visit.getSparqlQuery(ed.getDescription().toKBSyntaxString());
			sparqlQueryBad = visit.getSparqlQuery(ed.getDescription());
			if(!sparqlQueryGood.equals(sparqlQueryBad)){
				String file = "errorDescription/"+System.currentTimeMillis();
				justforFindingTheBug.writeExamples(file);
				Files.appendFile(new File(file), "\n\n/**\nGood:\n"+sparqlQueryGood+"\nBad:\n"+sparqlQueryBad+"**/");
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		sparqlQueryGood = " \n define input:inference \"" + rulegraph + "\" \n" + "" + sparqlQueryGood;
		logger.trace(sparqlQueryGood);

		
		result.addAll(sparqlTasks.queryAsSet(sparqlQueryGood, "subject"));
		m.stop();
		logger.debug("query avg: " + ((double)m.getAvg() / (double)1000)+ " seconds (last: "+((double)m.getLastValue() / (double)1000)+")");
		if(result.isEmpty()){
			
			logger.error("sparql query returned no results ");
			logger.error(sparqlQueryGood);
			System.exit(0);
		}
		return result;
	}
	
	private static void  _getLabels(SortedSet<String> sentenceURIs, int limit){
		Monitor m = JamonMonitorLogger.getTimeMonitor(TestIterativeLearning.class, "_getLabels").start();
		int i = 0;
		for (String sentenceURI : sentenceURIs) {
			if(i>=limit){
				break;
			}
			i++;
			_getLabel(sentenceURI);
		}
		m.stop();
	}
	
	private static void _getLabel(String sentenceURI){
		String 	query = "SELECT * FROM <"+graph+"> " +
			"{ <"+sentenceURI+"> rdfs:label ?label . }";
		SortedSet<String>  s = sparqlTasks.queryAsSet(query, "label");
		if(s.isEmpty()){
			logger.warn("no label for "+sentenceURI);
		}else{
			logger.debug(sentenceURI.replace(prefix, "")+" "+s.first());
		}
	}

	private static LearningAlgorithm _getROLLearner(LearningProblem lp, ReasonerComponent rc, ExperimentConfig config, Examples ex)
			throws Exception {
		
		int maxExecutionTime = config.maxExecutionTime;
		int valueFrequencyThreshold = ex.getPosTrain().size();
		if(config.adaptMaxRuntime){
			maxExecutionTime = config.factor * ex.sizeOfTrainingSets();
//			valueFrequencyThreshold = (int) Math.floor(0.8d*((double)ex.getPosTrain().size()));
		}
		
		ROLComponent2 la = ComponentFactory.getROLComponent2(lp, rc);
		la.getConfigurator().setUseExistsConstructor(true);

		la.getConfigurator().setUseAllConstructor(false);
		la.getConfigurator().setUseCardinalityRestrictions(false);
		la.getConfigurator().setUseNegation(false);
		la.getConfigurator().setUseHasValueConstructor(false);
		la.getConfigurator().setUseDataHasValueConstructor(config.useDataHasValue);
		la.getConfigurator().setValueFrequencyThreshold(valueFrequencyThreshold);
		
		la.getConfigurator().setIgnoredConcepts(new HashSet<String>(Arrays.asList(new String[]{
				"http://nlp2rdf.org/ontology/sentencefinalpunctuation_tag",
				"http://nlp2rdf.org/ontology/comma_tag",
				"http://nachhalt.sfb632.uni-potsdam.de/owl/stts.owl#SentenceFinalPunctuation",
				"http://nlp2rdf.org/ontology/generalsentenceinternalpunctuation_tag"
		})));
		

		la.getConfigurator().setNoisePercentage(config.noise);
		la.getConfigurator().setTerminateOnNoiseReached(true);
		la.getConfigurator().setMaxExecutionTimeInSeconds(maxExecutionTime);
		
		if(config.useStartClass){
			la.getConfigurator().setStartClass(prefix + "Sentence");
		}
		
		 la.getConfigurator().setWriteSearchTree(config.searchTree);
		 la.getConfigurator().setSearchTreeFile("log/searchTreeTiger.txt");
		 la.getConfigurator().setReplaceSearchTree(true);
		return la;
	}

	public static SortedSet<String> read(String f) {
		SortedSet<String> result = new TreeSet<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

			String line;
			while ((line = in.readLine()) != null) {
				try {
					line = line.trim();
					Integer.parseInt(line);
					if (!result.add(sentenceprefix + line)) {
						logger.error("reading failed");
						System.exit(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Could not read examples from: " + f);
			System.exit(0);

		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("read " + result.size() + " lines from " + f);

		return result;
	}

}
