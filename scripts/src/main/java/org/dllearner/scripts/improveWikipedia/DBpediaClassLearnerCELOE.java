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
 *
 */
package org.dllearner.scripts.improveWikipedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.aksw.commons.sparql.core.SparqlTemplate;
import org.apache.velocity.VelocityContext;
import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.core.AbstractReasonerComponent;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.OntologyFormat;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.EquivalentClassesAxiom;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.KB;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.Thing;
import org.dllearner.gui.Config;
import org.dllearner.gui.ConfigSave;
import org.dllearner.kb.sparql.Cache;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.kb.sparql.SparqlQuery;
import org.dllearner.learningproblems.PosNegLPStandard;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.utilities.Helper;
import org.dllearner.utilities.JamonMonitorLogger;
import org.dllearner.utilities.datastructures.Datastructures;
import org.dllearner.utilities.datastructures.SetManipulation;
import org.dllearner.utilities.datastructures.SortedSetTuple;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * A script, which learns definitions / super classes of classes in the DBpedia
 * ontology.
 * 
 * TODO: This script made heavy use of aksw-commons-sparql-scala and needs to be
 * rewritten to use aksw-commons-sparql (the new SPARQL API).
 * 
 * @author Jens Lehmann
 * @author Sebastian Hellmann
 */
public class DBpediaClassLearnerCELOE {
    
    public static String endpointurl = "http://live.dbpedia.org/sparql";
    public static int examplesize = 30;
    
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(DBpediaClassLearnerCELOE.class);
    private static String output;
    private static String input;
    
    SparqlEndpoint sparqlEndpoint = null;
    private Cache cache;
    
