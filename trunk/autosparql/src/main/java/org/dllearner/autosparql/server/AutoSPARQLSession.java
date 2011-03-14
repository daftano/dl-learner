package org.dllearner.autosparql.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.dllearner.autosparql.client.exception.AutoSPARQLException;
import org.dllearner.autosparql.client.exception.SPARQLQueryException;
import org.dllearner.autosparql.client.model.Example;
import org.dllearner.autosparql.server.search.Search;
import org.dllearner.autosparql.server.search.SolrSearch;
import org.dllearner.autosparql.server.util.SPARQLEndpointEx;
import org.dllearner.kb.sparql.ExtractionDBCache;
import org.dllearner.kb.sparql.SparqlQuery;
import org.dllearner.sparqlquerygenerator.util.ExactMatchFilter;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.simba.ner.QueryProcessor;

public class AutoSPARQLSession {
	
	private static final Logger logger = Logger.getLogger(AutoSPARQLSession.class);
	
	private SPARQLEndpointEx endpoint;
	
	private SPARQLSearch search;
	private ExtractionDBCache constructCache;
	private ExtractionDBCache selectCache;
	private ExampleFinder exampleFinder;
	private Search nlpSearch;
	
	private String servletContextPath;
	
	
	public AutoSPARQLSession(SPARQLEndpointEx endpoint, String cacheDir, String servletContextPath, String solrURL){
		this.endpoint = endpoint;
		this.servletContextPath = servletContextPath;
		
		constructCache = new ExtractionDBCache(cacheDir + "/" + endpoint.getPrefix() + "/construct-cache");
		selectCache = new ExtractionDBCache(cacheDir + "/" + endpoint.getPrefix() + "/select-cache");
		search = new SPARQLSearch(selectCache, servletContextPath);
		nlpSearch = new SolrSearch(solrURL);
		exampleFinder = new ExampleFinder(endpoint, selectCache, constructCache);
	}
	
	public void setQuestion(String question){
		exampleFinder.setQuestion(question);
	}
	
	public PagingLoadResult<Example> getSearchResult(String searchTerm, PagingLoadConfig config) throws AutoSPARQLException{
		try {
			int limit = config.getLimit();
			int offset = config.getOffset();
			
			List<Example> searchResult = nlpSearch.getExamples(searchTerm, offset);
			int totalLength = nlpSearch.getTotalHits(searchTerm);
			
			PagingLoadResult<Example> result = new BasePagingLoadResult<Example>(searchResult);
			result.setOffset(offset);
			result.setTotalLength(totalLength);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AutoSPARQLException(e);
		}
	}
	
	public PagingLoadResult<Example> getQueryResult(String query,
			PagingLoadConfig config) throws AutoSPARQLException {
		try {
			int limit = config.getLimit();
			int offset = config.getOffset();
			
			List<Example> searchResult = nlpSearch.getExamples(query, offset);
			int totalLength = nlpSearch.getTotalHits(query);
			
			PagingLoadResult<Example> result = new BasePagingLoadResult<Example>(searchResult);
			result.setOffset(offset);
			result.setTotalLength(totalLength);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AutoSPARQLException(e);
		}
	}

	public Example getNextQueryResult(String query)
			throws AutoSPARQLException {System.out.println("Getting next resource for query " + query);
			return nlpSearch.getExamples(query).get(0);
//		Example result = search.getNextQueryResult(query, endpoint);
//		exampleFinder.setObjectFilter(new ExactMatchFilter(new HashSet<String>(search.getAllQueryRelatedResources())));
//		return result;
	}
	
	public Example getSimilarExample(List<String> posExamples,
			List<String> negExamples) throws SPARQLQueryException{
		logger.info("Retrieving similiar example");
		logger.info("Pos examples: " + posExamples);
		logger.info("Neg examples: " + negExamples);
		
		try {
			Example example = exampleFinder.findSimilarExample(posExamples, negExamples);
			return example;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new SPARQLQueryException(exampleFinder.getCurrentQueryHTML());
		}
	}

	public PagingLoadResult<Example> getCurrentQueryResult(
			PagingLoadConfig config) throws SPARQLQueryException {
		logger.info("Retrieving results for current query.");
		List<Example> queryResult = new ArrayList<Example>();
		
		String currentQuery = exampleFinder.getCurrentQuery();
		logger.info("Current query:\n");
		logger.info(currentQuery);
		int limit = config.getLimit();
		int offset = config.getOffset();
		int totalLength = 10;
		
		try {
			ResultSetRewindable rs = SparqlQuery.convertJSONtoResultSet(selectCache.executeSelectQuery(endpoint, getCountQuery(currentQuery)));
			totalLength = rs.next().getLiteral(rs.getResultVars().get(0)).getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ResultSetRewindable rs = SparqlQuery.convertJSONtoResultSet(selectCache.executeSelectQuery(endpoint, modifyQuery(currentQuery + " OFFSET " + offset)));
			
			String uri;
			String label = "";
			String imageURL = "";
			String comment = "";
			QuerySolution qs;
			while(rs.hasNext()){
				qs = rs.next();
				uri = qs.getResource("x0").getURI();
				label = qs.getLiteral("label").getLexicalForm();
				queryResult.add(new Example(uri, label, imageURL, comment));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PagingLoadResult<Example> result = new BasePagingLoadResult<Example>(queryResult);
		result.setOffset(offset);
		result.setTotalLength(totalLength);
		
		return result;
	}
	
	public String getCurrentQuery() throws AutoSPARQLException {
		try{
			return exampleFinder.getCurrentQueryHTML();
		} catch (Exception e){
			logger.error(e);
			throw new AutoSPARQLException(e);
		}
	}
	
	
	private String modifyQuery(String query){
		String newQuery = query.replace("SELECT ?x0 WHERE {", 
				"SELECT DISTINCT(?x0) ?label WHERE{\n?x0 <" + RDFS.label + "> ?label.FILTER(LANGMATCHES(LANG(?label), 'en'))");
		
		return newQuery;
	}
	
	private String getCountQuery(String query){
		String newQuery = query.replace("SELECT ?x0", 
				"SELECT COUNT(DISTINCT ?x0)");
		newQuery = newQuery.substring(0, newQuery.indexOf('}') + 1);
		
		return newQuery;
	}
}
