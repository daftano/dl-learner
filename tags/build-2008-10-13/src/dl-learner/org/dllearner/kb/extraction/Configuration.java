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
package org.dllearner.kb.extraction;

import org.dllearner.kb.aquisitors.TupleAquisitor;
import org.dllearner.kb.manipulator.Manipulator;

/**
 * Stores all configuration settings. this class collects all configuration
 * information see the other classes, which are used as attributes here
 * 
 * @author Sebastian Hellmann
 */
public class Configuration {

	private OWLAPIOntologyCollector owlAPIOntologyCollector;

	private Manipulator manipulator;

	private TupleAquisitor tupelAquisitor;

	// the following needs to be moved to
	// class extraction algorithm or manipulator
	private boolean optimizeForDLLearner = true;

	private int recursiondepth;

	private boolean getAllSuperClasses = true;

	private boolean closeAfterRecursion = true;

	private boolean getPropertyInformation = false;

	private int breakSuperClassesAfter = 200;

	public Configuration(TupleAquisitor tupelAquisitor,
			Manipulator manipulator, int recursiondepth,
			boolean getAllSuperClasses, boolean closeAfterRecursion,
			boolean getPropertyInformation, int breakSuperClassesAfter) {

		this.tupelAquisitor = tupelAquisitor;
		this.manipulator = manipulator;
		this.recursiondepth = recursiondepth;
		this.getAllSuperClasses = getAllSuperClasses;
		this.closeAfterRecursion = closeAfterRecursion;
		this.getPropertyInformation = getPropertyInformation;
		this.breakSuperClassesAfter = breakSuperClassesAfter;
		
		this.owlAPIOntologyCollector = new OWLAPIOntologyCollector();

	}

	public Configuration(TupleAquisitor tupelAquisitor,
			Manipulator manipulator, int recursiondepth,
			boolean getAllSuperClasses, boolean closeAfterRecursion,
			boolean getPropertyInformation, int breakSuperClassesAfter,
			OWLAPIOntologyCollector owlAPIOntologyCollector) {
		this(tupelAquisitor, manipulator, recursiondepth, getAllSuperClasses,
				closeAfterRecursion, getAllSuperClasses, breakSuperClassesAfter);
		this.owlAPIOntologyCollector = owlAPIOntologyCollector;
	}

	public int getBreakSuperClassesAfter() {
		return breakSuperClassesAfter;
	}

	public boolean isCloseAfterRecursion() {
		return closeAfterRecursion;
	}

	public boolean isGetAllSuperClasses() {
		return getAllSuperClasses;
	}

	public Manipulator getManipulator() {
		return manipulator;
	}

	public int getRecursiondepth() {
		return recursiondepth;
	}

	public TupleAquisitor getTupelAquisitor() {
		return tupelAquisitor;
	}

	public boolean isOptimizeForDLLearner() {
		return optimizeForDLLearner;
	}

	public boolean isGetPropertyInformation() {
		return getPropertyInformation;
	}

	public OWLAPIOntologyCollector getOwlAPIOntologyCollector() {
		return owlAPIOntologyCollector;
	}

}