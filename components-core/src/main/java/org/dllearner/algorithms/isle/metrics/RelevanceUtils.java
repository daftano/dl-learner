/**
 * 
 */
package org.dllearner.algorithms.isle.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.dllearner.core.owl.Entity;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Lorenz Buehmann
 *
 */
public class RelevanceUtils {
	
	private static final Logger logger = Logger.getLogger(RelevanceUtils.class.getName());
	static int maxNrOfThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
	static boolean normalize = true;
	
	/**
	 * Returns a map containing the relevance score based on the given metric between the entity and each other entity.
	 * @param entity
	 * @param otherEntities
	 * @param metric
	 * @return
	 */
	public static synchronized Map<Entity, Double> getRelevantEntities(final Entity entity, Set<Entity> otherEntities, final RelevanceMetric metric){
		logger.info("Get relevant entities for " + entity);
		final Map<Entity, Double> relevantEntities = Collections.synchronizedMap(new HashMap<Entity, Double>());
		
		ExecutorService executor = Executors.newFixedThreadPool(maxNrOfThreads);
		
		for (final Entity otherEntity : otherEntities) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
//						double relevance = metric.getNormalizedRelevance(entity, otherEntity);
						double relevance = metric.getRelevance(entity, otherEntity);
//						logger.info(otherEntity + ":" + relevance);
						relevantEntities.put(otherEntity, relevance);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		executor.shutdown();
        try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//normalize the values
        if(normalize){
        	return AbstractRelevanceMetric.normalizeMinMax(relevantEntities);
        }
		return relevantEntities;
	}
	
	public static Map<Entity, Double> getRelevantEntities(Entity entity, OWLOntology ontology, RelevanceMetric metric){
		Set<OWLEntity> owlEntities = new TreeSet<OWLEntity>();
		owlEntities.addAll(ontology.getClassesInSignature());
		owlEntities.addAll(ontology.getDataPropertiesInSignature());
		owlEntities.addAll(ontology.getObjectPropertiesInSignature());
		
		Set<Entity> otherEntities = OWLAPIConverter.getEntities(owlEntities);
//		Set<Entity> otherEntities = OWLAPIConverter.getEntities(new HashSet<OWLEntity>(new ArrayList<OWLEntity>(owlEntities).subList(0, 20)));
		otherEntities.remove(entity);
		
		return getRelevantEntities(entity, otherEntities, metric);
	}

}
