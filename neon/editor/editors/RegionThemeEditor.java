/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
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

package neon.editor.editors;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Map;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import neon.editor.Editor;
import neon.resources.RCreature;
import neon.resources.RItem;
import neon.resources.RRegionTheme;
import neon.resources.RTerrain;

@SuppressWarnings("serial")
public class RegionThemeEditor extends ObjectEditor implements MouseListener {
	private JTextField floorField;
	private JComboBox<RRegionTheme.Type> typeBox;
	private JComboBox<RItem.Door> doorBox;
	private JComboBox<RTerrain> wallBox;
	private JTable creatureTable, plantTable;
	private DefaultTableModel creatureModel, plantModel;
	private RRegionTheme theme;
	
	public RegionThemeEditor(JFrame parent, RRegionTheme theme) {
		super(parent, "Region theme: " + theme.id);
		this.theme = theme;
		
		JPanel props = new JPanel(); 
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);
		props.setBorder(new TitledBorder("Properties"));
		JLabel typeLabel = new JLabel("Type: ");
		JLabel floorLabel = new JLabel("Surface: ");
		JLabel doorLabel = new JLabel("Doors: ");
		JLabel wallLabel = new JLabel("Walls: ");
		typeBox = new JComboBox<RRegionTheme.Type>(RRegionTheme.Type.values());
		floorField = new JTextField(15);
		doorBox = new JComboBox<RItem.Door>(Editor.resources.getResources(RItem.Door.class));
		wallBox = new JComboBox<RTerrain>(Editor.resources.getResources(RTerrain.class));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(typeLabel).addComponent(typeBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(floorLabel).addComponent(floorField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(doorLabel).addComponent(doorBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(wallLabel).addComponent(wallBox)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(typeLabel).addComponent(floorLabel).addComponent(doorLabel).
						addComponent(wallLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(typeBox).addComponent(floorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(doorBox).addComponent(wallBox)));

		JTabbedPane stuff = new JTabbedPane();
		
		String[] cColumns = {"id", "chance"};
		creatureModel = new ThemesTableModel(cColumns, String.class, Integer.class);
		creatureTable = new JTable(creatureModel);
		creatureTable.setFillsViewportHeight(true);
		creatureTable.addMouseListener(this);
		JScrollPane creatureScroller = new JScrollPane(creatureTable);

		String[] pColumns = {"id", "abundance"};
		plantModel = new ThemesTableModel(pColumns, String.class, Integer.class);
		plantTable = new JTable(plantModel);
		plantTable.setFillsViewportHeight(true);
		plantTable.addMouseListener(this);
		JScrollPane plantScroller = new JScrollPane(plantTable);

		stuff.add("Vegetation", plantScroller);
		stuff.add("Creatures", creatureScroller);
		stuff.setBorder(new TitledBorder("Contents"));
		
		JPanel center = new JPanel(new BorderLayout());
		center.add(props, BorderLayout.PAGE_START);
		center.add(stuff, BorderLayout.CENTER);
		
		frame.add(center, BorderLayout.CENTER);
	}
	
	protected void save() {
		theme.wall = ((RTerrain)wallBox.getSelectedItem()).id;
		theme.door = ((RItem.Door)doorBox.getSelectedItem()).id;
		theme.floor = floorField.getText();
		theme.type = (RRegionTheme.Type)typeBox.getSelectedItem();
		theme.creatures.clear();
		for(Vector<?> data : (Vector<Vector>)creatureModel.getDataVector()) {
			theme.creatures.put(data.get(0).toString(), (Integer)data.get(1));
		}
		for(Vector<?> data : (Vector<Vector>)plantModel.getDataVector()) {
			theme.vegetation.put(data.get(0).toString(), (Integer)data.get(1));
		}
		theme.setPath(Editor.getStore().getActive().get("id"));
	}

	protected void load() {
		typeBox.setSelectedItem(theme.type);
		RItem.Door door = (RItem.Door)Editor.resources.getResource(theme.door);
		doorBox.setSelectedItem(door);
		floorField.setText(theme.floor);
		RTerrain wall = (RTerrain)Editor.resources.getResource(theme.wall, "terrain");
		wallBox.setSelectedItem(wall);
		creatureModel.setRowCount(0);
		for(Map.Entry<String, Integer> creature : theme.creatures.entrySet()) {
			Object[] data = {creature.getKey(), creature.getValue()};
			creatureModel.insertRow(0, data);
		}		
		for(Map.Entry<String, Integer> plant : theme.vegetation.entrySet()) {
			Object[] data = {plant.getKey(), plant.getValue()};
			plantModel.insertRow(0, data);
		}		
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			if(e.getSource().equals(creatureTable)) {
				menu.add(new ClickAction("Add creature"));
				menu.add(new ClickAction("Remove creature"));
				int row = creatureTable.rowAtPoint(e.getPoint());
				creatureTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(plantTable)) {
				menu.add(new ClickAction("Add vegetation"));
				menu.add(new ClickAction("Remove vegetation"));
				int row = plantTable.rowAtPoint(e.getPoint());
				plantTable.getSelectionModel().setSelectionInterval(row, row);
			}
			menu.show(e.getComponent(), e.getX(), e.getY());
		}		
	}
	
	private class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add creature")) {
				Object[] creatures = Editor.resources.getResources(RCreature.class).toArray();
				String s = JOptionPane.showInputDialog(frame, "Choose creature:", "Add creature", 
						JOptionPane.PLAIN_MESSAGE, null, creatures, null).toString();
				if(s!= null) {
					Object[] row = {s, 1};
					creatureModel.addRow(row);					
				}
			} else if(e.getActionCommand().equals("Remove creature")) {
				creatureModel.removeRow(creatureTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add vegetation")) {
				Object[] plants = Editor.resources.getResources(RItem.class).toArray();
				String s = JOptionPane.showInputDialog(frame, "Choose creature:", 
						"Add vegetation", JOptionPane.PLAIN_MESSAGE, null, plants, null).toString();
				if(s!= null) {
					Object[] row = {s, 1};
					plantModel.addRow(row);					
				}
			} else if(e.getActionCommand().equals("Remove vegetation")) {
				plantModel.removeRow(plantTable.getSelectedRow());
			}
		}
	}
	
	private static class ThemesTableModel extends DefaultTableModel {
		private Class<?>[] classes;
		
		public ThemesTableModel(String[] columns, Class<?>...classes) {
			super(columns, 0);
			this.classes = classes;
		}
		
		public Class<?> getColumnClass(int i) {
			return classes[i];
		}

		public boolean isCellEditable(int row, int column) {
			return column != 0;
		}
	}
}
