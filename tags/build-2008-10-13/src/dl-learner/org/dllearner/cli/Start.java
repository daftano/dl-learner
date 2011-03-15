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
package org.dllearner.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.dllearner.Info;
import org.dllearner.algorithms.BruteForceLearner;
import org.dllearner.algorithms.RandomGuesser;
import org.dllearner.algorithms.gp.GP;
import org.dllearner.algorithms.refexamples.ExampleBasedROLComponent;
import org.dllearner.algorithms.refinement.ROLearner;
import org.dllearner.core.Component;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.ComponentManager;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.LearningAlgorithm;
import org.dllearner.core.LearningProblem;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.OntologyFormat;
import org.dllearner.core.ReasonerComponent;
import org.dllearner.core.ReasoningService;
import org.dllearner.core.Score;
import org.dllearner.core.config.BooleanConfigOption;
import org.dllearner.core.config.ConfigEntry;
import org.dllearner.core.config.ConfigOption;
import org.dllearner.core.config.DoubleConfigOption;
import org.dllearner.core.config.IntegerConfigOption;
import org.dllearner.core.config.InvalidConfigOptionValueException;
import org.dllearner.core.config.StringConfigOption;
import org.dllearner.core.config.StringSetConfigOption;
import org.dllearner.core.config.StringTupleListConfigOption;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.core.owl.ObjectProperty;
import org.dllearner.kb.KBFile;
import org.dllearner.kb.OWLFile;
import org.dllearner.kb.sparql.SparqlKnowledgeSource;
import org.dllearner.learningproblems.PosNegDefinitionLP;
import org.dllearner.learningproblems.PosNegInclusionLP;
import org.dllearner.learningproblems.PosOnlyDefinitionLP;
import org.dllearner.parser.ConfParser;
import org.dllearner.parser.KBParser;
import org.dllearner.parser.ParseException;
import org.dllearner.parser.TokenMgrError;
import org.dllearner.reasoning.DIGReasoner;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.reasoning.FastRetrievalReasoner;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.dllearner.utilities.Files;
import org.dllearner.utilities.Helper;
import org.dllearner.utilities.JamonMonitorLogger;
import org.dllearner.utilities.datastructures.Datastructures;
import org.dllearner.utilities.datastructures.StringTuple;
import org.dllearner.utilities.owl.ConceptComparator;
import org.dllearner.utilities.owl.RoleComparator;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * Startup file for Command Line Interface.
 * 
 * @author Jens Lehmann
 * 
 */
public class Start {

	private static Logger logger = Logger.getRootLogger();

	private static ConfMapper confMapper = new ConfMapper();
	
	private Set<KnowledgeSource> sources;
	private LearningAlgorithm la;
	private LearningProblem lp;
	private ReasoningService rs;
	private ReasonerComponent rc;

