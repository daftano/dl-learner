package org.dllearner.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.ReasoningService;
import org.dllearner.core.owl.ClassAssertionAxiom;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.EquivalentClassesAxiom;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.Intersection;
import org.dllearner.core.owl.KB;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectExactCardinalityRestriction;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.core.owl.PropertyAxiom;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.KBFile;
import org.dllearner.parser.KBParser;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.semanticweb.owl.model.OWLOntology;

public class OntologyCloser {
	KB kb;
	KBFile kbFile;
	OWLOntology onto;
	OWLAPIReasoner reasoner;
	boolean isKB = true;

	// Set<KnowledgeSource> ks;
	ReasoningService rs;
	HashMap<Individual, Set<ObjectExactCardinalityRestriction>> indToRestr;
	HashMap<Individual, Set<Description>> indToNamedClass;
	HashSet<Description> classes;

	public OntologyCloser(KB kb) {
		super();
		this.kb = kb;
		this.kbFile = new KBFile(this.kb);
		Set<KnowledgeSource> ks = new HashSet<KnowledgeSource>();
		ks.add(this.kbFile);
		OWLAPIReasoner owlapi = new OWLAPIReasoner(ks);
		owlapi.init();
		this.indToRestr = new HashMap<Individual, Set<ObjectExactCardinalityRestriction>>();
		this.classes = new HashSet<Description>();
		this.rs = new ReasoningService(owlapi);

	}

	/**
	 * counts the number of roles used by each individual and assigns
	 * ExactCardinalityRestriction
	 */
	public void applyNumberRestrictions() {
		Set<ObjectProperty> allRoles = this.rs.getAtomicRoles();
		// Set<Individual> allind = this.rs.getIndividuals();
		testForTransitiveProperties(true);

		for (ObjectProperty oneRole : allRoles) {

			// System.out.println(oneRole.getClass());
			Map<Individual, SortedSet<Individual>> allRoleMembers = this.rs
					.getRoleMembers(oneRole);
			for (Individual oneInd : allRoleMembers.keySet()) {
				SortedSet<Individual> fillers = allRoleMembers.get(oneInd);
				if (fillers.size() > 0) {
					ObjectExactCardinalityRestriction oecr = new ObjectExactCardinalityRestriction(
							fillers.size(), oneRole, new Thing());
					kb.addABoxAxiom(new ClassAssertionAxiom(oecr, oneInd));
				}
			}

		}
		// System.out.println("good ontology? " + rs.isSatisfiable());

	}

	

	/**
	 * counts the number of roles used by each individual and assigns
	 * ExactCardinalityRestriction
	 */
	public void applyNumberRestrictionsConcise() {
		Set<ObjectProperty> allRoles = this.rs.getAtomicRoles();
		// Set<Individual> allind = this.rs.getIndividuals();
		testForTransitiveProperties(true);

		for (ObjectProperty oneRole : allRoles) {

			// System.out.println(oneRole.getClass());
			Map<Individual, SortedSet<Individual>> allRoleMembers = this.rs
					.getRoleMembers(oneRole);
			for (Individual oneInd : allRoleMembers.keySet()) {
				SortedSet<Individual> fillers = allRoleMembers.get(oneInd);
				if (fillers.size() > 0) {
					ObjectExactCardinalityRestriction oecr = new ObjectExactCardinalityRestriction(
							fillers.size(), oneRole, new Thing());
					// indToRestr.put(oneInd,)
					collectExObjRestrForInd(oneInd, oecr);
				}
			}

			//

		}// end for

		// Intersection intersection = null;
		LinkedList<Description> ll = new LinkedList<Description>();
		Set<ObjectExactCardinalityRestriction> s = null;

		for (Individual oneInd : indToRestr.keySet()) {
			s = indToRestr.get(oneInd);
			for (ObjectExactCardinalityRestriction oecr : s) {
				ll.add(oecr);
			}
			kb.addABoxAxiom(new ClassAssertionAxiom(new Intersection(ll),
					oneInd));
			s = null;
			ll = new LinkedList<Description>();
		}

		//

		// System.out.println("good ontology? " + rs.isSatisfiable());

	}
	


