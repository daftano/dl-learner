/**
 * Copyright (C) 2007-2012, Jens Lehmann
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
 */
package org.dllearner.refinementoperators;

import java.util.List;
import java.util.Set;

import org.dllearner.core.owl.Description;

/**
 * A refinement operator for which the syntactic length of the generated
 * refinements can be limited.
 * 
 * @author Jens Lehmann
 *
 */
public interface LengthLimitedRefinementOperator extends RefinementOperator {

	/**
	 * Optional refinement operation, where the learning algorithm can
	 * specify an additional bound on the length of descriptions. 
	 * 
	 * @param description The description, which will be refined.
	 * @param maxLength The maximum length of returned description, where length is defined by {@link Description#getLength()}.
	 * @return A set of refinements obeying the above restrictions.
	 */
	public Set<Description> refine(Description description, int maxLength);
		
	/**
	 * Optional refinement operation, where the learning algorithm can
	 * specify an additional bound on the length of descriptions and
	 * a list of known refinements, which do not need to be returned. 
	 * 
	 * @param description The description, which will be refined.
	 * @param maxLength The maximum length of returned description, where length is defined by {@link Description#getLength()}.
	 * @param knownRefinements A collection of known refinements, which do not need to be returned. 
	 * @return A set of refinements obeying the above restrictions.
	 */
	public Set<Description> refine(Description description, int maxLength, List<Description> knownRefinements);
		
}
