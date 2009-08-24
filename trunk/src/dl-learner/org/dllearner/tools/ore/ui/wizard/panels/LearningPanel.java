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

package org.dllearner.tools.ore.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ListSelectionListener;

import org.dllearner.tools.ore.ui.ResultTable;
import org.dllearner.tools.protege.OptionPanel;


/**
 * The wizard panel where result table and buttons for learning step are shown.
 * @author Lorenz Buehmann
 *
 */
public class LearningPanel extends JPanel{

	private static final long serialVersionUID = -7411197973240429632L;

	private JPanel contentPanel;
	

	
	
	private ResultTable resultTable;
	private JScrollPane tableScrollPane;
	private JPanel listPanel;
	private JPanel noisePanel;
	private JSlider noiseSlider;
	private JLabel noiseLabel;
	private JButton stopButton;
	private JButton startButton;
	private JPanel buttonPanel;
	private JPanel buttonSliderPanel;
	private JLabel conceptLabel;
	private JLabel accuracyLabel;

	public LearningPanel() {
		
		super();

		contentPanel = getContentPanel();
		setLayout(new java.awt.BorderLayout());

		add(contentPanel, BorderLayout.CENTER);

		{
			buttonSliderPanel = new JPanel();
			this.add(buttonSliderPanel, BorderLayout.EAST);
			GridBagLayout buttonSliderPanelLayout = new GridBagLayout();
			buttonSliderPanelLayout.rowWeights = new double[] {0.0, 0.0};
			buttonSliderPanelLayout.rowHeights = new int[] {126, 7};
			buttonSliderPanelLayout.columnWeights = new double[] {0.1};
			buttonSliderPanelLayout.columnWidths = new int[] {7};
			buttonSliderPanel.setLayout(buttonSliderPanelLayout);
			{
				buttonPanel = new JPanel();
				BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, javax.swing.BoxLayout.X_AXIS);
				buttonPanel.setLayout(buttonPanelLayout);
				buttonSliderPanel.add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				{
					startButton = new JButton();
					buttonPanel.add(startButton);
					startButton.setText("Start");
				}
				{
					stopButton = new JButton();
					buttonPanel.add(stopButton);
					stopButton.setText("Stop");
				}
			}
			{
				noisePanel = new JPanel();
				BoxLayout noisePanelLayout = new BoxLayout(noisePanel, javax.swing.BoxLayout.Y_AXIS);
				noisePanel.setLayout(noisePanelLayout);
				buttonSliderPanel.add(new OptionPanel(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//				buttonSliderPanel.add(noisePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				{
					noiseLabel = new JLabel();
					noisePanel.add(noiseLabel);
					noiseLabel.setText("noise");
				}
				{
					noiseSlider = new JSlider(0, 100, 0);
					noiseSlider.setPaintTicks(true);
					noiseSlider.setMajorTickSpacing(10);
					noiseSlider.setMinorTickSpacing(5);
					Dictionary<Integer, JLabel> map = new Hashtable<Integer, JLabel>();
					map.put(new Integer(0), new JLabel("0%"));
					map.put(new Integer(50), new JLabel("50%"));
					map.put(new Integer(100), new JLabel("100%"));
					noiseSlider.setLabelTable(map);
					noiseSlider.setPaintLabels(true);
					noisePanel.add(noiseSlider);
				}
			}
		}
	}

	private JPanel getContentPanel() {
				
		{
			listPanel = new JPanel();
			resultTable = new ResultTable();
			tableScrollPane = new JScrollPane(resultTable);
			listPanel.add(tableScrollPane);
			
//			GridBagLayout jPanel1Layout = new GridBagLayout();
//			jPanel1Layout.rowWeights = new double[] {0.0, 0.5};
//			jPanel1Layout.rowHeights = new int[] {16, 400};
//			jPanel1Layout.columnWeights = new double[] {0.0, 0.5};
//			jPanel1Layout.columnWidths = new int[] {50, 500};
//			listPanel.setLayout(jPanel1Layout);
			listPanel.setBorder(BorderFactory.createTitledBorder("Learned class expressions"));
//			{
//				tableScrollPane = new JScrollPane();
//				listPanel.add(tableScrollPane, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//				{
//					resultTable = new ResultTable();
//					tableScrollPane.setViewportView(resultTable);
//				}
//			}
//			{
//				accuracyLabel = new JLabel();
//				listPanel.add(accuracyLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//				accuracyLabel.setText("accuracy");
//			}
//			{
//				conceptLabel = new JLabel();
//				listPanel.add(conceptLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//				conceptLabel.setText("class expression");
//			}
		}
		
		

	
		return listPanel;
	}
	
	public void addStartButtonListener(ActionListener a){
		startButton.addActionListener(a);
	}
	
	public void addStopButtonListener(ActionListener a){
		stopButton.addActionListener(a);
	}

	public JButton getStartButton() {
		return startButton;
	}

	public JButton getStopButton() {
		return stopButton;
	}
	
	public ResultTable getResultTable(){
		return resultTable;
	}
	
	public void addSelectionListener(ListSelectionListener l){
		resultTable.getSelectionModel().addListSelectionListener(l);
	}
	
	public double getNoise(){
		return noiseSlider.getValue();
	}
	
	
}  
    
 


	