	/**
	 * Entry point for CLI interface.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		
		System.out.println("DL-Learner " + Info.build + " command line interface");
		
		if(args.length == 0) {
			System.out.println("You need to give a conf file as argument.");
			System.exit(0);
		}
		
		File file = new File(args[args.length - 1]);
		
		if(!file.exists()) {
			System.out.println("File \"" + file + "\" does not exist.");
			System.exit(0);			
		}

		boolean inQueryMode = false;
		if (args.length > 1 && args[0].equals("-q")) {
			inQueryMode = true;
		}

		// create loggers (a simple logger which outputs
		// its messages to the console and a log file)
		
		// logger 1 is the console, where we print only info messages;
		// the logger is plain, i.e. does not output log level etc.
		Layout layout = new PatternLayout();
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		consoleAppender.setThreshold(Level.INFO);
		
		// logger 2 is writes to a file; it records all debug messages
		// and includes the log level
		Layout layout2 = new SimpleLayout();
		FileAppender fileAppenderNormal = null;
		File f = new File("log/sparql.txt");
		try {
		    	fileAppenderNormal = new FileAppender(layout2, "log/log.txt", false);
		    	f.delete();
		    	f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		fileAppenderNormal.setThreshold(Level.DEBUG);
		
		logger.removeAllAppenders();
		logger.addAppender(consoleAppender);
		logger.addAppender(fileAppenderNormal);
		logger.setLevel(Level.DEBUG);
		
//		Logger.getLogger(KnowledgeSource.class).setLevel(Level.WARN);
//		Logger.getLogger(SparqlKnowledgeSource.class).setLevel(Level.WARN);
//		Logger.getLogger(TypedSparqlQuery.class).setLevel(Level.WARN);

		Start start = null;
		try {
			start = new Start(file);
		} catch (FileNotFoundException e) {
			System.out.println("The specified file " + file + " does not exist. See stack trace below.");
			e.printStackTrace();
			System.exit(0);
		} catch (ComponentInitException e) {
			System.out.println("A component could not be initialised. See stack trace below.");
			e.printStackTrace();
			System.exit(0);
		} catch (ParseException e) {
			System.out.println("The specified file " + file + " is not a valid conf file. See stack trace below.");
			e.printStackTrace();
			System.exit(0);
		}
		start.start(inQueryMode);
		// write JaMON report in HTML file
		File jamonlog = new File("log/jamon.html");
		Files.createFile(jamonlog, MonitorFactory.getReport());
		Files.appendFile(jamonlog, "<xmp>\n"+JamonMonitorLogger.getStringForAllSortedByLabel());
	}

	/**
	 * Initialise all components based on conf file.
	 * 
	 * @param file
	 *            Conf file to read.
	 * @throws ComponentInitException
	 * @throws ParseException 
	 * @throws FileNotFoundException 
	 */
	public Start(File file) throws ComponentInitException, FileNotFoundException, ParseException {
		String baseDir = file.getParentFile().getPath();

		// create component manager instance
		String message = "starting component manager ... ";
		long cmStartTime = System.nanoTime();
		ComponentManager cm = ComponentManager.getInstance();
		long cmTime = System.nanoTime() - cmStartTime;
		message += "OK (" + Helper.prettyPrintNanoSeconds(cmTime) + ")";
		logger.info(message);

		// create a mapping between components and prefixes in the conf file and back
//		Map<Class<? extends Component>, String> componentPrefixMapping = createComponentPrefixMapping();
//		Map<String, Class<? extends Component>> prefixComponentMapping = invertComponentPrefixMapping(componentPrefixMapping);

		// parse conf file
		ConfParser parser = ConfParser.parseFile(file);

		// step 1: detect knowledge sources
		Monitor ksMonitor = JamonMonitorLogger.getTimeMonitor(Start.class, "initKnowledgeSource").start();
		sources = new HashSet<KnowledgeSource>();
		Map<URL, Class<? extends KnowledgeSource>> importedFiles = getImportedFiles(parser, baseDir);
		for (Map.Entry<URL, Class<? extends KnowledgeSource>> entry : importedFiles.entrySet()) {
			KnowledgeSource ks = cm.knowledgeSource(entry.getValue());
			// apply URL entry (this assumes that every knowledge source has a
			// configuration option "url"), so this may need to be changed in
			// the
			// future
			cm.applyConfigEntry(ks, "url", entry.getKey());

			sources.add(ks);
			configureComponent(cm, ks, parser);
			initComponent(cm, ks);
		}
		ksMonitor.stop();

		
		// step 2: detect used reasoner
		Monitor rsMonitor = JamonMonitorLogger.getTimeMonitor(Start.class, "initReasoningService").start();
		ConfFileOption reasonerOption = parser.getConfOptionsByName("reasoner");
		Class<? extends ReasonerComponent> rcClass;
		if(reasonerOption != null) {
			rcClass = confMapper.getReasonerComponentClass(reasonerOption.getStringValue());
			if(rcClass == null) {
				handleError("Invalid value \"" + reasonerOption.getStringValue() + "\" in " + reasonerOption + ". Valid values are " + confMapper.getReasoners() + ".");
			}			
		} else {
			rcClass = FastInstanceChecker.class;
		}
		rc = cm.reasoner(rcClass, sources);
		configureComponent(cm, rc, parser);
		initComponent(cm, rc);
		rs = cm.reasoningService(rc);
		rsMonitor.stop();

		// step 3: detect learning problem
		Monitor lpMonitor = JamonMonitorLogger.getTimeMonitor(Start.class, "initLearningProblem").start();
		ConfFileOption problemOption = parser.getConfOptionsByName("problem");
		Class<? extends LearningProblem> lpClass;
		if(problemOption != null) {
			lpClass = confMapper.getLearningProblemClass(problemOption.getStringValue());
			if(lpClass == null) {
				handleError("Invalid value \"" + problemOption.getStringValue() + "\" in " + problemOption + ". Valid values are " + confMapper.getLearningProblems() + ".");
			}			
		} else {
			lpClass = PosNegDefinitionLP.class;
		}
		lp = cm.learningProblem(lpClass, rs);
		SortedSet<String> posExamples = parser.getPositiveExamples();
		SortedSet<String> negExamples = parser.getNegativeExamples();
		cm.applyConfigEntry(lp, "positiveExamples", posExamples);
		if (lpClass != PosOnlyDefinitionLP.class)
			cm.applyConfigEntry(lp, "negativeExamples", negExamples);
		configureComponent(cm, lp, parser);
		initComponent(cm, lp);
		lpMonitor.stop();

		// step 4: detect learning algorithm
		Monitor laMonitor = JamonMonitorLogger.getTimeMonitor(Start.class, "initLearningAlgorithm").start();
		ConfFileOption algorithmOption = parser.getConfOptionsByName("algorithm");
		Class<? extends LearningAlgorithm> laClass;
		if(algorithmOption != null) {
			laClass = confMapper.getLearningAlgorithmClass(algorithmOption.getStringValue());
			if(laClass == null) {
				handleError("Invalid value \"" + algorithmOption.getStringValue() + "\" in " + algorithmOption + ". Valid values are " + confMapper.getLearningAlgorithms() + ".");
			}			
		} else {
			laClass = ExampleBasedROLComponent.class;
		}		
		try {
			la = cm.learningAlgorithm(laClass, lp, rs);
		} catch (LearningProblemUnsupportedException e) {
			e.printStackTrace();
		}
		configureComponent(cm, la, parser);
		initComponent(cm, la);
		laMonitor.stop();

		// perform file exports
		performExports(parser, baseDir, sources, rs);

		// handle any CLI options
		processCLIOptions(cm, parser, rs, lp);
	}

