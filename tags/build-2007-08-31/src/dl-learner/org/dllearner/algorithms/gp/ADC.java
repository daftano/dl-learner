package org.dllearner.algorithms.gp;

import org.dllearner.dl.Concept;

public class ADC extends Concept {

	/*
	@Override
	protected void calculateSets(FlatABox abox, SortedSet<String> adcPosSet, SortedSet<String> adcNegSet) {
		posSet = adcPosSet;
		negSet = adcNegSet;
	}
	*/

	public int getLength() {
		// ein ADC-Knoten hat Laenge 1, da effektiv nur ein Knoten benoetigt wird
		// um die Gesamtlaenge des gelernten Konzepts zu haben, muss man natuerlich
		// noch zusaetzliche die Laenge der ADC addieren
		return 1;
	}

	@Override
	public String toString() {
		return "ADC";
	}

	@Override
	public int getArity() {
		return 0;
	}
}