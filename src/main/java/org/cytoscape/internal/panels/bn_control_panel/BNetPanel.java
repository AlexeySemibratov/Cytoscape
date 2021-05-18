package org.cytoscape.internal.panels.bn_control_panel;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.boolnet.functions.alhoritms.AttractorSearch;
import org.cytoscape.internal.utils.FileUtils;
import org.cytoscape.internal.utils.SaveAndLoadUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

public class BNetPanel extends JPanel implements CytoPanelComponent, INetworkAnalyzePanel {

    private static final long serialVersionUID = 8292406955891826912L;
    public static final String CNET = "cnet";
    public static final String NET = "net";
    public static final String TXT = "txt";

    private JButton
            btnFixedPoints,
            btnSynchNet,
            btnFileChooser,
            btnAttractorSearch;

    private final FunctionsManager funManager;

    public JPanel attractorsPanel;


    private final BoolNetPresenter netPresenter;

    public BNetPanel(
            CyApplicationManager cyAppManager,
            FunctionsManager fman,
            CyNetworkFactory cnf,
            CyNetworkNaming namingUtil,
            CyNetworkManager netMgr
    ) {
        this.funManager = fman;
        this.attractorsPanel = new JPanel();
        attractorsPanel.setName("attr_panel");

        attractorsPanel.setSize(new Dimension(350, 1000));

        initButtons();
        this.add(attractorsPanel);

        netPresenter = new BoolNetPresenter(
                cyAppManager,
                fman,
                cnf,
                namingUtil,
                netMgr,
                this
        );

        addInputListeners();

        this.setVisible(true);
    }

    private void initButtons() {
        btnFixedPoints = new JButton("Search fixed points");
        btnAttractorSearch = new JButton("Search attractors");
        btnSynchNet = new JButton("Create synchronous network");
        btnFileChooser = new JButton("Export network");

        JPanel btnPanel = new JPanel();
        GridLayout layout = new GridLayout(2, 2, 5, 8);
        btnPanel.setLayout(layout);
        btnPanel.setPreferredSize(new Dimension(350, 110));

        btnPanel.add(btnFixedPoints);
        btnPanel.add(btnAttractorSearch);
        btnPanel.add(btnSynchNet);
        btnPanel.add(btnFileChooser);

        this.add(btnPanel);
    }


    private void addInputListeners() {
        btnFixedPoints.addActionListener(e -> netPresenter.searchAttractors(AttractorSearch.FIXED_POINT_MODE));
        btnAttractorSearch.addActionListener(e -> netPresenter.searchAttractors(AttractorSearch.FULL_ATTRACTORS_MODE));

        btnSynchNet.addActionListener(e -> netPresenter.createSynchNet());

        btnFileChooser.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            File networkFile;

            FileNameExtensionFilter filter1 = new FileNameExtensionFilter("boolean network (*.cnet, *.net)", CNET, NET);
            FileNameExtensionFilter filter2 = new FileNameExtensionFilter("text document (*.txt)", TXT);
            fileChooser.addChoosableFileFilter(filter1);
            fileChooser.addChoosableFileFilter(filter2);

            fileChooser.setDialogTitle("Save file");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.showSaveDialog(null);
            BufferedWriter writer;

            networkFile = fileChooser.getSelectedFile();
            if (!FileUtils.isFileExtension(networkFile, CNET, NET, TXT)) {
                networkFile = new File(networkFile.getParentFile(), networkFile.getName() + ".cnet");
            }
            try {
                writer = new BufferedWriter(new FileWriter(networkFile));
                SaveAndLoadUtil.saveNetwork(writer, funManager);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void clearAttractorsPanel() {
        attractorsPanel.removeAll();
    }

    @Override
    public void showAttractorsPanel() {
        attractorsPanel.setVisible(true);
    }

    @Override
    public void hideAttractorsPanel() {
        attractorsPanel.setVisible(false);
    }

    @Override
    public void addAttractorPane(JTabbedPane tabbedPane) {
        attractorsPanel.add(tabbedPane);
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
