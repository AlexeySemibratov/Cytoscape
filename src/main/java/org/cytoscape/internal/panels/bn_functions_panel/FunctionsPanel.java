package org.cytoscape.internal.panels.bn_functions_panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.internal.parser.MXParser;
import org.cytoscape.internal.utils.FileUtils;
import org.cytoscape.internal.utils.SwingUtils;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.internal.boolnet.functions.FunctionsManager;
import org.cytoscape.internal.utils.PopupMessage;
import org.cytoscape.internal.utils.TableUtils;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.*;


public class FunctionsPanel extends JPanel implements CytoPanelComponent, SessionAboutToBeSavedListener, SessionLoadedListener {
    private static final long serialVersionUID = 55145613390057L;

    private static final String FUNCTIONS_TABLE = "__tablesPanel";
    private static final Font BOLD_FONT = new Font("TimesRoman", Font.BOLD, 12);
    private static final String MX_PARSER_OPERATORS_LINK = "http://mathparser.org/mxparser-math-collection/boolean-operators/";

    private final CyApplicationManager cyAppManager;

    private JButton btnAddFunction, btnAddFunctionUseParser, btnAddFunctionWithoutIterGraph,
            btnClear, btnDelSelected, btnUpdate, btnCreateRandom;
    private JPanel btnPanel;
    private final JPanel tablesPanel;
    private final DefaultComboBoxModel<String> cbModel;
    private final JComboBox nodesBox;

    private final FunctionsManager funManager;
    private final CySessionManager sessionManager;

    public FunctionsPanel(CyApplicationManager cyAppManager, FunctionsManager info, CySessionManager sessionManager) {
        this.cyAppManager = cyAppManager;
        cbModel = new DefaultComboBoxModel<>();

        this.funManager = info;
        this.sessionManager = sessionManager;

        nodesBox = new JComboBox(cbModel);
        nodesBox.setPreferredSize(new Dimension(100, 25));
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        initButtons();

        tablesPanel = new JPanel();
        tablesPanel.setPreferredSize(new Dimension(350, 450));
        tablesPanel.setName(FUNCTIONS_TABLE);

        this.add(btnPanel);
        this.add(nodesBox);
        this.add(tablesPanel);
        nodesBox.addActionListener(new boxSelectionListener());
        addInputListeners();
        this.setVisible(true);
    }

