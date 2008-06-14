package org.dllearner.tools.protege;

import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

import org.dllearner.core.owl.Description;

public class SuggestClassPanel extends JPanel {
	
	private static final long serialVersionUID = 724628423947230L;
	
	private JList descriptions;
	private JPanel suggestPanel;
	private DefaultListModel model;
	private JScrollPane suggestScroll;
	
	public SuggestClassPanel()
	{
		super();
		suggestScroll = new JScrollPane();
		suggestScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		model = new DefaultListModel();
		descriptions = new JList(model);
		suggestPanel = new JPanel();
		descriptions.setVisible(true);
		suggestPanel.add(descriptions);
		suggestScroll.setPreferredSize(new Dimension(490,110));
		suggestScroll.setViewportView(descriptions);
		add(suggestScroll);
	}
	
	
	public SuggestClassPanel updateSuggestClassList()
	{
		//JPanel suggestPanel1 = new JPanel();
		
		//suggestScroll.setViewportView(suggestPanel1);
		//descriptions = new JList(desc);
		//descriptions.setVisible(true);
		//suggestScroll.add(descriptions);
		add(suggestScroll);
		return this;
		
	}
	public void setSuggestList(DefaultListModel desc)
	{
		descriptions.setModel(desc);
	}
	public JList getSuggestList()
	{
		return descriptions;
	}
	
	public void addSuggestPanelMouseListener(ActionHandler action)
	{
		descriptions.addMouseListener(action);
	}
	
	
          
}