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

package org.dllearner.core.configurators;

import java.util.Set;
import org.dllearner.algorithms.DBpediaNavigationSuggestor;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.ReasoningService;

/**
* automatically generated, do not edit manually.
* run org.dllearner.scripts.ConfigJavaGenerator to update
**/
public  class DBpediaNavigationSuggestorConfigurator implements Configurator {

private boolean reinitNecessary = false;
@SuppressWarnings("unused")

private DBpediaNavigationSuggestor dBpediaNavigationSuggestor;

/**
* @param dBpediaNavigationSuggestor see DBpediaNavigationSuggestor
**/
public DBpediaNavigationSuggestorConfigurator(DBpediaNavigationSuggestor dBpediaNavigationSuggestor){
this.dBpediaNavigationSuggestor = dBpediaNavigationSuggestor;
}

/**
* @param reasoningService see reasoningService
* @param learningProblem see learningProblem
* @throws LearningProblemUnsupportedException see 
* @return DBpediaNavigationSuggestor
**/
public static DBpediaNavigationSuggestor getDBpediaNavigationSuggestor(LearningProblem learningProblem, ReasoningService reasoningService) throws LearningProblemUnsupportedException{
DBpediaNavigationSuggestor component = ComponentManager.getInstance().learningAlgorithm(DBpediaNavigationSuggestor.class, learningProblem, reasoningService);
return component;
}

/**
* writeSearchTree specifies whether to write a search tree.
* mandatory: false| reinit necessary: true
* default value: false
* @return boolean 
**/
public boolean getWriteSearchTree() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "writeSearchTree") ;
}
/**
* searchTreeFile file to use for the search tree.
* mandatory: false| reinit necessary: true
* default value: log/searchTree.txt
* @return String 
**/
public String getSearchTreeFile() {
return (String) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "searchTreeFile") ;
}
/**
* replaceSearchTree specifies whether to replace the search tree in the log file after each run or append the new search tree.
* mandatory: false| reinit necessary: true
* default value: false
* @return boolean 
**/
public boolean getReplaceSearchTree() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "replaceSearchTree") ;
}
/**
* heuristic specifiy the heuristic to use.
* mandatory: false| reinit necessary: true
* default value: lexicographic
* @return String 
**/
public String getHeuristic() {
return (String) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "heuristic") ;
}
/**
* applyAllFilter usage of equivalence ALL R.C AND ALL R.D = ALL R.(C AND D).
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getApplyAllFilter() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "applyAllFilter") ;
}
/**
* applyExistsFilter usage of equivalence EXISTS R.C OR EXISTS R.D = EXISTS R.(C OR D).
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getApplyExistsFilter() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "applyExistsFilter") ;
}
/**
* useTooWeakList try to filter out too weak concepts without sending them to the reasoner.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseTooWeakList() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useTooWeakList") ;
}
/**
* useOverlyGeneralList try to find overly general concept without sending them to the reasoner.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseOverlyGeneralList() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useOverlyGeneralList") ;
}
/**
* useShortConceptConstruction shorten concept to see whether they already exist.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseShortConceptConstruction() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useShortConceptConstruction") ;
}
/**
* horizontalExpansionFactor horizontal expansion factor (see publication for description).
* mandatory: false| reinit necessary: true
* default value: 0.6
* @return double 
**/
public double getHorizontalExpansionFactor() {
return (Double) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "horizontalExpansionFactor") ;
}
/**
* improveSubsumptionHierarchy simplify subsumption hierarchy to reduce search space (see publication for description).
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getImproveSubsumptionHierarchy() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "improveSubsumptionHierarchy") ;
}
/**
* allowedConcepts concepts the algorithm is allowed to use.
* mandatory: false| reinit necessary: true
* default value: null
* @return Set(String) 
**/
@SuppressWarnings("unchecked")
public Set<String> getAllowedConcepts() {
return (Set<String>) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "allowedConcepts") ;
}
/**
* ignoredConcepts concepts the algorithm must ignore.
* mandatory: false| reinit necessary: true
* default value: null
* @return Set(String) 
**/
@SuppressWarnings("unchecked")
public Set<String> getIgnoredConcepts() {
return (Set<String>) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "ignoredConcepts") ;
}
/**
* allowedRoles roles the algorithm is allowed to use.
* mandatory: false| reinit necessary: true
* default value: null
* @return Set(String) 
**/
@SuppressWarnings("unchecked")
public Set<String> getAllowedRoles() {
return (Set<String>) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "allowedRoles") ;
}
/**
* ignoredRoles roles the algorithm must ignore.
* mandatory: false| reinit necessary: true
* default value: null
* @return Set(String) 
**/
@SuppressWarnings("unchecked")
public Set<String> getIgnoredRoles() {
return (Set<String>) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "ignoredRoles") ;
}
/**
* useAllConstructor specifies whether the universal concept constructor is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseAllConstructor() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useAllConstructor") ;
}
/**
* useExistsConstructor specifies whether the existential concept constructor is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseExistsConstructor() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useExistsConstructor") ;
}
/**
* useCardinalityRestrictions specifies whether CardinalityRestrictions is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseCardinalityRestrictions() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useCardinalityRestrictions") ;
}
/**
* useNegation specifies whether negation is used in the learning algorothm.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseNegation() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useNegation") ;
}
/**
* useBooleanDatatypes specifies whether boolean datatypes are used in the learning algorothm.
* mandatory: false| reinit necessary: true
* default value: true
* @return boolean 
**/
public boolean getUseBooleanDatatypes() {
return (Boolean) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "useBooleanDatatypes") ;
}
/**
* maxExecutionTimeInSeconds algorithm will stop after specified seconds.
* mandatory: false| reinit necessary: true
* default value: 0
* @return int 
**/
public int getMaxExecutionTimeInSeconds() {
return (Integer) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "maxExecutionTimeInSeconds") ;
}
/**
* minExecutionTimeInSeconds algorithm will run at least specified seconds.
* mandatory: false| reinit necessary: true
* default value: 0
* @return int 
**/
public int getMinExecutionTimeInSeconds() {
return (Integer) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "minExecutionTimeInSeconds") ;
}
/**
* guaranteeXgoodDescriptions algorithm will run until X good (100%) concept descritpions are found.
* mandatory: false| reinit necessary: true
* default value: 1
* @return int 
**/
public int getGuaranteeXgoodDescriptions() {
return (Integer) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "guaranteeXgoodDescriptions") ;
}
/**
* logLevel determines the logLevel for this component, can be {TRACE, DEBUG, INFO}.
* mandatory: false| reinit necessary: true
* default value: DEBUG
* @return String 
**/
public String getLogLevel() {
return (String) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "logLevel") ;
}
/**
* noisePercentage the (approximated) percentage of noise within the examples.
* mandatory: false| reinit necessary: true
* default value: 0.0
* @return double 
**/
public double getNoisePercentage() {
return (Double) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "noisePercentage") ;
}
/**
* startClass the named class which should be used to start the algorithm (GUI: needs a widget for selecting a class).
* mandatory: false| reinit necessary: true
* default value: null
* @return String 
**/
public String getStartClass() {
return (String) ComponentManager.getInstance().getConfigOptionValue(dBpediaNavigationSuggestor,  "startClass") ;
}

