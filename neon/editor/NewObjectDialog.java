/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
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
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

public class NewObjectDialog implements ActionListener {
	private JTextField idField;
	private JDialog dialog;
	private boolean cancelled;
	private String namespace = null;

	public Properties showInputDialog(JFrame frame, String message, String namespace) {
		this.namespace = namespace;
		return showInputDialog(frame, message);
	}
	
	public Properties showInputDialog(JFrame frame, String message) {
		dialog = new JDialog(frame, message, true);
		JPanel content = new JPanel(new BorderLayout());
		JPanel idPanel = new JPanel();
		idPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		idField = new JTextField(20);
		idPanel.add(new JLabel("Object id: "));
		idPanel.add(idField);

		content.add(idPanel, BorderLayout.PAGE_START);
		cancelled = false;
		
		// knoppekes
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		buttons.add(ok);
		buttons.add(cancel);

		// dialog laten zien
		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		
		String id = idField.getText();
		return new Properties(cancelled, id);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Ok")) {
			if(exists(idField.getText())) {
				JOptionPane.showMessageDialog(dialog, "Object id already exists.");
			} else if(!idField.getText().equals("")) {
				dialog.setVisible(false);
			}
		} else if(e.getActionCommand().equals("Cancel")) {
			cancelled = true;
			dialog.setVisible(false);
		}
	}
	
	public boolean exists(String id) {
		if(namespace == null) {
			return Editor.resources.getResource(id) != null;
		} else {
			return Editor.resources.getResource(id, namespace) != null;
		}
	}

	public static class Properties {
		private String id;
		private boolean ok;
		
		public Properties(boolean cancelled, String id) {
			this.ok = !cancelled;
			this.id = id;
		}
		
		public String getID() {
			return id;
		}
		
		public boolean cancelled() {
			return !ok;
		}
	}
}
