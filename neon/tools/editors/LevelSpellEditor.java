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

import java.awt.event.*;
import javax.swing.*;
import neon.tools.Editor;
import neon.objects.resources.LSpell;
import neon.objects.resources.RSpell;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * This class allows editing of levelled spells. Only real spells can be levelled,
 * not curses, diseases, ...
 * 
 * @author mdriesen
 */
@SuppressWarnings("serial")
public class LevelSpellEditor extends ObjectEditor implements MouseListener {
	private LSpell data;
	private JTable table;
	private DefaultTableModel model;
	
	public LevelSpellEditor(JFrame parent, LSpell data) {
		super(parent, "Levelled spell: " + data.id);
		this.data = data;
				
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
		scroller.setBorder(new TitledBorder("Spells"));	
		
		frame.add(scroller, BorderLayout.CENTER);
	}
	
	protected void save() {
		data.spells.clear();
		for(int i = 0; i < table.getModel().getRowCount(); i++) {
			data.spells.put((String)table.getModel().getValueAt(i, 0), 
					Integer.parseInt(table.getModel().getValueAt(i, 1).toString()));
		}
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		for(String s : data.spells.keySet()) {
			Object[] row = {s, data.spells.get(s)};
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
			menu.add(new ClickAction("Delete spell"));
			menu.add(new ClickAction("Add spell"));				
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add spell")) {
				ArrayList<String> spells = new ArrayList<String>();
				for(RSpell spell : Editor.resources.getResources(RSpell.class)) {
					spells.add(spell.id);
				}
				String s = (String)JOptionPane.showInputDialog(frame, "Add spell:",
						"Add spell", JOptionPane.PLAIN_MESSAGE, null, spells.toArray(), 0);
				if ((s != null) && (s.length() > 0)) {
					String[] item = {s, "0"};
					model.addRow(item);
				}	
			} else if(e.getActionCommand().equals("Delete spell")) {
				model.removeRow(table.getSelectedRow());
			} 
		}
	}
}
