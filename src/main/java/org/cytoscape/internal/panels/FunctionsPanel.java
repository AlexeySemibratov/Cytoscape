package org.cytoscape.internal.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.internal.load.LoadManager;
import org.cytoscape.internal.utils.FileUtils;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.utils.PopupMessage;
import org.cytoscape.internal.utils.TableUtils;
import org.cytoscape.session.CySession;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.*;


public class FunctionsPanel extends JPanel implements CytoPanelComponent, SessionAboutToBeSavedListener, SessionLoadedListener {
	private static final long serialVersionUID = 55145613390057L;

	private static final String FUNCTIONS_TABLE = "__tablesPanel";

	private CyApplicationManager cyAppManager;

	private JButton btnAddFunction, btnClear, btnDelSelected, btnUpdate, btnTest;
	private JPanel thisPanel, btnPanel,tablesPanel;
	private DefaultComboBoxModel<String> cbModel;
	private JComboBox<String> nodesBox;
	
	private FunctionsManager funManager;
	private CySessionManager sessionManager;
	private LoadManager loadManager;
	
	public FunctionsPanel(CyApplicationManager cyAppManager, FunctionsManager info, CySessionManager sessionManager){
		this.cyAppManager = cyAppManager;
		cbModel = new DefaultComboBoxModel<>();
		
		this.funManager = info;
		this.sessionManager = sessionManager;
		
		nodesBox = new JComboBox<String>(cbModel);
		nodesBox.setPreferredSize(new Dimension(100,25));
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		initButtons();

		tablesPanel = new JPanel();
		tablesPanel.setPreferredSize(new Dimension(350, 450));
		tablesPanel.setName(FUNCTIONS_TABLE);

		this.add(btnPanel);
		this.add(nodesBox);
		this.add(tablesPanel);
		thisPanel = this;
		nodesBox.addActionListener(new boxSelectionListener());
		addInputListeners();
		this.setVisible(true);
	}
	
	private void initButtons() {
		btnAddFunction = new JButton("Add function to selected node");
		btnClear = new JButton("Clear All!");
		btnClear.setFont(new Font("TimesRoman", Font.BOLD, 12));
		btnUpdate = new JButton("Update");
		btnDelSelected = new JButton("Delete selected function");

		btnAddFunction.setToolTipText("Create a new truth table for the selected node");
		btnUpdate.setToolTipText("Applies all entered data in tables");
		btnClear.setToolTipText("Remove all tables");

		GridLayout layout = new GridLayout(2, 2, 5, 8);
		btnPanel = new JPanel();
		btnPanel.setLayout(layout);
		btnPanel.setPreferredSize(new Dimension(350,75));
		btnPanel.add(btnAddFunction);
		btnPanel.add(btnUpdate);
		btnPanel.add(btnDelSelected);
		btnPanel.add(btnClear);
		
	}

	private void addInputListeners() {
		btnAddFunction.addActionListener(e -> {
			CyNetwork network = cyAppManager.getCurrentNetwork();
			List<CyNode> nodes = CyTableUtil.getNodesInState(network,"selected",true);

			if(nodes.size()!=1) {
				PopupMessage.ErrorMessage("You must select only 1 node!");
				return;
			}
			addTable(nodes.get(0), cyAppManager.getCurrentNetwork());
		});
		btnDelSelected.addActionListener(e -> {
				String selected = (String) nodesBox.getSelectedItem();
				if(selected == "") return;
				int select = PopupMessage.ConfirmMessage("Are you want to remove function for ["+selected+"]");
				if(select!=0) return;
				funManager.removeNodeTable(selected);
				nodesBox.removeItemAt(nodesBox.getSelectedIndex());
				Component[] components = thisPanel.getComponents();
				for(Component c:components)
					{
						if(c.getName() == selected) {
							thisPanel.remove(c);
							return;
						}
					}
		});
		btnClear.addActionListener(e -> {
				int select = PopupMessage.ConfirmMessage("Are you want to remove all functions?");
				if(select!=0) return;
				nodesBox.removeAllItems();
				funManager.removeAllNodeTables();
				Component[] components = thisPanel.getComponents();
				for(Component c:components)
					{
						if(c.getClass() == JScrollPane.class) thisPanel.remove(c);
					}
		});
		btnUpdate.addActionListener(arg0 -> {

			Component[] components = thisPanel.getComponents();
			for(Component c:components)
			{
				if(funManager.containsNode(c.getName()))
				{
					JScrollPane pane = (JScrollPane) c;
					JTable table = (JTable) pane.getViewport().getComponent(0);

					int n = table.getRowCount();
					int m = table.getColumnCount();

					int[] values = new int[n];
					String[] arguments = new String[m-1];

					for(int i=0; i<m-1; i++) {
						arguments[i] = table.getColumnName(i);
					}

					for(int i=0; i<n; i++) {
						if(table.getValueAt(i, m-1).getClass() == String.class) values[i] = Integer.parseInt((String) table.getValueAt(i, m-1));
						else values[i] = (Integer) table.getValueAt(i, m-1);
						if(!(values[i]==0 || values[i]==1)) {
							PopupMessage.ErrorMessage(
									new IllegalArgumentException("Illegal argument <"+ values[i] +"> for node <" + c.getName() + "> in " + (i+1) +"-th line. " +
											"Values can only be '0' (false) and '1' (true)."));
							return;
						}
					}

					funManager.getNodeTablebyNode(table.getColumnName(m-1)).setValues(values);

				}
			}
		});
	}
	
