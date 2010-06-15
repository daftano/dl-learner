/**
 * Copyright (C) 2007-2009, Jens Lehmann
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

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.dllearner.core.EvaluatedDescription;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;

/**
 * This class is the panel for the suggest list. It shows the descriptions made
 * by the DL-Learner.
 * 
 * @author Christian Koetteritzsch
 * 
 */
public class SuggestClassPanel extends JPanel {

	private static final long serialVersionUID = 724628423947230L;

	// Description List

	private final JList descriptions;
	
	private final SuggestionsTable suggestionTable;

	private final JScrollPane suggestScroll;
	private DefaultListModel suggestModel;
	private DLLearnerModel model;
	private DLLearnerView view;

	/**
	 * This is the constructor for the suggest panel. It creates a new Scroll
	 * panel and puts the Suggest List in it.
	 * @param m model of the DL-Learner
	 * @param v view of the DL-Learner
	 */
	public SuggestClassPanel(DLLearnerModel m, DLLearnerView v) {
		super();
		this.model = m;
		this.view = v;
		suggestModel = new DefaultListModel();
		this.setLayout(new BorderLayout());
		// renders scroll bars if necessary
		suggestScroll = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		descriptions = new JList(suggestModel);
		descriptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		descriptions.setVisible(true);
//		descriptions.setVisibleRowCount(6);
//		descriptions.getPreferredScrollableViewportSize();
//		suggestPanel.add(descriptions);
//		suggestScroll.setViewportView(descriptions);
//		descriptions.setCellRenderer(new SuggestListCellRenderer(m.getOWLEditorKit()));
		suggestionTable = new SuggestionsTable(m.getOWLEditorKit());
		suggestionTable.setVisibleRowCount(6);
		suggestScroll.setViewportView(suggestionTable);
		add(BorderLayout.CENTER, suggestScroll);
	}

	/**
	 * this method adds an new Scroll Panel and returns the updated
	 * SuggestClassPanel.
	 * 
	 * @return updated SuggestClassPanel
	 */
	public SuggestClassPanel updateSuggestClassList() {
		add(suggestScroll);
		return this;

	}
	
	@SuppressWarnings("unchecked")
	public void setSuggestions(List<? extends EvaluatedDescription> result){
		suggestionTable.setSuggestions((List<EvaluatedDescriptionClass>)result);
	}

	/**
	 * This method is called after the model for the suggest list is updated.
	 * 
	 * @param desc
	 *            List model of descriptions made by the DL-Learner
	 */
	public void setSuggestList(DefaultListModel desc) {
		if (desc.size() != 0) {
			if (suggestModel.size() == 0) {
				for (int i = 0; i < desc.size(); i++) {
					suggestModel.add(i, desc.get(i));
				}
			} else {
				for (int i = 0; i < suggestModel.size(); i++) {
					if (!suggestModel.get(i).equals(desc.get(i))) {
						descriptions.getSelectedIndex();
						suggestModel.set(i, desc.get(i));
						if (descriptions.getSelectedIndex() == i) {
							view
									.getMoreDetailForSuggestedConceptsPanel()
									.renderDetailPanel(
											model
													.getCurrentlySelectedClassDescription(i));
							view.setGraphicalPanel();

						}
					}
				}
				for (int i = suggestModel.getSize(); i < desc.size(); i++) {
					suggestModel.add(i, desc.get(i));
				}
			}
		}
	}

	
	public SuggestionsTable getSuggestionsTable() {
		return suggestionTable;
	}


	/**
	 * Thsi method returns the list model for the suggest list.
	 * 
	 * @return list model for the suggest list
	 */
	public DefaultListModel getSuggestModel() {
		return suggestModel;
	}

}
