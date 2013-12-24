/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class DialogEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {
	private JDialog frame;
	private JTextArea pre;
	private JTextArea answer;
	private JTextArea action;
	private String[] topic;
	private int col, row;
	private JTable table;
	
	public DialogEditor(JFrame parent) {
		frame = new JDialog(parent, "Dialog Editor");
		frame.setPreferredSize(new Dimension(500, 500));
		
		JPanel center = new JPanel(new GridLayout(0,1));
		answer = new JTextArea(); 
		JScrollPane textScroller = new JScrollPane(answer);
		textScroller.setBorder(new TitledBorder("Answer"));
		pre = new JTextArea(); 
		JScrollPane preScroller = new JScrollPane(pre);
		preScroller.setBorder(new TitledBorder("Preconditions"));
		action = new JTextArea(); 
		JScrollPane actionScroller = new JScrollPane(action);
		actionScroller.setBorder(new TitledBorder("Actions"));
		center.add(preScroller);
		center.add(textScroller);
		center.add(actionScroller);
		
		JPanel content = new JPanel(new BorderLayout());
		content.add(center, BorderLayout.CENTER);
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

	private void save() {
		topic[1] = pre.getText();
		table.setValueAt(topic[1], row, 1);
		topic[2] = answer.getText();
		table.setValueAt(topic[2], row, 2);
		topic[3] = action.getText();
		table.setValueAt(topic[3], row, 3);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Apply")) {
			save();
		} else if(e.getActionCommand().equals("Cancel")) {
			frame.dispose();
		} else if(e.getActionCommand().equals("Ok")) {
			save();
			frame.dispose();
		} else if(e.getActionCommand().equals("edit")) {
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int col) {
		this.col = col;
		this.row = row;
		this.table = table;
		topic = new String[4];
		topic[0] = table.getModel().getValueAt(row, 0).toString();
		if(table.getModel().getValueAt(row, 1) != null) {
			topic[1] = table.getModel().getValueAt(row, 1).toString();
		}
		pre.setText(topic[1]);
		if(table.getModel().getValueAt(row, 2) != null) {
			topic[2] = table.getModel().getValueAt(row, 2).toString();
		}
		answer.setText(topic[2]);
		if(table.getModel().getValueAt(row, 3) != null) {
			topic[3] = table.getModel().getValueAt(row, 3).toString();
		}
		action.setText(topic[3]);
		JButton button = new JButton(value == null ? "" : value.toString());
		button.setActionCommand("edit");
		//		button.setBorderPainted(false);
		button.addActionListener(this);
		return button;
	}

	public Object getCellEditorValue() {
		return topic[col];
	}
}
