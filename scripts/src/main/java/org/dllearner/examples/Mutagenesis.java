/**
 * Copyright (C) 2007-2010, Jens Lehmann
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
package org.dllearner.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.dllearner.core.owl.Axiom;
import org.dllearner.core.owl.BooleanDatatypePropertyAssertion;
import org.dllearner.core.owl.ClassAssertionAxiom;
import org.dllearner.core.owl.DatatypeProperty;
import org.dllearner.core.owl.DatatypePropertyAssertion;
import org.dllearner.core.owl.DoubleDatatypePropertyAssertion;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.KB;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.ObjectPropertyAssertion;
import org.dllearner.core.owl.SubClassAxiom;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.parser.PrologParser;
import org.dllearner.prolog.Atom;
import org.dllearner.prolog.Clause;
import org.dllearner.prolog.Program;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.utilities.Files;
import org.dllearner.utilities.Helper;
import org.semanticweb.owlapi.model.IRI;

/**
 * 
 * @author Christian Koetteritzsch
 * 
 */
public class Mutagenesis {

	private static IRI ontologyIRI = IRI
			.create("http://dl-learner.org/mutagenesis");

	// directory of Prolog files
	private static final String prologDirectory = "../examples/mutagenesis/prolog/";

	// mapping of symbols to names of chemical elements
	private static Map<String, String> chemElements;

	// types of atoms, bonds, and structures
	private static Set<String> atomTypes = new TreeSet<String>();
	private static Set<String> bondTypes = new TreeSet<String>();
	private static Set<String> ringStructureGroups = new TreeSet<String>();
	private static Set<String> ringStructureTypes = new TreeSet<String>();

	// we need a counter for bonds, because they are instances in OWL
	// but not in Prolog
	private static int bondNr = 0;
	private static int structureNr = 0;

	// list of all individuals in the knowlege base
	// private static Set<String> individuals = new TreeSet<String>();
	// list of all compounds
	private static Set<String> compounds = new TreeSet<String>();
	// list of all bonds
	private static Set<String> bonds = new TreeSet<String>();
	private static List<String> positiveExamples = new LinkedList<String>();
	private static List<String> negativeExamples = new LinkedList<String>();

	// list of all "hasProperty" test

	private static boolean learnCarcinogenic = true;

