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

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.entities.property.Ability;
import neon.resources.RSign;
import neon.resources.RSpell;

@SuppressWarnings("serial")
public class SignEditor extends ObjectEditor implements MouseListener {
	private DefaultTableModel abilityModel, powerModel;
	private JTable abilityTable, powerTable;
	private RSign sign;
	private JTextField nameField;
	
	public SignEditor(JFrame parent, RSign sign) {
		super(parent, "Birth sign: " + sign.id);
		this.sign = sign;
		
		JLabel nameLabel = new JLabel("Name: ");
		nameField = new JTextField(15);
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JPanel namePanel = new JPanel();
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		namePanel.add(new JLabel(" "));
		namePanel.add(nameHelpLabel);
		
		String[] abilities = {"ability", "magnitude"};
		abilityModel = new SignsTableModel(abilities, Ability.class, Integer.class);
		abilityTable = new JTable(abilityModel);
		abilityTable.setFillsViewportHeight(true);
		abilityTable.addMouseListener(this);
		JScrollPane abilityScroller = new JScrollPane(abilityTable);
		
		String[] powers = {"power"};
		powerModel = new SignsTableModel(powers, String.class);
		powerTable = new JTable(powerModel);
		powerTable.setFillsViewportHeight(true);
		powerTable.addMouseListener(this);
		JScrollPane powerScroller = new JScrollPane(powerTable);
		
		JTabbedPane stuff = new JTabbedPane();
		stuff.add("Abilities", abilityScroller);
		stuff.add("Powers", powerScroller);
		
		JPanel props = new JPanel(new BorderLayout());
		props.add(namePanel, BorderLayout.PAGE_START);
		props.add(stuff, BorderLayout.CENTER);
		props.setBorder(new TitledBorder("Properties"));
		frame.add(props, BorderLayout.CENTER);
	}

	protected void save() {
		sign.name = nameField.getText();
		sign.abilities.clear();
		for(Vector<?> data : (Vector<Vector>)abilityModel.getDataVector()) {
			sign.abilities.put((Ability)data.get(0), (Integer)data.get(1));
		}
		sign.powers.clear();
		for(Vector<String> data : (Vector<Vector>)powerModel.getDataVector()) {
			sign.powers.add(data.get(0).toString());
		}
		sign.setPath(Editor.getStore().getActive().get("id"));
	}

	protected void load() {
		nameField.setText(sign.name);
		powerModel.setNumRows(0);
		abilityModel.setNumRows(0);
		for(String power : sign.powers) {
			String[] data = {power};
			powerModel.insertRow(0, data);
		}
		for(Map.Entry<Ability, Integer> ability : sign.abilities.entrySet()) {
			Object[] data = {ability.getKey(), ability.getValue()};
			abilityModel.insertRow(0, data);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			if(e.getSource().equals(powerTable)) {
				menu.add(new ClickAction("Add power"));
				menu.add(new ClickAction("Remove power"));
				int row = powerTable.rowAtPoint(e.getPoint());
				powerTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(abilityTable)) {
				menu.add(new ClickAction("Add ability"));
				menu.add(new ClickAction("Remove ability"));
				int row = abilityTable.rowAtPoint(e.getPoint());
				abilityTable.getSelectionModel().setSelectionInterval(row, row);
			}
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add power")) {
				ArrayList<String> powers = new ArrayList<String>();
				for(RSpell.Power power : Editor.resources.getResources(RSpell.Power.class)) {
					powers.add(power.id);
				}
				String s = (String)JOptionPane.showInputDialog(frame, "Choose power:", 
						"Add power", JOptionPane.PLAIN_MESSAGE, null, powers.toArray(), null);
				if(s!= null) {
					String[] row = {s};
					powerModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove power")) {
				powerModel.removeRow(powerTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add ability")) {
				Object[] abilities = Ability.values();
				Ability a = (Ability)JOptionPane.showInputDialog(frame, "Choose ability:", "Add ability", 
						JOptionPane.PLAIN_MESSAGE, null, abilities, null);
				if(a!= null) {
					Object[] row = {a, 0};
					abilityModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove ability")) {
				abilityModel.removeRow(abilityTable.getSelectedRow());
			}
		}
	}

	private static class SignsTableModel extends DefaultTableModel {
		private Class<?>[] classes;
		
		public SignsTableModel(String[] columns, Class<?>...classes) {
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
