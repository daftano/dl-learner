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
package org.dllearner.algorithms.refexamples;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.algorithms.refinement.RefinementOperator;
import org.dllearner.algorithms.refinement.RhoDown;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.ReasoningService;
import org.dllearner.core.Score;
import org.dllearner.core.dl.AtomicConcept;
import org.dllearner.core.dl.AtomicRole;
import org.dllearner.core.dl.Concept;
import org.dllearner.core.dl.MultiConjunction;
import org.dllearner.core.dl.MultiDisjunction;
import org.dllearner.core.dl.Top;
import org.dllearner.learningproblems.PosNegLP;
import org.dllearner.learningproblems.PosOnlyDefinitionLP;
import org.dllearner.utilities.ConceptComparator;
import org.dllearner.utilities.ConceptTransformation;
import org.dllearner.utilities.Files;
import org.dllearner.utilities.Helper;

/**
 * Implements the example based refinement operator learning
 * approach.
 * 
 * @author Jens Lehmann
 *
 */
public class ExampleBasedROLearner {

	// all configuration options, which were passed to the
	// learning algorithms
	private boolean writeSearchTree;
	private File searchTreeFile;
	private boolean replaceSearchTree = false;
	Set<AtomicConcept> allowedConcepts;
	Set<AtomicRole> allowedRoles;
	Set<AtomicConcept> ignoredConcepts;
	Set<AtomicRole> ignoredRoles;
	// these are computed as the result of the previous four settings
	Set<AtomicConcept> usedConcepts;
	Set<AtomicRole> usedRoles;	

	private boolean useTooWeakList = true;
	private boolean useOverlyGeneralList = true;
	private boolean useShortConceptConstruction = true;
	private double horizontalExpansionFactor = 0.6;

	
	private boolean quiet = false;
	
	private boolean stop = false;
	
	private ReasoningService rs;
	
	private PosNegLP learningProblem;
	private PosOnlyDefinitionLP posOnlyLearningProblem;
	private boolean posOnly = false;
	
	// non-configuration variables	

	// Lösungen protokollieren
	boolean solutionFound = false;
	List<Concept> solutions = new LinkedList<Concept>();	
	
	// verwendeter Refinement-Operator (momentan werden für Statistik RhoDown-spezifische
	// Sachen abgefragt)
	// RefinementOperator operator;
	RhoDown operator;
	
	// Variablen zur Einstellung der Protokollierung
	// boolean quiet = false;
	boolean showBenchmarkInformation = false;
	// boolean createTreeString = false;
	// String searchTree = new String();

	// Konfiguration des Algorithmus
	// Faktor für horizontale Erweiterung (notwendig für completeness)
	// double horizontalExpansionFactor = 0.6;	

	
	
	private Comparator<ExampleBasedNode> nodeComparator;
	private NodeComparatorStable nodeComparatorStable = new NodeComparatorStable();
	private ConceptComparator conceptComparator = new ConceptComparator();
	DecimalFormat df = new DecimalFormat();		
	
	// Menge von Kandidaten für Refinement
	// (wird für Direktzugriff auf Baumknoten verwendet)
	private TreeSet<ExampleBasedNode> candidates;
	// während eines Durchlaufs neu gefundene Knoten
	private List<ExampleBasedNode> newCandidates = new LinkedList<ExampleBasedNode>();
	// stabiles candidate set, da sich die Knoten nach dem einfügen nicht
	// verschieben können => das Set enthält nicht die aktuellen horizontal
	// expansions, es dient nur dazu die besten Konzepte zu speichern; hat also
	// keine Funktion im Algorithmus
	private TreeSet<ExampleBasedNode> candidatesStable = new TreeSet<ExampleBasedNode>(nodeComparatorStable);
	// vorhandene Konzepte, die irgendwann als proper eingestuft worden
	private SortedSet<Concept> properRefinements = new TreeSet<Concept>(conceptComparator);
	// speichert Konzept und deren Evaluierung, um sie leicht wiederzufinden für
	// Strategien wie Konzeptverkürzung etc.
	// Zahl = covered negatives, -1 = too weak
	// private Map<Concept, Integer> evaluationCache = new TreeMap<Concept, Integer>(conceptComparator);
	// Blacklists
	private SortedSet<Concept> tooWeakList = new TreeSet<Concept>(conceptComparator);
	private SortedSet<Concept> overlyGeneralList = new TreeSet<Concept>(conceptComparator);
		
