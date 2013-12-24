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

package neon.editor;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;

import neon.resources.RScript;

public class ScriptEditor implements ListSelectionListener, ActionListener, MouseListener {
	private JDialog frame;
	private JTextArea text;
	private JList<RScript> list;
	private DefaultListModel<RScript> model;
	
	public ScriptEditor(JFrame parent) {
		frame = new JDialog(parent, "Script Editor");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(480, 300));
		
		model = new DefaultListModel<RScript>();
		list = new JList<RScript>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setCellRenderer(new ModCellRenderer());
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBorder(new TitledBorder("Scripts"));
		
		text = new JTextArea();
		JScrollPane textScroller = new JScrollPane(text);
		textScroller.setBorder(new TitledBorder("Edit script"));
		
		JPanel content = new JPanel(new BorderLayout());
		content.add(listScroller, BorderLayout.LINE_START);
		content.add(textScroller, BorderLayout.CENTER);
		frame.setContentPane(content);
		
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		JButton apply = new JButton("Apply");
		apply.addActionListener(this);
		buttons.add(ok);
		list.addMouseListener(this);
		buttons.add(cancel);
		buttons.add(apply);
	}

	public void show() {
		model.clear();
		for(RScript script : Editor.getStore().getScripts().values()) {
			model.addElement(new RScript(script));
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void valueChanged(ListSelectionEvent e) {
		// blijkbaar worden er twee events gefired bij selectie
		if(e.getValueIsAdjusting()) {
			if(list.isSelectedIndex(e.getFirstIndex()) && !list.isSelectedIndex(e.getLastIndex())) {
				model.elementAt(e.getLastIndex()).script = text.getText();
			} else if(list.isSelectedIndex(e.getLastIndex()) && !list.isSelectedIndex(e.getFirstIndex())) {
				model.elementAt(e.getFirstIndex()).script = text.getText();				
			}
			text.setText(list.getSelectedValue().script);
		}
	}

	private void save() {
		HashMap<String, RScript> temp = new HashMap<String, RScript>(Editor.getStore().getScripts());
		Editor.getStore().getScripts().clear();
		for(Enumeration<RScript> rs = model.elements(); rs.hasMoreElements(); ) {
			RScript script = rs.nextElement();
			if(!equals(script, temp.get(script.id))) {	// script gewijzigd, dan naar active mod
				script.setPath(Editor.getStore().getActive().get("id"));
			}
			Editor.getStore().getScripts().put(script.id, script);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Cancel")) {
			frame.dispose();
		} else if(e.getActionCommand().equals("Apply")) {
			save();
		} else if(e.getActionCommand().equals("Ok")) {
			save();
			frame.dispose();
		} 
	}	
	
	public boolean equals(RScript one, RScript two) {
		return one.id.equals(two.id) && one.script.equals(two.script);
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ClickAction("New script"));
			menu.add(new ClickAction("Delete script"));
			menu.show(e.getComponent(), e.getX(), e.getY());
			list.setSelectedIndex(list.locationToIndex(new Point(e.getX(), e.getY())));
		}
	}

	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("New script")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Script name:",
						"New script", JOptionPane.QUESTION_MESSAGE);
				if ((s != null) && (s.length() > 0)) {
					RScript script = new RScript(s, Editor.getStore().getActive().get("id"));
					model.addElement(script);
					list.setSelectedValue(script, true);
					text.setText("");
				}
			} else if(e.getActionCommand().equals("Delete script")) {
				try {
					if(list.getSelectedIndex() >= 0) {
						int index = list.getSelectedIndex();
						model.remove(index);
					}
				} catch(ArrayIndexOutOfBoundsException a) {}
			}
		}
	}
}
