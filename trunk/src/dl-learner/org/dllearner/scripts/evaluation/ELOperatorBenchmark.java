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
package org.dllearner.scripts.evaluation;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.dllearner.algorithms.el.ELDescriptionTree;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.owl.Thing;
import org.dllearner.kb.OWLFile;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.refinementoperators.ELDown2;
import org.dllearner.utilities.Files;
import org.dllearner.utilities.statistics.Stat;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * An evaluation of the EL refinement operator {@link ELDown2}. It creates
 * a set of artificial ontologies with varying complexity and performs
 * refinement steps on them.
 * 
 * @author Jens Lehmann
 *
 */
public class ELOperatorBenchmark {

	private static Random rand = new Random(1);
	private static DecimalFormat df = new DecimalFormat();
	
	public static void main(String[] args) throws ComponentInitException, IOException {
		
//		Logger logger = Logger.getRootLogger();
//		logger.setLevel(Level.TRACE);
//		SimpleLayout layout = new SimpleLayout();
//		FileAppender app = new FileAppender(layout, "log/el/log.txt", false);
//		logger.removeAllAppenders();
//		logger.addAppender(app);
		
		// create a directory for log files
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String dir = "log/el/" + df.format(dt) + "/";
		new File(dir).mkdir();		
		
		String example = "/home/jl/promotion/ontologien/galen2.owl";
		testOntology(dir, example, 100, 15);
		System.exit(0);
		
		/* TEST ON ARTIFICIAL ONTOLOGIES
		  
		 
		// number of concepts and roles
		int[] conceptCounts = new int[] { 5, 10 };
		int[] roleCounts = new int[] { 5, 10};
		
		// number of applications of operator
		int opApplications = 10;
		
		// statistics directory
		String statDir = "/log/stat/el/";
		String statFile = statDir + "stats.txt";
		String gnuPlotApplicationTimeFile = statDir + "application.gp";
		String gnuPlotRefinementTimeFile = statDir + "refinement.gp";
		boolean writeOntologies = true;
		String ontologyDir = "/log/stat/el/ontologies/";
		
		
		
		for(int conceptCount : conceptCounts) {
			for(int roleCount : roleCounts) {
				// code for ontology creation
				KB kb = new KB();
				
				// create class hierarchy (concept 0 is owl:Thing)
				for(int i=1; i<=conceptCount; i++) {
					// create class
					NamedClass nc = new NamedClass("a" + i);
					// pick an existing class as super class
					int j = (i == 0) ? 0 : rand.nextInt(i);
					Description superClass;
					if(j==0) {
						superClass = Thing.instance;
					} else {
						superClass = new NamedClass("a" + j);
					}
					kb.addAxiom(new SubClassAxiom(nc, superClass));
					// disjointness with siblings
				}
				
				
				// save ontology
				File f = new File(ontologyDir + "c" + conceptCount + "r" + roleCount + ".owl");
				kb.export(f, OntologyFormat.RDF_XML);
				

			}
		}
		*/
//		ELDown2 operator = new ELDown2();
	}
	