	TreeSet<ExampleBasedNode> expandedNodes = new TreeSet<ExampleBasedNode>(nodeComparatorStable);
	
	// statistische Variablen
	private int maxRecDepth = 0;
	private int maxNrOfRefinements = 0;
	private int maxNrOfChildren = 0;
	private int redundantConcepts = 0;
	int maximumHorizontalExpansion;
	int minimumHorizontalExpansion;
	// private int propernessTests = 0;
	private int propernessTestsReasoner = 0;
	private int propernessTestsAvoidedByShortConceptConstruction = 0;
	private int propernessTestsAvoidedByTooWeakList = 0;
	private int conceptTestsTooWeakList = 0;
	private int conceptTestsOverlyGeneralList = 0;
	private int conceptTestsReasoner = 0;
	
	// Zeitvariablen
	private long algorithmStartTime;
	private long propernessCalcTimeNs = 0;
	private long propernessCalcReasoningTimeNs = 0;	
	private long childConceptsDeletionTimeNs = 0;
	private long refinementCalcTimeNs = 0;
	private long redundancyCheckTimeNs = 0;
	private long evaluateSetCreationTimeNs = 0;
	private long improperConceptsRemovalTimeNs = 0;
	long someTimeNs = 0;
	int someCount = 0;	
	
	public ExampleBasedROLearner(
			LearningProblem learningProblem,
			RefinementOperator operator, 
			ExampleBasedHeuristic heuristic,
			Set<AtomicConcept> allowedConcepts,
			Set<AtomicRole> allowedRoles, 
			boolean writeSearchTree, 
			boolean replaceSearchTree, 
			File searchTreeFile, 
			boolean useTooWeakList, 
			boolean useOverlyGeneralList, 
			boolean useShortConceptConstruction
	) {	
		if(learningProblem instanceof PosNegLP) {
			this.learningProblem = (PosNegLP) learningProblem;
			posOnly = false;
		} else if(learningProblem instanceof PosOnlyDefinitionLP) {
			this.posOnlyLearningProblem = (PosOnlyDefinitionLP) learningProblem;
			posOnly = true;
		}
		
		// candidate sets entsprechend der gewählten Heuristik initialisieren
		candidates = new TreeSet<ExampleBasedNode>(nodeComparator);
		// newCandidates = new TreeSet<Node>(nodeComparator);
	}
	