/**
* @param writeSearchTree specifies whether to write a search tree.
* mandatory: false| reinit necessary: true
* default value: false
**/
public void setWriteSearchTree(boolean writeSearchTree) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "writeSearchTree", writeSearchTree);
reinitNecessary = true;
}
/**
* @param searchTreeFile file to use for the search tree.
* mandatory: false| reinit necessary: true
* default value: log/searchTree.txt
**/
public void setSearchTreeFile(String searchTreeFile) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "searchTreeFile", searchTreeFile);
reinitNecessary = true;
}
/**
* @param replaceSearchTree specifies whether to replace the search tree in the log file after each run or append the new search tree.
* mandatory: false| reinit necessary: true
* default value: false
**/
public void setReplaceSearchTree(boolean replaceSearchTree) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "replaceSearchTree", replaceSearchTree);
reinitNecessary = true;
}
/**
* @param heuristic specifiy the heuristic to use.
* mandatory: false| reinit necessary: true
* default value: lexicographic
**/
public void setHeuristic(String heuristic) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "heuristic", heuristic);
reinitNecessary = true;
}
/**
* @param applyAllFilter usage of equivalence ALL R.C AND ALL R.D = ALL R.(C AND D).
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setApplyAllFilter(boolean applyAllFilter) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "applyAllFilter", applyAllFilter);
reinitNecessary = true;
}
/**
* @param applyExistsFilter usage of equivalence EXISTS R.C OR EXISTS R.D = EXISTS R.(C OR D).
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setApplyExistsFilter(boolean applyExistsFilter) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "applyExistsFilter", applyExistsFilter);
reinitNecessary = true;
}
/**
* @param useTooWeakList try to filter out too weak concepts without sending them to the reasoner.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseTooWeakList(boolean useTooWeakList) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useTooWeakList", useTooWeakList);
reinitNecessary = true;
}
/**
* @param useOverlyGeneralList try to find overly general concept without sending them to the reasoner.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseOverlyGeneralList(boolean useOverlyGeneralList) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useOverlyGeneralList", useOverlyGeneralList);
reinitNecessary = true;
}
/**
* @param useShortConceptConstruction shorten concept to see whether they already exist.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseShortConceptConstruction(boolean useShortConceptConstruction) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useShortConceptConstruction", useShortConceptConstruction);
reinitNecessary = true;
}
/**
* @param horizontalExpansionFactor horizontal expansion factor (see publication for description).
* mandatory: false| reinit necessary: true
* default value: 0.6
**/
public void setHorizontalExpansionFactor(double horizontalExpansionFactor) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "horizontalExpansionFactor", horizontalExpansionFactor);
reinitNecessary = true;
}
/**
* @param improveSubsumptionHierarchy simplify subsumption hierarchy to reduce search space (see publication for description).
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setImproveSubsumptionHierarchy(boolean improveSubsumptionHierarchy) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "improveSubsumptionHierarchy", improveSubsumptionHierarchy);
reinitNecessary = true;
}
/**
* @param allowedConcepts concepts the algorithm is allowed to use.
* mandatory: false| reinit necessary: true
* default value: null
**/
public void setAllowedConcepts(Set<String> allowedConcepts) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "allowedConcepts", allowedConcepts);
reinitNecessary = true;
}
/**
* @param ignoredConcepts concepts the algorithm must ignore.
* mandatory: false| reinit necessary: true
* default value: null
**/
public void setIgnoredConcepts(Set<String> ignoredConcepts) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "ignoredConcepts", ignoredConcepts);
reinitNecessary = true;
}
/**
* @param allowedRoles roles the algorithm is allowed to use.
* mandatory: false| reinit necessary: true
* default value: null
**/
public void setAllowedRoles(Set<String> allowedRoles) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "allowedRoles", allowedRoles);
reinitNecessary = true;
}
/**
* @param ignoredRoles roles the algorithm must ignore.
* mandatory: false| reinit necessary: true
* default value: null
**/
public void setIgnoredRoles(Set<String> ignoredRoles) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "ignoredRoles", ignoredRoles);
reinitNecessary = true;
}
/**
* @param useAllConstructor specifies whether the universal concept constructor is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseAllConstructor(boolean useAllConstructor) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useAllConstructor", useAllConstructor);
reinitNecessary = true;
}
/**
* @param useExistsConstructor specifies whether the existential concept constructor is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseExistsConstructor(boolean useExistsConstructor) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useExistsConstructor", useExistsConstructor);
reinitNecessary = true;
}
/**
* @param useCardinalityRestrictions specifies whether CardinalityRestrictions is used in the learning algorithm.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseCardinalityRestrictions(boolean useCardinalityRestrictions) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useCardinalityRestrictions", useCardinalityRestrictions);
reinitNecessary = true;
}
/**
* @param useNegation specifies whether negation is used in the learning algorothm.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseNegation(boolean useNegation) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useNegation", useNegation);
reinitNecessary = true;
}
/**
* @param useBooleanDatatypes specifies whether boolean datatypes are used in the learning algorothm.
* mandatory: false| reinit necessary: true
* default value: true
**/
public void setUseBooleanDatatypes(boolean useBooleanDatatypes) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "useBooleanDatatypes", useBooleanDatatypes);
reinitNecessary = true;
}
/**
* @param maxExecutionTimeInSeconds algorithm will stop after specified seconds.
* mandatory: false| reinit necessary: true
* default value: 0
**/
public void setMaxExecutionTimeInSeconds(int maxExecutionTimeInSeconds) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "maxExecutionTimeInSeconds", maxExecutionTimeInSeconds);
reinitNecessary = true;
}
/**
* @param minExecutionTimeInSeconds algorithm will run at least specified seconds.
* mandatory: false| reinit necessary: true
* default value: 0
**/
public void setMinExecutionTimeInSeconds(int minExecutionTimeInSeconds) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "minExecutionTimeInSeconds", minExecutionTimeInSeconds);
reinitNecessary = true;
}
/**
* @param guaranteeXgoodDescriptions algorithm will run until X good (100%) concept descritpions are found.
* mandatory: false| reinit necessary: true
* default value: 1
**/
public void setGuaranteeXgoodDescriptions(int guaranteeXgoodDescriptions) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "guaranteeXgoodDescriptions", guaranteeXgoodDescriptions);
reinitNecessary = true;
}
/**
* @param logLevel determines the logLevel for this component, can be {TRACE, DEBUG, INFO}.
* mandatory: false| reinit necessary: true
* default value: DEBUG
**/
public void setLogLevel(String logLevel) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "logLevel", logLevel);
reinitNecessary = true;
}
/**
* @param noisePercentage the (approximated) percentage of noise within the examples.
* mandatory: false| reinit necessary: true
* default value: 0.0
**/
public void setNoisePercentage(double noisePercentage) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "noisePercentage", noisePercentage);
reinitNecessary = true;
}
/**
* @param startClass the named class which should be used to start the algorithm (GUI: needs a widget for selecting a class).
* mandatory: false| reinit necessary: true
* default value: null
**/
public void setStartClass(String startClass) {
ComponentManager.getInstance().applyConfigEntry(dBpediaNavigationSuggestor, "startClass", startClass);
reinitNecessary = true;
}

/**
* true, if this component needs reinitializsation.
* @return boolean
**/
public boolean isReinitNecessary(){
return reinitNecessary;
}


}
