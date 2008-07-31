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
package org.dllearner.scripts.improveWikipedia;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.dllearner.core.EvaluatedDescription;
import org.dllearner.core.owl.Individual;
import org.dllearner.kb.sparql.SPARQLTasks;
import org.dllearner.utilities.Helper;
import org.dllearner.utilities.owl.EvaluatedDescriptionComparator;

/**
 * @author Sebastian Hellmann 
 * 
 * 	The EvaluatedDescriptions from a fragment are
 *         validated against the SPARQLendpoint.
 *         There are different strategies, see the methods;
 */
public class ConceptSPARQLReEvaluator {

	private static Logger logger = Logger
			.getLogger(ConceptSPARQLReEvaluator.class);

	List<EvaluatedDescription> descToBeReevaluated;

	SPARQLTasks sparqlTasks;

	int sparqlResultLimit = 1000;

	int depthOfRDFS = 1;

	public ConceptSPARQLReEvaluator(SPARQLTasks sparqlTasks,
			List<EvaluatedDescription> descToBeReevaluated) {
		this.descToBeReevaluated = descToBeReevaluated;
		this.sparqlTasks = sparqlTasks;
	}

	public ConceptSPARQLReEvaluator(SPARQLTasks sparqlTasks,
			List<EvaluatedDescription> descToBeReevaluated, int depthOfRDFS,
			int sparqlResultLimit) {
		this(sparqlTasks, descToBeReevaluated);
		this.depthOfRDFS = depthOfRDFS;
		this.sparqlResultLimit = sparqlResultLimit;
	}


	public List<EvaluatedDescription> reevaluateConceptsByDataCoverage(
			SortedSet<String> positiveSet, int maxNrOfConcepts) {
		List<EvaluatedDescription> tmp = reevaluateConceptsByLowestRecall(positiveSet);
		List<EvaluatedDescription> returnSet = new ArrayList<EvaluatedDescription>();

		while ((!tmp.isEmpty()) && (returnSet.size() <= maxNrOfConcepts)) {
			returnSet.add(tmp.remove(0));
		}

		return returnSet;
	}
	
	/**
	 * Accuracy is calculated as correct positive classified over (correct
	 * positive classified + incorrect negative classified) "How many are
	 * correctly positive classified?" e.g. 50 individuals of a 60-individual
	 * Category (50/60)
	 * 
	 * @param positiveSet
	 * @return
	 */
	public List<EvaluatedDescription> reevaluateConceptsByDataCoverage(
			SortedSet<String> positiveSet) {

		SortedSet<EvaluatedDescription> returnSet = new TreeSet<EvaluatedDescription>(
				new EvaluatedDescriptionComparator());

		SortedSet<String> instances = new TreeSet<String>();
		SortedSet<String> PosAsPos = new TreeSet<String>();
		SortedSet<String> PosAsNeg = new TreeSet<String>();

		// NegAsPos doesnt exist, because they are supposed to be possible
		// candidates
		SortedSet<Individual> NegAsPos = new TreeSet<Individual>();
		// NegAsNeg doesnt exist, because all
		SortedSet<Individual> NegAsNeg = new TreeSet<Individual>();

		for (EvaluatedDescription ed : descToBeReevaluated) {
			instances = retrieveInstances(ed);

			// PosAsPos
			PosAsPos.addAll(positiveSet);
			PosAsPos.retainAll(instances);

			// PosAsNeg
			PosAsNeg.addAll(positiveSet);
			PosAsNeg.removeAll(PosAsPos);

			returnSet.add(new EvaluatedDescription(ed.getDescription(), Helper
					.getIndividualSet(PosAsPos), Helper
					.getIndividualSet(PosAsNeg), NegAsPos, NegAsNeg));

			PosAsPos.clear();
			PosAsNeg.clear();

		}

		return new ArrayList<EvaluatedDescription>(returnSet);

	}

	public List<EvaluatedDescription> reevaluateConceptsByLowestRecall(
			SortedSet<String> positiveSet, int maxNrOfConcepts) {
		List<EvaluatedDescription> tmp = reevaluateConceptsByLowestRecall(positiveSet);
		List<EvaluatedDescription> returnSet = new ArrayList<EvaluatedDescription>();

		while ((!tmp.isEmpty()) && (returnSet.size() <= maxNrOfConcepts)) {
			returnSet.add(tmp.remove(0));
		}

		return returnSet;
	}

	/**
	 * Accuracy is calculated as correct positive classified over all retrieved
	 * e.g. 50 correct out of 400 retrieved (50/400)
	 * 
	 * @param positiveSet
	 * @return
	 */
	public List<EvaluatedDescription> reevaluateConceptsByLowestRecall(
			SortedSet<String> positiveSet) {
		logger.info("reevaluating by lowest recall "
				+ descToBeReevaluated.size() + " concepts");
		SortedSet<EvaluatedDescription> returnSet = new TreeSet<EvaluatedDescription>(
				new EvaluatedDescriptionComparator());

		SortedSet<String> instances = new TreeSet<String>();

		SortedSet<String> PosAsPos = new TreeSet<String>();
		SortedSet<String> PosAsNeg = new TreeSet<String>();

		SortedSet<Individual> NegAsPos = new TreeSet<Individual>();

		SortedSet<Individual> NegAsNeg = new TreeSet<Individual>();

		// elements are immediately removed from the list to save memory
		while (!descToBeReevaluated.isEmpty()) {
			EvaluatedDescription ed = descToBeReevaluated.remove(0);

			instances = retrieveInstances(ed);

			// PosAsPos
			PosAsPos.addAll(positiveSet);
			PosAsPos.retainAll(instances);

			// PosAsNeg
			PosAsNeg.addAll(instances);
			PosAsNeg.removeAll(PosAsPos);

			returnSet.add(new EvaluatedDescription(ed.getDescription(), Helper
					.getIndividualSet(PosAsPos), Helper
					.getIndividualSet(PosAsNeg), NegAsPos, NegAsNeg));

			PosAsPos.clear();
			PosAsNeg.clear();

		}
		logger.info("finished reevaluating by lowest recall :"
				+ returnSet.size() + " concepts");
		return new ArrayList<EvaluatedDescription>(returnSet);

	}

	private SortedSet<String> retrieveInstances(EvaluatedDescription ed) {
		String kbsyntax = ed.getDescription().toKBSyntaxString();
		return sparqlTasks
				.retrieveInstancesForClassDescriptionIncludingSubclasses(
						kbsyntax, sparqlResultLimit, depthOfRDFS);
	}

}
