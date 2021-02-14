package org.cytoscape.internal.panels;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.boolnet.functions.State;
import org.cytoscape.internal.boolnet.functions.alhoritms.AttractorSearch;
import org.cytoscape.internal.boolnet.functions.alhoritms.FixedPoints;
import org.cytoscape.internal.boolnet.functions.alhoritms.SynchNetwork;
import org.cytoscape.internal.utils.FileUtils;
import org.cytoscape.internal.utils.PopupMessage;
import org.cytoscape.internal.utils.SaveAndLoadUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.internal.utils.TableUtils;
import org.cytoscape.session.CyNetworkNaming;

public class BNetPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292406955891826912L;
	public static final String CNET = "cnet";
	public static final String NET = "net";
	public static final String TXT = "txt";
	
	private CyApplicationManager cyAppManager;
	private CyNetworkFactory cnf;
	private CyNetworkNaming namingUtil;
	private final CyNetworkManager netMgr;

	private JButton btnCombine, btnChain, btnCycle, btnFixedPoints, btnSynchNet,
					btnFileChooser, btnAttractorSearch, btnMotiffsConstructor, btnCloseConstructor;
	
	private FunctionsManager funManager;

	private JPanel btnPanel, motiffsPanel;
	private JPanel attractorsPanel;

	private KeyListeners listeners;

	
	public BNetPanel(CyApplicationManager cyAppManager, CySwingApplication swingApp, FunctionsManager fman, CyNetworkFactory cnf, CyNetworkNaming namingUtil, CyNetworkManager netMgr){
		this.netMgr = netMgr;
		this.cyAppManager=cyAppManager;
		this.listeners = new KeyListeners(cyAppManager);
		this.cnf = cnf;
		this.namingUtil = namingUtil;
		
		this.funManager = fman;
		this.attractorsPanel = new JPanel();
		attractorsPanel.setName("attr_panel");

		attractorsPanel.setSize(new Dimension(350, 1000));

		initButtons();

		this.setVisible(true);
	}

	private void initButtons(){

		btnMotiffsConstructor = new JButton("Motiffs constructor");
		btnFixedPoints = new JButton("Search fixed points");
		btnAttractorSearch = new JButton("Search attractors");
		btnSynchNet = new JButton("Create synchronous network");
		btnFileChooser = new JButton("Export network");

		btnCombine = new JButton("Combine 2 nodes");
		btnChain = new JButton("Add chain");
		btnCycle = new JButton("Add cycle");
		btnCloseConstructor = new JButton("Close constructor panel");

		btnPanel = new JPanel();
		GridLayout layout = new GridLayout(3, 2, 5, 8);
		btnPanel.setLayout(layout);
		btnPanel.setPreferredSize(new Dimension(350,110));


		btnPanel.add(btnMotiffsConstructor);
		btnPanel.add(btnCombine);
		btnPanel.add(btnFixedPoints);
		btnPanel.add(btnAttractorSearch);
		btnPanel.add(btnSynchNet);
		btnPanel.add(btnFileChooser);

		this.add(btnPanel);

		motiffsPanel = new JPanel();
		motiffsPanel.setLayout(layout);
		motiffsPanel.setPreferredSize(new Dimension(350,110));
		motiffsPanel.add(btnChain);
		motiffsPanel.add(btnCycle);
		motiffsPanel.add(btnCloseConstructor);

		addInputListeners();
	}
	

	private void addInputListeners() {
		btnMotiffsConstructor.addActionListener(e -> {
			this.add(motiffsPanel);
			this.repaint();
			cyAppManager.getCurrentNetwork();
		});
		btnCloseConstructor.addActionListener(e -> {
			this.remove(motiffsPanel);
			this.repaint();
		});

		btnChain.addActionListener(listeners.chain());
		btnCycle.addActionListener(listeners.cycle());

		btnCombine.addActionListener(listeners.combine());

		btnFixedPoints.addActionListener(arg0 -> {

			int select = 0;
			int n = (int) Math.pow(2.0,funManager.getAllNodeFunctions().size());
			if(n>=256) select = PopupMessage.ConfirmMessage("The number of all possible values is " + n + ".The execution time can be very long.");
			if(select!=0) return;

			String[] arguments = funManager.getAllNodeFunctions().keySet().toArray(new String[funManager.getAllNodeFunctions().size()]);

			HashSet<int[]> fixedPoints = FixedPoints.simpleIterationMethod(funManager.getAllNodeFunctions(), arguments);

			attractorsPanel.removeAll();

			if(fixedPoints.isEmpty()) {
				JOptionPane.showMessageDialog(null, "No fixed points.");
				return;
			}

			DefaultTableModel tableModel = TableUtils.createTableWithNoEditableCells();
			tableModel.setColumnIdentifiers(arguments);

			Integer[] p = new Integer[arguments.length];
			for (int[] point:fixedPoints) {
				tableModel.addRow(Arrays.stream(point).boxed().toArray());
			}

			JTable tab = new JTable(tableModel);
			tab.setPreferredScrollableViewportSize(new Dimension(350, 200));
			JScrollPane pane = new JScrollPane(tab);
			pane.setPreferredSize(new Dimension(350, 300));
			attractorsPanel.add(pane);

			this.add(attractorsPanel);

		});
		btnAttractorSearch.addActionListener(e -> {
			attractorsPanel.removeAll();

			AttractorSearch attractorSearchAlhoritm = new AttractorSearch();
			ArrayList<ArrayList<State>> attractorsSet = attractorSearchAlhoritm.search(funManager);

			if(attractorsSet.isEmpty()) {
				PopupMessage.Message("No attractors found");
				return;
			}

			String[] arguments = funManager.getAllNodeFunctions().keySet().toArray(new String[funManager.getAllNodeFunctions().size()]);

			JTabbedPane tabbedPane = new JTabbedPane();

			for(ArrayList<State> attractor:attractorsSet){

				DefaultTableModel tableModel = TableUtils.createTableWithNoEditableCells();
				tableModel.setColumnIdentifiers(arguments);
				Integer[] p = new Integer[arguments.length];

				for(State state:attractor) {
					tableModel.addRow(Arrays.stream(state.toArray()).boxed().toArray());
				}

				JTable tab = new JTable(tableModel);
				tab.setPreferredScrollableViewportSize(new Dimension(350, 200));
				JScrollPane pane = new JScrollPane(tab);
				pane.setPreferredSize(new Dimension(350, 300));

				tabbedPane.addTab("Attr " + attractorsSet.indexOf(attractor), pane);
			}
			attractorsPanel.add(tabbedPane);
			this.add(attractorsPanel);
		});
		btnSynchNet.addActionListener(arg0 -> {
			if(funManager.getAllNodeFunctions().size() > 10) {
				PopupMessage.ErrorMessage("The total number of nodes in a synchronous graph is too large. The function is not available.");
				return;
			}
			CyNetwork network = SynchNetwork.create(funManager.getAllNodeFunctions(), cnf);
			String currentNetName = cyAppManager.getCurrentNetwork().getRow(cyAppManager.getCurrentNetwork()).get("name", String.class);
			network.getRow(network).set(CyNetwork.NAME, namingUtil.getSuggestedNetworkTitle(currentNetName + "_synch"));
			netMgr.addNetwork(network);
			cyAppManager.setCurrentNetwork(network);
		});
		btnFileChooser.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			File networkFile;

			FileNameExtensionFilter filter1 = new FileNameExtensionFilter("boolean network (*.cnet, *.net)", CNET, NET);
			FileNameExtensionFilter filter2 = new FileNameExtensionFilter("text document (*.txt)", TXT);
			fileChooser.addChoosableFileFilter(filter1);
			fileChooser.addChoosableFileFilter(filter2);

			fileChooser.setDialogTitle("Save file");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showSaveDialog(null);
			BufferedWriter writer = null;

			networkFile = fileChooser.getSelectedFile();
			if(!FileUtils.isFileExtension(networkFile, CNET,NET,TXT)) {
				networkFile = new File(networkFile.getParentFile(), networkFile.getName()+".cnet");
			}
			try {
				writer = new BufferedWriter(new FileWriter(networkFile));
				SaveAndLoadUtil.saveNetwork(writer,funManager);
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
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
		return "BN control panel";
	}

	@Override
	public Icon getIcon() {
		return null;
	}
	
	

}
