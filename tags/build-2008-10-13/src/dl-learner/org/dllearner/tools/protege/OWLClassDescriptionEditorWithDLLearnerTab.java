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
package org.dllearner.tools.protege;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
//import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionChecker;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectProperty;

import org.protege.editor.owl.ui.frame.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.net.*;

/**
 * Added a new Tab for the DL-Learner GUI.
 * 
 * @author Matthew Horridge
 * @author Christian Koetteritzsch
 */
public class OWLClassDescriptionEditorWithDLLearnerTab extends
		AbstractOWLFrameSectionRowObjectEditor<OWLDescription> implements
		VerifiedInputEditor {

	private static final String CLASS_EXPRESSION_EDITOR_LABEL = "Class expression editor";
	private static final String CLASS_TREE_LABEL = "Class tree";
	private static final String RESTRICTION_CREATOR_LABEL = "Restriction creator";
	private static final String SUGGEST_EQUIVALENT_CLASS_LABEL = "Suggest a equivalent class";
	private static final String SUGGEST_SUBCLASS_LABEL = "Suggest a subclass";

	private OWLEditorKit editorKit;

	//private OWLDescriptionChecker checker;

	private ExpressionEditor<OWLDescription> editor;

	private JComponent editingComponent;

	private JTabbedPane tabbedPane;

	private DLLearnerView dllearner;
	private ActionHandler action;

	private OWLClassSelectorPanel classSelectorPanel;

	private ObjectRestrictionCreatorPanel restrictionCreatorPanel;

	// private OWLDescription initialDescription;

	private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

	private ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent changeEvent) {
			handleVerifyEditorContents();
		}
	};
	/**
	 * Konstruktor of the Class Description Editor with integrated DL-Learner Tab.
	 * @param editorKit OWLEditorKit
	 * @param description OWLDescription
	 * @param frame OWLFrame
	 * @param label String 
	 */
	public OWLClassDescriptionEditorWithDLLearnerTab(OWLEditorKit editorKit,
			OWLDescription description, OWLFrame<OWLClass> frame, String label) {
		this.editorKit = editorKit;
		// this.initialDescription = description;
		//checker = new OWLDescriptionChecker(editorKit);
		editor = new ExpressionEditor<OWLDescription>(editorKit, editorKit.getModelManager().getOWLExpressionCheckerFactory().getOWLDescriptionChecker());
		editor.setExpressionObject(description);
		dllearner = new DLLearnerView(frame, label);
		action = new ActionHandler(this.action, null, dllearner, null,
				editorKit);
		tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		editingComponent = new JPanel(new BorderLayout());
		editingComponent.add(tabbedPane);
		editingComponent.setPreferredSize(new Dimension(600, 520));

		if (label.equals("Equivalent classes")) {
			tabbedPane.add(SUGGEST_EQUIVALENT_CLASS_LABEL, dllearner);
		}
		if (label.equals("Superclasses")) {
			tabbedPane.add(SUGGEST_SUBCLASS_LABEL, dllearner);
		}
		tabbedPane.add(CLASS_EXPRESSION_EDITOR_LABEL, new JScrollPane(editor));
		if (description == null || !description.isAnonymous()) {
			classSelectorPanel = new OWLClassSelectorPanel(editorKit);
			tabbedPane.add(CLASS_TREE_LABEL, classSelectorPanel);
			if (description != null) {
				classSelectorPanel.setSelection(description.asOWLClass());
			}
			classSelectorPanel.addSelectionListener(changeListener);

			restrictionCreatorPanel = new ObjectRestrictionCreatorPanel();
			tabbedPane.add(RESTRICTION_CREATOR_LABEL, restrictionCreatorPanel);
			restrictionCreatorPanel.classSelectorPanel
					.addSelectionListener(changeListener);
			restrictionCreatorPanel.objectPropertySelectorPanel
					.addSelectionListener(changeListener);

			tabbedPane.addChangeListener(changeListener);
		}
	}

	private void handleVerifyEditorContents() {
		for (InputVerificationStatusChangedListener l : listeners) {
			l.verifiedStatusChanged(isValidated());
		}
	}

	private boolean isValidated() {
		boolean validated = false;
		final String selectedTabTitle = tabbedPane.getTitleAt(tabbedPane
				.getSelectedIndex());
		if (selectedTabTitle.equals(CLASS_EXPRESSION_EDITOR_LABEL)) {
			validated = editor.isWellFormed();
		} else if (selectedTabTitle.equals(CLASS_TREE_LABEL)) {
			validated = classSelectorPanel.getSelectedObject() != null;
		} else if (selectedTabTitle.equals(RESTRICTION_CREATOR_LABEL)) {
			validated = restrictionCreatorPanel.classSelectorPanel
					.getSelectedObject() != null
					&& restrictionCreatorPanel.objectPropertySelectorPanel
							.getSelectedObject() != null;
		} else if (selectedTabTitle.equals(SUGGEST_EQUIVALENT_CLASS_LABEL)) {
			validated = true;
		} else if (selectedTabTitle.equals(SUGGEST_SUBCLASS_LABEL)) {
			validated = true;
		}
		return validated;
	}
	/**
	 * Returns Editor Component.
	 * @return JComponent
	 */
	public JComponent getInlineEditorComponent() {
		// Same as general editor component
		return editingComponent;
	}

	/**
	 * Gets a component that will be used to edit the specified object.
	 * 
	 * @return The component that will be used to edit the object
	 */
	public JComponent getEditorComponent() {
		return editingComponent;
	}
	/**
	 * Removes everything after closing the Class Description Editor.
	 */
	public void clear() {
		dllearner.unsetEverything();
		dllearner.makeView();
		handleVerifyEditorContents();
		// initialDescription = null;
		editor.setText("");
	}
	/**
	 * returns the edited Components.
	 * @return Set of OWLDescriptions
	 */
	@Override
	public Set<OWLDescription> getEditedObjects() {
		if (tabbedPane.getSelectedComponent() == classSelectorPanel) {
			return new HashSet<OWLDescription>(classSelectorPanel
					.getSelectedObjects());
		} else if (tabbedPane.getSelectedComponent() == restrictionCreatorPanel) {
			return restrictionCreatorPanel.createRestrictions();
		} else if (tabbedPane.getSelectedComponent() == dllearner) {
			return dllearner.getSollutions();
		}
		return super.getEditedObjects();
	}

	/**
	 * Gets the object that has been edited.
	 * 
	 * @return The edited object
	 */
	public OWLDescription getEditedObject() {
		try {
			if (editor.isWellFormed()) {
				String expression = editor.getText();
				return editorKit.getModelManager().getOWLDescriptionParser()
						.createOWLDescription(expression);
			}
			if (!dllearner.getSollutions().isEmpty()) {
				return dllearner.getSollution();
			} else {
				return null;
			}
		} catch (OWLException e) {
			return null;
		}
	
	}
	/**
	 * Removes everything after protege is closed.
	 */
	public void dispose() {
		if (classSelectorPanel != null) {
			classSelectorPanel.dispose();
		}
		if (restrictionCreatorPanel != null) {
			restrictionCreatorPanel.dispose();
		}
		if (dllearner != null) {
			dllearner.dispose();
		}
	}

	private OWLDataFactory getDataFactory() {
		return editorKit.getModelManager().getOWLDataFactory();
	}
	/**
	 * Adds a Status Changed Listener to all components of the 
	 * class description editor.
	 * @param listener InputVerificationStatusChangedListener
	 */
	public void addStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.add(listener);
		editor.addStatusChangedListener(listener);
		listener.verifiedStatusChanged(isValidated());
	}
	/**
	 * Removes the Status Changed Listener from all components of the 
	 * class description editor.
	 * @param listener InputVerificationStatusChangedListener
	 */
	public void removeStatusChangedListener(
			InputVerificationStatusChangedListener listener) {
		listeners.remove(listener);
		editor.removeStatusChangedListener(listener);
	}

	/**
	 * This class is responsible for the view of the dllearner. It renders the
	 * output for the user and is the graphical component of the plugin.
	 * 
	 * @author Christian Koetteritzsch
	 * 
	 */
	public class DLLearnerView extends JPanel {

		private static final  long serialVersionUID = 624829578325729385L;

		// this is the Component which shows the view of the dllearner

		private JComponent learner;

		// Accept button to add the learned concept to the owl

		private JButton accept;

		// Runbutton to start the learning algorithm

		private JButton run;

		// private JButton why;

		// This is the label for the advanced button.

		private JLabel adv;

		// This is the color for the error message. It is red.

		private final Color colorRed = Color.red;

		// This is the text area for the error message when an error occurred

		private JTextArea errorMessage;

		// Advanced Button to activate/deactivate the example select panel

		private JToggleButton advanced;

		// Action Handler that manages the Button actions

		private ActionHandler action;

		// This is the model of the dllearner plugin which includes all data

		private DLLearnerModel model;

		// Panel for the suggested concepts

		private SuggestClassPanel sugPanel;

		// Selection panel for the positive and negative examples

		private PosAndNegSelectPanel posPanel;

		// Picture for the advanced button when it is not toggled

		private ImageIcon icon;

		// Picture of the advanced button when it is toggled
		private JPanel addButtonPanel;
		private ImageIcon toggledIcon;
		private JTextArea hint;
		// This is the Panel for more details of the suggested concept
		private MoreDetailForSuggestedConceptsPanel detail;

		/**
		 * The constructor for the DL-Learner tab in the class description
		 * editor.
		 * 
		 * @param current OWLFrame
		 * @param label String
		 */
		public DLLearnerView(OWLFrame<OWLClass> current, String label) {
			classSelectorPanel = new OWLClassSelectorPanel(editorKit);
			classSelectorPanel.firePropertyChange("test", false, true);
			URL iconUrl = this.getClass().getResource("pfeil.gif");
			icon = new ImageIcon(iconUrl);
			URL toggledIconUrl = this.getClass().getResource("pfeil2.gif");
			toggledIcon = new ImageIcon(toggledIconUrl);
			model = new DLLearnerModel(editorKit, current, label, this);
			sugPanel = new SuggestClassPanel();
			action = new ActionHandler(this.action, model, this, label,
					editorKit);
			adv = new JLabel("Advanced");
			advanced = new JToggleButton(icon);
			advanced.setVisible(true);
			run = new JButton("Suggest " + label);
			accept = new JButton("ADD");
			addButtonPanel = new JPanel(new BorderLayout());
			sugPanel.addSuggestPanelMouseListener(action);
			errorMessage = new JTextArea();
			errorMessage.setEditable(false);
			hint = new JTextArea();
			hint.setEditable(false);
			hint.setText("To get more Detail please doubleclick a concept");
			learner = new JPanel();
			advanced.setSize(20, 20);
			learner.setLayout(null);
			learner.setPreferredSize(new Dimension(600, 520));
			accept.setPreferredSize(new Dimension(290, 50));
			advanced.setName("Advanced");
			posPanel = new PosAndNegSelectPanel(model, action);
			addAcceptButtonListener(this.action);
			addRunButtonListener(this.action);
			addAdvancedButtonListener(this.action);

		}
		/**
		 * This method returns the SuggestClassPanel.
		 * @return SuggestClassPanel
		 */
		public SuggestClassPanel getSuggestClassPanel() {
			return sugPanel;
		}
		/**
		 * This method returns the PosAndNegSelectPanel.
		 * @return PosAndNegSelectPanel
		 */
		public PosAndNegSelectPanel getPosAndNegSelectPanel() {
			return posPanel;
		}

		/**
		 * This Method renders the view of the plugin.
		 */
		public void makeView() {
			advanced.setIcon(icon);
			model.clearVector();
			model.unsetListModel();
			//model.initReasoner();
			model.setPosVector();
			posPanel.setJCheckBoxes();
			accept.setEnabled(false);
			action.resetToggled();
			addButtonPanel.add("North", accept);
			sugPanel.setSuggestList(model.getSuggestList());
			sugPanel = sugPanel.updateSuggestClassList();
			advanced.setSelected(false);
			sugPanel.setBounds(10, 35, 490, 110);
			adv.setBounds(40, 200, 200, 20);
			addButtonPanel.setBounds(510, 40, 80, 110);
			run.setBounds(10, 0, 200, 30);
			advanced.setBounds(10, 200, 20, 20);
			sugPanel.setVisible(true);
			posPanel.setVisible(false);
			posPanel.setBounds(10, 230, 490, 250);
			accept.setBounds(510, 40, 80, 110);
			hint.setBounds(10, 150, 490, 20);
			errorMessage.setBounds(10, 170, 490, 20);
			learner.add(run);
			learner.add(adv);
			learner.add(advanced);
			learner.add(sugPanel);
			learner.add(addButtonPanel);
			learner.add(hint);
			learner.add(errorMessage);
			learner.add(posPanel);
			posPanel.addHelpButtonListener(action);
			detail = new MoreDetailForSuggestedConceptsPanel(model);
			add(learner);
		}
		/**
		 * This method sets the right icon for the advanced Panel.
		 * @param toggled boolean
		 */
		public void setIconToggled(boolean toggled) {
			if (toggled) {
				advanced.setIcon(toggledIcon);
			}
			if (!toggled) {
				advanced.setIcon(icon);
			}
		}

		/**
		 * This Method returns the DL_Learner tab.
		 * @return JComponent
		 */
		public JComponent getLearnerPanel() {
			return learner;
		}

		/**
		 * Sets the panel to select/deselect the examples visible/invisible.
		 * @param visible boolean
		 */
		public void setExamplePanelVisible(boolean visible) {
			posPanel.setVisible(visible);
		}

		/**
		 * Returns nothing.
		 * @return null
		 */
		public JPanel getOptionPanel() {
			return null;
		}

		/**
		 * Returns the AddButton.
		 * @return JButton
		 */
		public JButton getAddButton() {
			return accept;
		}

		/**
		 * Returns all added descriptions.
		 * @return Set(OWLDescription) 
		 */
		public Set<OWLDescription> getSollutions() {

			return model.getNewOWLDescription();
		}

		/**
		 * Returns the last added description.
		 * @return OWLDescriptio
		 */
		public OWLDescription getSollution() {
			return model.getSolution();
		}

		/**
	    * Destroys everything in the view after the plugin is closed.
	    */
		public void unsetEverything() {
			run.setEnabled(true);
			model.unsetNewConcepts();
			action.destroyDLLearnerThread();
			errorMessage.setText("");
			posPanel.unsetPosAndNegPanel();
			learner.removeAll();
		}

		/**
		 * Renders the error message when an error occured.
		 * @param s String 
		 */
		public void renderErrorMessage(String s) {
			errorMessage.setForeground(colorRed);
			errorMessage.setText(s);
		}
		/**
		 * This Method returns the panel for more details for the chosen concept.
		 * @return MoreDetailForSuggestedConceptsPanel
		 */
		public MoreDetailForSuggestedConceptsPanel getMoreDetailForSuggestedConceptsPanel() {
			return detail;
		}

		/**
		 * This Method returns the run button.
		 * @return JButton
		 */
		public JButton getRunButton() {
			return run;
		}

		/**
	    * Destroys the view after the plugin is closed.
	    */
		public void dispose() {
			System.out.println("dispose");
		}

		/**
		 * Adds Actionlistener to the run button.
		 * @param a ActionListener
		 */
		public void addRunButtonListener(ActionListener a) {
			run.addActionListener(a);
		}

		/**
		 * Adds Actionlistener to the add button.
		 * @param a ActionListener
		 */
		public void addAcceptButtonListener(ActionListener a) {
			accept.addActionListener(a);
		}

		/**
		 * Adds Actionlistener to the advanced button.
		 * @param a ActionListener
		 */
		public void addAdvancedButtonListener(ActionListener a) {
			advanced.addActionListener(a);
		}

	}
	/**
	 * This is the class for the object restriktion creator panel.
	 * 
	 *
	 */
	private class ObjectRestrictionCreatorPanel extends JPanel {

	
		private static final long serialVersionUID = 1695484991927845068L;

		private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

		private OWLClassSelectorPanel classSelectorPanel;

		private JSpinner cardinalitySpinner;

		private JComboBox typeCombo;

		public ObjectRestrictionCreatorPanel() {
			objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(
					editorKit);
			objectPropertySelectorPanel.setBorder(ComponentFactory
					.createTitledBorder("Restricted properties"));
			cardinalitySpinner = new JSpinner(new SpinnerNumberModel(1, 0,
					Integer.MAX_VALUE, 1));
			JComponent cardinalitySpinnerEditor = cardinalitySpinner
					.getEditor();
			Dimension prefSize = cardinalitySpinnerEditor.getPreferredSize();
			cardinalitySpinnerEditor.setPreferredSize(new Dimension(50,
					prefSize.height));
			classSelectorPanel = new OWLClassSelectorPanel(editorKit);
			classSelectorPanel.setBorder(ComponentFactory
					.createTitledBorder("Restriction fillers"));
			setLayout(new BorderLayout());
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					false);
			splitPane.setResizeWeight(0.5);
			splitPane.setLeftComponent(objectPropertySelectorPanel);
			splitPane.setRightComponent(classSelectorPanel);
			add(splitPane);
			splitPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			List<RestrictionCreator> types = new ArrayList<RestrictionCreator>();
			types.add(new RestrictionCreator("Some (existential)") {
				@Override
				public void createRestrictions(
						Set<OWLObjectProperty> properties,
						Set<OWLDescription> fillers, Set<OWLDescription> result) {
					for (OWLObjectProperty prop : properties) {
						for (OWLDescription filler : fillers) {
							result.add(getDataFactory()
									.getOWLObjectSomeRestriction(prop, filler));
						}
					}
				}
			});
			types.add(new RestrictionCreator("Only (universal)") {
				@Override
				public void createRestrictions(
						Set<OWLObjectProperty> properties,
						Set<OWLDescription> fillers, Set<OWLDescription> result) {
					for (OWLObjectProperty prop : properties) {
						if (fillers.isEmpty()) {
							return;
						}
						OWLDescription filler;
						if (fillers.size() > 1) {
							filler = getDataFactory().getOWLObjectUnionOf(
									fillers);
						} else {
							filler = fillers.iterator().next();
						}
						result.add(getDataFactory().getOWLObjectAllRestriction(
								prop, filler));
					}
				}
			});
			types.add(new CardinalityRestrictionCreator(
					"Min (min cardinality)", cardinalitySpinner) {
				@Override
				public OWLDescription createRestriction(OWLObjectProperty prop,
						OWLDescription filler, int card) {
					return getDataFactory()
							.getOWLObjectMinCardinalityRestriction(prop, card,
									filler);
				}
			});
			types.add(new CardinalityRestrictionCreator(
					"Exactly (exact cardinality)", cardinalitySpinner) {
				@Override
				public OWLDescription createRestriction(OWLObjectProperty prop,
						OWLDescription filler, int card) {
					return getDataFactory()
							.getOWLObjectExactCardinalityRestriction(prop,
									card, filler);
				}
			});
			types.add(new CardinalityRestrictionCreator(
					"Max (max cardinality)", cardinalitySpinner) {
				@Override
				public OWLDescription createRestriction(OWLObjectProperty prop,
						OWLDescription filler, int card) {
					return getDataFactory()
							.getOWLObjectMaxCardinalityRestriction(prop, card,
									filler);
				}
			});
			typeCombo = new JComboBox(types.toArray());

			final JPanel typePanel = new JPanel();
			typePanel.setBorder(ComponentFactory
					.createTitledBorder("Restriction type"));
			add(typePanel, BorderLayout.SOUTH);
			typePanel.add(typeCombo);
			typeCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardinalitySpinner
							.setEnabled(typeCombo.getSelectedItem() instanceof CardinalityRestrictionCreator);
				}
			});
			JPanel spinnerHolder = new JPanel(new BorderLayout(4, 4));
			spinnerHolder.setBorder(BorderFactory
					.createEmptyBorder(0, 10, 0, 0));
			spinnerHolder.add(new JLabel("Cardinality"), BorderLayout.WEST);
			spinnerHolder.add(cardinalitySpinner, BorderLayout.EAST);
			JPanel spinnerAlignmentPanel = new JPanel(new BorderLayout());
			spinnerAlignmentPanel.add(spinnerHolder, BorderLayout.WEST);
			typePanel.add(spinnerAlignmentPanel);
			cardinalitySpinner
					.setEnabled(typeCombo.getSelectedItem() instanceof CardinalityRestrictionCreator);
		}

		public Set<OWLDescription> createRestrictions() {
			Set<OWLDescription> result = new HashSet<OWLDescription>();
			RestrictionCreator creator = (RestrictionCreator) typeCombo
					.getSelectedItem();
			if (creator == null) {
				return Collections.emptySet();
			}
			creator.createRestrictions(objectPropertySelectorPanel
					.getSelectedObjects(), new HashSet<OWLDescription>(
					classSelectorPanel.getSelectedObjects()), result);
			return result;
		}

		public void dispose() {
			objectPropertySelectorPanel.dispose();
			classSelectorPanel.dispose();
		}
	}
	/**
	 * This is the abstract class of the restriction creator.
	 * 
	 *
	 */
	private abstract class RestrictionCreator {

		private String name;

		protected RestrictionCreator(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		abstract void createRestrictions(Set<OWLObjectProperty> properties,
				Set<OWLDescription> fillers, Set<OWLDescription> result);
	}
	/**
	 * This is the abstract class for the cardinality restriction creator. 
	 *
	 *
	 */
	private abstract class CardinalityRestrictionCreator extends
			RestrictionCreator {

		private JSpinner cardinalitySpinner;

		protected CardinalityRestrictionCreator(String name,
				JSpinner cardinalitySpinner) {
			super(name);
			this.cardinalitySpinner = cardinalitySpinner;
		}

		@Override
		public void createRestrictions(Set<OWLObjectProperty> properties,
				Set<OWLDescription> fillers, Set<OWLDescription> result) {
			for (OWLObjectProperty prop : properties) {
				for (OWLDescription desc : fillers) {
					result.add(createRestriction(prop, desc,
							(Integer) cardinalitySpinner.getValue()));
				}
			}
		}

		public abstract OWLDescription createRestriction(
				OWLObjectProperty prop, OWLDescription filler, int card);
	}

}