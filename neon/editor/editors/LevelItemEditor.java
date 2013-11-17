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
import neon.resources.LItem;
import neon.resources.RItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class LevelItemEditor extends ObjectEditor implements MouseListener {
	private LItem data;
	private JTable table;
	private DefaultTableModel model;
	
	@SuppressWarnings("serial")
	public LevelItemEditor(JFrame parent, LItem data) {
		super(parent, "Leveled Item Editor: " + data.id);
		this.data = data;
				
		// help
		JLabel help = new JLabel("<html>Right click on the list to add or "
				+ "delete an item. An item will start showing up when the "
				+ "player is at the indicated level.</html>");
		help.setBorder(new TitledBorder("Instructions"));
		
		// tabel
		String[] columns = {"id", "level"};
		model = new DefaultTableModel(columns, 0);
		table = new JTable(model) { 
			public boolean isCellEditable(int rowIndex, int colIndex) { 
				return colIndex == 1; 
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
		data.items.clear();
		for(int i = 0; i < table.getModel().getRowCount(); i++) {
			data.items.put((String)table.getModel().getValueAt(i, 0), 
					Integer.parseInt(table.getModel().getValueAt(i, 1).toString()));
		}
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		for(String s : data.items.keySet()) {
			Object[] row = {s, data.items.get(s)};
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
			menu.add(new ClickAction("Delete item"));
			menu.add(new ClickAction("Add item"));				
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add item")) {
				ArrayList<String> items = new ArrayList<String>();
				for(RItem item : Editor.resources.getResources(RItem.class)) {
					if(!item.id.equals(data.id)) {	// item niet in zichzelf steken
						items.add(item.id);
					}
				}
				String s = (String)JOptionPane.showInputDialog(frame, "Add item:",
						"Add item", JOptionPane.PLAIN_MESSAGE, null, items.toArray(), 0);
				if ((s != null) && (s.length() > 0)) {
					String[] item = {s, "0"};
					model.addRow(item);
				}	
			} else if(e.getActionCommand().equals("Delete item")) {
				model.removeRow(table.getSelectedRow());
			} 
		}
	}
}
