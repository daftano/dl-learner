package org.dllearner.tools.ore.ui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.dllearner.core.owl.NamedClass;
import org.dllearner.tools.ore.OREApplication;
import org.dllearner.tools.ore.ui.rendering.ManchesterSyntaxTableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class MarkableClassesTable extends JXTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4193878042914394758L;
	private ImageIcon icon = new ImageIcon(OREApplication.class.getResource("untoggled.gif"));
	
	public MarkableClassesTable(){
		super(new MarkableClassesTableModel());
		getColumn(1).setCellRenderer(new ManchesterSyntaxTableCellRenderer());
		getColumn(0).setMaxWidth(30);
		setTableHeader(null);
		setBorder(null);
		setShowVerticalLines(false);
		setShowHorizontalLines(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(false);
		getColumn(0).setCellRenderer(new TableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				if(value.equals(">")){
					return new JLabel(icon);
				} else {
					return new JLabel("");
				}
			}
		});
	}
	
	@Override
	public String getToolTipText(MouseEvent e){
		String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        if(rowIndex != -1){
        	tip = getValueAt(rowIndex, 1).toString();
        	
        } else {
        	tip = super.getToolTipText(e);
        }
        return tip;
	}
	
	public void addClasses(Set<NamedClass> classes){
		((MarkableClassesTableModel)getModel()).addClasses(classes);
	}
	
	public NamedClass getSelectedClass(int rowIndex){
		return ((MarkableClassesTableModel)getModel()).getSelectedValue(rowIndex);
	}
	
	public void clear(){
		((MarkableClassesTableModel)getModel()).clear();
	}
	
	public void setSelectedClass(int rowIndex){
		((MarkableClassesTableModel)getModel()).setSelectedClass(rowIndex);
	}

}
