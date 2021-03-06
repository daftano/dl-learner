package org.dllearner.algorithms.isle.metrics;

import org.dllearner.algorithms.isle.index.Index;
import org.dllearner.core.owl.Entity;

/**
 * @author Andre Melo
 *
 */
public class JaccardRelevanceMetric extends AbstractRelevanceMetric{

	public JaccardRelevanceMetric(Index index) {
		super(index);
	}

	@Override
	public double getRelevance(Entity entityA, Entity entityB) {
		long nrOfDocumentsA = index.getNumberOfDocumentsFor(entityA);
		long nrOfDocumentsB = index.getNumberOfDocumentsFor(entityB);
		
		if (nrOfDocumentsA==0 || nrOfDocumentsB==0)
			return 0;
		
		double nrOfDocumentsAandB = index.getNumberOfDocumentsFor(entityA, entityB);
		double nrOfDocumentsAorB = nrOfDocumentsA + nrOfDocumentsB - nrOfDocumentsAandB;
		
		double jaccard = nrOfDocumentsAandB / nrOfDocumentsAorB;
		
		return jaccard;
	}

	@Override
	public double getNormalizedRelevance(Entity entity1, Entity entity2) {
		return getRelevance(entity1, entity2);
	}


}