	public void start(boolean inQueryMode) {
		if (inQueryMode)
			processQueryMode(lp, rs);
		else {
			// start algorithm
			long algStartTime = System.nanoTime();
			la.start();
			long algDuration = System.nanoTime() - algStartTime;

			printConclusions(rs, algDuration);
		}
	}

	/**
	 * @deprecated See ConfMapper.
	 * creates a mapping from components to option prefix strings
	 */
	@Deprecated
	public static Map<Class<? extends Component>, String> createComponentPrefixMapping() {
		Map<Class<? extends Component>, String> componentPrefixMapping = new HashMap<Class<? extends Component>, String>();
		// knowledge sources
		componentPrefixMapping.put(SparqlKnowledgeSource.class, "sparql");
		// reasoners
		componentPrefixMapping.put(DIGReasoner.class, "digReasoner");
		componentPrefixMapping.put(FastInstanceChecker.class, "fastInstanceChecker");
		componentPrefixMapping.put(OWLAPIReasoner.class, "owlAPIReasoner");
		componentPrefixMapping.put(FastRetrievalReasoner.class, "fastRetrieval");

		// learning problems - configured via + and - flags for examples
		componentPrefixMapping.put(PosNegDefinitionLP.class, "posNegDefinitionLP");
		componentPrefixMapping.put(PosNegInclusionLP.class, "posNegInclusionLP");
		componentPrefixMapping.put(PosOnlyDefinitionLP.class, "posOnlyDefinitionLP");

		// learning algorithms
		componentPrefixMapping.put(ROLearner.class, "refinement");
		componentPrefixMapping.put(ExampleBasedROLComponent.class, "refexamples");
		componentPrefixMapping.put(GP.class, "gp");
		componentPrefixMapping.put(BruteForceLearner.class, "bruteForce");
		componentPrefixMapping.put(RandomGuesser.class, "random");

		return componentPrefixMapping;
	}
	
	/**
	 * convenience method basically every prefix (e.g. "refinement" in
	 * "refinement.horizontalExpFactor) corresponds to a specific component -
	 * this way the CLI will automatically support any configuration options
	 * supported by the component
	 */
	private static void configureComponent(ComponentManager cm, Component component,
			ConfParser parser) {
		String prefix = confMapper.getComponentString(component.getClass());
		if (prefix != null)
			configureComponent(cm, component, parser.getConfOptionsByPrefix(prefix));
	}

	// convenience method - see above method
	private static void configureComponent(ComponentManager cm, Component component,
			List<ConfFileOption> options) {
		if (options != null)
			for (ConfFileOption option : options)
				applyConfFileOption(cm, component, option);
	}

