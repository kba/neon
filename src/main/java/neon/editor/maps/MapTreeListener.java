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

package neon.editor.maps;

import java.awt.event.*;
import javax.swing.*;

import neon.editor.Editor;
import neon.editor.resources.IObject;
import neon.editor.resources.IRegion;
import neon.editor.resources.Instance;
import neon.editor.resources.RMap;
import neon.editor.resources.RZone;
import neon.maps.DungeonGenerator;
import neon.resources.RCreature;
import neon.resources.RItem;
import neon.resources.RTerrain;
import neon.resources.RZoneTheme;
import neon.ui.graphics.Scene;

import org.jdom2.Element;
import javax.swing.tree.*;

public class MapTreeListener implements MouseListener {
	private JTree tree;
	private JTabbedPane tabs;
	private MapEditor editor;
	
	public MapTreeListener(JTree tree, JTabbedPane mapPane, MapEditor editor) {
		this.tree = tree;
		tabs = mapPane;
		this.editor = editor;
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount() == 2) {
				Object selection = tree.getLastSelectedPathComponent();
				if(tree.getSelectionPath() != null && selection instanceof ZoneTreeNode) {
					ZoneTreeNode node = (ZoneTreeNode)selection;
					RMap map = node.getZone().map;
					int choice = JOptionPane.NO_OPTION;	// standaard gewoon zone op het scherm zetten
					if(node.getZone().theme != null) {	// tenzij er een theme is gezet
						String question = "A zone theme was set for this zone. Do you wish to generate the zone " +
								"content from this theme?";
						choice = JOptionPane.showConfirmDialog(Editor.getFrame(), question, "Theme warning", 
								JOptionPane.YES_NO_CANCEL_OPTION);
					}
					
					if(choice == JOptionPane.YES_OPTION) {	// content moet gegenereerd worden
						generateZone(node.getZone());
					}
					
					if(choice != JOptionPane.CANCEL_OPTION) {	// en op scherm zetten
						node.getZone().theme = null;
						if(node.getPane() == null) {
							node.setPane(new EditablePane(node, tabs.getWidth(), tabs.getHeight()));
							editor.getActiveMaps().add(node.getZone().map);
						}
						if(tabs.indexOfComponent(node.getPane()) == -1) {
							tabs.addTab(map.toString(), node.getPane());
							TabLabel label = new TabLabel(node.toString());
							label.addActionListener(new TabLabelListener(node));
							tabs.setTabComponentAt(tabs.indexOfComponent(node.getPane()), label);
						} 
						tabs.setSelectedComponent(node.getPane());
					} 
				} 
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			tree.setSelectionRow(tree.getRowForLocation(e.getX(), e.getY()));
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			RMap map;
			if(node instanceof ZoneTreeNode) {
				map = ((MapTreeNode)node.getParent()).getMap();				
				if(tree.getSelectionPath() != null) {
					if(map.isDungeon()) {
						menu.add(new ClickAction("Delete zone", map));
					}
					menu.add(new ClickAction("Edit zone", map));
					menu.addSeparator();
				}
			} else if(node instanceof MapTreeNode) {
				map = ((MapTreeNode)node).getMap();
				if(map.isDungeon()) {
					menu.add(new ClickAction("Add zone", map));
					menu.addSeparator();
				}
				menu.add(new ClickAction("Edit map", map));			
				menu.add(new ClickAction("Delete map", map));			
			} 
			menu.add(new ClickAction("Add map", null));				
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private void generateZone(RZone zone) {
		RZoneTheme theme = zone.theme;
		String[][] terrain = new DungeonGenerator(theme).generateTiles();
		int width = terrain.length;
		int height = terrain[0].length;
		zone.map.load();	// map in orde brengen
		Scene scene = zone.getScene();
		
		Instance r = new IRegion((RTerrain)Editor.resources.getResource(theme.walls, "terrain"), 0, 0, 0, width, height);
		scene.addElement(r, r.getBounds(), r.z);

		byte layer = 1;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(terrain[x][y] != null) {	// kijken of hier items en creatures moeten komen
					String[] content = terrain[x][y].split(";");
					
					Instance region = new IRegion((RTerrain)Editor.resources.getResource(content[0], "terrain"), x, y, layer, 1, 1);				
					scene.addElement(region, region.getBounds(), region.z);
					
					if(content.length > 1) {
						for(int i = 1; i < content.length; i++) {
							if(content[i].startsWith("i")) {
								String id = content[i].replace("i:", "");
								Instance item = new IObject((RItem)Editor.resources.getResource(id), x, y, 1, 1);				
								scene.addElement(item, item.getBounds(), item.z);
							} else if(content[i].startsWith("c")) {
								String id = content[i].replace("c:", "");
								Instance creature = new IObject((RCreature)Editor.resources.getResource(id), x, y, 1, 1);				
								scene.addElement(creature, creature.getBounds(), creature.z);
//							} else if(content[i].startsWith("p")) {
//								String id = content[i].replace("p:", "");
							}
						}
					}
				}
			}
		}
	}
	
	private class TabLabelListener implements ActionListener {
		private ZoneTreeNode node;
		
		public TabLabelListener(ZoneTreeNode node) {
			this.node = node;
		}
		
        public void actionPerformed(ActionEvent e) {
        	tabs.remove(node.getPane());
        }		
	}
	
	@SuppressWarnings("serial")
	private class ClickAction extends AbstractAction {
		private RMap map;
		
		public ClickAction(String name, RMap map) {
			super(name);
			this.map = map;
		}

		public void actionPerformed(ActionEvent e) {
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			if(e.getActionCommand().equals("Add zone")) {
				String question = "A dungeon theme was set for this map. Adding a zone will remove this theme. " +
						"Do you wish to continue?";
				if(map.theme == null || JOptionPane.showConfirmDialog(Editor.getFrame(), question, "Theme warning", 
						JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
					map.theme = null;
					LevelDialog.Properties props = new LevelDialog().showInputDialog(Editor.getFrame());
					if(!props.cancelled()) {
						int index = 0;
						while(map.zones.containsKey(index)) { 
							index++; 
						}
						RZone zone;
						if(props.getTheme().equals("none")) {
							Element region = new Element("region");
							region.setAttribute("x", "0");
							region.setAttribute("y", "0");
							region.setAttribute("w", Integer.toString(props.getWidth()));
							region.setAttribute("h", Integer.toString(props.getHeight()));
							region.setAttribute("text", props.getTerrain());
							region.setAttribute("l", "0");
							IRegion ri = new IRegion(region);
							zone = new RZone(props.getName(), map.getPath()[0], ri, map);
						} else {
							System.out.println(props.getTheme());
							RZoneTheme theme = (RZoneTheme)Editor.resources.getResource(props.getTheme(), "theme");
							zone = new RZone(props.getName(), map.getPath()[0], theme, map);
						}
						ZoneTreeNode node = new ZoneTreeNode(index, zone);
						map.zones.put(index, zone);
						MapTreeNode top = (MapTreeNode)tree.getLastSelectedPathComponent();
						model.insertNodeInto(node, top, index);
					}
				}
			} else if(e.getActionCommand().equals("Delete zone")) {
				ZoneTreeNode node = (ZoneTreeNode)tree.getLastSelectedPathComponent();
				model.removeNodeFromParent(node);
				RMap map = node.getZone().map;
				map.removeZone(node.getZoneLevel());
			} else if(e.getActionCommand().equals("Edit zone")) {
				ZoneTreeNode node = (ZoneTreeNode)tree.getLastSelectedPathComponent();
				new ZoneEditor(node, Editor.getFrame()).show();
			} else if(e.getActionCommand().equals("Add map")) {
				editor.makeMap(new MapDialog().showInputDialog(Editor.getFrame()));
			} else if(e.getActionCommand().equals("Delete map")) {
				MapTreeNode map = (MapTreeNode)tree.getLastSelectedPathComponent();
				model.removeNodeFromParent(map);	    		
				editor.deleteMap(map.getMap().id);
			} else if(e.getActionCommand().equals("Edit map")) {
				MapTreeNode map = (MapTreeNode)tree.getLastSelectedPathComponent();
				new MapInfoEditor(Editor.getFrame(), map, tree).show();
			}
		}
	}
}
