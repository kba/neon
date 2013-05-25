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

package neon.tools.editors;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.border.*;
import neon.objects.resources.RCreature;
import neon.objects.resources.RItem;
import neon.objects.resources.RTerrain;
import neon.objects.resources.RZoneTheme;
import neon.tools.Editor;
import neon.tools.NeonFormat;
import neon.tools.help.HelpLabels;

@SuppressWarnings("serial")
public class ZoneThemeEditor extends ObjectEditor implements MouseListener {
	private JTextField floorField, wallsField, doorsField;
	private JFormattedTextField minField, maxField;
	private DefaultTableModel creatureModel, itemModel, featureModel;
	private JTable creatureTable, itemTable, featureTable;
	private RZoneTheme theme;
	private JComboBox<String> typeBox;
	
	public ZoneThemeEditor(JFrame parent, RZoneTheme theme) {
		super(parent, "Zone theme: " + theme.id);
		this.theme = theme;
		
		JPanel props = new JPanel(); 
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);
		props.setBorder(new TitledBorder("Properties"));
		String[] types = {"cave", "pits", "maze", "mine", "bsp", "packed", "sparse"};
		typeBox = new JComboBox<String>(types);
		floorField = new JTextField(15);
		wallsField = new JTextField(15);
		doorsField = new JTextField(15);
		minField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		maxField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		JLabel typeLabel = new JLabel("Type: ");
		JLabel floorLabel = new JLabel("Floors: ");
		JLabel wallsLabel = new JLabel("Walls: ");
		JLabel doorsLabel = new JLabel("Doors: ");
		JLabel minLabel = new JLabel("Min. size: ");
		JLabel maxLabel = new JLabel("Max. size: ");
		JLabel floorHelpLabel = HelpLabels.getFloorHelpLabel();
		JLabel wallHelpLabel = HelpLabels.getWallHelpLabel();
		JLabel doorHelpLabel = HelpLabels.getDoorHelpLabel();
		JLabel minHelpLabel = HelpLabels.getMinSizeHelpLabel();
		JLabel maxHelpLabel = HelpLabels.getMaxSizeHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(typeLabel).addComponent(typeBox).addComponent(floorLabel)
						.addComponent(floorField).addComponent(floorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(wallsLabel).addComponent(wallsField).addComponent(wallHelpLabel)
						.addComponent(doorsLabel).addComponent(doorsField).addComponent(doorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(minLabel).addComponent(minField).addComponent(minHelpLabel)
						.addComponent(maxLabel).addComponent(maxField).addComponent(maxHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(typeLabel).addComponent(wallsLabel)
						.addComponent(minLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(typeBox).addComponent(wallsField).addComponent(minField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(wallHelpLabel).addComponent(minHelpLabel))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(floorLabel).addComponent(doorsLabel).addComponent(maxLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(floorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(doorsField).addComponent(maxField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(floorHelpLabel).addComponent(doorHelpLabel).addComponent(maxHelpLabel)));

		
		JTabbedPane stuff = new JTabbedPane();
		
		String[] columns = {"id", "chance"};
		itemModel = new ThemesTableModel(columns, String.class, Integer.class);
		itemTable = new JTable(itemModel);
		itemTable.setFillsViewportHeight(true);
		itemTable.addMouseListener(this);
		JScrollPane itemScroller = new JScrollPane(itemTable);
		
		creatureModel = new ThemesTableModel(columns, String.class, Integer.class);
		creatureTable = new JTable(creatureModel);
		creatureTable.setFillsViewportHeight(true);
		creatureTable.addMouseListener(this);
		JScrollPane creatureScroller = new JScrollPane(creatureTable);
		
		String[] moreColumns = {"id", "type", "size", "chance"};
		featureModel = new ThemesTableModel(moreColumns, String.class, String.class, Integer.class, Integer.class);
		featureTable = new JTable(featureModel);
		featureTable.setFillsViewportHeight(true);
		featureTable.addMouseListener(this);
		TableColumn typeColumn = featureTable.getColumnModel().getColumn(1);
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("stain");
		comboBox.addItem("lake");
		comboBox.addItem("patch");
		comboBox.addItem("river");
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
		JScrollPane featureScroller = new JScrollPane(featureTable);
		
		stuff.add("Features", featureScroller);
		stuff.add("Items", itemScroller);
		stuff.add("Creatures", creatureScroller);
		stuff.setBorder(new TitledBorder("Contents"));
		
		JPanel center = new JPanel(new BorderLayout());
		center.add(props, BorderLayout.PAGE_START);
		center.add(stuff);

		frame.add(center, BorderLayout.CENTER);
	}

	protected void save() {
		theme.type = typeBox.getSelectedItem().toString();
		theme.floor = floorField.getText();
		theme.walls = wallsField.getText();
		theme.doors = doorsField.getText();
		theme.min = Integer.parseInt(minField.getText());
		theme.max = Integer.parseInt(maxField.getText());		
		theme.setPath(Editor.getStore().getActive().get("id"));

		theme.creatures.clear();
		for(Vector<?> data : (Vector<Vector>)creatureModel.getDataVector()) {
			theme.creatures.put(data.get(0).toString(), (Integer)data.get(1));
		}

		theme.features.clear();
		for(Vector<?> data : (Vector<Vector>)featureModel.getDataVector()) {
			theme.features.add(data.toArray());
		}

		theme.items.clear();
		for(Vector<?> data : (Vector<Vector>)itemModel.getDataVector()) {
			theme.items.put(data.get(0).toString(), (Integer)data.get(1));
		}
	}

	protected void load() {
		typeBox.setSelectedItem(theme.type);
		floorField.setText(theme.floor);
		wallsField.setText(theme.walls);
		doorsField.setText(theme.doors);
		minField.setValue(theme.min);
		maxField.setValue(theme.max);
		
		creatureModel.setNumRows(0);
		featureModel.setNumRows(0);
		itemModel.setNumRows(0);
		
		for(Map.Entry<String, Integer> creature : theme.creatures.entrySet()) {
			Object[] data = {creature.getKey(), creature.getValue()};
			creatureModel.insertRow(0, data);
		}
		
		for(Map.Entry<String, Integer> item : theme.items.entrySet()) {
			Object[] data = {item.getKey(), item.getValue()};
			itemModel.insertRow(0, data);
		}
		
		for(Object[] feature : theme.features) {
			featureModel.insertRow(0, feature);
		}		
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			if(e.getSource().equals(itemTable)) {
				menu.add(new ClickAction("Add item"));
				menu.add(new ClickAction("Remove item"));
				int row = itemTable.rowAtPoint(e.getPoint());
				itemTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(creatureTable)) {
				menu.add(new ClickAction("Add creature"));
				menu.add(new ClickAction("Remove creature"));
				int row = creatureTable.rowAtPoint(e.getPoint());
				creatureTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(featureTable)) {
				menu.add(new ClickAction("Add feature"));
				menu.add(new ClickAction("Remove feature"));
				int row = featureTable.rowAtPoint(e.getPoint());
				featureTable.getSelectionModel().setSelectionInterval(row, row);
			}
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add item")) {
				Object[] items = Editor.resources.getResources(RItem.class).toArray();
				String s = (String)JOptionPane.showInputDialog(frame, "Choose item:", 
						"Add item", JOptionPane.PLAIN_MESSAGE, null, items, "ham");
				if(s!= null) {
					String[] row = {s, "1"};
					itemModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove item")) {
				itemModel.removeRow(itemTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add feature")) {
				Object[] terrain = Editor.resources.getResources(RTerrain.class).toArray();
				String s = (String)JOptionPane.showInputDialog(frame, "Choose terrain type:", 
						"Add feature", JOptionPane.PLAIN_MESSAGE, null, terrain, "ham");
				if(s!= null) {
					String[] row = {s, "patch", "1", "1"};
					featureModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove feature")) {
				featureModel.removeRow(featureTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add creature")) {
				Object[] creatures = Editor.resources.getResources(RCreature.class).toArray();
				String s = JOptionPane.showInputDialog(frame, "Choose creature:", 
						"Add creature", JOptionPane.PLAIN_MESSAGE, null, creatures, null).toString();
				if(s!= null) {
					String[] row = {s, "1"};
					creatureModel.addRow(row);					
				}
			} else if(e.getActionCommand().equals("Remove creature")) {
				creatureModel.removeRow(creatureTable.getSelectedRow());				
			}
		}
	}

	private class ThemesTableModel extends DefaultTableModel {
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
