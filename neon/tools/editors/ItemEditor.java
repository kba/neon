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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import neon.objects.resources.RItem.Type;
import neon.tools.ColorCellRenderer;
import neon.tools.Editor;
import neon.tools.NeonFormat;
import neon.tools.help.HelpLabels;
import neon.objects.resources.RItem;
import neon.util.ColorFactory;

public class ItemEditor extends ObjectEditor {
	private JTextField nameField;
	private JTextArea svgArea;
	private JComboBox<String> colorBox;
	private JCheckBox aidBox, topBox, svgBox;
	private JFormattedTextField costField, weightField, charField;
	private RItem data;
	
	public ItemEditor(JFrame parent, RItem data) {
		super(parent, "Item Editor: " + data.id);
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
		JLabel aidLabel = new JLabel("First aid: ");
		JLabel topLabel = new JLabel("Always on top: ");
		JLabel svgLabel = new JLabel("Custom graphics: ");
		nameField = new JTextField(15);
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		weightField = new JFormattedTextField(NeonFormat.getFloatInstance());
		aidBox = new JCheckBox();
		topBox = new JCheckBox();
		svgBox = new JCheckBox();
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel costHelpLabel = HelpLabels.getCostHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel weightHelpLabel = HelpLabels.getWeightHelpLabel();
		JLabel aidHelpLabel = HelpLabels.getAidHelpLabel();
		JLabel topHelpLabel = HelpLabels.getTopHelpLabel();
		JLabel svgHelpLabel = HelpLabels.getSVGHelpLabel();
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
						.addComponent(aidLabel).addComponent(aidBox).addComponent(aidHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(topLabel).addComponent(topBox).addComponent(topHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(svgLabel).addComponent(svgBox).addComponent(svgHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(costLabel).addComponent(colorLabel)
						.addComponent(charLabel).addComponent(weightLabel).addComponent(aidLabel)
						.addComponent(topLabel).addComponent(svgLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(costField).addComponent(colorBox).addComponent(charField).addComponent(weightField)
						.addComponent(aidBox).addComponent(topBox).addComponent(svgBox))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(costHelpLabel).addComponent(colorHelpLabel)
						.addComponent(charHelpLabel).addComponent(weightHelpLabel).addComponent(aidHelpLabel)
						.addComponent(topHelpLabel).addComponent(svgHelpLabel)));
		JScrollPane propScroller = new JScrollPane(itemProps);
		propScroller.setBorder(new TitledBorder("Properties"));		
		frame.add(propScroller, BorderLayout.PAGE_START);
		
		svgArea = new JTextArea();
		JScrollPane svgScroller = new JScrollPane(svgArea);
		svgScroller.setBorder(new TitledBorder("Custom graphics"));		
		frame.add(svgScroller, BorderLayout.CENTER);
	}
	
	protected void load() {
		nameField.setText(data.name);
		costField.setValue(data.cost);
		colorBox.setSelectedItem(data.color);
		charField.setValue(data.text);
		weightField.setValue(data.weight);
		aidBox.setSelected(data.type == Type.aid);
		topBox.setSelected(data.top);
		svgBox.setSelected(data.svg != null);
		svgArea.setText(data.svg);
	}
	
	protected void save() {
		if(aidBox.isSelected()) {
			data.type = Type.aid;
		} else {
			data.type = Type.item;
		}
		data.name = nameField.getText();
		data.cost = Integer.parseInt(costField.getText());
		data.weight = Float.parseFloat(weightField.getText());
		data.setPath(Editor.getStore().getActive().get("id"));
		data.top = topBox.isSelected();
		if(svgBox.isSelected()) {
			data.svg = svgArea.getText();
		} else {
			data.color = colorBox.getSelectedItem().toString();
			data.text = charField.getText();
			data.svg = null;
		}
	}
}
