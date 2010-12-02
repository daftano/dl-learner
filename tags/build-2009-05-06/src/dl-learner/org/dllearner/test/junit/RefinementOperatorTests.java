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
package org.dllearner.test.junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.TreeSet;

import org.dllearner.algorithms.refinement2.ROLComponent2;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.owl.ClassHierarchy;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.test.junit.TestOntologies.TestOntology;
import org.dllearner.utilities.Helper;
import org.junit.Test;

/**
 * A suite of JUnit tests related to refinement operators.
 * 
 * @author Jens Lehmann
 *
 */
public class RefinementOperatorTests {

	private String baseURI;
	
	/**
	 * Applies the RhoDRDown operator to a concept and checks that the number of
	 * refinements is correct.
	 *
	 */
	@Test
	public void rhoDRDownTest() {
		try {
			String file = "examples/carcinogenesis/carcinogenesis.owl";
			ComponentManager cm = ComponentManager.getInstance();
			KnowledgeSource ks = cm.knowledgeSource(OWLFile.class);
			try {
				cm.applyConfigEntry(ks, "url", new File(file).toURI().toURL());
			} catch (MalformedURLException e) {
				// should never happen
				e.printStackTrace();
			}
			ks.init();
			ReasonerComponent rc = cm.reasoner(OWLAPIReasoner.class, ks);
			rc.init();
			baseURI = rc.getBaseURI();
//			ReasonerComponent rs = cm.reasoningService(rc);
			
			// TODO the following two lines should not be necessary
//			rs.prepareSubsumptionHierarchy();
//			rs.prepareRoleHierarchy();
			
			RhoDRDown op = new RhoDRDown(rc);
			Description concept = KBParser.parseConcept(uri("Compound"));
			Set<Description> results = op.refine(concept, 4, null);

			for(Description result : results) {
				System.out.println(result);
			}
			
			int desiredResultSize = 141;
			if(results.size() != desiredResultSize) {
				System.out.println(results.size() + " results found, but should be " + desiredResultSize + ".");
			}
			assertTrue(results.size()==desiredResultSize);
		} catch(ComponentInitException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void rhoDRDownTest2() throws ParseException {
		ReasonerComponent reasoner = TestOntologies.getTestOntology(TestOntology.EPC_OE);
		baseURI = reasoner.getBaseURI();
		
		RhoDRDown op = new RhoDRDown(reasoner);
		Description concept = KBParser.parseConcept("(\"http://localhost/aris/sap_model.owl#EPC\" AND EXISTS \"http://localhost/aris/sap_model.owl#hasModelElements\".\"http://localhost/aris/sap_model.owl#Object\")");
		Set<Description> results = op.refine(concept,10);

		for(Description result : results) {
			System.out.println(result.toString("http://localhost/aris/sap_model.owl#",null));
		}
			
		int desiredResultSize = 116;
		if(results.size() != desiredResultSize) {
			System.out.println(results.size() + " results found, but should be " + desiredResultSize + ".");
		}
		assertTrue(results.size()==desiredResultSize);
	}
	
	@Test
	public void rhoDRDownTest3() throws ParseException, LearningProblemUnsupportedException {
		ReasonerComponent reasoner = TestOntologies.getTestOntology(TestOntology.KRK_ZERO_ONE);
		baseURI = reasoner.getBaseURI();
		
		// create learning algorithm in order to test under similar conditions than 
		// within a learning algorithm
		ComponentManager cm = ComponentManager.getInstance();
		LearningProblem lp = cm.learningProblem(PosNegLPStandard.class, reasoner);
		ROLComponent2 la = cm.learningAlgorithm(ROLComponent2.class, lp, reasoner);
		
		Set<NamedClass> ignoredConcepts = new TreeSet<NamedClass>();
		ignoredConcepts.add(new NamedClass("http://www.test.de/test#ZERO"));
		ignoredConcepts.add(new NamedClass("http://www.test.de/test#ONE"));
		Set<NamedClass> usedConcepts = Helper.computeConceptsUsingIgnoreList(reasoner, ignoredConcepts);
		
		ClassHierarchy classHierarchy = reasoner.getClassHierarchy().cloneAndRestrict(usedConcepts); 
		classHierarchy.thinOutSubsumptionHierarchy();
		RhoDRDown op = new RhoDRDown(
				reasoner,
				classHierarchy,
				Thing.instance,
				la.getConfigurator()
			);		
		
		Description concept = KBParser.parseConcept("EXISTS \"http://www.test.de/test#hasPiece\".EXISTS \"http://www.test.de/test#hasLowerRankThan\".(\"http://www.test.de/test#WRook\" AND TOP)");
		Set<Description> results = op.refine(concept,8);

		for(Description result : results) {
			System.out.println(result.toString("http://www.test.de/test#",null));
		}
			
		int desiredResultSize = 8;
		if(results.size() != desiredResultSize) {
			System.out.println(results.size() + " results found, but should be " + desiredResultSize + ".");
		}
		
		// the 8 refinements found on 2009/04/16 are as follows:
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(BKing AND WRook)
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(WKing AND WRook)
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(WRook AND WRook)
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(WRook AND (NOT BKing))
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(WRook AND (NOT WKing))
		// EXISTS hasPiece.EXISTS hasLowerRankThan.(WRook AND (NOT WRook))
		// EXISTS hasPiece.>= 2 hasLowerRankThan.(WRook AND TOP)
		// >= 2 hasPiece.EXISTS hasLowerRankThan.(WRook AND TOP)
		
		assertTrue(results.size()==desiredResultSize);
	}
			
	private String uri(String name) {
		return "\""+baseURI+name+"\"";
	}
}