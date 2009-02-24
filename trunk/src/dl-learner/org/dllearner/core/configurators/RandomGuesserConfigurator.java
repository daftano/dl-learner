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

import org.dllearner.algorithms.RandomGuesser;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.ReasonerComponent;

/**
* automatically generated, do not edit manually.
* run org.dllearner.scripts.ConfigJavaGenerator to update
**/
public  class RandomGuesserConfigurator  implements Configurator {

private boolean reinitNecessary = false;
@SuppressWarnings("all")

private RandomGuesser randomGuesser;

/**
* @param randomGuesser see RandomGuesser
**/
public RandomGuesserConfigurator(RandomGuesser randomGuesser){
this.randomGuesser = randomGuesser;
}

/**
* @param reasoningService see reasoningService
* @param learningProblem see learningProblem
* @throws LearningProblemUnsupportedException see 
* @return RandomGuesser
**/
public static RandomGuesser getRandomGuesser(LearningProblem learningProblem, ReasonerComponent reasoningService) throws LearningProblemUnsupportedException{
RandomGuesser component = ComponentManager.getInstance().learningAlgorithm(RandomGuesser.class, learningProblem, reasoningService);
return component;
}

/**
* numberOfTrees number of randomly generated concepts/trees.
* mandatory: false| reinit necessary: true
* default value: 5
* @return int 
**/
public int getNumberOfTrees() {
return (Integer) ComponentManager.getInstance().getConfigOptionValue(randomGuesser,  "numberOfTrees") ;
}
/**
* maxDepth maximum depth of generated concepts/trees.
* mandatory: false| reinit necessary: true
* default value: 5
* @return int 
**/
public int getMaxDepth() {
return (Integer) ComponentManager.getInstance().getConfigOptionValue(randomGuesser,  "maxDepth") ;
}

/**
* @param numberOfTrees number of randomly generated concepts/trees.
* mandatory: false| reinit necessary: true
* default value: 5
**/
public void setNumberOfTrees(int numberOfTrees) {
ComponentManager.getInstance().applyConfigEntry(randomGuesser, "numberOfTrees", numberOfTrees);
reinitNecessary = true;
}
/**
* @param maxDepth maximum depth of generated concepts/trees.
* mandatory: false| reinit necessary: true
* default value: 5
**/
public void setMaxDepth(int maxDepth) {
ComponentManager.getInstance().applyConfigEntry(randomGuesser, "maxDepth", maxDepth);
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
