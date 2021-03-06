/**
 * Copyright (C) 2007-2009, Jens Lehmann
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
package org.dllearner.scripts.matching;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.zip.DataFormatException;

import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.kb.manipulator.Manipulator;
import org.dllearner.kb.manipulator.StringToResource;
import org.dllearner.kb.manipulator.Rule.Months;
import org.dllearner.kb.sparql.Cache;
import org.dllearner.kb.sparql.SPARQLTasks;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.learningproblems.PosOnlyLP;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.utilities.Helper;

/**
 * Uses learning algorithms to learn class definitions for DBpedia
 * classes in LinkedGeoData. As a final result, we can classify most
 * objects in LinkedGeoData using the DBpedia ontology. 
 * 
 * @author Jens Lehmann
 *
 */
public class LearnOSMClasses {

	private static SparqlEndpoint dbpediaEndpoint = SparqlEndpoint.getEndpointLOCALDBpedia();
//	private static SparqlEndpoint geoDataEndpoint = SparqlEndpoint.getEndpointLOCALGeoData();	
	
	public static void main(String args[]) throws IOException, DataFormatException, LearningProblemUnsupportedException, ComponentInitException {
		File matchesFile = new File("log/geodata/owlsameas_en.dat");
		Map<URI,URI> matches = Utility.getMatches(matchesFile);
		
		SPARQLTasks dbpedia = new SPARQLTasks(new Cache("cache/matching"),dbpediaEndpoint);
		Set<String> positives = new TreeSet<String>();
		Set<String> negatives = new TreeSet<String>();
		
		// loop through all matches
		for(Entry<URI,URI> match : matches.entrySet()) {
			URI dbpediaURI = match.getKey();
			URI lgdURI = match.getValue();
			// test whether the dbpediaURI is a city
//			String query = "ASK {<"+dbpediaURI+"> a <http://dbpedia.org/ontology/City>}";
			String query = "ASK {<"+dbpediaURI+"> a <http://dbpedia.org/ontology/Organisation>}";
			boolean isInClass = dbpedia.ask(query);
//			if(!isCity) {
//				// DBpedia ontology does not capture all cities, so we also use UMBEL, YAGO
//				String query2 = "ASK {<"+dbpediaURI+"> a ?x . FILTER(?x LIKE <%City%>) }";
//				String query3 = "ASK {<"+dbpediaURI+"> a ?x . FILTER(?x LIKE <%Cities%>) }";
//				isCity = dbpedia.ask(query2) || dbpedia.ask(query3);
//			}
//			System.out.println(isCity + " " + lgdURI);
			if(isInClass) {
				positives.add(lgdURI.toString());
				System.out.println("+\""+lgdURI+"\"");
			} else {
				negatives.add(lgdURI.toString());
				System.out.println("-\""+lgdURI+"\"");
			}
		}
		
		Set<String> instances = new TreeSet<String>();
		instances.addAll(positives);
		instances.addAll(negatives);
		
		System.out.println(instances.size() + " instances - " + positives.size() + " positive examples");
		
		// plug together DL-Learner components
		ComponentManager cm = ComponentManager.getInstance();
		
		SparqlKnowledgeSource ks = cm.knowledgeSource(SparqlKnowledgeSource.class);
		ks.setInstances(instances);
		ks.setPredefinedEndpoint("LOCALGEODATA");
		ks.setSaveExtractedFragment(true);
		Manipulator m = Manipulator.getDefaultManipulator();
		m.addRule(new StringToResource(Months.DECEMBER,"http://linkedgeodata.org/vocabulary", 0));
		ks.setManipulator(m);
		ks.init();
		
		AbstractReasonerComponent reasoner = cm.reasoner(FastInstanceChecker.class, ks);
		reasoner.init();
		
		PosOnlyLP lp = cm.learningProblem(PosOnlyLP.class, reasoner);
		lp.setPositiveExamples(Helper.getIndividualSet(positives));
		lp.init();
		
		CELOE celoe = cm.learningAlgorithm(CELOE.class, lp, reasoner);
		RhoDRDown op = (RhoDRDown) celoe.getOperator();
//		ROLComponent2 celoe = cm.learningAlgorithm(ROLComponent2.class, lp, reasoner);
		op.setUseAllConstructor(false);
//		celoe.getConfigurator().setUseExistsConstructor(false);
		op.setUseCardinalityRestrictions(false);
		op.setUseBooleanDatatypes(false);
		op.setUseDoubleDatatypes(false);
		op.setUseNegation(false);
		op.setUseHasValueConstructor(true);
		op.setFrequencyThreshold(3);
		celoe.setMaxExecutionTimeInSeconds(100);
		celoe.setNoisePercentage(0.2);
		celoe.init();
		
		// debugging
//		ObjectProperty place = new ObjectProperty("http://linkedgeodata.org/vocabulary#place");
//		Individual city = new Individual("http://linkedgeodata.org/vocabulary/city");
//		Individual village = new Individual("http://linkedgeodata.org/vocabulary/village");
//		Individual town = new Individual("http://linkedgeodata.org/vocabulary/town");
//		Individual suburb = new Individual("http://linkedgeodata.org/vocabulary/suburb");
//		Description vd = new ObjectValueRestriction(place, village);
//		Description vc = new ObjectValueRestriction(place, city);
//		Description vt = new ObjectValueRestriction(place, town);
//		Description vs = new ObjectValueRestriction(place, suburb);
//		Description d = new Union(vd, vt, vs);
//		EvaluatedDescriptionPosOnly ed = lp.evaluate(d);
//		System.out.println(ed);
//		System.out.println(ed.getCoveredPositives().size() + ": " + ed.getCoveredPositives());
//		System.out.println(ed.getNotCoveredPositives().size() + ": " + ed.getNotCoveredPositives());
//		System.out.println(ed.getAdditionalInstances().size() + ": " + ed.getAdditionalInstances());
		
		// execute algorithm
		celoe.start();
//		Set<EvaluatedDescriptionPosOnly> solutions = (Set<EvaluatedDescriptionPosOnly>) algorithm.getCurrentlyBestEvaluatedDescriptions();
	}
	
}
