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
import javax.swing.border.TitledBorder;

import neon.editor.help.HelpLabels;
import neon.resources.RMod;

import java.awt.event.*;
import java.awt.*;

public class InfoEditor implements ActionListener {
	private JDialog frame;
	private JTextField titleField, bigField, smallField;
	
	public InfoEditor(JFrame parent) {
		frame = new JDialog(parent, "Game Info Editor");
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		frame.setContentPane(content);
		
		JPanel edit = new JPanel();
		edit.setBorder(new TitledBorder("Properties"));
		GroupLayout layout = new GroupLayout(edit);
		edit.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel titleLabel = new JLabel("Title: ");
		JLabel bigLabel = new JLabel("Big coins: ");
		JLabel smallLabel = new JLabel("Small coins: ");
		titleField = new JTextField(20);
		bigField = new JTextField(20);
		bigField.setText("€");
		smallField = new JTextField(20);
		smallField.setText("c");
		JLabel titleHelpLabel = HelpLabels.getTitleHelpLabel();
		JLabel bigHelpLabel = HelpLabels.getBigHelpLabel();
		JLabel smallHelpLabel = HelpLabels.getSmallHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(titleLabel).addComponent(titleField).addComponent(titleHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(bigLabel).addComponent(bigField).addComponent(bigHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(smallLabel).addComponent(smallField).addComponent(smallHelpLabel))
				.addGap(10));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(titleLabel).addComponent(bigLabel).addComponent(smallLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(titleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(bigField).addComponent(smallField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(titleHelpLabel).addComponent(bigHelpLabel).addComponent(smallHelpLabel)));
		content.add(edit, BorderLayout.PAGE_START);
		
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
		// veronderstellen dat data altijd geldig is...
		RMod data = Editor.getStore().getActive();
		titleField.setText(data.get("title"));
		if(data.get("big") != null) {
			bigField.setText(data.get("big"));
		}
		if(data.get("small") != null) {
			smallField.setText(data.get("small"));
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void save() {
		RMod data = Editor.getStore().getActive();
		// big mag niet hetzelfde zijn als small
		if(!bigField.getText().equals(smallField.getText())) {
			// big niet null of "€"
			if(!bigField.getText().isEmpty() && !bigField.getText().equals("€")) {
				data.set("big", bigField.getText());
			}
			// small niet null of "c"
			if(!smallField.getText().isEmpty() && !bigField.getText().equals("c")) {
				data.set("small", smallField.getText());
			}
		}
		// title niet null of "", tenzij bij extension
		if(!Editor.getStore().getActive().isExtension() || !(titleField.getText() == null && titleField.getText().isEmpty())) {
			data.set("title", titleField.getText());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if("Ok".equals(e.getActionCommand())) {
			save();
			frame.dispose();
		} else if("Cancel".equals(e.getActionCommand())){
			frame.dispose();
		} else if("Apply".equals(e.getActionCommand())){
			save();
		}
	}
}