	private static void testOntology(String statDir, String ont, int nrOfChains, int chainLength) throws ComponentInitException, IOException {
		System.out.print("Reading in " + ont + " ... ");
		ComponentManager cm = ComponentManager.getInstance();
		// reading ontology into a reasoner
		KnowledgeSource source = cm.knowledgeSource(OWLFile.class);
		File ontFile = new File(ont);
		cm.applyConfigEntry(source, "url", ontFile.toURI().toURL());
		source.init();
		ReasonerComponent reasoner = cm.reasoner(OWLAPIReasoner.class, source);
		reasoner.init();
		System.out.println("done.");
		System.out.println();
		
		// log file name
		String name = ontFile.getName();
		String statFileName = name.substring(0, name.lastIndexOf(".")) + ".txt";
		File statFile = new File(statDir + statFileName);
		
		String statString = "";
		MonitorFactory.reset();
		for(int loop = 0; loop < nrOfChains; loop++) {
		
			// application of operator and statistics recording	
			ELDescriptionTree currTree = new ELDescriptionTree(reasoner, Thing.instance);
			ELDown2 operator = new ELDown2(reasoner);
			Stat runtime = new Stat();
			Stat runtimePerRefinement = new Stat();
			
			System.out.println("Testing operator (applying it " + chainLength + " times):");
			for(int i=0; i < chainLength; i++) {
//				System.out.println(currTree.transformToDescription().toKBSyntaxString());
				System.out.print("current concept: " + currTree.transformToDescription().toString(reasoner.getBaseURI(), reasoner.getPrefixes()));
				// apply operator on current description
				long start = System.nanoTime();
				List<ELDescriptionTree> refinements = operator.refine(currTree);
				long time = System.nanoTime() - start;
				runtime.addNumber(time/1000000d);
				runtimePerRefinement.addNumber(time/1000000d/refinements.size());
				MonitorFactory.add("operator application time", "ms.", time/1000000d);
				MonitorFactory.add("operator application time per refinement", "ms.", time/1000000d/refinements.size());
				MonitorFactory.add("refinement count", "count", refinements.size());
				
				int sizeSum = 0;
				for(ELDescriptionTree tree : refinements) {
//					System.out.println("   " + tree.toDescriptionString());
					sizeSum += tree.getSize();
				}
				
				MonitorFactory.add("refinement size", "count", sizeSum/(double)refinements.size());
				MonitorFactory.add("refinement size increase", "count", (sizeSum-refinements.size()*currTree.getSize())/(double)refinements.size());
				
				System.out.println("  [has " + refinements.size() + " refinements]");
				// pick a refinement randomly (which is kind of slow for a set, but
				// this does not matter here)
				int index = rand.nextInt(refinements.size());
//				currTree = new ArrayList<ELDescriptionTree>(refinements).get(index);
				currTree = refinements.get(index);
				MonitorFactory.add("picked refinement size", "count", currTree.getSize());
			}
			System.out.println("operator time: " + runtime.prettyPrint("ms"));
			System.out.println("operator time per refinement: " + runtimePerRefinement.prettyPrint("ms"));
			System.out.println();
			
		}
		
		statString += "file: " + name + "\n";
		statString += "nr of refinement chains: " + nrOfChains + "\n";
		statString += "refinement chain length: " + chainLength + "\n\n";
		
		statString += getMonitorData(MonitorFactory.getMonitor("operator application time", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("operator application time per refinement", "ms."));
		statString += "\n";
	
		statString += getMonitorDataCount(MonitorFactory.getMonitor("refinement count", "count"));		
		statString += getMonitorDataCount(MonitorFactory.getMonitor("refinement size", "count"));
		statString += getMonitorDataCount(MonitorFactory.getMonitor("picked refinement size", "count"));
		statString += getMonitorDataCount(MonitorFactory.getMonitor("refinement size increase", "count"));
		statString += "\n";
		
		statString += getMonitorData(MonitorFactory.getMonitor("extend label", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("refine label", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("refine edge", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("attach tree", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("as.merge trees", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("as.complex check", "ms."));
//		statString += getMonitorData(MonitorFactory.getMonitor("as.tmp", "ms."));
//		statString += getMonitorData(MonitorFactory.getMonitor("el.tmp", "ms."));
		statString += getMonitorDataBoolean(MonitorFactory.getMonitor("as.minimal", "boolean"));
		statString += getMonitorDataBoolean(MonitorFactory.getMonitor("as.check", "boolean"));		
		statString += getMonitorData(MonitorFactory.getMonitor("tree clone", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("simulation update", "ms."));
		statString += getMonitorData(MonitorFactory.getMonitor("disjointness reasoning", "ms."));
		
		Files.createFile(statFile, statString);
	}
	
	private static String getMonitorData(Monitor mon) {
		return mon.getLabel() + ": av " + df.format(mon.getAvg()) + "ms  (stddev " + df.format(mon.getStdDev()) + "ms,  min " + df.format(mon.getMin()) +  "ms, max " + df.format(mon.getMax()) + "ms, " +  df.format(mon.getTotal()/1000) + "s total, " + (int)mon.getHits() + " hits)\n";
	}
	
	private static String getMonitorDataCount(Monitor mon) {
		return mon.getLabel() + ": av " + df.format(mon.getAvg()) + " (stddev " + df.format(mon.getStdDev()) + ",  min " + df.format(mon.getMin()) +  ", max " + df.format(mon.getMax()) + ", " +  df.format(mon.getTotal()) + " total, " + (int)mon.getHits() + " hits)\n";		
	}
	
	private static String getMonitorDataBoolean(Monitor mon) {
		return mon.getLabel() + ": " + df.format(mon.getAvg()*100) + "%\n";		
	}	
}