	private boolean addTable(CyNode node, CyNetwork network) {
		String mainNode = network.getDefaultNodeTable().getRow(node.getSUID()).get("name", String.class);
		if(!funManager.nodeTableIsEmpty()) {
			if(funManager.containsNode(mainNode)) {
				JOptionPane.showMessageDialog(null, "You already set function for this Node");
				return false;
			}
		}

		List<CyEdge> incomingEdges = network.getAdjacentEdgeList(node, CyEdge.Type.INCOMING);
		if(incomingEdges.isEmpty()) return false;
		int n = incomingEdges.size();

        Object[] columnsHeader = new String[n+1];
        String nodeName;
        for(int i=0; i < columnsHeader.length - 1; i++)
        {
        	nodeName = network.getDefaultNodeTable().getRow(incomingEdges.get(i).getSource().getSUID()).get("name", String.class);
        	columnsHeader[i] = nodeName;
        }
        columnsHeader[n] = network.getDefaultNodeTable().getRow(node.getSUID()).get("name", String.class);
        
        Integer[] values = new Integer[(int) Math.pow(2.0, n)];
        for(int i=0; i<values.length;i++) values[i]=0;

		String[] arguments = Arrays.copyOfRange((String[])columnsHeader,0,columnsHeader.length-1);
		funManager.addNodeTable(arguments,(String) columnsHeader[columnsHeader.length-1]);

        initTable((String[]) columnsHeader, values);
        return true;
	}
	
	private boolean initTable(String[] columnsHeader, Integer[] values) {
		DefaultTableModel tableModel = new DefaultTableModel(){
			private static final long serialVersionUID = 199459756891294L;

			@Override
	        public boolean isCellEditable(int i, int j) {
	            return (j==this.getColumnCount()-1) ? true:false;
	        }
	    };
	    
        tableModel.setColumnIdentifiers(columnsHeader);
        for (int i = 0; i < values.length; i++)
            tableModel.addRow(new Integer[columnsHeader.length]);
        setDefaultValues(tableModel);
        
        for (int i = 0; i < values.length; i++) {
        	tableModel.setValueAt(values[i], i, columnsHeader.length-1);
        }


        JTable tab = new JTable(tableModel);
        tab.setDefaultRenderer(Object.class, TableUtils.tableRenderer());
        tab.setPreferredScrollableViewportSize(new Dimension(350, 200));
        
        JScrollPane pane = new JScrollPane(tab);
        pane.setName(columnsHeader[columnsHeader.length-1]);
        pane.setPreferredSize(new Dimension(350, 300));
        tablesPanel.add(pane);
        cbModel.addElement(columnsHeader[columnsHeader.length-1]);
        cbModel.setSelectedItem(columnsHeader[columnsHeader.length-1]);
        pane.setVisible(true);

        this.repaint();
		return true;
	}
	
	private void setDefaultValues(DefaultTableModel model) {
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		int m = rowCount;
		int s = 1;
		for(int j=0; j<(columnCount-1); j++) {
			m = m/2;
			for(int i = 0;i<rowCount;i++) 
			{
				if(s<=m) model.setValueAt(0, i, j);
				else model.setValueAt(1, i, j);
				if(s==2*m) s=0;
				s++;
			}
		}
	}
	
	class boxSelectionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String selected = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();

			for (Component c : tablesPanel.getComponents()) {
				if (c.getClass() == JScrollPane.class) {
					if (c.getName().equals(selected)) {
						c.setVisible(true);
					} else c.setVisible(false);
				}
			}
			
		}
		
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		return "Functions panel";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleEvent(SessionAboutToBeSavedEvent e) {
		String tmpDir = System.getProperty("java.io.tmpdir");
		String currentSession = FileUtils.getSessionSimpleName(sessionManager.getCurrentSessionFileName());
		File tableFile = new File(tmpDir,   currentSession  + "__table");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
			for(Component c:tablesPanel.getComponents())
			{
				if(funManager.containsNode(c.getName()))
				{
					JScrollPane pane = (JScrollPane) c;
					JTable table = (JTable) pane.getViewport().getComponent(0);
					writer.write("#" + c.getName() + '\n');
					saveTable(table, writer);
					writer.newLine();
				}
			}
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		ArrayList<File> files = new ArrayList<File>();
		files.add(tableFile);
		try {
			e.addAppFiles("Network_Functions", files);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	private void saveTable(JTable table, BufferedWriter writer) throws IOException {
		int n = table.getRowCount();
		int m = table.getColumnCount();
		for(int i=0; i<m; i++) writer.write(table.getColumnName(i) + "//");
		writer.newLine();
		for(int i=0; i<n; i++) {
			writer.write(table.getValueAt(i, m-1).toString() + " ");
		}
	}
	
	@Override
	public void handleEvent(SessionLoadedEvent e) {
		List<File> files = e.getLoadedSession().getAppFileListMap().get("Network_Functions");
		if (files == null || files.size() ==0){
			return;
		}

		String currentSession = FileUtils.getSessionSimpleName(sessionManager.getCurrentSessionFileName());

		try {
			File propFile = null;

			for(File f:files){
				if(f.getName().equals(currentSession  + "__table"))	{
					propFile = f;
					break;
				}
			}

			if(propFile == null) return;

			tablesPanel.removeAll();
			nodesBox.removeAllItems();
			
			BufferedReader in = new BufferedReader(new FileReader(propFile));
			String s = in.readLine();
			while(s != null) {
				if(s.startsWith("#")) {
					s = in.readLine();
					String[] headers = s.split("//");
					String[] svalues = in.readLine().split(" ");
					Integer[] values = new Integer[svalues.length];
					for(int i=0; i<values.length;i++) values[i] = Integer.parseInt(svalues[i]);
					initTable(headers, values);
					funManager.addNodeTable(Arrays.copyOfRange(headers,0,headers.length-1),
							headers[headers.length-1],values);
				}
				s = in.readLine();
			}
			in.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		
	}
}
