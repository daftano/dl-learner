/**
 * Copyright (C) 2007-2011, Jens Lehmann
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

package org.dllearner.core.owl;

import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * An object property expression is an object property construct, which
 * can be used in axioms, e.g. complex class descriptions. It can be
 * either an object property or an inverse of an object property.
 * 
 * @author Jens Lehmann
 *
 */
public abstract class ObjectPropertyExpression implements PropertyExpression, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5590591991241290070L;
	protected String name;
	
	public ObjectPropertyExpression(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	 /**
     * If the property is a named object property then this method will obtain
     * the property as such. The general pattern of use is that the
     * {@code isAnonymous} method should first be used to determine if the
     * property is named (i.e. not an object property expression such as
     * inv(p)). If the property is named then this method may be used to obtain
     * the property as a named property without casting.
     * 
     * @return The property as an {@code ObjectProperty} if possible.
     * @throws OWLRuntimeException
     *         if the property is not a named property.
     */
	public abstract ObjectProperty asObjectProperty();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectPropertyExpression other = (ObjectPropertyExpression) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