	public void start() {
		// Suche wird mit Top-Konzept gestartet
		Top top = new Top();
		ExampleBasedNode topNode = new ExampleBasedNode(top);
		// int coveredNegativeExamples = learningProblem.coveredNegativeExamplesOrTooWeak(top);
		// aus Top folgen immer alle negativen Beispiele, d.h. es ist nur eine Lösung, wenn
		// es keine negativen Beispiele gibt
		int coveredNegativeExamples = getNumberOfNegatives();
		topNode.setCoveredNegativeExamples(coveredNegativeExamples);
		// topNode.setHorizontalExpansion(1); // die 0 ist eigentlich richtig, da keine Refinements
		// der Länge 1 untersucht wurden
		candidates.add(topNode);
		candidatesStable.add(topNode);
		// Abbruchvariable => beachten, dass bereits TOP eine Lösung sein kann
		solutionFound = (coveredNegativeExamples == 0);
		solutions = new LinkedList<Concept>();
		if(solutionFound)
			solutions.add(top);
		
		int loop = 0;

		// Voreinstellung für horizontal expansion
		maximumHorizontalExpansion = 0;
		minimumHorizontalExpansion = 0;		
		
		algorithmStartTime = System.nanoTime();
		
		// TODO: effizienter Traversal der Subsumption-Hierarchie
		// TODO: Äquivalenzen nutzen
		// TODO: Gibt es auch eine andere Abbruchbedingung? Es könnte sein, dass irgendwann keine
		// proper refinements mehr gefunden werden, aber wie stelle man das fest?
		while(!solutionFound && !stop) {
			
			if(!quiet) 
				printStatistics(false);			
			
			// besten Knoten nach Heuristik auswählen
			ExampleBasedNode bestNode = candidates.last();
			// besten Knoten erweitern			
			// newCandidates = new TreeSet<Node>(nodeComparator);	
			newCandidates.clear();
			candidates.remove(bestNode);
			extendNodeProper(bestNode, bestNode.getHorizontalExpansion()+1);
			candidates.add(bestNode);
			candidates.addAll(newCandidates);
			candidatesStable.addAll(newCandidates);			
			
			// minimum horizontal expansion berechnen
			if(bestNode.getHorizontalExpansion()>maximumHorizontalExpansion)
				maximumHorizontalExpansion = bestNode.getHorizontalExpansion();
			minimumHorizontalExpansion = (int) Math.floor(horizontalExpansionFactor*maximumHorizontalExpansion);
			
			// neu: es werden solange Knoten erweitert bis wirklich jeder Knoten die
			// notwendige minimum horizontal expansion hat
			boolean nodesExpanded;
			do {
				nodesExpanded = false;
				

				// es darf nicht candidatesStable geklont werden, da diese Menge nicht
				// aktualisiert wird, also die falschen horizontal expansions vorliegen
				// TODO: bei Tests war die Performance der clone-Operation ganz gut, aber
				// es skaliert natürlich nicht so gut mit größer werdenden candidate set
				// => Lösung ist vielleicht einfach einen Iterator zu verwenden und das
				// aktuelle Konzept gleich hier zu löschen (wird dann bei expansion wieder
				// hinzugefügt)
				// TreeSet<Node> candidatesClone = (TreeSet<Node>) candidates.clone();
				newCandidates.clear();


				// for(Node candidate : candidatesClone) {
				Iterator<ExampleBasedNode> it = candidates.iterator();
				List<ExampleBasedNode> changedNodes = new LinkedList<ExampleBasedNode>();
				 while(it.hasNext()){
					ExampleBasedNode candidate = it.next();
					// alle Kandidaten, die nicht too weak sind und unter minimumHorizontalExpansion
					// liegen, werden erweitert
					if(!candidate.isTooWeak() && candidate.getHorizontalExpansion()<minimumHorizontalExpansion) {
						// Vorsicht, falls candidates irgendwann in extendProper benutzt
						// werden sollten! Es könnten auf diese Weise Knoten fehlen 
						// (momentan wird candidates nur zur Auswahl des besten Knotens
						// benutzt).
						it.remove();
						
						extendNodeProper(candidate, minimumHorizontalExpansion);
						nodesExpanded = true;

						changedNodes.add(candidate);
					}
				}
				 
				long someTimeStart = System.nanoTime();
				someCount++;				 
				// geänderte temporär entfernte Knoten wieder hinzufügen
				candidates.addAll(changedNodes);
				// neu gefundene Knoten hinzufügen
				candidates.addAll(newCandidates);
				candidatesStable.addAll(newCandidates);
				someTimeNs += System.nanoTime() - someTimeStart;

			} while(nodesExpanded && !stop);
			
			//System.out.println("candidate set:");
			//for(Node n : candidates) {
			//	System.out.println(n);
			//}
			
			if(writeSearchTree) {
				// String treeString = "";
				String treeString = "best expanded node: " + bestNode+ "\n";
				if(expandedNodes.size()>1) {
					treeString += "all expanded nodes:\n"; // due to minimum horizontal expansion:\n";
					for(ExampleBasedNode n : expandedNodes) {
						treeString += "   " + n + "\n";
					}
				}
				expandedNodes.clear();
				treeString += "horizontal expansion: " + minimumHorizontalExpansion + " to " + maximumHorizontalExpansion + "\n";
				treeString += topNode.getTreeString();
				treeString += "\n";
				// System.out.println(treeString);
				// searchTree += treeString + "\n";
				// TODO: ev. immer nur einen search tree speichern und den an die
				// Datei anhängen => spart Speicher
				if(replaceSearchTree)
					Files.createFile(searchTreeFile, treeString);
				else
					Files.appendFile(searchTreeFile, treeString);
			}
			
			// Anzahl Schleifendurchläufe
			loop++;
			
			if(!quiet)
				System.out.println("--- loop " + loop + " finished ---");	
			
		}
		
		// Suchbaum in Datei schreiben
//		if(writeSearchTree)
//			Files.createFile(searchTreeFile, searchTree);
		
		// Ergebnisausgabe
		/*
		System.out.println("candidate set:");
		for(Node n : candidates) {
			System.out.println(n);
		}*/
		
		// Set<Concept> solutionsSorted = new TreeSet(conceptComparator);
		// solutionsSorted.addAll(solutions);
		
		// System.out.println("retrievals:");
		// for(Concept c : ReasoningService.retrievals) {
		// 	System.out.println(c);
		// }
		
		if(solutionFound) {
			System.out.println();
			System.out.println("solutions:");
			for(Concept c : solutions) {
				System.out.println("  " + c + " (length " + c.getLength() +", depth " + c.getDepth() + ")");
			}
		}
		System.out.println("horizontal expansion: " + minimumHorizontalExpansion + " to " + maximumHorizontalExpansion);
		System.out.println("size of candidate set: " + candidates.size());
		printStatistics(true);
		
		if(stop)
			System.out.println("Algorithm stopped.");
		else
			System.out.println("Algorithm terminated succesfully.");		
	}
	
