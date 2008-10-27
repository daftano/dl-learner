<?php
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

/**
 * Abstract template class. Templates are used for displaying information
 * about a particular entity in a user friendly way.
 * 
 * You can also use this class for convenience functions accessible by
 * all concrete templates.
 *  
 * @author Jens Lehmann
 */
class AbstractTemplate {

	abstract function printTemplate($triples);
	
	// utility method, which checks whether the given DBpedia ontology properties exists in the triples
	// is they exist, the method returns true and false otherwise;
	// TODO: use $dbpediaOntologyPrefix in $settings (how do we access those settings in all scripts?)
	function areDBpediaPropertiesSet($triples, $properties) {
		foreach($properties as $property) {
			if(!isset($triples['http://dbpedia.org/ontology/'.$property])) {
				return false;
			}
		}
		return true;
	}
	
	// gets the value of the property
	function getPropValue($triples, $property) {
		return $triples['http://dbpedia.org/ontology/'.$property];
	}
	
	// gets the value of the property and removes it from the triple array
	// (this means you cannot access this information anymore afterwards)
	function extractPropValue($triples, $property) {
		$value = $triples['http://dbpedia.org/ontology/'.$property];
		unset($triples['http://dbpedia.org/ontology/'.$property]);
		return $value;
	}
	
}

?>