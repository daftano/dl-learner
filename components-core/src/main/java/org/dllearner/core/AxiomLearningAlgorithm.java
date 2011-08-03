package org.dllearner.core;

import java.util.List;

import org.dllearner.core.owl.Axiom;

public interface AxiomLearningAlgorithm extends LearningAlgorithm {
	
	/**
	 * @param nrOfAxioms Limit for the number or returned axioms.
	 * @return The best axioms found by the learning algorithm so far.
	 */
	public List<Axiom> getCurrentlyBestAxioms(int nrOfAxioms);
	
	/**
	 * @param nrOfAxioms Limit for the number or returned evaluated axioms.
	 * @return The best evaluated axioms found by the learning algorithm so far.
	 */
	public List<EvaluatedAxiom> getCurrentlyBestEvaluatedAxioms(int nrOfAxioms);

}