	private void extendNodeProper(ExampleBasedNode node, int maxLength) {
		// Rekursionsanfang ist das Konzept am Knoten selbst; danach wird der Operator
		// so lange darauf angewandt bis alle proper refinements bis zu maxLength
		// gefunden wurden
		long propCalcNsStart = System.nanoTime();
		
		if(writeSearchTree)
			expandedNodes.add(node);
		
		if(node.getChildren().size()>maxNrOfChildren)
			maxNrOfChildren = node.getChildren().size();
		
		// Knoten in instabiler Menge muss aktualisiert werden
		// => wird jetzt schon vom Algorithmus entfernt
		/*
		boolean remove = candidates.remove(node);
		
		if(!remove) {
			System.out.println(candidates);
			System.out.println(candidatesStable);
			System.out.println(node);
			
			throw new RuntimeException("remove failed");
		}*/
		
		extendNodeProper(node, node.getConcept(), maxLength, 0);
		node.setHorizontalExpansion(maxLength);
		
		// wird jetzt schon im Kernalgorithmus hinzugefügt
		/*
		boolean add = candidates.add(node);
		if(!add) {
			throw new RuntimeException("add failed");
		}*/
		
		// Knoten wird entfernt und wieder hinzugefügt, da sich seine
		// Position geändert haben könnte => geht noch nicht wg. ConcurrentModification
		// falls Knoten wg. min. horiz. exp. expandiert werden 
		// candidates.remove(node);
		// candidates.add(node);
		propernessCalcTimeNs += (System.nanoTime()-propCalcNsStart);
	}
	

	
	// für alle proper refinements von concept bis maxLength werden Kinderknoten
	// für node erzeugt;
	// recDepth dient nur zur Protokollierung
	private void extendNodeProper(ExampleBasedNode node, Concept concept, int maxLength, int recDepth) {
		
		// führe Methode nicht aus, wenn Algorithmus gestoppt wurde (alle rekursiven Funktionsaufrufe
		// werden nacheinander abgebrochen, so dass ohne weitere Reasoninganfragen relativ schnell beendet wird)
		if(stop)
			return;
		
		if(recDepth > maxRecDepth)
			maxRecDepth = recDepth;
		
		// Refinements berechnen => hier dürfen dürfen refinements <= horizontal expansion
		// des Konzepts nicht gelöscht werden!
		long refinementCalcTimeNsStart = System.nanoTime();
		Set<Concept> refinements = operator.refine(concept, maxLength, null);
		refinementCalcTimeNs += System.nanoTime() - refinementCalcTimeNsStart;
		
		if(refinements.size()>maxNrOfRefinements)
			maxNrOfRefinements = refinements.size();
		
		long childConceptsDeletionTimeNsStart = System.nanoTime();
		// entferne aus den refinements alle Konzepte, die bereits Kinder des Knotens sind
		// for(Node n : node.getChildren()) {
		// 	refinements.remove(n.getConcept());
		// }
		
		// das ist viel schneller, allerdings bekommt man ein anderes candidate set(??)
		refinements.removeAll(node.getChildConcepts());

		childConceptsDeletionTimeNs += System.nanoTime() - childConceptsDeletionTimeNsStart;
		
		long evaluateSetCreationTimeNsStart = System.nanoTime();
		
		// alle Konzepte, die länger als horizontal expansion sind, müssen ausgewertet
		// werden
		Set<Concept> toEvaluateConcepts = new TreeSet<Concept>(conceptComparator);
		Iterator<Concept> it = refinements.iterator();
		// for(Concept refinement : refinements) {
		while(it.hasNext()) {
			Concept refinement = it.next();
			if(refinement.getLength()>node.getHorizontalExpansion()) {
				// TODO: an dieser Stelle könnte man Algorithmen ansetzen lassen, die
				// versuchen properness-Anfragen zu vermeiden:
				// 1. Konzept kürzen und schauen, ob es Mutterkonzept entspricht
				// 2. Blacklist, die überprüft, ob Konzept too weak ist
				// (dann ist es auch proper)
				
				// sagt aus, ob festgestellt wurde, ob refinement proper ist
				// (sagt nicht aus, dass das refinement proper ist!)
				boolean propernessDetected = false;
				
				// 1. short concept construction
				if(useShortConceptConstruction) {
					// kurzes Konzept konstruieren
					Concept shortConcept = ConceptTransformation.getShortConcept(refinement, conceptComparator);
					int n = conceptComparator.compare(shortConcept, concept);
					
					// Konzepte sind gleich also Refinement improper
					if(n==0) {
						propernessTestsAvoidedByShortConceptConstruction++;
						propernessDetected = true;
					}
				}
				
				// 2. too weak test
				if(!propernessDetected && useTooWeakList) {
					if(refinement instanceof MultiConjunction) {
						boolean tooWeakElement = containsTooWeakElement((MultiConjunction)refinement);
						if(tooWeakElement) {
							propernessTestsAvoidedByTooWeakList++;
							conceptTestsTooWeakList++;
							propernessDetected = true;
							tooWeakList.add(refinement);
							
							// Knoten wird direkt erzeugt (es ist buganfällig zwei Plätze
							// zu haben, an denen Knoten erzeugt werden, aber es erscheint
							// hier am sinnvollsten)
							properRefinements.add(refinement);
							tooWeakList.add(refinement);
							
							ExampleBasedNode newNode = new ExampleBasedNode(refinement);
							newNode.setHorizontalExpansion(refinement.getLength()-1);
							newNode.setTooWeak(true);
							newNode.setQualityEvaluationMethod(ExampleBasedNode.QualityEvaluationMethod.TOO_WEAK_LIST);
							node.addChild(newNode);
							
							// Refinement muss gelöscht werden, da es proper ist
							it.remove();
						}
					}
				}
				
				// properness konnte nicht vorher ermittelt werden
				if(!propernessDetected)
					toEvaluateConcepts.add(refinement);
				
					
			}
		}
		evaluateSetCreationTimeNs += System.nanoTime() - evaluateSetCreationTimeNsStart;
		
		// System.out.println(toEvaluateConcepts.size());
		
		Set<Concept> improperConcepts = null;
		if(toEvaluateConcepts.size()>0) {
			// Test aller Konzepte auf properness (mit DIG in nur einer Anfrage)
			long propCalcReasoningStart = System.nanoTime();
			improperConcepts = rs.subsumes(toEvaluateConcepts, concept);
			propernessTestsReasoner+=toEvaluateConcepts.size();
			// boolean isProper = !learningProblem.getReasoningService().subsumes(refinement, concept);
			propernessCalcReasoningTimeNs += System.nanoTime() - propCalcReasoningStart;
		}

		long improperConceptsRemovalTimeNsStart = System.nanoTime();
		// die improper Konzepte werden von den auszuwertenden gelöscht, d.h.
		// alle proper concepts bleiben übrig (einfache Umbenennung)
		if(improperConcepts != null)
			toEvaluateConcepts.removeAll(improperConcepts);
		Set<Concept> properConcepts = toEvaluateConcepts;
		// alle proper concepts von refinements löschen
		refinements.removeAll(properConcepts);
		improperConceptsRemovalTimeNs += System.nanoTime() - improperConceptsRemovalTimeNsStart;
		
		for(Concept refinement : properConcepts) {
			long redundancyCheckTimeNsStart = System.nanoTime();
			boolean nonRedundant = properRefinements.add(refinement);
			redundancyCheckTimeNs += System.nanoTime() - redundancyCheckTimeNsStart;
			
			if(!nonRedundant)
				redundantConcepts++;
			
			// es wird nur ein neuer Knoten erzeugt, falls das Konzept nicht
			// schon existiert
			if(nonRedundant) {
			
				// Knoten erzeugen
				ExampleBasedNode newNode = new ExampleBasedNode(refinement);
				// die -1 ist wichtig, da sonst keine gleich langen Refinements 
				// für den neuen Knoten erlaubt wären z.B. person => male
				newNode.setHorizontalExpansion(refinement.getLength()-1);

				
				// hier finden Tests statt, die Retrieval-Anfrage vermeiden sollen
				/*
				Integer n = evaluationCache.get(concept);
				// Konzept gefunden
				if(n!=null) {
					// Knoten erzeugen
					Node newNode = new Node(refinement);
					newNode.setHorizontalExpansion(refinement.getLength()-1);
					node.addChild(newNode);
					
					// too weak
					if(n==-1) {
						newNode.setTooWeak(true);
					// nicht too weak
					} else {
						// feststellen, ob proper => geht so nicht
						// gleiche covered negatives bedeutet nicht improper
						boolean proper = (n==node.getCoveredNegativeExamples());
						newNode.setCoveredNegativeExamples(n);
						
					}
				// Konzept nicht gefunden => muss ausgewertet werden
				} else {
					toEvaluateConcepts.add(refinement);
				}
				*/
				
				boolean qualityKnown = false;
				int quality = -2;
				
				// overly general list verwenden
				if(useOverlyGeneralList && refinement instanceof MultiDisjunction) {
					if(containsOverlyGeneralElement((MultiDisjunction)refinement)) {
						conceptTestsOverlyGeneralList++;
						quality = getNumberOfNegatives();
						qualityKnown = true;
						newNode.setQualityEvaluationMethod(ExampleBasedNode.QualityEvaluationMethod.OVERLY_GENERAL_LIST);
					}	
				}
				
				// Qualität des Knotens auswerten
				if(!qualityKnown) {
					long propCalcReasoningStart2 = System.nanoTime();
					conceptTestsReasoner++;
					quality = coveredNegativesOrTooWeak(refinement);
					propernessCalcReasoningTimeNs += System.nanoTime() - propCalcReasoningStart2;
					newNode.setQualityEvaluationMethod(ExampleBasedNode.QualityEvaluationMethod.REASONER);
				}

				if(quality == -1) {
					newNode.setTooWeak(true);
					// Blacklist für too weak concepts
					tooWeakList.add(refinement);
				} else {
					// Lösung gefunden
					if(quality == 0) {
						solutionFound = true;
						solutions.add(refinement);
					}			
					
					newNode.setCoveredNegativeExamples(quality);
					newCandidates.add(newNode);
					// candidates.add(newNode);
					// candidatesStable.add(newNode);
				
					
					if(quality == getNumberOfNegatives())
						overlyGeneralList.add(refinement);
					
					// System.out.print(".");
				}
				
				node.addChild(newNode);
			}			
		}
		
		
		/*
		Iterator<Concept> it = refinements.iterator();
		while(it.hasNext()) {
			Concept refinement = it.next();
			if(refinement.getLength()>node.getHorizontalExpansion()) {
				// Test auf properness
				long propCalcReasoningStart = System.nanoTime();
				boolean isProper = !learningProblem.getReasoningService().subsumes(refinement, concept);
				propernessCalcReasoningTimeNs += System.nanoTime() - propCalcReasoningStart;
				
				if(isProper) {
					long redundancyCheckTimeNsStart = System.nanoTime();
					boolean nonRedundant = properRefinements.add(refinement);
					redundancyCheckTimeNs += System.nanoTime() - redundancyCheckTimeNsStart;
					
					if(!nonRedundant)
						redundantConcepts++;
					
					// es wird nur ein neuer Knoten erzeugt, falls das Konzept nicht
					// schon existiert
					if(nonRedundant) {
					
						// Knoten erzeugen
						Node newNode = new Node(refinement);
						// die -1 ist wichtig, da sonst keine gleich langen Refinements 
						// für den neuen Knoten erlaubt wären z.B. person => male
						newNode.setHorizontalExpansion(refinement.getLength()-1);
						node.addChild(newNode);
						
						// Qualität des Knotens auswerten
						long propCalcReasoningStart2 = System.nanoTime();
						int quality = learningProblem.coveredNegativeExamplesOrTooWeak(refinement);
						propernessCalcReasoningTimeNs += System.nanoTime() - propCalcReasoningStart2;
						
						if(quality == -1) {
							newNode.setTooWeak(true);
						} else {
							// Lösung gefunden
							if(quality == 0) {
								solutionFound = true;
								solutions.add(refinement);
							}			
							
							newNode.setCoveredNegativeExamples(quality);
							newCandidates.add(newNode);
							
							// System.out.print(".");
						}
					}
					
					// jedes proper Refinement wird gelöscht
					it.remove();

				}
			}
		}
		*/
		
		
		
		// es sind jetzt noch alle Konzepte übrig, die improper refinements sind
		// auf jedem dieser Konzepte wird die Funktion erneut aufgerufen, da sich
		// proper refinements ergeben könnten
		for(Concept refinement : refinements) {
			// for(int i=0; i<=recDepth; i++)
			//	System.out.print("  ");
			// System.out.println("call: " + refinement + " [maxLength " + maxLength + "]");
			extendNodeProper(node, refinement, maxLength, recDepth+1);
			// for(int i=0; i<=recDepth; i++)
			//	System.out.print("  ");
			// System.out.println("finished: " + refinement + " [maxLength " + maxLength + "]");			
		}
	}
	
