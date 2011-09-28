/**
 * Copyright (C) 2007-2011, Jens Lehmann
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
 */

package org.dllearner.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.core.AbstractAxiomLearningAlgorithm;
import org.dllearner.core.ClassExpressionLearningAlgorithm;
import org.dllearner.core.ComponentAnn;
import org.dllearner.core.EvaluatedAxiom;
import org.dllearner.core.EvaluatedDescription;
import org.dllearner.core.config.ConfigOption;
import org.dllearner.core.config.IntegerEditor;
import org.dllearner.core.config.NamedClassEditor;
import org.dllearner.core.owl.Axiom;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.DisjointClassesAxiom;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SPARQLTasks;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.learningproblems.AxiomScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Learns disjoint classes using SPARQL queries.
 * 
 * @author Lorenz Bühmann
 * @author Jens Lehmann
 *
 */
@ComponentAnn(name = "disjoint classes learner", shortName = "cldisjoint", version = 0.1)
public class DisjointClassesLearner extends AbstractAxiomLearningAlgorithm implements ClassExpressionLearningAlgorithm {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DisjointClassesLearner.class);
	
	@ConfigOption(name="classToDescribe", description="", propertyEditorClass=NamedClassEditor.class)
	private NamedClass classToDescribe;
	@ConfigOption(name="maxFetchedRows", description="The maximum number of rows fetched from the endpoint to approximate the result.", propertyEditorClass=IntegerEditor.class)
	private int maxFetchedRows = 0;
	
	private List<EvaluatedDescription> currentlyBestEvaluatedDescriptions;
	private long startTime;
	private int fetchedRows;
	
	public DisjointClassesLearner(SparqlEndpointKS ks){
		this.ks = ks;
	}
	
	public int getMaxExecutionTimeInSeconds() {
		return maxExecutionTimeInSeconds;
	}

	public void setMaxExecutionTimeInSeconds(int maxExecutionTimeInSeconds) {
		this.maxExecutionTimeInSeconds = maxExecutionTimeInSeconds;
	}

	public NamedClass getClassToDescribe() {
		return classToDescribe;
	}

	public void setClassToDescribe(NamedClass classToDescribe) {
		this.classToDescribe = classToDescribe;
	}
	
	public int getMaxFetchedRows() {
		return maxFetchedRows;
	}

	public void setMaxFetchedRows(int maxFetchedRows) {
		this.maxFetchedRows = maxFetchedRows;
	}

	@Override
	public void start() {
		logger.info("Start learning...");
		startTime = System.currentTimeMillis();
		fetchedRows = 0;
		currentlyBestEvaluatedDescriptions = new ArrayList<EvaluatedDescription>();
		
		//TODO
		
		//at first get all existing classes in knowledgebase
		Set<NamedClass> classes = new SPARQLTasks(ks.getEndpoint()).getAllClasses();
		classes.remove(classToDescribe);
		
		//get classes and how often they occur
				int limit = 1000;
				int offset = 0;
				String queryTemplate = "SELECT ?type COUNT(?s) AS ?count WHERE {?s a ?type." +
				"{SELECT ?s WHERE {?s a <%s>.} LIMIT %d OFFSET %d}" +
				"}";
				String query;
				Map<NamedClass, Integer> result = new HashMap<NamedClass, Integer>();
				NamedClass cls;
				Integer oldCnt;
				boolean repeat = true;
				
				while(!terminationCriteriaSatisfied() && repeat){
					query = String.format(queryTemplate, classToDescribe, limit, offset);
					ResultSet rs = executeSelectQuery(query);
					QuerySolution qs;
					repeat = false;
					while(rs.hasNext()){
						qs = rs.next();
						cls = new NamedClass(qs.getResource("type").getURI());
						int newCnt = qs.getLiteral("count").getInt();
						oldCnt = result.get(cls);
						if(oldCnt == null){
							oldCnt = Integer.valueOf(newCnt);
						}
						result.put(cls, oldCnt);
						qs.getLiteral("count").getInt();
						repeat = true;
					}
					if(!result.isEmpty()){
						currentlyBestEvaluatedDescriptions = buildEvaluatedClassDescriptions(result, classes);
						offset += 1000;
					}
				}
		
		logger.info("...finished in {}ms.", (System.currentTimeMillis()-startTime));
	}

	@Override
	public List<Description> getCurrentlyBestDescriptions(int nrOfDescriptions) {
		List<Description> bestDescriptions = new ArrayList<Description>();
		for(EvaluatedDescription evDesc : getCurrentlyBestEvaluatedDescriptions(nrOfDescriptions)){
			bestDescriptions.add(evDesc.getDescription());
		}
		return bestDescriptions;
	}

	@Override
	public List<? extends EvaluatedDescription> getCurrentlyBestEvaluatedDescriptions(
			int nrOfDescriptions) {
		int max = Math.min(currentlyBestEvaluatedDescriptions.size(), nrOfDescriptions);
		return currentlyBestEvaluatedDescriptions.subList(0, max);
	}
	
	@Override
	public List<Axiom> getCurrentlyBestAxioms(int nrOfAxioms) {
		List<Axiom> bestAxioms = new ArrayList<Axiom>();
		
		for(EvaluatedAxiom evAx : getCurrentlyBestEvaluatedAxioms(nrOfAxioms)){
			bestAxioms.add(evAx.getAxiom());
		}
		
		return bestAxioms;
	}

	@Override
	public List<EvaluatedAxiom> getCurrentlyBestEvaluatedAxioms() {
		return getCurrentlyBestEvaluatedAxioms(currentlyBestEvaluatedDescriptions.size());
	}	
	
	@Override
	public List<EvaluatedAxiom> getCurrentlyBestEvaluatedAxioms(int nrOfAxioms) {
		List<EvaluatedAxiom> axioms = new ArrayList<EvaluatedAxiom>();
		List<Description> descriptions;
		for(EvaluatedDescription ed : getCurrentlyBestEvaluatedDescriptions(nrOfAxioms)){
			descriptions = new ArrayList<Description>();
			descriptions.add(classToDescribe);
			descriptions.add(ed.getDescription());
			axioms.add(new EvaluatedAxiom(new DisjointClassesAxiom(descriptions), new AxiomScore(ed.getAccuracy())));
		}
		return axioms;
	}
	
	private List<EvaluatedDescription> buildEvaluatedClassDescriptions(Map<NamedClass, Integer> class2Count, Set<NamedClass> allClasses){
		List<EvaluatedDescription> evalDescs = new ArrayList<EvaluatedDescription>();
		
		//Remove temporarily classToDescribe but keep track of their count
				Integer all = class2Count.get(classToDescribe);
				class2Count.remove(classToDescribe);
		
		//get complete disjoint classes
		Set<NamedClass> completeDisjointclasses = new TreeSet<NamedClass>(allClasses);
		completeDisjointclasses.removeAll(class2Count.keySet());
		
		EvaluatedDescription evalDesc;
		//firstly, create disjoint classexpressions which not occur and give score of 1
		if(reasoner.isPrepared()){
			SortedSet<Description> mostGeneralClasses = reasoner.getClassHierarchy().getMostGeneralClasses();
		}
		for(NamedClass cls : completeDisjointclasses){
			evalDesc = new EvaluatedDescription(cls, new AxiomScore(1));
			evalDescs.add(evalDesc);
		}
		
		//secondly, create disjoint classexpressions with score 1 - (#occurence/#all)
		for(Entry<NamedClass, Integer> entry : sortByValues(class2Count)){
			evalDesc = new EvaluatedDescription(entry.getKey(),
					new AxiomScore(1 - (entry.getValue() / (double)all)));
			evalDescs.add(evalDesc);
		}
		
		class2Count.put(classToDescribe, all);
		return evalDescs;
	}
	
	private boolean terminationCriteriaSatisfied(){
		boolean timeLimitExceeded = maxExecutionTimeInSeconds == 0 ? false : (System.currentTimeMillis() - startTime) >= maxExecutionTimeInSeconds * 1000;
		boolean resultLimitExceeded = maxFetchedRows == 0 ? false : fetchedRows >= maxFetchedRows;
		return  timeLimitExceeded || resultLimitExceeded; 
	}
	
	public static void main(String[] args) throws Exception{
		DisjointClassesLearner l = new DisjointClassesLearner(new SparqlEndpointKS(SparqlEndpoint.getEndpointDBpedia()));
		l.setClassToDescribe(new NamedClass("http://dbpedia.org/ontology/SoccerClub"));
		l.init();
		l.start();
		
		for(EvaluatedAxiom e : l.getCurrentlyBestEvaluatedAxioms(50)){
			System.out.println(e);
		}
		
	}


}