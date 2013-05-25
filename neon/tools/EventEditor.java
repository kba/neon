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

package neon.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import neon.util.MultiMap;

public class EventEditor implements ListSelectionListener, ActionListener, MouseListener {
	private JDialog frame;
	private MultiMap<String, String> events;
	private JList<String> times;
	private JList<String> list;
	private DefaultListModel<String> model;
	private DefaultListModel<String> stampModel;
	private String[] scripts;
	
	public EventEditor(JFrame parent) {
		frame = new JDialog(parent, "Event Editor");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(480, 300));
		
		model = new DefaultListModel<String>();
		list = new JList<String>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(this);
		list.addListSelectionListener(this);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBorder(new TitledBorder("Events"));
		
		stampModel = new DefaultListModel<String>();
		times = new JList<String>(stampModel);
		times.addMouseListener(this);
		JScrollPane textScroller = new JScrollPane(times);
		textScroller.setBorder(new TitledBorder("Timestamps"));
		
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
		buttons.add(cancel);
		buttons.add(apply);
	}

	public void show() {
		model.clear();
		scripts = Editor.getStore().getScripts().keySet().toArray(new String[0]);
		events = new MultiMap<String, String>();
		for(String event : Editor.getStore().getEvents().keySet()) {
			events.putAll(event, Editor.getStore().getEvents().get(event));
			model.addElement(event);
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void valueChanged(ListSelectionEvent e) {
		// blijkbaar worden er twee events gefired bij selectie
		if(e.getValueIsAdjusting()) {
			stampModel.clear();
			for(String s : events.get(list.getSelectedValue().toString())) {
				stampModel.addElement(s);
			}
		}
	}

	private void save() {
		Editor.getStore().getEvents().clear();
		for(String event : events.keySet()) {
			Editor.getStore().getEvents().putAll(event, events.get(event));
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
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(e.getSource().equals(list)) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new ClickAction("Add event"));
				menu.add(new ClickAction("Remove event"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				list.setSelectedIndex(list.locationToIndex(new Point(e.getX(), e.getY())));
			} else if(e.getSource().equals(times)) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new ClickAction("Add timestamp"));
				menu.add(new ClickAction("Remove timestamp"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				times.setSelectedIndex(times.locationToIndex(new Point(e.getX(), e.getY())));
			}
		}
	}

	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add event")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Select event:",
						"New event", JOptionPane.PLAIN_MESSAGE, null, scripts, 0);
				if ((s != null) && (s.length() > 0)) {
					model.addElement(s);
					events.put(s, "");
					list.setSelectedValue(s, true);
				}
			} else if(e.getActionCommand().equals("Remove event")) {
				try {
					if(list.getSelectedIndex() >= 0) {
						int index = list.getSelectedIndex();
						events.remove(list.getSelectedValue().toString());
						model.remove(index);
					}
				} catch(ArrayIndexOutOfBoundsException a) {}
			} else if(e.getActionCommand().equals("Add timestamp")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Timestamp:",
						"Add timestamp", JOptionPane.QUESTION_MESSAGE);
				if(s.matches("\\d*:?\\d*:?\\d*")) {	// X:Y:Z
					stampModel.addElement(s);
					events.put(list.getSelectedValue().toString(), s);
					times.setSelectedValue(s, true);
				}
			} else if(e.getActionCommand().equals("Remove timestamp")) {
				try {
					if(times.getSelectedIndex() >= 0) {
						int index = times.getSelectedIndex();
						events.remove(list.getSelectedValue().toString(), times.getSelectedValue().toString());
						stampModel.remove(index);
					}
				} catch(ArrayIndexOutOfBoundsException a) {}
			}
		}
	}
}
