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
package org.dllearner.core.owl;

import java.net.URI;
import java.util.Map;

import org.dllearner.utilities.Helper;

/**
 * Represents an atomic concept in a knowledge base / ontology, 
 * e.g. "car", "person".
 * 
 * @author Jens Lehmann
 *
 */
public class NamedClass extends Description implements Entity, NamedKBElement, Comparable<NamedClass> {

    private String name;
    
	public NamedClass(String name) {
        this.name = name;
    }    
    
	public NamedClass(URI uri) {
        this.name = uri.toString();
    }
	
    public String getName() {
		return name;
	}
    
	public int getLength() {
		return 1;
	}

	@Override
	public int getArity() {
		return 0;
	}

    public String toString(String baseURI, Map<String,String> prefixes) {
    	return Helper.getAbbreviatedString(name, baseURI, prefixes);
    }
    
    public String toKBSyntaxString(String baseURI, Map<String,String> prefixes) {
    	return "\"" + Helper.getAbbreviatedString(name, baseURI, prefixes) + "\"";
    }
	
	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Description#accept(org.dllearner.core.owl.DescriptionVisitor)
	 */
	@Override
	public void accept(DescriptionVisitor visitor) {
		visitor.visit(this);
	}   
	
	public void accept(KBElementVisitor visitor) {
		visitor.visit(this);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Description#toManchesterSyntaxString(java.lang.String, java.util.Map)
	 */
	@Override
	public String toManchesterSyntaxString(String baseURI, Map<String, String> prefixes) {
		return Helper.getAbbreviatedString(name, baseURI, prefixes);
	}	
	
	public int compareTo(NamedClass o) {
		return name.compareTo(o.name);
	}	
	
	@Override
	public boolean equals(Object nc) {
		// standard equals code - always return true for object identity and
		// false if classes differ
		if(nc == this) {
			return true;
		} else if(getClass() != nc.getClass()) {
			return false;
		}
		// compare on URIs
		return ((NamedClass)nc).name.equals(name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}