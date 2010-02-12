package org.dllearner.tools.evaluationplugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dllearner.core.owl.NamedClass;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;
import org.dllearner.utilities.owl.ConceptComparator;
import org.dllearner.utilities.owl.OWLAPIDescriptionConvertVisitor;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;



public class EvaluationPlugin extends AbstractOWLViewComponent implements ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EvaluationTable evaluationTable;
	private GraphicalCoveragePanel coveragePanel;
	private JLabel inconsistencyLabel;
	private JButton nextSaveButton;
	private JLabel currentClassLabel;
	
	private List<NamedClass> classes = new ArrayList<NamedClass>();
	private int currentClassIndex = 0;
	
	
	private final ConceptComparator comparator = new ConceptComparator();
	
	private static final String CURRENT_CLASS_MESSAGE = "Showing equivalent class expressions for class ";
	private static final String INCONSISTENCY_WARNING = 
		"<html>Warning. Selected class expressions leads to an inconsistent ontology!<br>" +
		"(Often, suggestions leading to an inconsistency should still be added. They help to detect problems in " +
		"the ontology elsewhere.<br>" +
		" See http://dl-learner.org/files/screencast/protege/screencast.htm .)</html>";
	
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceStandardMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalencePredaccMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceGenFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceJaccardMap;

	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperStandardMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperPredaccMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperGenFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperJaccardMap;

	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceStandardMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalencePredaccMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceGenFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceJaccardMap;

	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperStandardMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperPredaccMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperGenFMeasureMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperJaccardMap;

	private Map<NamedClass, List<EvaluatedDescriptionClass>> defaultEquivalenceMap;
	private Map<NamedClass, List<EvaluatedDescriptionClass>> defaultSuperMap;

	
	@Override
	protected void initialiseOWLView() throws Exception {
		System.out.println("Initializing DL-Learner Evaluation Plugin...");
		createUI();
		parseEvaluationFile();
		showNextEvaluatedDescriptions();
	}
	
	@Override
	protected void disposeOWLView() {
		evaluationTable.getSelectionModel().removeListSelectionListener(this);
		evaluationTable.dispose();
	}
	
	/**
	 * Create the user interface.
	 */
	private void createUI(){
		setLayout(new BorderLayout());
		
		currentClassLabel = new JLabel();
		add(currentClassLabel, BorderLayout.NORTH);
		
		JPanel tableHolderPanel = new JPanel(new BorderLayout());
		evaluationTable = new EvaluationTable(getOWLEditorKit());
		evaluationTable.getSelectionModel().addListSelectionListener(this);
        JScrollPane sp = new JScrollPane(evaluationTable);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableHolderPanel.add(sp);
        inconsistencyLabel = new JLabel(INCONSISTENCY_WARNING);
        inconsistencyLabel.setForeground(getBackground());
        tableHolderPanel.add(inconsistencyLabel, BorderLayout.SOUTH);
        add(tableHolderPanel);
        
        
        JPanel coverageHolderPanel = new JPanel(new BorderLayout());
        coveragePanel = new GraphicalCoveragePanel(getOWLEditorKit());
        coverageHolderPanel.add(coveragePanel);
        
        nextSaveButton = new JButton();
        nextSaveButton.setActionCommand("next");
        nextSaveButton.setAction(new AbstractAction("Next"){
			/**
			 * 
			 */
			private static final long serialVersionUID = 6982520538511324236L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showNextEvaluatedDescriptions();
			}
        });
        coverageHolderPanel.add(nextSaveButton, BorderLayout.SOUTH);
        add(coverageHolderPanel, BorderLayout.SOUTH);
        
	}
	
	/**
	 * Show the descriptions for next class to evaluate.
	 */
	private void showNextEvaluatedDescriptions(){
		showInconsistencyWarning(false);
		NamedClass currentClass = classes.get(currentClassIndex++);
		
		//show the name for the current class in manchester syntax
		String renderedClass = getOWLModelManager().getRendering(OWLAPIDescriptionConvertVisitor.getOWLDescription(currentClass));
		currentClassLabel.setText(CURRENT_CLASS_MESSAGE + renderedClass);
		System.out.println("Showing evaluated descriptions for class " + currentClass.toString());
		
		//refresh coverage panel to the current class
		coveragePanel.setConcept(currentClass);
		
		//necessary to set the current class to evaluate as activated entity
		OWLDescription desc = OWLAPIDescriptionConvertVisitor.getOWLDescription(currentClass);
		OWLEntity curEntity = desc.asOWLClass();
		getOWLEditorKit().getWorkspace().getOWLSelectionModel().setSelectedEntity(curEntity);
		
		evaluationTable.setDescriptions(getMergedDescriptions(currentClass));
		//if the currently shown class expressions are for the last class to evaluate, change
		//button text and action to save the user input
		if(currentClassIndex == classes.size()){
			nextSaveButton.setAction(new AbstractAction("Save"){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
		}
	}
	
	/**
	 * Load the computed DL-Learner results from a file, which name corresponds to the loaded owl-file.
	 */
	@SuppressWarnings("unchecked")
	private void parseEvaluationFile(){
		OWLOntology activeOnt = getOWLModelManager().getActiveOntology();
		URI uri = getOWLModelManager().getOntologyPhysicalURI(activeOnt);
		String resultFile = uri.toString().substring(0, uri.toString().lastIndexOf('.') + 1) + "res";
		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(URI.create(resultFile)));
			ObjectInputStream o = new ObjectInputStream(fis);
			owlEquivalenceStandardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlEquivalenceFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlEquivalencePredaccMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlEquivalenceJaccardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlEquivalenceGenFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();

			owlSuperStandardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlSuperFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlSuperPredaccMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlSuperJaccardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			owlSuperGenFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();

			fastEquivalenceStandardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastEquivalenceFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastEquivalencePredaccMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastEquivalenceJaccardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastEquivalenceGenFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();

			fastSuperStandardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastSuperFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastSuperPredaccMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastSuperJaccardMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			fastSuperGenFMeasureMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();

			defaultEquivalenceMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			defaultSuperMap = (HashMap<NamedClass, List<EvaluatedDescriptionClass>>) o.readObject();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		classes.addAll(new TreeSet<NamedClass>(owlEquivalenceStandardMap.keySet()));
		
		
	}
	
	/**
	 * Get a disjoint list of all computed evaluated descriptions. 
	 * @param nc The class which is currently to evaluate.
	 * @return A List of disjoint evaluated descriptions - here disjointness only by the description
	 * not the accuracy.
	 */
	private List<EvaluatedDescriptionClass> getMergedDescriptions(NamedClass nc){
		
		Set<EvaluatedDescriptionClass> evaluatedDescriptions = new TreeSet<EvaluatedDescriptionClass>(new Comparator<EvaluatedDescriptionClass>(){
			
			public int compare(EvaluatedDescriptionClass o1, EvaluatedDescriptionClass o2) {
				return comparator.compare(o1.getDescription(), o2.getDescription());
				
			};
		});
		evaluatedDescriptions.addAll(owlEquivalenceStandardMap.get(nc));
		evaluatedDescriptions.addAll(owlEquivalenceJaccardMap.get(nc));
		evaluatedDescriptions.addAll(owlEquivalenceGenFMeasureMap.get(nc));
		evaluatedDescriptions.addAll(owlEquivalenceFMeasureMap.get(nc));
		evaluatedDescriptions.addAll(owlEquivalencePredaccMap.get(nc));
		evaluatedDescriptions.addAll(fastEquivalenceStandardMap.get(nc));
		evaluatedDescriptions.addAll(fastEquivalenceJaccardMap.get(nc));
		evaluatedDescriptions.addAll(fastEquivalenceGenFMeasureMap.get(nc));
		evaluatedDescriptions.addAll(fastEquivalenceFMeasureMap.get(nc));
		evaluatedDescriptions.addAll(fastEquivalencePredaccMap.get(nc));
		List<EvaluatedDescriptionClass> merged = new ArrayList<EvaluatedDescriptionClass>(evaluatedDescriptions);
		
		return merged;
	}
	
	/**
	 * Show a red colored warning, if adding the selected class expression would lead to an inconsistent
	 * ontology.
	 * @param show If true a warning is displayed, otherwise not.
	 */
	private void showInconsistencyWarning(boolean show){
		if(show){
			inconsistencyLabel.setForeground(Color.RED);
		} else {
			inconsistencyLabel.setForeground(getBackground());
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && evaluationTable.getSelectedRow() >= 0) {
			coveragePanel.setNewClassDescription(evaluationTable.getSelectedEvaluatedDescription());
			showInconsistencyWarning(!evaluationTable.getSelectedEvaluatedDescription().isConsistent());
		}
	}

}
