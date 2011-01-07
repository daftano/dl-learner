package org.dllearner.tools.ore.ui;

import java.awt.Color;
import java.util.List;

import org.dllearner.tools.ore.ui.rendering.TextAreaRenderer;
import org.jdesktop.swingx.JXTable;
import org.semanticweb.owlapi.model.OWLAxiom;

public class RemainingAxiomsTable extends JXTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8948590659747547909L;

	public RemainingAxiomsTable(List<OWLAxiom> remainingAxioms) {
		
		setBackground(Color.WHITE);
		setModel(new RemainingAxiomsTableModel(remainingAxioms));
		getColumn(0).setCellRenderer(new TextAreaRenderer());
		getColumn(1).setMaxWidth(30);
		setTableHeader(null);
	}
	
	public List<OWLAxiom> getSelectedAxioms(){
		return ((RemainingAxiomsTableModel)getModel()).getSelectedAxioms();
	}

}