	public static void main(String[] args) throws FileNotFoundException,
			IOException, ParseException {
		createChemElementsMapping();
		createRingGroups();
		String[] files = new String[] { "atom_bond.pl", "log_mutag.pl",
				"logp.pl", "lumo.pl", "ring_struc.pl", "inda.pl", "ind1.pl" };

		File owlFile = new File("../examples/mutagenesis/mutagenesis.owl");

		Program program = null;
		long startTime, duration;
		String time;

		// reading files
		System.out.print("Reading in mutagenesis Prolog files ... ");
		startTime = System.nanoTime();
		String content = "";
		for (String file : files) {
			content += Files.readFile(new File(prologDirectory + file));
		}
		duration = System.nanoTime() - startTime;
		time = Helper.prettyPrintNanoSeconds(duration, false, false);
		System.out.println("OK (" + time + ").");

		// parsing files
		System.out.print("Parsing Prolog files ... ");
		startTime = System.nanoTime();
		PrologParser pp = new PrologParser();
		program = pp.parseProgram(content);
		duration = System.nanoTime() - startTime;
		time = Helper.prettyPrintNanoSeconds(duration, false, false);
		System.out.println("OK (" + time + ").");

		// prepare mapping
		KB kb = new KB();

		// create subclasses of atom
		NamedClass atomClass = getAtomicConcept("Atom");
		for (String element : chemElements.values()) {
			NamedClass elClass = getAtomicConcept(element);
			SubClassAxiom sc = new SubClassAxiom(elClass, atomClass);
			kb.addAxiom(sc);
		}

		// define properties including domain and range
		String kbString = "DPDOMAIN(" + getURI2("charge") + ") = "
				+ getURI2("Atom") + ".\n";
		kbString += "DPRANGE(" + getURI2("charge") + ") = DOUBLE.\n";

		kbString += "DPDOMAIN(" + getURI2("hasFifeExamplesOfAcenthrylenes") + ") = "
		+ getURI2("Compound") + ".\n";
		kbString += "DPRANGE(" + getURI2("hasFifeExamplesOfAcenthrylenes") + ") = BOOLEAN.\n";
		
		kbString += "DPDOMAIN(" + getURI2("hasThreeOrMoreFusedRings") + ") = "
		+ getURI2("Compound") + ".\n";
		kbString += "DPRANGE(" + getURI2("hasThreeOrMoreFusedRings") + ") = BOOLEAN.\n";
		
		kbString += "DPDOMAIN(" + getURI2("logp") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "DPRANGE(" + getURI2("logp") + ") = DOUBLE.\n";

		kbString += "DPDOMAIN(" + getURI2("act") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "DPRANGE(" + getURI2("act") + ") = DOUBLE.\n";

		kbString += "DPDOMAIN(" + getURI2("lumo") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "DPRANGE(" + getURI2("lumo") + ") = DOUBLE.\n";
		
		kbString += "OPDOMAIN(" + getURI2("hasAtom") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "OPRANGE(" + getURI2("hasAtom") + ") = " + getURI2("Atom")
				+ ".\n";
		kbString += "OPDOMAIN(" + getURI2("hasBond") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "OPRANGE(" + getURI2("hasBond") + ") = " + getURI2("Bond")
				+ ".\n";
		kbString += "OPDOMAIN(" + getURI2("inBond") + ") = " + getURI2("Bond")
				+ ".\n";
		kbString += "OPRANGE(" + getURI2("inBond") + ") = " + getURI2("Atom")
				+ ".\n";
		kbString += "OPDOMAIN(" + getURI2("hasStructure") + ") = "
				+ getURI2("Compound") + ".\n";
		kbString += "OPRANGE(" + getURI2("inStructure") + ") = "
				+ getURI2("RingStructure") + ".\n";
		KB kb2 = KBParser.parseKBFile(kbString);
		kb.addKB(kb2);

		// mapping clauses to axioms
		System.out.print("Mapping clauses to axioms ... ");
		startTime = System.nanoTime();
		ArrayList<Clause> clauses = program.getClauses();
		for (Clause clause : clauses) {
			List<Axiom> axioms = mapClause(clause);
			for (Axiom axiom : axioms)
				kb.addAxiom(axiom);
		}
		System.out.println("OK (" + time + ").");

		// writing generated knowledge base
		System.out.print("Writing OWL file ... ");
		startTime = System.nanoTime();
		OWLAPIReasoner.exportKBToOWL(owlFile, kb, ontologyIRI);
		duration = System.nanoTime() - startTime;
		time = Helper.prettyPrintNanoSeconds(duration, false, false);
		System.out.println("OK (" + time + ").");

		// generating first conf file
		System.out.print("Generatin first conf file ... ");
		startTime = System.nanoTime();
		File confTrainFile = new File("../examples/mutagenesis/train1.conf");
		Files.clearFile(confTrainFile);
		generateConfFile(confTrainFile);
		String[] trainingFiles = new String[] { "s1.pl", "s2.pl", "s3.pl",
				"s4.pl", "s5.pl", "s6.pl", "s7.pl", "s8.pl", "s9.pl", "s10.pl" };

		for (String file : trainingFiles) {
			generatePositiveExamples(prologDirectory + "188/" + file);
		}
		appendExamples(confTrainFile, positiveExamples);
		duration = System.nanoTime() - startTime;
		time = Helper.prettyPrintNanoSeconds(duration, false, false);
		System.out.println("OK (" + time + ").");

		// generating second conf file
		System.out.print("Generatin second conf file ... ");
		File confSecondTrainFile = new File("../examples/mutagenesis/train2.conf");
		Files.clearFile(confSecondTrainFile);
		generateConfFile(confSecondTrainFile);
		positiveExamples.clear();
		negativeExamples.clear();
		generatePositiveExamples(prologDirectory + "42/all.pl");
		appendExamples(confSecondTrainFile, positiveExamples);
		duration = System.nanoTime() - startTime;
		time = Helper.prettyPrintNanoSeconds(duration, false, false);
		System.out.println("OK (" + time + ").");
		System.out.println("Finished");
	}

	private static void generateConfFile(File file) {
		String confHeader = "import(\"mutagenesis.owl\");\n\n";
		confHeader += "reasoner = fastInstanceChecker;\n";
		confHeader += "algorithm = refexamples;\n";
		confHeader += "refexamples.noisePercentage = 30;\n";
		confHeader += "refexamples.startClass = " + getURI2("Compound") + ";\n";
		confHeader += "refexamples.writeSearchTree = false;\n";
		confHeader += "refexamples.searchTreeFile = \"log/mutagenesis/searchTree.log\";\n";
		confHeader += "\n";
		Files.appendToFile(file, confHeader);
	}