    private void initButtons() {
        btnAddFunction = new JButton("Add empty truth table to selected node");
        btnAddFunctionUseParser = new JButton("[Iteraction graph] Add function to selected node");
        btnAddFunctionWithoutIterGraph = new JButton("Add function");
        btnClear = new JButton("Clear All!");
        btnClear.setFont(BOLD_FONT);
        btnUpdate = new JButton("Update");
        btnDelSelected = new JButton("Delete selected function");
        btnCreateRandom = new JButton("Random network");

        btnAddFunction.setToolTipText("Create a new truth table for the selected node");
        btnUpdate.setToolTipText("Applies all entered data in tables");
        btnClear.setToolTipText("Remove all tables");

        GridLayout layout = new GridLayout(4, 2, 5, 8);
        btnPanel = new JPanel();
        btnPanel.setLayout(layout);
        btnPanel.setPreferredSize(new Dimension(350, 115));
        btnPanel.add(btnAddFunctionWithoutIterGraph);
        btnPanel.add(btnAddFunction);
        btnPanel.add(btnAddFunctionUseParser);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelSelected);
        btnPanel.add(btnClear);
        btnPanel.add(btnCreateRandom);

    }

    private void addInputListeners() {
        btnAddFunctionWithoutIterGraph.addActionListener(e -> {
            JFrame frame = new JFrame();
            frame.setPreferredSize(new Dimension(100, 100));
            JPanel panel = new JPanel();
            panel.setSize(new Dimension(200, 400));

            URI uri = null;
            try {
                uri = new URI(MX_PARSER_OPERATORS_LINK);
            } catch (URISyntaxException exc) {
                PopupMessage.ErrorMessage(exc);
            }
            JLabel linkLabel = new JLabel();
            linkLabel.addMouseListener(SwingUtils.uriListener(uri));
            linkLabel.setText("<HTML><U>Click for browse available operators </U></HTML>");

            JTextField argField = new JTextField("example for x = f(x,y,z): x y z x", 30);
            JTextField expressionField = new JTextField("example: (x || y) (+) z", 30);
            JButton btnConfirm = new JButton("Confirm");

            GridLayout layout = new GridLayout(4, 1, 4, 2);
            panel.setLayout(layout);
            panel.add(linkLabel);
            panel.add(argField);
            panel.add(expressionField);
            panel.add(btnConfirm);
            frame.add(panel);
            frame.setVisible(true);

            btnConfirm.addActionListener(ev -> {
                String[] fullArguments = argField.getText().split(" ");
                String[] args = Arrays.copyOf(fullArguments, fullArguments.length - 1);
                Integer[] values = MXParser.parse(expressionField.getText(), args);

                String mainNode = fullArguments[fullArguments.length - 1];
                if (!funManager.nodeTableIsEmpty()) {
                    if (funManager.containsNode(mainNode)) {
                        JOptionPane.showMessageDialog(null, "You already set function for " + mainNode);
                        return;
                    }
                }
                funManager.addNodeTable(args, mainNode, values);
                initTable(fullArguments, values);
            });
        });
        btnAddFunction.addActionListener(e -> {
            CyNetwork network = cyAppManager.getCurrentNetwork();
            List<CyNode> nodes = CyTableUtil.getNodesInState(network, "selected", true);

            if (nodes.size() != 1) {
                PopupMessage.ErrorMessage("You must select only 1 node!");
                return;
            }
            addTable(nodes.get(0), cyAppManager.getCurrentNetwork(), false);
        });
        btnAddFunctionUseParser.addActionListener(e -> {
            CyNetwork network = cyAppManager.getCurrentNetwork();
            List<CyNode> nodes = CyTableUtil.getNodesInState(network, "selected", true);

            if (nodes.size() != 1) {
                PopupMessage.ErrorMessage("You must select only 1 node!");
                return;
            }
            addTable(nodes.get(0), cyAppManager.getCurrentNetwork(), true);
        });
        btnDelSelected.addActionListener(e -> {
            String selected = (String) nodesBox.getSelectedItem();
            if (selected == "") return;
            int select = PopupMessage.ConfirmMessage("Are you want to remove function for [" + selected + "]");
            if (select != 0) return;
            funManager.removeNodeTable(selected);
            nodesBox.removeItemAt(nodesBox.getSelectedIndex());
            for (Component c : tablesPanel.getComponents()) {
                if (c.getName().equals(selected)) {
                    tablesPanel.remove(c);
                    return;
                }
            }
        });
        btnClear.addActionListener(e -> {
            int select = PopupMessage.ConfirmMessage("Are you want to remove all functions?");
            if (select != 0) return;
            nodesBox.removeAllItems();
            funManager.removeAllNodeTables();
            tablesPanel.removeAll();
        });
        btnCreateRandom.addActionListener(e -> {
            for (Component c : tablesPanel.getComponents()) {
                if (funManager.containsNode(c.getName())) {
                    JScrollPane pane = (JScrollPane) c;
                    JTable table = (JTable) pane.getViewport().getComponent(0);

                    int n = table.getRowCount();
                    int m = table.getColumnCount();

                    int[] values = new int[n];
                    Random rand = new Random();

                    for (int i = 0; i < n; i++) {
                        values[i] = (rand.nextDouble() < 0.35) ? 1 : 0;
                    }
                    String[] arguments = new String[m - 1];

                    for (int i = 0; i < m - 1; i++) {
                        arguments[i] = table.getColumnName(i);
                    }

                    for (int i = 0; i < n; i++) {
                        table.setValueAt(values[i], i, m - 1);
                    }

                    funManager.getNodeTablebyNode(table.getColumnName(m - 1)).setValues(values);

                }
            }
        });

        btnUpdate.addActionListener(arg0 -> {
            for (Component c : tablesPanel.getComponents()) {
                if (funManager.containsNode(c.getName())) {
                    JScrollPane pane = (JScrollPane) c;
                    JTable table = (JTable) pane.getViewport().getComponent(0);

                    int n = table.getRowCount();
                    int m = table.getColumnCount();

                    int[] values = new int[n];
                    String[] arguments = new String[m - 1];

                    for (int i = 0; i < m - 1; i++) {
                        arguments[i] = table.getColumnName(i);
                    }

                    for (int i = 0; i < n; i++) {
                        if (table.getValueAt(i, m - 1).getClass() == String.class)
                            values[i] = Integer.parseInt((String) table.getValueAt(i, m - 1));
                        else values[i] = (Integer) table.getValueAt(i, m - 1);
                        if (!(values[i] == 0 || values[i] == 1)) {
                            PopupMessage.ErrorMessage(
                                    new IllegalArgumentException("Illegal argument <" + values[i] +
                                            "> for node <" + c.getName() + "> in " + (i + 1) + "-th line. " +
                                            "Values can only be '0' (false) and '1' (true)."));
                            return;
                        }
                    }

                    funManager.getNodeTablebyNode(table.getColumnName(m - 1)).setValues(values);

                }
            }
        });
    }

    private void addTable(CyNode node, CyNetwork network, boolean useParser) {
        String mainNode = network.getDefaultNodeTable().getRow(node.getSUID()).get("name", String.class);
        if (!funManager.nodeTableIsEmpty()) {
            if (funManager.containsNode(mainNode)) {
                JOptionPane.showMessageDialog(null, "You already set function for this Node");
                return;
            }
        }

        List<CyEdge> incomingEdges = network.getAdjacentEdgeList(node, CyEdge.Type.INCOMING);
        if (incomingEdges.isEmpty()) return;
        int n = incomingEdges.size();

        Object[] columnsHeader = new String[n + 1];
        String nodeName;
        for (int i = 0; i < columnsHeader.length - 1; i++) {
            nodeName = network.getDefaultNodeTable().getRow(incomingEdges.get(i).getSource().getSUID()).get("name", String.class);
            columnsHeader[i] = nodeName;
        }
        columnsHeader[n] = network.getDefaultNodeTable().getRow(node.getSUID()).get("name", String.class);

        Integer[] values = new Integer[(int) Math.pow(2.0, n)];
        String[] arguments = Arrays.copyOfRange((String[]) columnsHeader, 0, columnsHeader.length - 1);

        if (!useParser) {
            Arrays.fill(values, 0);

            funManager.addNodeTable(arguments, (String) columnsHeader[columnsHeader.length - 1]);

            initTable((String[]) columnsHeader, values);
        } else {
            boolean exitFlag = false;

            JPanel panel = new JPanel();
            JLabel label;
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(new JLabel("Type function for node [" + columnsHeader[n] + "]"));
            panel.add(new JLabel("Use this arguments list: " + Arrays.toString(arguments)));

            URI uri = null;
            try {
                uri = new URI(MX_PARSER_OPERATORS_LINK);
            } catch (URISyntaxException e) {
                PopupMessage.ErrorMessage(e);
            }
            JLabel linkLabel = new JLabel();
            linkLabel.addMouseListener(SwingUtils.uriListener(uri));
            linkLabel.setText("<HTML><U>Click for browse available operators </U></HTML>");
            panel.add(linkLabel);

            label = new JLabel("Variable name should not contain a space!");
            label.setForeground(Color.RED);
            panel.add(label);

            for (Component c : panel.getComponents()) {
                if (c instanceof JLabel) {
                    c.setFont(new Font("TimesRoman", Font.LAYOUT_LEFT_TO_RIGHT, 14));
                }
            }

            while (!exitFlag) {
                String function = JOptionPane.showInputDialog(null, panel, "Function", JOptionPane.INFORMATION_MESSAGE);
                values = MXParser.parse(function, arguments);
                exitFlag = true;
            }
            funManager.addNodeTable(arguments, (String) columnsHeader[n], values);
            initTable((String[]) columnsHeader, values);
        }
    }

    private void initTable(String[] columnsHeader, Integer[] values) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 199459756891294L;

            @Override
            public boolean isCellEditable(int i, int j) {
                return j == this.getColumnCount() - 1;
            }
        };

        tableModel.setColumnIdentifiers(columnsHeader);
        for (int i = 0; i < values.length; i++)
            tableModel.addRow(new Integer[columnsHeader.length]);
        setDefaultValues(tableModel);

        for (int i = 0; i < values.length; i++) {
            tableModel.setValueAt(values[i], i, columnsHeader.length - 1);
        }


        JTable tab = new JTable(tableModel);
        tab.setDefaultRenderer(Object.class, TableUtils.tableRenderer());
        tab.setPreferredScrollableViewportSize(new Dimension(350, 200));

        JScrollPane pane = new JScrollPane(tab);
        pane.setName(columnsHeader[columnsHeader.length - 1]);
        pane.setPreferredSize(new Dimension(350, 300));
        tablesPanel.add(pane);
        cbModel.addElement(columnsHeader[columnsHeader.length - 1]);
        cbModel.setSelectedItem(columnsHeader[columnsHeader.length - 1]);
        pane.setVisible(true);


        this.repaint();
    }

    private void setDefaultValues(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        int m = rowCount;
        int s = 1;
        for (int j = 0; j < (columnCount - 1); j++) {
            m = m / 2;
            for (int i = 0; i < rowCount; i++) {
                if (s <= m) model.setValueAt(0, i, j);
                else model.setValueAt(1, i, j);
                if (s == 2 * m) s = 0;
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

    @Override
    public void handleEvent(SessionAboutToBeSavedEvent e) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String currentSession = FileUtils.getSessionSimpleName(sessionManager.getCurrentSessionFileName());
        File tableFile = new File(tmpDir, currentSession + "__table");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile));
            for (Component c : tablesPanel.getComponents()) {
                if (funManager.containsNode(c.getName())) {
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

        ArrayList<File> files = new ArrayList<>();
        files.add(tableFile);
        try {
            e.addAppFiles("Network_Functions", files);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saveTable(JTable table, BufferedWriter writer) throws IOException {
        int n = table.getRowCount();
        int m = table.getColumnCount();
        for (int i = 0; i < m; i++) writer.write(table.getColumnName(i) + "//");
        writer.newLine();
        for (int i = 0; i < n; i++) {
            writer.write(table.getValueAt(i, m - 1).toString() + " ");
        }
    }

    @Override
    public void handleEvent(SessionLoadedEvent e) {
        List<File> files = e.getLoadedSession().getAppFileListMap().get("Network_Functions");
        if (files == null || files.size() == 0) {
            return;
        }

        String currentSession = FileUtils.getSessionSimpleName(sessionManager.getCurrentSessionFileName());

        try {
            File propFile = null;

            for (File f : files) {
                if (f.getName().equals(currentSession + "__table")) {
                    propFile = f;
                    break;
                }
            }

            if (propFile == null) return;

            tablesPanel.removeAll();
            nodesBox.removeAllItems();

            BufferedReader in = new BufferedReader(new FileReader(propFile));
            String s = in.readLine();
            while (s != null) {
                if (s.startsWith("#")) {
                    s = in.readLine();
                    String[] headers = s.split("//");
                    String[] svalues = in.readLine().split(" ");
                    Integer[] values = new Integer[svalues.length];
                    for (int i = 0; i < values.length; i++) values[i] = Integer.parseInt(svalues[i]);
                    initTable(headers, values);
                    funManager.addNodeTable(Arrays.copyOfRange(headers, 0, headers.length - 1),
                            headers[headers.length - 1], values);
                }
                s = in.readLine();
            }
            in.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
