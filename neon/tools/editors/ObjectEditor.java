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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;
import neon.util.ColorFactory;

public abstract class ObjectEditor {
	protected JDialog frame;

	public ObjectEditor(JFrame parent, String title) {
		frame = new JDialog(parent, title);
		JPanel content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
		
		ButtonListener listener = new ButtonListener();
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(listener);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(listener);
		JButton apply = new JButton("Apply");
		apply.addActionListener(listener);
		buttons.add(ok);
		buttons.add(cancel);
		buttons.add(apply);
	}
	
	public void show() {
		load();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	protected abstract void load();
	protected abstract void save();
	
	protected MaskFormatter getMaskFormatter(String s, char c) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
			formatter.setPlaceholderCharacter(c);
		} catch (java.text.ParseException e) { }
		return formatter;
	}

	protected class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ("Ok".equals(e.getActionCommand())) {
				save();
				frame.dispose();
			} else if ("Cancel".equals(e.getActionCommand())){
				frame.dispose();
			} else if ("Apply".equals(e.getActionCommand())){
				save();
			}
		}		
	}
	
	protected class ColorListener implements ActionListener {
		private JComboBox<String> box;
		
		public ColorListener(JComboBox<String> box) {
			this.box = box;
		}
		
		public void actionPerformed(ActionEvent e) {
			box.setForeground(ColorFactory.getColor((String)box.getSelectedItem()));			
		}
	}
}