	private static void generatePositiveExamples(String fileName)
			throws FileNotFoundException {
		Scanner input = new Scanner(new File(fileName), "UTF-8");
		String trainingContent = "";
		
		while (input.hasNextLine()) {
			trainingContent = input.nextLine();
			if (trainingContent.startsWith("active")) {
				int start = trainingContent.indexOf("(") + 1;
				int end = trainingContent.indexOf(")");
				String individual = trainingContent.substring(start, end);
				positiveExamples.add(individual);
			} else {
				int start = trainingContent.indexOf("(") + 1;
				int end = trainingContent.indexOf(")");
				String individual = trainingContent.substring(start, end);
				negativeExamples.add(individual);
			}
		}
	}

	private static List<Axiom> mapClause(Clause clause) throws IOException,
			ParseException {
		List<Axiom> axioms = new LinkedList<Axiom>();
		Atom head = clause.getHead();
		String headName = head.getName();
		// Body body = clause.getBody();
		// ArrayList<Literal> literals = body.getLiterals();
		// handle: atm(compound,atom,element,atomtype,charge)

		if (headName.equals("atm")) {
			String compoundName = head.getArgument(0).toPLString();
			String atomName = head.getArgument(1).toPLString();
			String elementName = head.getArgument(2).toPLString();
			String type = head.getArgument(3).toPLString();
			double charge = Double
					.parseDouble(head.getArgument(4).toPLString());
			// make the compound an instance of the Compound class
			ClassAssertionAxiom cmpAxiom = getConceptAssertion("Compound",
					compoundName);
			axioms.add(cmpAxiom);
			compounds.add(compoundName);
			// relate compound and atom
			ObjectPropertyAssertion ra = getRoleAssertion("hasAtom",
					compoundName, atomName);
			axioms.add(ra);
			// atom is made instance of the correct class
			String atomClass = getAtomClass(elementName, type);

			ClassAssertionAxiom ca = getConceptAssertion(atomClass, atomName);
			axioms.add(ca);

			// write subclass axiom if doesn't exist already
			if (!atomTypes.contains(atomClass)) {
				NamedClass subClass = getAtomicConcept(atomClass);
				NamedClass superClass = getAtomicConcept(getFullElementName(elementName));
				SubClassAxiom sc = new SubClassAxiom(subClass, superClass);
				axioms.add(sc);
				atomTypes.add(atomClass);
			}

			// charge of atom
			DatatypePropertyAssertion dpa = getDoubleDatatypePropertyAssertion(
					atomName, "charge", charge);
			axioms.add(dpa);
		} else if (headName.equals("bond")) {
			String compoundName = head.getArgument(0).toPLString();
			String atom1Name = head.getArgument(1).toPLString();
			String atom2Name = head.getArgument(2).toPLString();
			String bondType = head.getArgument(3).toPLString();
			String bondClass = "Bond-" + bondType;
			String bondInstance = "bond" + bondNr;
			bonds.add(bondInstance);
			ObjectPropertyAssertion op = getRoleAssertion("hasBond",
					compoundName, "bond" + bondNr);
			axioms.add(op);
			// make Bond-X subclass of Bond if that hasn't been done already
			if (!bondTypes.contains(bondClass)) {
				NamedClass subClass = getAtomicConcept(bondClass);
				SubClassAxiom sc = new SubClassAxiom(subClass,
						getAtomicConcept("Bond"));
				axioms.add(sc);
				bondTypes.add(bondClass);
			}
			// make e.g. bond382 instance of Bond-3
			ClassAssertionAxiom ca = getConceptAssertion(bondClass,
					bondInstance);
			axioms.add(ca);
			bondNr++;
			// connect atoms with bond
			ObjectPropertyAssertion op1 = getRoleAssertion("inBond",
					bondInstance, atom1Name);
			ObjectPropertyAssertion op2 = getRoleAssertion("inBond",
					bondInstance, atom2Name);
			axioms.add(op1);
			axioms.add(op2);
		} else if (headName.equals("act")) {

			String compoundName = head.getArgument(0).toPLString();
			double charge = Double
					.parseDouble(head.getArgument(1).toPLString());

			ClassAssertionAxiom cmpAxiom = getConceptAssertion("Compound",
					compoundName);
			axioms.add(cmpAxiom);
			DatatypePropertyAssertion dpa = getDoubleDatatypePropertyAssertion(
					compoundName, "act", charge);
			axioms.add(dpa);

		} else if (headName.equals("logp")) {

			String compoundName = head.getArgument(0).toPLString();
			double charge = Double
					.parseDouble(head.getArgument(1).toPLString());

			ClassAssertionAxiom cmpAxiom = getConceptAssertion("Compound",
					compoundName);
			axioms.add(cmpAxiom);
			DatatypePropertyAssertion dpa = getDoubleDatatypePropertyAssertion(
					compoundName, "logp", charge);
			axioms.add(dpa);

		} else if (headName.equals("lumo")) {

			String compoundName = head.getArgument(0).toPLString();
			double charge = Double
					.parseDouble(head.getArgument(1).toPLString());

			ClassAssertionAxiom cmpAxiom = getConceptAssertion("Compound",
					compoundName);
			axioms.add(cmpAxiom);
			DatatypePropertyAssertion dpa = getDoubleDatatypePropertyAssertion(
					compoundName, "lumo", charge);
			axioms.add(dpa);

		} else if (ringStructureGroups.contains(headName)) {
			String compoundName = head.getArgument(0).toPLString();
			String structureName = headName;
			// upper case first letter
			String structureClass = structureName.substring(0, 1).toUpperCase()
					+ structureName.substring(1);
			;
			String structureInstance = structureName + "-" + structureNr;

			addStructureSubclass(axioms, structureClass);

			ObjectPropertyAssertion op = getRoleAssertion("hasStructure",
					compoundName, structureInstance);
			axioms.add(op);
			ClassAssertionAxiom ca = getConceptAssertion(structureClass,
					structureInstance);
			axioms.add(ca);
			structureNr++;
		} else if (headName.equals("inda")) {
			String compoundName = head.getArgument(0).toPLString();
			double hasAcenthrylenes = Double
			.parseDouble(head.getArgument(1).toPLString());
			if(hasAcenthrylenes == 1.0) {
			BooleanDatatypePropertyAssertion lumb = getBooleanDatatypePropertyAssertion(
					compoundName, "hasFifeExamplesOfAcenthrylenes", true);
			axioms.add(lumb);
			} else {
				BooleanDatatypePropertyAssertion lumb = getBooleanDatatypePropertyAssertion(
						compoundName, "hasFifeExamplesOfAcenthrylenes", false);
				axioms.add(lumb);
			}
		} else if (headName.equals("ind1")) {
			String compoundName = head.getArgument(0).toPLString();
			double hasAcenthrylenes = Double
			.parseDouble(head.getArgument(1).toPLString());
			if(hasAcenthrylenes == 1.0) {
			BooleanDatatypePropertyAssertion lumb = getBooleanDatatypePropertyAssertion(
					compoundName, "hasThreeOrMoreFusedRings", true);
			axioms.add(lumb);
			} else {
				BooleanDatatypePropertyAssertion lumb = getBooleanDatatypePropertyAssertion(
						compoundName, "hasThreeOrMoreFusedRings", false);
				axioms.add(lumb);
			}
		} else {
			System.out.println("clause not supportet: " + headName);
		}
		return axioms;
	}

