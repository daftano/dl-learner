package org.dllearner.scripts.evaluation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dllearner.core.ComponentInitException;
import org.dllearner.core.LearningProblemUnsupportedException;
import org.dllearner.core.owl.NamedClass;
import org.dllearner.learningproblems.EvaluatedDescriptionClass;
import org.dllearner.tools.ore.ui.GraphicalCoveragePanel;
import org.dllearner.tools.ore.ui.MarkableClassesTable;
import org.dllearner.tools.ore.ui.ResultTable;
import org.dllearner.tools.ore.ui.SelectableClassExpressionsTable;

public class EvaluationGUI extends JFrame implements ActionListener, ListSelectionListener, MouseMotionListener{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3097551929270352556L;
	
	private ResultTable tab1;
	private ResultTable tab2;
	private ResultTable tab3;
	private ResultTable tab4;
	private ResultTable tab5;
	private ResultTable tab6;
	private ResultTable tab7;
	private ResultTable tab8;
	private ResultTable tab9;
	private ResultTable tab10;
	
	private SelectableClassExpressionsTable defaultTab;
	
	private GraphicalCoveragePanel graphPanel;
	private GraphicalCoveragePanel graphPanel2;
	
	private MarkableClassesTable classesTable;
	private JButton nextFinishButton;
	private JLabel messageLabel;
	
	private JWindow coverageWindow;
	
	private JPanel cardPanel;
	private CardLayout cardLayout;
	
	private static String SUPERCLASSTEXT = "Showing suggestions for super class";
	private static String EQUIVALENTCLASSTEXT = "Showing suggestions for equivalent class";
	
	private static String SINGLETABLEVIEW = "single";
	private static String MULTITABLEVIEW ="multi";
	
	private int currentClassIndex = 0;
	
	private boolean showingEquivalentSuggestions = false;
	private boolean showingMultiTables = true;
	

	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceStandardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalencePredaccMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceGenFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastEquivalenceJaccardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperStandardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperPredaccMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperGenFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> fastSuperJaccardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceStandardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalencePredaccMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceGenFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlEquivalenceJaccardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	
	
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperStandardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperPredaccMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperGenFMeasureMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> owlSuperJaccardMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	
	
	private Map<NamedClass, List<EvaluatedDescriptionClass>> defaultEquivalenceMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	private Map<NamedClass, List<EvaluatedDescriptionClass>> defaultSuperMap = new HashMap<NamedClass, List<EvaluatedDescriptionClass>>();
	
	private Map<NamedClass, EvaluatedDescriptionClass> selectedEquivalenceMap = new HashMap<NamedClass, EvaluatedDescriptionClass>();
	private Map<NamedClass, EvaluatedDescriptionClass> selectedSuperMap = new HashMap<NamedClass, EvaluatedDescriptionClass>();
	

	public EvaluationGUI(File input) throws ComponentInitException, MalformedURLException, LearningProblemUnsupportedException{
		super();
		loadResults(input);
		setTitle(input.getName());
		createUI();
		createCoverageWindow();
		classesTable.setSelectedClass(currentClassIndex);
		showEquivalentSuggestions(classesTable.getSelectedClass(currentClassIndex));
		cardLayout.last(cardPanel);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}
	
	private void createUI(){
		
		setLayout(new BorderLayout());
		
		classesTable = new MarkableClassesTable();
		classesTable.addClasses(new TreeSet<NamedClass>(owlEquivalenceStandardMap.keySet()));
		JScrollPane classesScroll = new JScrollPane(classesTable);
		add(classesScroll, BorderLayout.WEST);
		
		JScrollPane suggestionsScroll = new JScrollPane(createMainPanel());
		add(suggestionsScroll, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());       
        JSeparator separator = new JSeparator();
        buttonPanel.add(separator, BorderLayout.NORTH);
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        nextFinishButton = new JButton("Next");
        nextFinishButton.setActionCommand("next");
        nextFinishButton.addActionListener(this);
        buttonBox.add(nextFinishButton);
        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
        
	}
	