	private void printStatistics(boolean finalStats) {
		// TODO: viele Tests haben ergeben, dass man nie 100% mit der Zeitmessung abdecken
		// kann (zum einen weil Stringausgabe verzögert erfolgt und zum anderen weil 
		// Funktionsaufrufe, garbage collection, Zeitmessung selbst auch Zeit benötigt); 
		// es empfiehlt sich folgendes Vorgehen:
		// - Messung der Zeit eines Loops im Algorithmus
		// - Messung der Zeit für alle node extensions innerhalb eines Loops
		// => als Normalisierungsbasis empfehlen sich dann die Loopzeit statt
		// Algorithmuslaufzeit
		// ... momentan kann es aber auch erstmal so lassen

		long algorithmRuntime = System.nanoTime() - algorithmStartTime;
		
		if(!finalStats) {
			// Refinementoperator auf Konzept anwenden
			String bestNodeString = "currently best node: " + candidatesStable.last();
			// searchTree += bestNodeString + "\n";
			System.out.println(bestNodeString);
			String expandedNodeString = "next expanded node: " + candidates.last();
			// searchTree += expandedNodeString + "\n";
			System.out.println(expandedNodeString);		
			System.out.println("algorithm runtime " + Helper.prettyPrintNanoSeconds(algorithmRuntime));
			String expansionString = "horizontal expansion: " + minimumHorizontalExpansion + " to " + maximumHorizontalExpansion;
			// searchTree += expansionString + "\n";
			System.out.println(expansionString);
			System.out.println("size of candidate set: " + candidates.size());
			// System.out.println("properness max recursion depth: " + maxRecDepth);
			// System.out.println("max. number of one-step refinements: " + maxNrOfRefinements);
			// System.out.println("max. number of children of a node: " + maxNrOfChildren);
		}
		
		if(showBenchmarkInformation) {
			

			long reasoningTime = rs.getOverallReasoningTimeNs();
			double reasoningPercentage = 100 * reasoningTime/(double)algorithmRuntime;
			long propWithoutReasoning = propernessCalcTimeNs-propernessCalcReasoningTimeNs;
			double propPercentage = 100 * propWithoutReasoning/(double)algorithmRuntime;
			double deletionPercentage = 100 * childConceptsDeletionTimeNs/(double)algorithmRuntime;
			long subTime = rs.getSubsumptionReasoningTimeNs();
			double subPercentage = 100 * subTime/(double)algorithmRuntime;
			double refinementPercentage = 100 * refinementCalcTimeNs/(double)algorithmRuntime;
			double redundancyCheckPercentage = 100 * redundancyCheckTimeNs/(double)algorithmRuntime;
			double evaluateSetCreationTimePercentage = 100 * evaluateSetCreationTimeNs/(double)algorithmRuntime;
			double improperConceptsRemovalTimePercentage = 100 * improperConceptsRemovalTimeNs/(double)algorithmRuntime;
			double mComputationTimePercentage = 100 * operator.mComputationTimeNs/(double)algorithmRuntime;
			double topComputationTimePercentage = 100 * operator.topComputationTimeNs/(double)algorithmRuntime;
			double cleanTimePercentage = 100 * ConceptTransformation.cleaningTimeNs/(double)algorithmRuntime;
			double onnfTimePercentage = 100 * ConceptTransformation.onnfTimeNs/(double)algorithmRuntime;
			double shorteningTimePercentage = 100 * ConceptTransformation.shorteningTimeNs/(double)algorithmRuntime;
			
			// nur temporär 
			double someTimePercentage = 100 * someTimeNs/(double)algorithmRuntime;			
			
			System.out.println("reasoning percentage: " + df.format(reasoningPercentage) + "%");
			System.out.println("   subsumption check time: " + df.format(subPercentage) + "%");		
			System.out.println("proper calculation percentage (wo. reasoning): " + df.format(propPercentage) + "%");
			System.out.println("   deletion time percentage: " + df.format(deletionPercentage) + "%");
			System.out.println("   refinement calculation percentage: " + df.format(refinementPercentage) + "%");
			System.out.println("      some time percentage: " + df.format(someTimePercentage) + "% " + Helper.prettyPrintNanoSeconds(someTimeNs) + " " + someCount + " times");
			System.out.println("      m calculation percentage: " + df.format(mComputationTimePercentage) + "%");
			System.out.println("      top calculation percentage: " + df.format(topComputationTimePercentage) + "%");
			System.out.println("   redundancy check percentage: " + df.format(redundancyCheckPercentage) + "%");
			System.out.println("   evaluate set creation time percentage: " + df.format(evaluateSetCreationTimePercentage) + "%");
			System.out.println("   improper concepts removal time percentage: " + df.format(improperConceptsRemovalTimePercentage) + "%");
			System.out.println("clean time percentage: " + df.format(cleanTimePercentage) + "%");
			System.out.println("onnf time percentage: " + df.format(onnfTimePercentage) + "%");
			System.out.println("shortening time percentage: " + df.format(shorteningTimePercentage) + "%");			
		}
		System.out.println("properness tests (reasoner/short concept/too weak list): " + propernessTestsReasoner + "/" + propernessTestsAvoidedByShortConceptConstruction 
				+ "/" + propernessTestsAvoidedByTooWeakList);
		System.out.println("concept tests (reasoner/too weak list/overly general list/redundant concepts): " + conceptTestsReasoner + "/"
				+ conceptTestsTooWeakList + "/" + conceptTestsOverlyGeneralList + "/" + redundantConcepts);	
	}
	
