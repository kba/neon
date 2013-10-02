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
import java.awt.*;
import javax.swing.border.*;

import neon.editor.ColorCellRenderer;
import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.resources.RItem;
import neon.util.ColorFactory;

public class DoorEditor extends ObjectEditor {
	private JTextField nameField;
	private JComboBox<String> colorBox;
	private JFormattedTextField openField;
	private JFormattedTextField closedField;
	private JFormattedTextField lockedField;
	private RItem.Door data;
	
	public DoorEditor(JFrame parent, RItem.Door data) {
		super(parent, "Door Editor: " + data.id);
		this.data = data;
				
		JPanel itemProps = new JPanel();
		GroupLayout layout = new GroupLayout(itemProps);
		itemProps.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel colorLabel = new JLabel("Color: ");
		JLabel openLabel = new JLabel("Open character: ");
		JLabel closedLabel = new JLabel("Closed character: ");
		JLabel lockedLabel = new JLabel("Locked character: ");
		nameField = new JTextField(15);
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		openField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		lockedField = new JFormattedTextField(getMaskFormatter("*", ' '));
		closedField = new JFormattedTextField(getMaskFormatter("*", ' '));
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel openHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel closedHelpLabel = HelpLabels.getClosedHelpLabel();
		JLabel lockedHelpLabel = HelpLabels.getLockedHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(colorLabel).addComponent(colorBox).addComponent(colorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(openLabel).addComponent(openField).addComponent(openHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(closedLabel).addComponent(closedField).addComponent(closedHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lockedLabel).addComponent(lockedField).addComponent(lockedHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(colorLabel).addComponent(openLabel)
						.addComponent(closedLabel).addComponent(lockedLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(colorBox).addComponent(openField).addComponent(closedField).addComponent(lockedField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(colorHelpLabel).addComponent(openHelpLabel)
						.addComponent(closedHelpLabel).addComponent(lockedHelpLabel)));

		JScrollPane propScroller = new JScrollPane(itemProps);
		propScroller.setBorder(new TitledBorder("Properties"));
		frame.add(propScroller, BorderLayout.CENTER);
	}
	
	protected void load() {
		nameField.setText(data.name);
		colorBox.setSelectedItem(data.color);
		openField.setValue(data.text);
		if(!data.text.equals(data.closed)) {
			closedField.setValue(data.closed);
		}
		if(!data.locked.equals(data.closed)) {
			lockedField.setValue(data.locked);
		}
	}
	
	protected void save() {
		data.name = nameField.getText();
		data.color = colorBox.getSelectedItem().toString();
		data.text = openField.getText();
		if(!openField.getText().equals(closedField.getText())) {
			data.closed = closedField.getText();
		}
		if(!lockedField.getText().equals(closedField.getText())) {
			data.locked = lockedField.getText();
		}
		data.setPath(Editor.getStore().getActive().get("id"));
	}
}