    public DBpediaClassLearnerCELOE() {
        // OPTIONAL: if you want to do some case distinctions in the learnClass
        // method, you could add
        // parameters to the constructure e.g. YAGO_
        try {
            sparqlEndpoint = new SparqlEndpoint(new URL(endpointurl));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cache = new Cache("basCache");
    }
    
    public static void main(String args[]) throws LearningProblemUnsupportedException, IOException,
            Exception {
        if (args.length < 3) {
            usage();
            return;
        }
        int iter;
        try {
            output = args[1];
            input = args[0];
            iter = Integer.parseInt(args[2]);
        } catch (Exception e) {
            usage();
            return;
        }
        for (int i = 0; i < iter; i++) {
            DBpediaClassLearnerCELOE dcl = new DBpediaClassLearnerCELOE();
            Set<String> classesToLearn = dcl.getClasses();
            Monitor mon = MonitorFactory.start("Learn DBpedia");
            KB kb = dcl.learnAllClasses(classesToLearn);
            mon.stop();
            kb.export(new File(output + "/result" + i + ".owl"), OntologyFormat.RDF_XML);
            // Set<String> pos =
            // dcl.getPosEx("http://dbpedia.org/ontology/Person");
            // dcl.getNegEx("http://dbpedia.org/ontology/Person", pos);
            logger.info("Test" + i + ":\n" + JamonMonitorLogger.getStringForAllSortedByLabel());
            System.gc();
        }
    }
    
    /**
     * Show the required parameters for usage
     */
    private static void usage() {
        System.out.println("***************************************************************");
        System.out.println("* Usage: java DBpediaClassLearnerCELOE input output iteration *");
        System.out.println("* As input is the dbpedia schema as owl necessary             *");
        System.out.println("* As output is a directory for the owl results file expected  *");
        System.out.println("***************************************************************");
    }
    
    public KB learnAllClasses(Set<String> classesToLearn) {
        KB kb = new KB();
        for (String classToLearn : classesToLearn) {
            logger.info("Learning class: " + classToLearn);
            try {
                Description d = learnClass(classToLearn);
                if (d == null || d.toKBSyntaxString().equals(new Thing().toKBSyntaxString())) {
                    logger.error("Description was " + d + ", continueing");
                    continue;
                }
                kb.addAxiom(new EquivalentClassesAxiom(new NamedClass(classToLearn), d));
                kb.export(new File(output+"/result_partial.owl"),
                        OntologyFormat.RDF_XML);
                
            } catch (Exception e) {
                logger.warn("", e);
            }
            this.dropCache();
        }
        
        return kb;
    }
    
    public Description learnClass(String classToLearn) throws Exception {
        // TODO: use aksw-commons-sparql instead of sparql-scala
        SortedSet<String> posEx = new TreeSet<String>(getPosEx(classToLearn));
        logger.info("Found " + posEx.size() + " positive examples");
        if (posEx.isEmpty()) {
            return null;
        }
        SortedSet<String> negEx = new TreeSet<String>(getNegEx(classToLearn, posEx));
        
        posEx = SetManipulation.fuzzyShrink(posEx, examplesize);
        negEx = SetManipulation.fuzzyShrink(negEx, examplesize);
        
        SortedSet<Individual> posExamples = Helper.getIndividualSet(posEx);
        SortedSet<Individual> negExamples = Helper.getIndividualSet(negEx);
        SortedSetTuple<Individual> examples = new SortedSetTuple<Individual>(posExamples,
                negExamples);
        
        ComponentManager cm = ComponentManager.getInstance();
        
        SparqlKnowledgeSource ks = cm.knowledgeSource(SparqlKnowledgeSource.class);
        ks.setInstances(Datastructures.individualSetToStringSet(examples.getCompleteSet()));
        // ks.getConfigurator().setPredefinedEndpoint("DBPEDIA"); // TODO:
        // probably the official endpoint is too slow?
        ks.setUrl(new URL(endpointurl));
        ks.setUseLits(false);
        ks.setUseCacheDatabase(true);
        ks.setUseCache(true);
        ks.setRecursionDepth(1);
        ks.setCloseAfterRecursion(true);
        ks.setSaveExtractedFragment(true);
        ks.setPredList(new HashSet<String>(Arrays
                .asList(new String[] { "http://dbpedia.org/property/wikiPageUsesTemplate",
                        "http://dbpedia.org/ontology/wikiPageExternalLink",
                        "http://dbpedia.org/property/wordnet_type",
                        "http://www.w3.org/2002/07/owl#sameAs" })));
        
        ks.setObjList(new HashSet<String>(Arrays.asList(new String[] {
                "http://dbpedia.org/class/yago/", "http://dbpedia.org/resource/Category:" })));
        
        ks.init();
        
        AbstractReasonerComponent rc = cm.reasoner(FastInstanceChecker.class, ks);
        rc.init();
        
        PosNegLPStandard lp = cm.learningProblem(PosNegLPStandard.class, rc);
        lp.setPositiveExamples(posExamples);
        lp.setNegativeExamples(negExamples);
        lp.setAccuracyMethod("fmeasure");
        lp.setUseApproximations(false);
        lp.init();
        
        CELOE la = cm.learningAlgorithm(CELOE.class, lp, rc);
        // CELOEConfigurator cc = la.getConfigurator();
        la.setMaxExecutionTimeInSeconds(100);
        la.init();
        RhoDRDown op = (RhoDRDown) la.getOperator();
        
        op.setUseNegation(false);
        op.setUseAllConstructor(false);
        op.setUseCardinalityRestrictions(false);
        op.setUseHasValueConstructor(true);
        la.setNoisePercentage(20);
        la.setIgnoredConcepts(new HashSet<NamedClass>(Arrays
                .asList(new NamedClass[] { new NamedClass(classToLearn) })));
        la.init();
        
        // to write the above configuration in a conf file (optional)
        Config cf = new Config(cm, ks, rc, lp, la);
        new ConfigSave(cf).saveFile(new File("/dev/null"));
        
        la.start();
        
        cm.freeAllComponents();
        return la.getCurrentlyBestDescription();
    }
    
    public Set<String> getClasses() throws Exception {
        OntModel model = ModelFactory.createOntologyModel();
        model.read(new FileInputStream(input), null);
        Set<OntClass> classes = model.listClasses().toSet();
        Set<String> results = new HashSet<String>();
        for (OntClass ontClass : classes) {
            results.add(ontClass.getURI());
        }
        return results;
    }
    
    // gets all DBpedia Classes
    // public Set<String> getClasses() throws Exception {
    // SparqlTemplate st = SparqlTemplate.getInstance("allClasses.vm");
    // st.setLimit(0);
    // st.addFilter(sparqlEndpoint.like("classes", new
    // HashSet<String>(Arrays.asList(new
    // String[]{"http://dbpedia.org/ontology/"}))));
    // VelocityContext vc = st.putSgetVelocityContext();
    // String query = st.getQuery();
    // return new
    // HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
    // }
    //
    public Set<String> getPosEx(String clazz) throws Exception {
        // SparqlTemplate st =
        // SparqlTemplate.getInstance("instancesOfClass.vm");
        // st.setLimit(0);
        // VelocityContext vc = st.getVelocityContext();
        // vc.put("class", clazz);
        // String queryString = st.getQuery();
        StringBuilder queryString = new StringBuilder();
        queryString.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        queryString.append(" SELECT ?instances WHERE { ?instances rdf:type <");
        queryString.append(clazz);
        queryString.append("> }");
        System.out.println(queryString);
        return this.executeResourceQuery(queryString.toString());
    }
    
    /**
     * gets all direct classes of all instances and has a look, what the most
     * common is
     * 
     * @param clazz
     * @param posEx
     * @return
     * @throws Exception
     */
    public String selectClass(String clazz, Set<String> posEx) throws Exception {
        Map<String, Integer> m = new HashMap<String, Integer>();
        // TODO: use aksw-commons-sparql instead of sparql-scala
        /*
         * for (String pos : posEx) { SparqlTemplate st =
         * SparqlTemplate.getInstance("directClassesOfInstance.vm");
         * st.setLimit(0); st.addFilter(sparqlEndpoint.like("direct", new
         * HashSet<String>(Arrays.asList(new
         * String[]{"http://dbpedia.org/ontology/"})))); VelocityContext vc =
         * st.getVelocityContext(); vc.put("instance", pos); String query =
         * st.getQuery(); Set<String> classes = new
         * HashSet<String>(ResultSetRenderer
         * .asStringSet(sparqlEndpoint.executeSelect(query)));
         * classes.remove(clazz); for (String s : classes) { if (m.get(s) ==
         * null) { m.put(s, 0); } m.put(s, m.get(s).intValue() + 1); } }
         */
        
        int max = 0;
        String maxClass = "";
        for (String key : m.keySet()) {
            if (m.get(key).intValue() > max) {
                maxClass = key;
            }
        }
        
        return maxClass;
    }
    
    /**
     * gets instances of a class or random instances
     * 
     * @param clazz
     * @param posEx
     * @return
     * @throws Exception
     */
    
    public Set<String> getNegEx(String clazz, Set<String> posEx) throws Exception {
        Set<String> negEx = new HashSet<String>();
        // TODO: use aksw-commons-sparql instead of sparql-scala
        /*
         * String targetClass = getParallelClass(clazz);
         * logger.info("using class for negatives: " + targetClass); if
         * (targetClass != null) {
         * 
         * SparqlTemplate st =
         * SparqlTemplate.getInstance("instancesOfClass.vm"); st.setLimit(0);
         * VelocityContext vc = st.getVelocityContext(); vc.put("class",
         * targetClass); // st.addFilter(sparqlEndpoint.like("class", new
         * HashSet<String>(Arrays.asList(new
         * String[]{"http://dbpedia.org/ontology/"})))); String query =
         * st.getQuery(); // negEx.addAll(new
         * HashSet<String>(ResultSetRenderer.asStringSet
         * (sparqlEndpoint.executeSelect(query)))); } else {
         * 
         * SparqlTemplate st = SparqlTemplate.getInstance("someInstances.vm");
         * st.setLimit(posEx.size() + 100); VelocityContext vc =
         * st.getVelocityContext(); String query = st.getQuery(); //
         * negEx.addAll(new
         * HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint
         * .executeSelect(query)))); } negEx.removeAll(posEx);
         */
        
        String targetClass = getParallelClass(clazz);
        logger.info("using class for negatives: " + targetClass);
        if (targetClass != null) {
            SparqlTemplate st = SparqlTemplate.getInstance("instancesOfClass2.vm");
            st.setLimit(0);
            VelocityContext vc = st.getVelocityContext();
            vc.put("class", targetClass);
            st.addFilter("FILTER ( ?class LIKE (<http://dbpedia.org/ontology/%>");
            
            String query = st.getQuery();
            negEx.addAll(this.executeResourceQuery(query));
        } else {
            SparqlTemplate st = SparqlTemplate.getInstance("someInstances.vm");
            st.setLimit(posEx.size() + 100);
            VelocityContext vc = st.getVelocityContext();
            String query = st.getQuery();
            negEx.addAll(this.executeResourceQuery(query));
        }
        negEx.removeAll(posEx);
        return negEx;
        
    }
    
    public String getParallelClass(String clazz) throws Exception {
        // TODO: use aksw-commons-sparql instead of sparql-scala
        // SparqlTemplate st = SparqlTemplate.getInstance("parallelClass.vm");
        // st.setLimit(0);
        // VelocityContext vc = st.getVelocityContext();
        // vc.put("class", clazz);
        // String query = st.getQuery();
        // Set<String> parClasses = new
        // HashSet<String>(ResultSetRenderer.asStringSet(sparqlEndpoint.executeSelect(query)));
        // for (String s : parClasses) {
        // return s;
        // }
        SparqlTemplate st = SparqlTemplate.getInstance("parallelClass.vm");
        st.setLimit(0);
        VelocityContext vc = st.getVelocityContext();
        vc.put("class", clazz);
        String query = st.getQuery();
        Set<String> parClasses = this.executeClassQuery(query);
        for (String s : parClasses) {
            if (s.startsWith("http://dbpedia.org/ontology")) {
                if (!s.endsWith("Unknown")) {
                    return s;
                }
            }
        }
        return null;
    }
    
    public Set<String> executeResourceQuery(String queryString) {
        // Query query = QueryFactory.create(queryString);
        // QueryExecution qexec =
        // QueryExecutionFactory.sparqlService(endpointurl,
        // query);
        // ResultSet resultSet = qexec.execSelect();
        ResultSetRewindable resultSet = SparqlQuery.convertJSONtoResultSet(cache
                .executeSparqlQuery(new SparqlQuery(queryString, sparqlEndpoint)));
        QuerySolution solution;
        Set<String> results = new HashSet<String>();
        while (resultSet.hasNext()) {
            solution = resultSet.next();
            results.add(solution.getResource("instances").getURI());
        }
        return results;
    }
    
    public Set<String> executeClassQuery(String queryString) {
        // Query query = QueryFactory.create(queryString);
        // QueryExecution qexec =
        // QueryExecutionFactory.sparqlService(endpointurl,
        // query);
        // ResultSet resultSet = qexec.execSelect();
        ResultSetRewindable resultSet = SparqlQuery.convertJSONtoResultSet(cache
                .executeSparqlQuery(new SparqlQuery(queryString, sparqlEndpoint)));
        QuerySolution solution;
        Set<String> results = new HashSet<String>();
        while (resultSet.hasNext()) {
            solution = resultSet.next();
            results.add(solution.getResource("sub").getURI());
        }
        return results;
    }
    
    private void dropCache() {
        try {
            Class.forName("org.h2.Driver");
            String databaseName = "extraction";
            String databaseDirectory = "cache";
            Connection conn = DriverManager.getConnection("jdbc:h2:" + databaseDirectory + "/"
                    + databaseName, "sa", "");
            Statement st = conn.createStatement();
            st.execute("DELETE FROM QUERY_CACHE");
            st.close();
            conn.close();
            System.gc();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}