	public static void appendPosExamples(File file, List<Individual> examples) {
		StringBuffer content = new StringBuffer();
		for (Individual example : examples) {
			if (learnCarcinogenic)
				content.append("+\"" + example.toString() + "\"\n");
			else
				content.append("-\"" + example.toString() + "\"\n");
		}
		Files.appendToFile(file, content.toString());
	}

	public static void appendNegExamples(File file, List<Individual> examples) {
		StringBuffer content = new StringBuffer();
		for (Individual example : examples) {
			if (learnCarcinogenic)
				content.append("-\"" + example.toString() + "\"\n");
			else
				content.append("+\"" + example.toString() + "\"\n");
		}
		Files.appendToFile(file, content.toString());
	}

	private static String getAtomClass(String element, String atomType) {
		// return element + "-" + atomType;
		return getFullElementName(element) + "-" + atomType;
	}

	private static ClassAssertionAxiom getConceptAssertion(String concept,
			String i) {
		Individual ind = getIndividual(i);
		NamedClass c = getAtomicConcept(concept);
		return new ClassAssertionAxiom(c, ind);
	}

	private static ObjectPropertyAssertion getRoleAssertion(String role,
			String i1, String i2) {
		Individual ind1 = getIndividual(i1);
		Individual ind2 = getIndividual(i2);
		ObjectProperty ar = getRole(role);
		return new ObjectPropertyAssertion(ar, ind1, ind2);
	}

