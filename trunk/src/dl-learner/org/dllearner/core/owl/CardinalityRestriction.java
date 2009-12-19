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
package org.dllearner.core.owl;

/**
 * @author Jens Lehmann
 *
 */
public abstract class CardinalityRestriction extends Restriction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2405514686030313790L;
	int cardinality;
	PropertyRange propertyRange;
	
	public CardinalityRestriction(PropertyExpression propertyExpression, PropertyRange propertyRange, int cardinality) {
		super(propertyExpression);
		this.propertyRange = propertyRange;
		this.cardinality = cardinality;
	}

	/**
	 * @return the cardinality
	 */
	public int getCardinality() {
		return cardinality;
	}

	/**
	 * @return the propertyRange
	 */
	public PropertyRange getPropertyRange() {
		return propertyRange;
	}

}
