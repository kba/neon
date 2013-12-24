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
import java.text.ParseException;
import java.util.*;
import javax.swing.border.*;

import neon.editor.*;
import neon.editor.help.HelpLabels;
import neon.resources.RItem;
import neon.resources.RSpell;
import neon.resources.RSpell.SpellType;
import neon.util.ColorFactory;

public class ScrollEditor extends ObjectEditor {
	private JTextField nameField;
	private JFormattedTextField costField, weightField, charField;
	private JComboBox<String> colorBox;
	private JComboBox<String> spellBox;
	private JTextArea textArea;
	private RItem.Text data;
	private Vector<String> spells;
	
	public ScrollEditor(JFrame parent, RItem.Text data) {
		super(parent, "Scroll Editor: " + data.id);
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
		JLabel spellLabel = new JLabel("Spell: ");
		JLabel textLabel = new JLabel("Text: ");
		nameField = new JTextField(15);
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		weightField = new JFormattedTextField(NeonFormat.getFloatInstance());
		loadSpells();
		spellBox = new JComboBox<String>(spells);
		textArea = new JTextArea();
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel costHelpLabel = HelpLabels.getCostHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel weightHelpLabel = HelpLabels.getWeightHelpLabel();
		JLabel spellHelpLabel = HelpLabels.getSpellHelpLabel();
		JLabel textHelpLabel = HelpLabels.getScrollTextHelpLabel();
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
						.addComponent(spellLabel).addComponent(spellBox).addComponent(spellHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(textLabel).addComponent(textArea).addComponent(textHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(costLabel).addComponent(colorLabel)
						.addComponent(charLabel).addComponent(weightLabel).addComponent(spellLabel)
						.addComponent(textLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(costField).addComponent(colorBox).addComponent(charField).addComponent(weightField)
						.addComponent(spellBox).addComponent(textArea))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(costHelpLabel).addComponent(colorHelpLabel)
						.addComponent(charHelpLabel).addComponent(weightHelpLabel).addComponent(spellHelpLabel)
						.addComponent(textHelpLabel)));
		JScrollPane propScroller = new JScrollPane(itemProps);
		propScroller.setBorder(new TitledBorder("Properties"));

		frame.add(propScroller, BorderLayout.CENTER);
	}
	
	protected void save() {
		data.name = nameField.getText();
		try { // gefoefel om altijd long terug te krijgen
			costField.commitEdit();
			data.cost = ((Long)costField.getValue()).intValue();
		} catch (ParseException e) {
			data.cost = 0;
		}
		data.color = colorBox.getSelectedItem().toString();
		data.text = charField.getText();
		data.weight = Float.parseFloat(weightField.getText());
		data.content = textArea.getText();		
		if(spellBox.getSelectedItem() != null) {
			data.spell = spellBox.getSelectedItem().toString();
		} else {
			data.spell = null;
		}
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		nameField.setText(data.name);
		costField.setValue(data.cost);
		colorBox.setSelectedItem(data.color);
		charField.setValue(data.text);
		weightField.setValue(data.weight);
		textArea.setText(data.content);
		spellBox.setSelectedItem(data.spell);
	}
	
	private void loadSpells() {
		spells = new Vector<String>();
		for(RSpell spell : Editor.resources.getResources(RSpell.class)) {
			if(spell.type.equals(SpellType.SPELL)) {
				spells.add(spell.id);
			}
		}
	}
}