	private static DoubleDatatypePropertyAssertion getDoubleDatatypePropertyAssertion(
			String individual, String datatypeProperty, double value) {
		Individual ind = getIndividual(individual);
		DatatypeProperty dp = getDatatypeProperty(datatypeProperty);
		return new DoubleDatatypePropertyAssertion(dp, ind, value);
	}

	private static Individual getIndividual(String name) {
		return new Individual(ontologyIRI + "#" + name);
	}

	private static ObjectProperty getRole(String name) {
		return new ObjectProperty(ontologyIRI + "#" + name);
	}

	private static DatatypeProperty getDatatypeProperty(String name) {
		return new DatatypeProperty(ontologyIRI + "#" + name);
	}

	private static NamedClass getAtomicConcept(String name) {
		return new NamedClass(ontologyIRI + "#" + name);
	}

	private static String getFullElementName(String abbreviation) {
		// return corresponding element or throw an error if it
		// is not in the list
		String result = chemElements.get(abbreviation);
		if (result == null)
			throw new Error("Unknown element " + abbreviation);
		else
			return result;
	}

	// create chemical element list
	private static void createChemElementsMapping() {
		chemElements = new HashMap<String, String>();
		chemElements.put("as", "Arsenic");
		chemElements.put("ba", "Barium");
		chemElements.put("br", "Bromine");
		chemElements.put("c", "Carbon");
		chemElements.put("ca", "Calcium");
		chemElements.put("cl", "Chlorine");
		chemElements.put("cu", "Copper");
		chemElements.put("f", "Fluorine");
		chemElements.put("ga", "Gallium");
		chemElements.put("h", "Hydrogen");
		chemElements.put("hg", "Mercury");
		chemElements.put("i", "Iodine");
		chemElements.put("k", "Krypton");
		chemElements.put("mn", "Manganese");
		chemElements.put("mo", "Molybdenum");
		chemElements.put("n", "Nitrogen");
		chemElements.put("na", "Sodium");
		chemElements.put("o", "Oxygen");
		chemElements.put("p", "Phosphorus");
		chemElements.put("pb", "Lead");
		chemElements.put("s", "Sulfur");
		chemElements.put("se", "Selenium");
		chemElements.put("sn", "Tin");
		chemElements.put("te", "Tellurium");
		chemElements.put("ti", "Titanium");
		chemElements.put("v", "Vanadium");
		chemElements.put("zn", "Zinc");
	}

	private static String getURI(String name) {
		return ontologyIRI + "#" + name;
	}

	// returns URI including quotationsmark (need for KBparser)
	private static String getURI2(String name) {
		return "\"" + getURI(name) + "\"";
	}

	private static void createRingGroups() {
		String[] groups = new String[] { "benzene", "ring_size_6", "nitro",
				"phenanthrene", "ring_size_5", "ball3", "anthracene",
				"carbon_6_ring", "carbon_5_aromatic_ring",
				"hetero_aromatic_5_ring", "hetero_aromatic_6_ring", "methyl" };

		List<String> list = Arrays.asList(groups);
		ringStructureGroups.addAll(list);
	}

	private static void addStructureSubclass(List<Axiom> axioms,
			String structureClass) {
		// build in more fine-grained subclasses e.g. Di+number is subclass of
		if (!ringStructureTypes.contains(structureClass)) {
			NamedClass nc = getAtomicConcept("RingStructure");
			NamedClass subClass = getAtomicConcept(structureClass);
			SubClassAxiom sc = new SubClassAxiom(subClass, nc);
			axioms.add(sc);
			ringStructureTypes.add(structureClass);
		}
	}

	/**
	 * This method
	 * 
	 * @param file
	 * @param examples
	 */
	public static void appendExamples(File file, List<String> examples) {
		StringBuffer content = new StringBuffer();
		for (String posEx : positiveExamples) {
			content.append("+\"" + getIndividual(posEx) + "\"\n");
		}
		for (String negEx : negativeExamples) {
			content.append("-\"" + getIndividual(negEx) + "\"\n");
		}
		Files.appendToFile(file, content.toString());
	}

	private static BooleanDatatypePropertyAssertion getBooleanDatatypePropertyAssertion(
			String individual, String datatypeProperty, boolean value) {
		Individual ind = getIndividual(individual);
		DatatypeProperty dp = getDatatypeProperty(datatypeProperty);
		return new BooleanDatatypePropertyAssertion(dp, ind, value);
	}
}