	// applies an option to a component - checks whether the option and its
	// value is valid
	private static void applyConfFileOption(ComponentManager cm, Component component,
			ConfFileOption option) {
		// the name of the option is suboption-part (the first part refers
		// to its component)
		String optionName = option.getSubOption();

		ConfigOption<?> configOption = cm.getConfigOption(component.getClass(), optionName);
		// check whether such an option exists
		if (configOption != null) {

			// catch all invalid config options
			try {

				// perform compatibility checks
				if (configOption instanceof StringConfigOption && option.isStringOption()) {

					ConfigEntry<String> entry = new ConfigEntry<String>(
							(StringConfigOption) configOption, option.getStringValue());
					cm.applyConfigEntry(component, entry);

				} else if (configOption instanceof IntegerConfigOption && option.isIntegerOption()) {

					ConfigEntry<Integer> entry = new ConfigEntry<Integer>(
							(IntegerConfigOption) configOption, option.getIntValue());
					cm.applyConfigEntry(component, entry);

				} else if (configOption instanceof DoubleConfigOption
						&& (option.isIntegerOption() || option.isDoubleOption())) {

					double value;
					if (option.isIntegerOption())
						value = option.getIntValue();
					else
						value = option.getDoubleValue();

					ConfigEntry<Double> entry = new ConfigEntry<Double>(
							(DoubleConfigOption) configOption, value);
					cm.applyConfigEntry(component, entry);

				} else if (configOption instanceof BooleanConfigOption && option.isStringOption()) {

					ConfigEntry<Boolean> entry = new ConfigEntry<Boolean>(
							(BooleanConfigOption) configOption, Datastructures.strToBool(option
									.getStringValue()));
					cm.applyConfigEntry(component, entry);

				} else if (configOption instanceof StringSetConfigOption && option.isSetOption()) {

					ConfigEntry<Set<String>> entry = new ConfigEntry<Set<String>>(
							(StringSetConfigOption) configOption, option.getSetValues());
					cm.applyConfigEntry(component, entry);

				} else if (configOption instanceof StringTupleListConfigOption
						&& option.isListOption()) {

					ConfigEntry<List<StringTuple>> entry = new ConfigEntry<List<StringTuple>>(
							(StringTupleListConfigOption) configOption, option.getListTuples());
					cm.applyConfigEntry(component, entry);

				} else {
					handleError("The type of conf file entry \"" + option.getFullName()
							+ "\" is not correct: value \"" + option.getValue()
							+ "\" not valid for option type \"" + configOption.getClass().getName()
							+ "\".");
				}

			} catch (InvalidConfigOptionValueException e) {
				e.printStackTrace();
				System.exit(0);
			}

		} else
			handleError("Unknow option " + option + ".");
	}