	private JPanel createMainPanel(){
		JPanel messageTablesPanel = new JPanel();
		messageTablesPanel.setLayout(new BorderLayout());
		
		messageLabel = new JLabel();
		messageTablesPanel.add(messageLabel, BorderLayout.NORTH);
		
		cardPanel = new JPanel();
		cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));       
        cardLayout = new CardLayout(); 
        cardPanel.add(createSingleTablePanel(), SINGLETABLEVIEW);
        cardPanel.add(createMultiTablesPanel(), MULTITABLEVIEW);
        cardPanel.setLayout(cardLayout);
		
		messageTablesPanel.add(cardPanel, BorderLayout.CENTER);
		
		return messageTablesPanel;
	}
	
	private JPanel createSingleTablePanel(){
		JPanel tableHolderPanel = new JPanel(new BorderLayout());
		defaultTab = new SelectableClassExpressionsTable();
		defaultTab.getSelectionModel().addListSelectionListener(this);
		tableHolderPanel.add(defaultTab);
		
		graphPanel = new GraphicalCoveragePanel("");
		tableHolderPanel.add(graphPanel, BorderLayout.EAST);
		
		
		return tableHolderPanel;
	}
	
	private JPanel createMultiTablesPanel(){
		JPanel tablesHolderPanel = new JPanel();
		tablesHolderPanel.setLayout(new GridLayout(5, 2, 5, 5));
		tab1 = new ResultTable();
		tab1.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab1));
		tab2 = new ResultTable();
		tab2.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab2));
		tab3 = new ResultTable();
		tab3.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab3));
		tab4 = new ResultTable();
		tab4.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab4));
		tab5 = new ResultTable();
		tab5.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab5));
		tab6 = new ResultTable();
		tab6.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab6));
		tab7 = new ResultTable();
		tab7.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab7));
		tab8 = new ResultTable();
		tab8.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab8));
		tab9 = new ResultTable();
		tab9.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab9));
		tab10 = new ResultTable();
		tab10.addMouseMotionListener(this);
		tablesHolderPanel.add(createSelectablePanel(tab10));
		
		return tablesHolderPanel;
	}
	
	private JPanel createSelectablePanel(ResultTable table){
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JCheckBox box = new JCheckBox();
		panel.add(table, BorderLayout.CENTER);
		panel.add(box, BorderLayout.EAST);
		
		return panel;
	}
	
	private void showSingleTable(){
		defaultTab.clearSelection();
		graphPanel.clear();
		cardLayout.first(cardPanel);
		showingMultiTables = false;
	}
	
	private void showMultiTables(){
		cardLayout.last(cardPanel);
		showingMultiTables = true;
	}
	
	private void showEquivalentSuggestions(NamedClass nc){
		messageLabel.setText(EQUIVALENTCLASSTEXT);
		
		tab1.addResults(owlEquivalenceStandardMap.get(nc));
		tab2.addResults(owlEquivalenceFMeasureMap.get(nc));
		tab3.addResults(owlEquivalencePredaccMap.get(nc));
		tab4.addResults(owlEquivalenceJaccardMap.get(nc));
		tab5.addResults(owlEquivalenceGenFMeasureMap.get(nc));
		
		tab6.addResults(fastEquivalenceStandardMap.get(nc));
		tab7.addResults(fastEquivalenceFMeasureMap.get(nc));
		tab8.addResults(fastEquivalencePredaccMap.get(nc));
		tab9.addResults(fastEquivalenceJaccardMap.get(nc));
		tab10.addResults(fastEquivalenceGenFMeasureMap.get(nc));
		
		defaultTab.addResults(defaultEquivalenceMap.get(nc));
		
		showingEquivalentSuggestions = true;
	}
	
	private void showSuperSuggestions(NamedClass nc){
		messageLabel.setText(SUPERCLASSTEXT);
		
		tab1.addResults(owlSuperStandardMap.get(nc));
		tab2.addResults(owlSuperFMeasureMap.get(nc));
		tab3.addResults(owlSuperPredaccMap.get(nc));
		tab4.addResults(owlSuperJaccardMap.get(nc));
		tab5.addResults(owlSuperGenFMeasureMap.get(nc));
		
		tab6.addResults(fastSuperStandardMap.get(nc));
		tab7.addResults(fastSuperFMeasureMap.get(nc));
		tab8.addResults(fastSuperPredaccMap.get(nc));
		tab9.addResults(fastSuperJaccardMap.get(nc));
		tab10.addResults(fastSuperGenFMeasureMap.get(nc));
		
		defaultTab.addResults(defaultSuperMap.get(nc));
		
		showingEquivalentSuggestions = false;
	}
	
	private void createCoverageWindow(){
		coverageWindow = new JWindow(this);
		graphPanel2 = new GraphicalCoveragePanel("");
		coverageWindow.add(graphPanel2);
		coverageWindow.pack();
		coverageWindow.setLocationRelativeTo(classesTable);
	}

	@SuppressWarnings("unchecked")
	private void loadResults(File input) {
		InputStream fis = null;

		try {
			fis = new FileInputStream(input);
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
			
		}

		catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}
	
	private void closeDialog(){
		setVisible(false);
		dispose();
	}
	
	
	private void showCoveragePanel(boolean visible){
		coverageWindow.setVisible(visible);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("next")){
			NamedClass nc = classesTable.getSelectedClass(currentClassIndex);
			if(showingMultiTables && showingEquivalentSuggestions){
				showSingleTable();
			} else if(!showingMultiTables && showingEquivalentSuggestions){
				showSuperSuggestions(nc);
				showMultiTables();
			} else if(showingMultiTables && !showingEquivalentSuggestions){
				showSingleTable();
				if(currentClassIndex + 1 >= owlEquivalenceStandardMap.keySet().size()){
					nextFinishButton.setText("Finish");
					nextFinishButton.setActionCommand("finish");
				}
			} else {
				
				currentClassIndex++;
				classesTable.setSelectedClass(currentClassIndex);
				showEquivalentSuggestions(classesTable.getSelectedClass(currentClassIndex));
				showMultiTables();
			}

		} else if(e.getActionCommand().equals("finish")){
			
			closeDialog();
		}
		
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting() && defaultTab.getSelectedRow() >= 0){
			graphPanel.setNewClassDescription(defaultTab.getSelectedValue());
		}
		
	}
	
	
	

	/**
	 * @param args
	 * @throws ComponentInitException 
	 * @throws MalformedURLException 
	 * @throws LearningProblemUnsupportedException 
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ComponentInitException, MalformedURLException, LearningProblemUnsupportedException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		if (args.length == 0) {
			System.out.println("You need to give an file as argument.");
			System.exit(0);
		}
		final File input = new File(args[0]);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					new EvaluationGUI(input);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ComponentInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LearningProblemUnsupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		ResultTable result = ((ResultTable)e.getSource());
		int column = result.columnAtPoint(e.getPoint());
		int row = result.rowAtPoint(e.getPoint());
		EvaluatedDescriptionClass ec = result.getValueAtRow(row);
		if(column == 0){
			graphPanel2.clear();
			graphPanel2.setNewClassDescription(ec);
			showCoveragePanel(true);
			
		} else {
			showCoveragePanel(false);
		}
		
	} 
		
		

}
