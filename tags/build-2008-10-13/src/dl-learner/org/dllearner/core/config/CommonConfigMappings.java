/**
 * Copyright (C) 2007, Jens Lehmann
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
package org.dllearner.core.config;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.utilities.owl.ConceptComparator;

/**
 * @author Jens Lehmann
 *
 */
public class CommonConfigMappings {

	private static ConceptComparator cm = new ConceptComparator();
	// private static RoleComparator rc = new RoleComparator();
	
	public static SortedSet<Individual> getIndividualSet(Set<String> individuals) {
		SortedSet<Individual> set = new TreeSet<Individual>();
		for(String individual : individuals)
			set.add(new Individual(individual));
		return set;
	}
	
	public static SortedSet<NamedClass> getAtomicConceptSet(Set<String> atomicConcepts) {
		SortedSet<NamedClass> set = new TreeSet<NamedClass>(cm);
		for(String atomicConcept : atomicConcepts)
			set.add(new NamedClass(atomicConcept));
		return set;
	}	
	
	public static SortedSet<ObjectProperty> getAtomicRoleSet(Set<String> atomicRoles) {
		SortedSet<ObjectProperty> set = new TreeSet<ObjectProperty>();
		for(String atomicRole : atomicRoles)
			set.add(new ObjectProperty(atomicRole));
		return set;
	}
}