	private int coveredNegativesOrTooWeak(Concept concept) {
		if(posOnly)
			return posOnlyLearningProblem.coveredPseudoNegativeExamplesOrTooWeak(concept);
		else
			return learningProblem.coveredNegativeExamplesOrTooWeak(concept);
	}
	
	private int getNumberOfNegatives() {
		if(posOnly)
			return posOnlyLearningProblem.getPseudoNegatives().size();
		else
			return learningProblem.getNegativeExamples().size();
	}	
	
	private boolean containsTooWeakElement(MultiConjunction mc) {
		for(Concept child : mc.getChildren()) {
			if(tooWeakList.contains(child))
				return true;
		}
		return false;
	}
	
	private boolean containsOverlyGeneralElement(MultiDisjunction md) {
		for(Concept child : md.getChildren()) {
			if(overlyGeneralList.contains(child))
				return true;
		}
		return false;
	}		
	
	public void stop() {
		
	}

	public Concept getBestSolution() {
		return candidatesStable.last().getConcept();
	}

	public synchronized List<Concept> getBestSolutions(int nrOfSolutions) {
		List<Concept> best = new LinkedList<Concept>();
		int i=0;
		for(ExampleBasedNode n : candidatesStable.descendingSet()) {
			best.add(n.getConcept());
			if(i==nrOfSolutions)
				return best;
			i++;
		}
		return best;
	}
	
	public Score getSolutionScore() {
		if(posOnly)
			return posOnlyLearningProblem.computeScore(getBestSolution());
		else
			return learningProblem.computeScore(getBestSolution());
	}	
	

	
}