	/**
	 * counts the number of roles used by each individual and assigns
	 * ExactCardinalityRestriction
	 */
	public void applyNumberRestrictionsNamed() {
		Set<ObjectProperty> allRoles = this.rs.getAtomicRoles();
		// Set<Individual> allind = this.rs.getIndividuals();
		testForTransitiveProperties(true);

		for (ObjectProperty oneRole : allRoles) {

			// System.out.println(oneRole.getClass());
			Map<Individual, SortedSet<Individual>> allRoleMembers = this.rs
					.getRoleMembers(oneRole);
			for (Individual oneInd : allRoleMembers.keySet()) {
				SortedSet<Individual> fillers = allRoleMembers.get(oneInd);
				//if (fillers.size() > 0) {
					ObjectExactCardinalityRestriction oecr = new ObjectExactCardinalityRestriction(
							fillers.size(), oneRole, new Thing());
					// indToRestr.put(oneInd,)
					//make Description
					Description d = new NamedClass(oneRole+"Exact"+fillers.size()+"gen");
					d.addChild(oecr);
					kb.addTBoxAxiom(new EquivalentClassesAxiom(d,oecr));
					//System.out.println(d.toManchesterSyntaxString("", new HashMap<String, String>()));
					kb.addABoxAxiom(new ClassAssertionAxiom(d,oneInd));
					//collectExObjRestrForInd(oneInd, oecr);
				
			}

			//

		}// end for

		// Intersection intersection = null;
		/*
		LinkedList<Description> ll = new LinkedList<Description>();
		Set<ObjectExactCardinalityRestriction> s = null;

		for (Individual oneInd : indToRestr.keySet()) {
			s = indToRestr.get(oneInd);
			for (ObjectExactCardinalityRestriction oecr : s) {
				ll.add(oecr);
			}
			kb.addABoxAxiom(new ClassAssertionAxiom(new Intersection(ll),
					oneInd));
			s = null;
			ll = new LinkedList<Description>();
		}*/

		//

		// System.out.println("good ontology? " + rs.isSatisfiable());

	}

	public boolean testForTransitiveProperties(boolean printflag) {
		boolean retval = false;
		Set<PropertyAxiom> ax = kb.getRbox();
		for (PropertyAxiom propertyAxiom : ax) {
			if (propertyAxiom.getClass().getSimpleName().equals(
					"TransitiveObjectPropertyAxiom")) {
				retval = true;
				if (printflag) {
					System.out
							.println("WARNING transitive object property can't be used in cardinality restriction\n"
									+ propertyAxiom.toString());
				}
			}
		}

		return retval;
	}

	public static void closeKB(KB kb) {
		new OntologyCloser(kb).applyNumberRestrictions();
	}

	public SortedSet<Individual> verifyConcept(String conceptStr) {
		Description d;
		SimpleClock sc = new SimpleClock();
		StringBuffer sb = new StringBuffer();
		sb.append(conceptStr);
		conceptStr = sb.toString();
		SortedSet<Individual> ind = new TreeSet<Individual>();
		try {
			d = KBParser.parseConcept(conceptStr);
			System.out.println("\n*******************\nStarting retrieval");
			System.out.println(d.toManchesterSyntaxString("",
					new HashMap<String, String>()));
			// System.out.println(d.toString());
			sc.setTime();
			this.rs.retrieval(d);

			System.out.println("retrieved: " + ind.size() + " instances");
			sc.printAndSet();
			for (Individual individual : ind) {
				System.out.print(individual + "|");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ind;
	}

	private boolean collectExObjRestrForInd(Individual ind,
			ObjectExactCardinalityRestriction oecr) {
		Set<ObjectExactCardinalityRestriction> s = indToRestr.get(ind);
		if (s == null) {
			indToRestr.put(ind,
					new HashSet<ObjectExactCardinalityRestriction>());
			s = indToRestr.get(ind);
		}
		return s.add(oecr);
	}
	
	@SuppressWarnings("unused")
	private boolean collectDescriptionForInd(Individual ind,
			Description d) {
		Set<Description> s = indToNamedClass.get(ind);
		if (s == null) {
			indToNamedClass.put(ind,
					new HashSet<Description>());
			s = indToNamedClass.get(ind);
		}
		return s.add(d);
	}

}
