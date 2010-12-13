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

package org.dllearner.core;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectPropertyHierarchy;
import org.dllearner.core.owl.SubsumptionHierarchy;
import org.dllearner.reasoning.ReasonerType;
import org.dllearner.utilities.SortedSetTuple;

/**
 * Reasoner Interface. Lists all available reasoning methods.
 * 
 * @author Jens Lehmann
 *
 */
public interface Reasoner {

	public ReasonerType getReasonerType();
	
	// Methode, die Subsumptionhierarchie initialisiert (sollte nur einmal
	// pro erstelltem ReasoningService bzw. Reasoner aufgerufen werden)
	// => erstellt auch vereinfachte Sichten auf Subsumptionhierarchie
	// (siehe einfacher Traversal in Diplomarbeit)
	public void prepareSubsumptionHierarchy(Set<NamedClass> allowedConcepts);
	public void prepareRoleHierarchy(Set<ObjectProperty> allowedRoles) throws ReasoningMethodUnsupportedException;
	
	public boolean subsumes(Description superConcept, Description subConcept) throws ReasoningMethodUnsupportedException;
	
	// mehrere subsumption checks - spart bei DIG Anfragen (nur die zweite Methode wird gebraucht)
	public Set<Description> subsumes(Description superConcept, Set<Description> subConcepts) throws ReasoningMethodUnsupportedException;
	public Set<Description> subsumes(Set<Description> superConcepts, Description subConcept) throws ReasoningMethodUnsupportedException;	
	
	// liefert eine Menge paarweise nicht äquivalenter Konzepte zurück, die über dem Konzept in der
	// Subsumption-Hierarchie stehen
	// Methoden veraltet, da das jetzt von der SubsumptionHierarchy-Klasse geregelt wird
	// public SortedSet<Concept> getMoreGeneralConcepts(Concept concept) throws ReasoningMethodUnsupportedException;
	// public SortedSet<Concept> getMoreSpecialConcepts(Concept concept) throws ReasoningMethodUnsupportedException;
	
	public SubsumptionHierarchy getSubsumptionHierarchy() throws ReasoningMethodUnsupportedException;
	
	public ObjectPropertyHierarchy getRoleHierarchy() throws ReasoningMethodUnsupportedException;
	
	public SortedSet<Individual> retrieval(Description concept) throws ReasoningMethodUnsupportedException;
	
	public Map<Individual, SortedSet<Individual>> getRoleMembers(ObjectProperty atomicRole) throws ReasoningMethodUnsupportedException;
	
	public boolean instanceCheck(Description concept, Individual individual) throws ReasoningMethodUnsupportedException;
	
	// mehrere instance checks für ein Konzept - spart bei DIG Anfragen
	public SortedSet<Individual> instanceCheck(Description concept, Set<Individual> individuals) throws ReasoningMethodUnsupportedException;
	
	public SortedSetTuple<Individual> doubleRetrieval(Description concept) throws ReasoningMethodUnsupportedException;
	
	public SortedSetTuple<Individual> doubleRetrieval(Description concept, Description adc) throws ReasoningMethodUnsupportedException;	
	
	public boolean isSatisfiable() throws ReasoningMethodUnsupportedException;
	
	// alle Konzepte, die i als Instanz haben
	public Set<NamedClass> getConcepts(Individual i) throws ReasoningMethodUnsupportedException;
	
	public Set<NamedClass> getAtomicConcepts();

	public Set<ObjectProperty> getAtomicRoles();

	public SortedSet<Individual> getIndividuals();
}