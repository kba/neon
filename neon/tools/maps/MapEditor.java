/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
 * 
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neon.tools.maps;

import javax.swing.*;
import neon.tools.Editor;
import neon.tools.resources.IObject;
import neon.tools.resources.IRegion;
import neon.tools.resources.Instance;
import neon.tools.resources.RMap;
import neon.tools.resources.RZone;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class MapEditor {
	private static String terrain;
	private static JToggleButton drawButton;
	private static JToggleButton selectButton;
	private static UndoAction undoAction;
	private static HashMap<String, Short> mapUIDs;
	private JScrollPane mapScrollPane;
	private JTree mapTree;
	private JButton undo;
	private HashSet<RMap> activeMaps;
	private JTabbedPane tabs;
	private JCheckBox levelBox;
	private JSpinner levelSpinner;
	
	public MapEditor(JTabbedPane tabs, JPanel panel) {
		activeMaps = new HashSet<RMap>();
		mapUIDs = new HashMap<String, Short>();
		this.tabs = tabs;
		
		// tree met maps
		mapTree = new JTree(new DefaultMutableTreeNode("maps"));
		mapTree.setRootVisible(false);
		mapTree.setShowsRootHandles(true);
		mapTree.addMouseListener(new MapTreeListener(mapTree, tabs, this));
		mapTree.setVisible(false);
		mapScrollPane = new JScrollPane(mapTree);
    	mapScrollPane.setBorder(new TitledBorder("Maps"));
    	panel.add(mapScrollPane, BorderLayout.CENTER);
    	
    	// toolbars
		JToolBar zoomBar = new JToolBar();
		ToolBarListener toolBarListener = new ToolBarListener();
		zoomBar.add(new JLabel("Zoom: "));
		JButton minButton = new JButton("-");
		minButton.addActionListener(toolBarListener);
		JButton plusButton = new JButton("+");
		plusButton.addActionListener(toolBarListener);
		zoomBar.add(minButton);
		zoomBar.add(plusButton);
		JToolBar layerBar = new JToolBar();
		levelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Byte.MAX_VALUE, 1));
		levelSpinner.addChangeListener(toolBarListener);
		levelBox = new JCheckBox("View only current layer");
		levelBox.setActionCommand("layer");
		levelBox.addActionListener(toolBarListener);
		layerBar.add(new JLabel("Layer: "));
		layerBar.add(levelSpinner);
		layerBar.addSeparator();
		layerBar.add(levelBox);
		JToolBar editBar = new JToolBar();
		drawButton = new JToggleButton(new ImageIcon(Editor.class.getResource("brush.png")));
		drawButton.setToolTipText("Draw mode");
		drawButton.addActionListener(toolBarListener);
		editBar.add(drawButton);
		selectButton = new JToggleButton(new ImageIcon(Editor.class.getResource("mouse.png")));
		selectButton.setToolTipText("Select mode");
		selectButton.addActionListener(toolBarListener);
		selectButton.setSelected(true);
		editBar.add(selectButton);
		ButtonGroup mode = new ButtonGroup();
		mode.add(selectButton);
		mode.add(drawButton);
		undo = new JButton(new ImageIcon(Editor.class.getResource("undo.png")));
		undo.setToolTipText("Undo last action");
		undo.addActionListener(toolBarListener);
		undo.setActionCommand("undo");
		editBar.add(undo);
		Editor.addToolBar(layerBar);
		Editor.addToolBar(zoomBar);
		Editor.addToolBar(editBar);
	}

	public static boolean isVisible(Instance r) {
		if(r instanceof IRegion && Editor.tShow.isSelected()) {
			return true;
		} else if(r instanceof IObject && Editor.oShow.isSelected()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setUndoAction(UndoAction undo) {
		undoAction = undo;
	}
	
	private short createNewUID() {
		short uid = (short)(Math.random()*Short.MAX_VALUE);
		while(mapUIDs.containsKey(uid)) {
			uid++;
		}
		return uid;
	}
	
	public static boolean drawMode() {
		return drawButton.isSelected();
	}
	
    public void deleteMap(String id) {
		activeMaps.remove(id);
		Editor.resources.removeResource(id);
    }
    
    public void makeMap(MapDialog.Properties props) {
		if(!props.cancelled()) {
			// editableMap maken
			short uid = createNewUID();
			RMap map = new RMap(uid, Editor.getStore().getActive().get("id"), props);
			activeMaps.add(map);
			// en node maken
			DefaultTreeModel model = (DefaultTreeModel)mapTree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			MapTreeNode node = new MapTreeNode(map);
			if(!map.isDungeon()) {
				node.add(new ZoneTreeNode(0, map.getZone(0)));
			}
			model.insertNodeInto(node, root, root.getChildCount());
			mapTree.expandPath(new TreePath(root));
			Editor.resources.addResource(map, "maps");
		}
    }

    public void loadMaps(Collection<RMap> maps, String path) {
		DefaultTreeModel model = (DefaultTreeModel)mapTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		for(RMap map : maps) {
			mapUIDs.put(map.id, map.uid);
			MapTreeNode node = new MapTreeNode(map);
			if(map.isDungeon()) {
				for(Map.Entry<Integer, RZone> zone : map.zones.entrySet()) {
					node.add(new ZoneTreeNode(zone.getKey(), zone.getValue()));
				}
			} else {
				node.add(new ZoneTreeNode(0, map.getZone(0)));
			}
			model.insertNodeInto(node, root, root.getChildCount());
		}
		mapTree.expandPath(new TreePath(root));
		mapTree.setVisible(true);
	}
	
	public static HashMap<String, Short> getMaps() {
		return mapUIDs;
	}
	
	public Collection<RMap> getActiveMaps() {
		return activeMaps;
	}
	
	public void setTerrain(String type) {
		terrain = type;
	}
	
	public static String getSelectedTerrain() {
		return terrain;
	}
	
	private class ToolBarListener implements ActionListener, ChangeListener {
		public void stateChanged(ChangeEvent e) {
			EditablePane mapPane = (EditablePane)tabs.getSelectedComponent();
			if(mapPane != null) {
				mapPane.setLayer((Integer)levelSpinner.getValue());
				reload(mapPane);
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			EditablePane mapPane = (EditablePane)tabs.getSelectedComponent();
			if("layer".equals(e.getActionCommand())){
				reload(mapPane);
			} else if("undo".equals(e.getActionCommand())){
				if(undoAction != null) {
					undoAction.undo();
				}
				mapPane.repaint();
				System.out.println("undo");
			} else {
				if ("-".equals(e.getActionCommand())) {
					mapPane.setZoom(mapPane.getZoom()/2);
				} else if ("+".equals(e.getActionCommand())){
					mapPane.setZoom(mapPane.getZoom()*2);
				} 
			}
		}
		
		private void reload(EditablePane pane) {
			if(levelBox.isSelected()) {
				pane.setLayer((Integer)levelSpinner.getValue());
				pane.toggleView(false);
			} else {
				pane.toggleView(true);
			}
			pane.repaint();			
		}
	}
}