	/**
	 * detects all imported files and their format
	 */
	public static Map<URL, Class<? extends KnowledgeSource>> getImportedFiles(ConfParser parser,
			String baseDir) {
		List<List<String>> imports = parser.getFunctionCalls().get("import");
		Map<URL, Class<? extends KnowledgeSource>> importedFiles = new HashMap<URL, Class<? extends KnowledgeSource>>();

		if (imports != null) {
			for (List<String> arguments : imports) {
				// step 1: detect URL
				URL url = null;
				try {
					String fileString = arguments.get(0);
					if (fileString.startsWith("http:") || fileString.startsWith("file:")) {
						url = new URL(fileString);
					} else {
						File f = new File(baseDir, arguments.get(0));
						url = f.toURI().toURL();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				// step 2: detect format
				Class<? extends KnowledgeSource> ksClass;
				if (arguments.size() == 1) {
					String filename = url.getPath();
					String ending = filename.substring(filename.lastIndexOf(".") + 1);

					if (ending.equals("rdf") || ending.equals("owl"))
						ksClass = OWLFile.class;
					else if (ending.equals("nt"))
						ksClass = OWLFile.class;
					else if (ending.equals("kb"))
						ksClass = KBFile.class;
					else {
						System.err.println("Warning: no format given for " + arguments.get(0)
								+ " and could not detect it. Chosing RDF/XML.");
						ksClass = OWLFile.class;
					}

					importedFiles.put(url, ksClass);
				} else {
					String formatString = arguments.get(1);

					if (formatString.equals("RDF/XML"))
						ksClass = OWLFile.class;
					else if (formatString.equals("KB"))
						ksClass = KBFile.class;
					else if (formatString.equals("SPARQL"))
						ksClass = SparqlKnowledgeSource.class;
					else if (formatString.equals("NT"))
						ksClass = OWLFile.class;
					else {
						throw new RuntimeException("Unsupported knowledge source format "
								+ formatString + ". Exiting.");
					}

					importedFiles.put(url, ksClass);
				}
			}
		}

		return importedFiles;
	}

	private static void performExports(ConfParser parser, String baseDir,
			Set<KnowledgeSource> sources, ReasoningService rs) {
		List<List<String>> exports = parser.getFunctionCalls().get("export");

		if (exports == null)
			return;

		File file = null;
		OntologyFormat format = null;
		for (List<String> export : exports) {
			file = new File(baseDir, export.get(0));
			if (export.size() == 1)
				// use RDF/XML by default
				format = OntologyFormat.RDF_XML;
			// rs.saveOntology(file, OntologyFileFormat.RDF_XML);
			else {
				String formatString = export.get(1);
				// OntologyFileFormat format;
				if (formatString.equals("RDF/XML"))
					format = OntologyFormat.RDF_XML;
				else
					format = OntologyFormat.N_TRIPLES;
				// rs.saveOntology(file, format);
			}
		}
		// hack: ideally we would have the possibility to export each knowledge
		// source to specified files with specified formats (and maybe including
		// the option to merge them all in one file)
		// however implementing this requires quite some effort so for the
		// moment we just stick to exporting KB files (moreover all but the last
		// export statement are ignored)
		for (KnowledgeSource source : sources) {
			if (source instanceof KBFile)
				((KBFile) source).export(file, format);
		}
	}

	private static void processCLIOptions(ComponentManager cm, ConfParser parser,
			ReasoningService rs, LearningProblem lp) {
		// CLI options (i.e. options which are related to the CLI
		// user interface but not to one of the components)
		List<ConfFileOption> cliOptions = parser.getConfOptionsByPrefix("cli");
		if (cliOptions != null) {
			int maxLineLength = 100;
			for (ConfFileOption cliOption : cliOptions) {
				String name = cliOption.getSubOption();
				if (name.equals("showExamples")) {
					// show examples (display each one if they do not take up
					// much space,
					// otherwise just show the number of examples)
					SortedSet<String> posExamples = parser.getPositiveExamples();
					SortedSet<String> negExamples = parser.getNegativeExamples();
					boolean oneLineExampleInfo = true;
					int maxExampleStringLength = Math.max(posExamples.toString().length(),
							negExamples.toString().length());
					if (maxExampleStringLength > 100)
						oneLineExampleInfo = false;
					if (oneLineExampleInfo) {
						System.out.println("positive examples[" + posExamples.size() + "]: "
								+ posExamples);
						System.out.println("negative examples[" + negExamples.size() + "]: "
								+ negExamples);
					} else {
						System.out.println("positive examples[" + posExamples.size() + "]: ");
						for (String ex : posExamples)
							System.out.println("  " + ex);
						System.out.println("negative examples[" + negExamples.size() + "]: ");
						for (String ex : negExamples)
							System.out.println("  " + ex);
					}
				} else if (name.equals("showIndividuals")) {
					if (cliOption.getStringValue().equals("true")) {
						int stringLength = rs.getIndividuals().toString().length();
						if (stringLength > maxLineLength) {
							System.out.println("individuals[" + rs.getIndividuals().size() + "]: ");
							for (Individual ind : rs.getIndividuals())
								System.out.println("  " + ind);
						} else
							System.out.println("individuals[" + rs.getIndividuals().size() + "]: "
									+ rs.getIndividuals());
					}
				} else if (name.equals("showConcepts")) {
					if (cliOption.getStringValue().equals("true")) {
						int stringLength = rs.getNamedClasses().toString().length();
						if (stringLength > maxLineLength) {
							System.out.println("concepts[" + rs.getNamedClasses().size() + "]: ");
							for (NamedClass ac : rs.getNamedClasses())
								System.out.println("  " + ac);
						} else
							System.out.println("concepts[" + rs.getNamedClasses().size() + "]: "
									+ rs.getNamedClasses());
					}
				} else if (name.equals("showRoles")) {
					if (cliOption.getStringValue().equals("true")) {
						int stringLength = rs.getObjectProperties().toString().length();
						if (stringLength > maxLineLength) {
							System.out.println("roles[" + rs.getObjectProperties().size() + "]: ");
							for (ObjectProperty r : rs.getObjectProperties())
								System.out.println("  " + r);
						} else
							System.out.println("roles[" + rs.getObjectProperties().size() + "]: "
									+ rs.getObjectProperties());
					}
				} else if (name.equals("showSubsumptionHierarchy")) {
					if (cliOption.getStringValue().equals("true")) {
						System.out.println("Subsumption Hierarchy:");
						System.out.println(rs.getSubsumptionHierarchy());
					}
					// satisfiability check
				} else if (name.equals("checkSatisfiability")) {
					if (cliOption.getStringValue().equals("true")) {
						System.out.print("Satisfiability Check ... ");
						long satStartTime = System.nanoTime();
						boolean satisfiable = rs.isSatisfiable();
						long satDuration = System.nanoTime() - satStartTime;

						String result = satisfiable ? "OK" : "not satisfiable!";
						System.out.println(result + " ("
								+ Helper.prettyPrintNanoSeconds(satDuration, true, false) + ")");
						if (!satisfiable)
							System.exit(0);
					}
				} else if (name.equals("logLevel")) {
					String level = cliOption.getStringValue();
					if (level.equals("off"))
						logger.setLevel(Level.OFF);
					else if (level.equals("trace"))
						logger.setLevel(Level.TRACE);
					else if (level.equals("info"))
						logger.setLevel(Level.INFO);
					else if (level.equals("debug"))
						logger.setLevel(Level.DEBUG);
					else if (level.equals("warn"))
						logger.setLevel(Level.WARN);
					else if (level.equals("error"))
						logger.setLevel(Level.ERROR);
					else if (level.equals("fatal"))
						logger.setLevel(Level.FATAL);
				} else
					handleError("Unknown CLI option \"" + name + "\".");
			}
		}
	}

	private static void initComponent(ComponentManager cm, Component component)
			throws ComponentInitException {
		String startMessage = "initialising component \""
				+ cm.getComponentName(component.getClass()) + "\" ... ";
		long initStartTime = System.nanoTime();
		component.init();
		// standard messsage is just "OK" but can be more detailed for certain
		// components
		String message = "OK";
		if (component instanceof KBFile)
			message = ((KBFile) component).getURL().toString() + " read";
		else if (component instanceof DIGReasoner) {
			DIGReasoner reasoner = (DIGReasoner) component;
			message = "using " + reasoner.getIdentifier() + " connected via DIG 1.1 at "
					+ reasoner.getReasonerURL().toString();
		}

		long initTime = System.nanoTime() - initStartTime;
		logger.info(startMessage + message + " ("
				+ Helper.prettyPrintNanoSeconds(initTime, false, false) + ")");
	}

	private static void printConclusions(ReasoningService rs, long algorithmDuration) {
		if (rs.getNrOfRetrievals() > 0) {
			logger.info("number of retrievals: " + rs.getNrOfRetrievals());
			logger.info("retrieval reasoning time: "
							+ Helper.prettyPrintNanoSeconds(rs.getRetrievalReasoningTimeNs())
							+ " ( " + Helper.prettyPrintNanoSeconds(rs.getTimePerRetrievalNs())
							+ " per retrieval)");
		}
		if (rs.getNrOfInstanceChecks() > 0) {
			logger.info("number of instance checks: " + rs.getNrOfInstanceChecks() + " ("
					+ rs.getNrOfMultiInstanceChecks() + " multiple)");
			logger.info("instance check reasoning time: "
					+ Helper.prettyPrintNanoSeconds(rs.getInstanceCheckReasoningTimeNs()) + " ( "
					+ Helper.prettyPrintNanoSeconds(rs.getTimePerInstanceCheckNs())
					+ " per instance check)");
		}
		if (rs.getNrOfSubsumptionHierarchyQueries() > 0) {
			logger.info("subsumption hierarchy queries: "
					+ rs.getNrOfSubsumptionHierarchyQueries());
			/*
			 * System.out.println("subsumption hierarchy reasoning time: " +
			 * Helper.prettyPrintNanoSeconds(rs
			 * .getSubsumptionHierarchyTimeNs()) + " ( " +
			 * Helper.prettyPrintNanoSeconds(rs
			 * .getTimePerSubsumptionHierarchyQueryNs()) + " per subsumption
			 * hierachy query)");
			 */
		}
		if (rs.getNrOfSubsumptionChecks() > 0) {
			logger.info("(complex) subsumption checks: " + rs.getNrOfSubsumptionChecks()
					+ " (" + rs.getNrOfMultiSubsumptionChecks() + " multiple)");
			logger.info("subsumption reasoning time: "
					+ Helper.prettyPrintNanoSeconds(rs.getSubsumptionReasoningTimeNs()) + " ( "
					+ Helper.prettyPrintNanoSeconds(rs.getTimePerSubsumptionCheckNs())
					+ " per subsumption check)");
		}
		DecimalFormat df = new DecimalFormat();
		double reasoningPercentage = 100 * rs.getOverallReasoningTimeNs()
				/ (double) algorithmDuration;
		logger.info("overall reasoning time: "
				+ Helper.prettyPrintNanoSeconds(rs.getOverallReasoningTimeNs()) + " ("
				+ df.format(reasoningPercentage) + "% of overall runtime)");
		logger.info("overall algorithm runtime: "
				+ Helper.prettyPrintNanoSeconds(algorithmDuration));
	}

	// performs a query - used for debugging learning examples
	private static void processQueryMode(LearningProblem lp, ReasoningService rs) {

		logger.info("Entering query mode. Enter a concept for performing "
				+ "retrieval or q to quit. Use brackets for complex expresssions,"
				+ "e.g. (a AND b).");
		String queryStr = "";
		do {

			logger.info("enter query: ");
			// read input string
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			try {
				queryStr = input.readLine();
				logger.debug(queryStr);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!(queryStr.equalsIgnoreCase("q") || queryStr.equalsIgnoreCase("quit"))) {

				// parse concept
				Description concept = null;
				boolean parsedCorrectly = true;

				try {
					concept = KBParser.parseConcept(queryStr);
				} catch (ParseException e1) {
					e1.printStackTrace();
					System.err
							.println("The concept you entered could not be parsed. Please try again.");
					parsedCorrectly = false;
				} catch (TokenMgrError e) {
					e.printStackTrace();
					System.err
							.println("An error occured during parsing. Please enter a syntactically valid concept.");
					parsedCorrectly = false;
				}

				if (parsedCorrectly) {
					// compute atomic roles and concepts used in concept
					SortedSet<NamedClass> occurringConcepts = new TreeSet<NamedClass>(
							new ConceptComparator());
					occurringConcepts.addAll(Helper.getAtomicConcepts(concept));
					SortedSet<ObjectProperty> occurringRoles = new TreeSet<ObjectProperty>(
							new RoleComparator());
					occurringRoles.addAll(Helper.getAtomicRoles(concept));

					// substract existing roles/concepts from detected
					// roles/concepts -> the resulting sets should be
					// empty, otherwise print a warning (the DIG reasoner
					// will just treat them as concepts about which it
					// has no knowledge - this makes it hard to
					// detect typos
					// (note that removeAll currently gives a different
					// result here, because the comparator of the argument
					// is used)
					for (NamedClass ac : rs.getNamedClasses())
						occurringConcepts.remove(ac);
					for (ObjectProperty ar : rs.getObjectProperties())
						occurringRoles.remove(ar);

					boolean nonExistingConstructs = false;
					if (occurringConcepts.size() != 0 || occurringRoles.size() != 0) {
						logger
								.debug("You used non-existing atomic concepts or roles. Please correct your query.");
						if (occurringConcepts.size() > 0)
							logger.debug("non-existing concepts: " + occurringConcepts);
						if (occurringRoles.size() > 0)
							logger.debug("non-existing roles: " + occurringRoles);
						nonExistingConstructs = true;
					}

					if (!nonExistingConstructs) {

						if (!queryStr.startsWith("(")
								&& (queryStr.contains("AND") || queryStr.contains("OR"))) {
							logger.info("Make sure you did not forget to use outer brackets.");
						}

						logger.info("The query is: " + concept.toKBSyntaxString() + ".");

						// pose retrieval query
						Set<Individual> result = null;
						result = rs.retrieval(concept);

						logger.info("retrieval result (" + result.size() + "): " + result);

						Score score = lp.computeScore(concept);
						logger.info(score);

					}
				}
			}// end if

		} while (!(queryStr.equalsIgnoreCase("q") || queryStr.equalsIgnoreCase("quit")));

	}

	/**
	 * error handling over the logger
	 * 
	 * @param message
	 *            is a string and you message for problem
	 */
	public static void handleError(String message) {
		logger.error(message);
		System.exit(0);
	}

	/**
	 * @return the sources
	 */
	public Set<KnowledgeSource> getSources() {
		return sources;
	}	
	
	public LearningAlgorithm getLearningAlgorithm() {
		return la;
	}

	public LearningProblem getLearningProblem() {
		return lp;
	}

	public ReasonerComponent getReasonerComponent() {
		return rc;
	}
	
	public ReasoningService getReasoningService() {
		return rs;
	}

	/**
	 * @deprecated See ConfMapper.
	 * @param componentSuperClass
	 * @return String.
	 */
	@Deprecated
	public static String getCLIMapping(String componentSuperClass){
		HashMap<String, String> m = new HashMap<String, String>();
		m.put("KnowledgeSource", "import");
		m.put("ReasonerComponent", "reasoner");
		m.put("PosNegLP", "problem");
		m.put("PosOnlyLP", "problem");
		m.put("LearningAlgorithm", "algorithm");
		return m.get(componentSuperClass);
	}

	/**
	 * Set Reasoner class. Define here all possible reasoners.
	 * 
	 * @deprecated See ConfMapper.
	 * @param reasonerOption
	 *            from config file
	 * @return reasonerClass reasoner class
	 */
	@Deprecated
	public static Class<? extends ReasonerComponent> getReasonerClass(ConfFileOption reasonerOption) {
		Class<? extends ReasonerComponent> reasonerClass = null;
		if (reasonerOption == null || reasonerOption.getStringValue().equals("fastInstanceChecker"))
			reasonerClass = FastInstanceChecker.class;
		else if (reasonerOption.getStringValue().equals("owlAPI"))
			reasonerClass = OWLAPIReasoner.class;
		else if (reasonerOption.getStringValue().equals("fastRetrieval"))
			reasonerClass = FastRetrievalReasoner.class;
		else if (reasonerOption.getStringValue().equals("dig"))
			reasonerClass = DIGReasoner.class;
		else {
			handleError("Unknown value " + reasonerOption.getStringValue()
					+ " for option \"reasoner\".");
		}
		return reasonerClass;
	}

	/**
	 * Set LearningProblem class. Define here all possible problems.
	 * 
	 * @deprecated See ConfMapper.
	 * @param problemOption
	 *            from config file
	 * @return lpClass learning problem class
	 */
	@Deprecated
	public static Class<? extends LearningProblem> getLearningProblemClass(
			ConfFileOption problemOption) {
		Class<? extends LearningProblem> lpClass = null;
		if (problemOption == null || problemOption.getStringValue().equals("posNegDefinitionLP"))
			lpClass = PosNegDefinitionLP.class;
		else if (problemOption.getStringValue().equals("posNegInclusionLP"))
			lpClass = PosNegInclusionLP.class;
		else if (problemOption.getStringValue().equals("posOnlyDefinitionLP"))
			lpClass = PosOnlyDefinitionLP.class;
		else
			handleError("Unknown value " + problemOption.getValue() + " for option \"problem\".");

		return lpClass;
	}

	/**
	 * Set LearningAlorithm class. Define here all possible learning algorithms.
	 * 
	 * @deprecated See ConfMapper.
	 * @param algorithmOption
	 *            from config file
	 * @return laClass learning algorithm class
	 */
	@Deprecated
	public static Class<? extends LearningAlgorithm> getLearningAlgorithm(
			ConfFileOption algorithmOption) {
		Class<? extends LearningAlgorithm> laClass = null;
		if (algorithmOption == null || algorithmOption.getStringValue().equals("refexamples"))
			laClass = ExampleBasedROLComponent.class;
		else if (algorithmOption.getStringValue().equals("refinement"))
			laClass = ROLearner.class;
		else if (algorithmOption.getStringValue().equals("gp"))
			laClass = GP.class;
		else if (algorithmOption.getStringValue().equals("bruteForce"))
			laClass = BruteForceLearner.class;
		else if (algorithmOption.getStringValue().equals("randomGuesser"))
			laClass = RandomGuesser.class;
		else
			handleError("Unknown value in " + algorithmOption);

		return laClass;
	}

}