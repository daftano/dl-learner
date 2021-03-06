package org.dllearner.tools.protege;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.dllearner.algorithms.celoe.CELOE;
import org.dllearner.core.ComponentInitException;
import org.dllearner.core.EvaluatedDescription;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.kb.OWLAPIOntology;
import org.dllearner.learningproblems.ClassLearningProblem;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;
import org.dllearner.reasoning.ProtegeReasoner;
import org.dllearner.refinementoperators.RhoDRDown;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.dllearner.utilities.owl.OWLAPIDescriptionConvertVisitor;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class Manager implements OWLModelManagerListener, OWLSelectionModelListener, Disposable{
	
	private static Manager instance;
	
	private OWLEditorKit editorKit;
	private ReasonerProgressMonitor progressMonitor;
	
	private boolean reinitNecessary = true;
	
	private ClassLearningProblem lp;
	private CELOE la;
	private ProtegeReasoner reasoner;
	private KnowledgeSource ks;
	
	private LearningType learningType;
	private int maxExecutionTimeInSeconds;
	private double noisePercentage;
	private double threshold;
	private int maxNrOfResults;
	private boolean useAllConstructor;
	private boolean useExistsConstructor;
	private boolean useHasValueConstructor;
	private boolean useNegation;
	private boolean useCardinalityRestrictions;
	private int cardinalityLimit;
	
	private volatile boolean isPreparing = false;
	
	public static synchronized Manager getInstance(OWLEditorKit editorKit){
		if(instance == null){
			instance = new Manager(editorKit);
		}
		return instance;
	}
	
	public static synchronized Manager getInstance(){
		return instance;
	}
	
	private Manager(OWLEditorKit editorKit){
		this.editorKit = editorKit;
	}
	
	public void setOWLEditorKit(OWLEditorKit editorKit){
		this.editorKit = editorKit;
	}

	public boolean isReinitNecessary(){
		return reinitNecessary;
	}
	
	public void init() throws Exception{
		initKnowledgeSource();
		if(reinitNecessary){
			initReasoner();
		}
		initLearningProblem();
		initLearningAlgorithm();
		reinitNecessary = false;
	}
	
	public void initLearningAlgorithm() throws Exception {
		System.out.print("Initializing learning algorithm...");
		long startTime = System.currentTimeMillis();
		la = new CELOE(lp, reasoner);
		
		RhoDRDown op = new RhoDRDown();
		op.setReasoner(reasoner);
		op.setUseNegation(useNegation);
		op.setUseHasValueConstructor(useAllConstructor);
		op.setUseCardinalityRestrictions(useCardinalityRestrictions);
		if(useCardinalityRestrictions){
			op.setCardinalityLimit(cardinalityLimit);
		}
		op.setUseExistsConstructor(useExistsConstructor);
		op.setUseHasValueConstructor(useHasValueConstructor);
		op.init();
		
		la.setOperator(op);
		
		la.setMaxExecutionTimeInSeconds(maxExecutionTimeInSeconds);
		la.setNoisePercentage(noisePercentage);
		la.setMaxNrOfResults(maxNrOfResults);

		la.init();
		System.out.println("done in " + (System.currentTimeMillis()-startTime) + "ms.");

	}
	
	public void initLearningProblem() throws Exception {
		System.out.print("Initializing learning problem...");
		long startTime = System.currentTimeMillis();
		lp = new ClassLearningProblem(reasoner);
		lp.setClassToDescribe(new NamedClass(editorKit.getOWLWorkspace()
				.getOWLSelectionModel().getLastSelectedClass().getIRI().toURI()));
		lp.setEquivalence(learningType == LearningType.EQUIVALENT);
		lp.setCheckConsistency(DLLearnerPreferences.getInstance().isCheckConsistencyWhileLearning());

		lp.init();
		System.out.println("Done in " + (System.currentTimeMillis()-startTime) + "ms.");
	}
	
	public void initKnowledgeSource() throws Exception{
		ks = new OWLAPIOntology(editorKit.getOWLModelManager().getActiveOntology());
		ks.init();
	}
	
	public void initReasoner() throws Exception{
		System.out.print("Initializing DL-Learner internal reasoner...");
		long startTime = System.currentTimeMillis();
		reasoner = new ProtegeReasoner(Collections.singleton(ks), editorKit.getOWLModelManager().getReasoner());
		reasoner.setProgressMonitor(progressMonitor);
		reasoner.init();
		System.out.println("Done in " + (System.currentTimeMillis()-startTime) + "ms.");
	}
	
	public void initReasonerAsynchronously(){
		reasoner = new ProtegeReasoner(Collections.singleton(ks), editorKit.getOWLModelManager().getReasoner());
		reasoner.setOWLReasoner(editorKit.getOWLModelManager().getReasoner());
		reasoner.setProgressMonitor(progressMonitor);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					reasoner.init();
				} catch (ComponentInitException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public void addAxiom(EvaluatedDescription description){
		OWLClass selectedClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
		OWLClassExpression exp = OWLAPIDescriptionConvertVisitor.getOWLClassExpression(description.getDescription());
		if(learningType == LearningType.EQUIVALENT){
			addEquivalentClassesAxiom(selectedClass, exp);
		} else {
			addSuperClassAxiom(selectedClass, exp);
		}
	}
	
	private void addSuperClassAxiom(OWLClassExpression subClass, OWLClassExpression superClass){
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLAxiom subClassAxiom = manager.getOWLDataFactory().getOWLSubClassOfAxiom(subClass, superClass);
		OWLOntologyChange change = new AddAxiom(ontology, subClassAxiom);
		manager.applyChange(change);
	}
	
	private void addEquivalentClassesAxiom(OWLClassExpression equiv1, OWLClassExpression equiv2){
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLAxiom equivalentClassesAxiom = manager.getOWLDataFactory().getOWLEquivalentClassesAxiom(equiv1, equiv2);
		OWLOntologyChange change = new AddAxiom(ontology, equivalentClassesAxiom);
		manager.applyChange(change);
	}
	
	public void setProgressMonitor(ReasonerProgressMonitor progressMonitor){
		this.progressMonitor = progressMonitor;
	}
	
	public void startLearning(){
		System.out.print("Started learning algorithm...");
		la.start();
		System.out.println("Done.");
	}
	
	public void stopLearning(){
		System.out.println("Stopped learning algorithm.");
		la.stop();
	}
	
	public boolean isLearning(){
		return la != null && la.isRunning();
	}
	
	public LearningType getLearningType() {
		return learningType;
	}

	public void setLearningType(LearningType learningType) {
		this.learningType = learningType;
	}

	public int getMaxExecutionTimeInSeconds() {
		return maxExecutionTimeInSeconds;
	}

	public void setMaxExecutionTimeInSeconds(int maxExecutionTimeInSeconds) {
		this.maxExecutionTimeInSeconds = maxExecutionTimeInSeconds;
	}

	public void setNoisePercentage(double noisePercentage) {
		this.noisePercentage = noisePercentage;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getMaxNrOfResults() {
		return maxNrOfResults;
	}

	public void setMaxNrOfResults(int maxNrOfResults) {
		this.maxNrOfResults = maxNrOfResults;
	}

	public void setUseAllConstructor(boolean useAllConstructor) {
		this.useAllConstructor = useAllConstructor;
	}

	public void setUseExistsConstructor(boolean useExistsConstructor) {
		this.useExistsConstructor = useExistsConstructor;
	}

	public void setUseHasValueConstructor(boolean useHasValueConstructor) {
		this.useHasValueConstructor = useHasValueConstructor;
	}

	public void setUseNegation(boolean useNegation) {
		this.useNegation = useNegation;
	}

	public void setUseCardinalityRestrictions(boolean useCardinalityRestrictions) {
		this.useCardinalityRestrictions = useCardinalityRestrictions;
	}

	public void setCardinalityLimit(int cardinalityLimit) {
		this.cardinalityLimit = cardinalityLimit;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized List<EvaluatedDescriptionClass> getCurrentlyLearnedDescriptions() {
		List<EvaluatedDescriptionClass> result;
		if (la != null) {
			result = Collections.unmodifiableList((List<EvaluatedDescriptionClass>) la
					.getCurrentlyBestEvaluatedDescriptions(maxNrOfResults, threshold, true));
		} else {
			result = Collections.emptyList();
		}
		return result;
	}
	
	public int getMinimumHorizontalExpansion(){
		return ((CELOE)la).getMinimumHorizontalExpansion();
	}
	
	public int getMaximumHorizontalExpansion(){
		return ((CELOE)la).getMaximumHorizontalExpansion();
	}
	
	public boolean isConsistent(){
		return reasoner.isConsistent();
	}
	
	public SortedSet<Individual> getIndividuals(){
		OWLClass selectedClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
		return reasoner.getIndividuals(OWLAPIConverter.convertClass(selectedClass));
	}
	
	public boolean canLearn(){
		OWLClass selectedClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
		boolean canLearn = reasoner.getIndividuals(OWLAPIConverter.convertClass(selectedClass)).size() > 0;
		return canLearn;
	}
	
	public String getRendering(Description desc){
		String rendering = editorKit.getModelManager().getRendering(
				OWLAPIDescriptionConvertVisitor.getOWLClassExpression(desc));
		return rendering;
	}
	
	public String getRendering(OWLClass cl){
		String rendering = editorKit.getModelManager().getRendering(cl);
		return rendering;
	}
	
	public String getRendering(Individual ind){
		String rendering = editorKit.getModelManager().getRendering(
				OWLAPIConverter.getOWLAPIIndividual(ind));
		return rendering;
	}
	
	public ProtegeReasoner getReasoner(){
		return reasoner;
	}
	
	public OWLOntology getActiveOntology(){
		return editorKit.getOWLModelManager().getActiveOntology();
	}
	
	public OWLClass getCurrentlySelectedClass(){
		return editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
	}
	
	public String getCurrentlySelectedClassRendered(){
		return getRendering(getCurrentlySelectedClass());
	}
	
	public synchronized void setIsPreparing(boolean isPreparing){
		this.isPreparing = isPreparing;
	}
	
	public synchronized boolean isPreparing(){
		return isPreparing;
	}
	
	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if(event.isType(EventType.REASONER_CHANGED) || event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)){
			reinitNecessary = true;
		}
	}

	@Override
	public void selectionChanged() throws Exception {
	}

	@Override
	public void dispose() throws Exception {
		reasoner.releaseKB();
		editorKit.getOWLModelManager().removeListener(this);
		editorKit.getOWLWorkspace().getOWLSelectionModel().removeListener(this);
		
	}

}
