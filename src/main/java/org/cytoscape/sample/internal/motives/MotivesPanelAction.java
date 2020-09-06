package org.cytoscape.sample.internal.motives;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;


public class MotivesPanelAction extends AbstractCyAction {
	
	private static final long serialVersionUID = 1L;
	private CySwingApplication csApp;
	private final CytoPanel cytoPanelWest;
	private MotivesPanel mcPanel;
	
	public MotivesPanelAction (CySwingApplication csApp,
			MotivesPanel mcPanel){
		super("Motives Panel");
		setPreferredMenu("Apps");

		this.csApp = csApp;
		
		this.cytoPanelWest = this.csApp.getCytoPanel(CytoPanelName.WEST);
		this.mcPanel = mcPanel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		int index = cytoPanelWest.indexOfComponent(mcPanel);
		if (index == -1) {
			return;
		}
		cytoPanelWest.setSelectedIndex(index);
	}
}
