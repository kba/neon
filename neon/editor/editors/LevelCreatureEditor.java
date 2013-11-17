/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

import java.awt.event.*;
import javax.swing.*;
import neon.editor.Editor;
import neon.resources.LCreature;
import neon.resources.RCreature;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class LevelCreatureEditor extends ObjectEditor implements MouseListener {
	private LCreature data;
	private JTable table;
	private DefaultTableModel model;
	
	@SuppressWarnings("serial")
	public LevelCreatureEditor(JFrame parent, LCreature data) {
		super(parent, "Leveled Creature Editor: " + data.id);
		this.data = data;
		
		// help
		JLabel help = new JLabel("<html>Right click on the list to add or delete "
				+ "a creature. A creature will start showing up when the player "
				+ "is at the indicated level.</html>");
		help.setBorder(new TitledBorder("Instructions"));
		
		// tabel
		String[] columns = {"id", "level"};
		model = new DefaultTableModel(columns, 0);
		table = new JTable(model) { 
			public boolean isCellEditable(int rowIndex, int vColIndex) { 
				return vColIndex == 1; 
			} 
		};
		table.addMouseListener(this);
		table.getTableHeader().addMouseListener(this);
		JScrollPane scroller = new JScrollPane(table);

		frame.add(help, BorderLayout.PAGE_START);
		frame.add(scroller, BorderLayout.CENTER);
		frame.setPreferredSize(new Dimension(400, 400));
	}
	
	protected void save() {
		data.creatures.clear();
		for(int i = 0; i < table.getModel().getRowCount(); i++) {
			data.creatures.put((String)table.getModel().getValueAt(i, 0), 
					Integer.parseInt(table.getModel().getValueAt(i, 1).toString()));
		}
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		for(String s : data.creatures.keySet()) {
			Object[] row = {s, data.creatures.get(s)};
			model.addRow(row);
		}
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			int rowNumber = table.rowAtPoint(e.getPoint());
 			ListSelectionModel model = table.getSelectionModel();
			model.setSelectionInterval(rowNumber, rowNumber);
			
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ClickAction("Delete creature"));
			menu.add(new ClickAction("Add creature"));				
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add creature")) {
				ArrayList<String> creatures = new ArrayList<String>();
				for(RCreature rc : Editor.resources.getResources(RCreature.class)) {
					if(!rc.id.equals(data.id)) {	// levelled creature niet in zichzelf steken
						creatures.add(rc.id);
					}
				}
				String s = (String)JOptionPane.showInputDialog(frame, "Choose creature:",
						"Add creature", JOptionPane.PLAIN_MESSAGE, null, creatures.toArray(), 0);
				if ((s != null) && (s.length() > 0)) {
					String[] creature = {s, "0"};
					model.addRow(creature);
				}	
			} else if(e.getActionCommand().equals("Delete creature")) {
				model.removeRow(table.getSelectedRow());
			} 
		}
	}
}
