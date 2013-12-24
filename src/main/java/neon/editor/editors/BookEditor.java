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
import neon.editor.NeonFormat;
import neon.editor.help.HelpLabels;
import neon.resources.RItem;
import neon.util.ColorFactory;

public class BookEditor extends ObjectEditor {
	private JTextField nameField, textField;
	private JFormattedTextField costField, weightField, charField;
	private JComboBox<String> colorBox;
	private RItem.Text data;
	
	public BookEditor(JFrame parent, RItem.Text data) {
		super(parent, "Book Editor: " + data.id);
		this.data = data;
				
		JPanel itemProps = new JPanel();
		GroupLayout layout = new GroupLayout(itemProps);
		itemProps.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel costLabel = new JLabel("Cost: ");
		JLabel colorLabel = new JLabel("Color: ");
		JLabel charLabel = new JLabel("Character: ");
		JLabel weightLabel = new JLabel("Weight: ");
		JLabel textLabel = new JLabel("Text: ");
		nameField = new JTextField(15);
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		weightField = new JFormattedTextField(NeonFormat.getFloatInstance());
		textField = new JTextField(15);
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel costHelpLabel = HelpLabels.getCostHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel weightHelpLabel = HelpLabels.getWeightHelpLabel();
		JLabel textHelpLabel = HelpLabels.getBookTextHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(costLabel).addComponent(costField).addComponent(costHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(colorLabel).addComponent(colorBox).addComponent(colorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(charLabel).addComponent(charField).addComponent(charHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(weightLabel).addComponent(weightField).addComponent(weightHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(textLabel).addComponent(textField).addComponent(textHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(costLabel).addComponent(colorLabel)
						.addComponent(charLabel).addComponent(weightLabel).addComponent(textLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(costField).addComponent(colorBox).addComponent(charField).addComponent(weightField)
						.addComponent(textField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(costHelpLabel).addComponent(colorHelpLabel)
						.addComponent(charHelpLabel).addComponent(weightHelpLabel).addComponent(textHelpLabel)));

		JScrollPane propScroller = new JScrollPane(itemProps);
		propScroller.setBorder(new TitledBorder("Properties"));
		frame.add(propScroller, BorderLayout.CENTER);
	}
	
	protected void load() {
		nameField.setText(data.name);
		costField.setValue(data.cost);
		colorBox.setSelectedItem(data.color);
		charField.setValue(data.text);
		weightField.setValue(data.weight);
		textField.setText(data.content);
	}
	
	protected void save() {
		data.name = nameField.getText();
		data.cost = Integer.parseInt(costField.getText());
		data.color = colorBox.getSelectedItem().toString();
		data.text = charField.getText();
		data.weight = Float.parseFloat(weightField.getText());
		data.content = textField.getText();		
		
		data.setPath(Editor.getStore().getActive().get("id"));
	}
}
