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
import javax.swing.border.TitledBorder;

import java.awt.*;

import neon.editor.Editor;
import neon.editor.NeonFormat;
import neon.editor.help.HelpLabels;
import neon.entities.property.Ability;
import neon.resources.RTattoo;

public class TattooEditor extends ObjectEditor {
	private RTattoo tattoo;
	private JComboBox<Ability> abilityBox;
	private JSpinner abilitySpinner;
	private JTextField nameField;
	private JFormattedTextField costField;
	
	public TattooEditor(JFrame parent, RTattoo tattoo) {
		super(parent, "Tattoo: " + tattoo.id);
		this.tattoo = tattoo;
		
		JPanel abilityPanel = new JPanel();
		abilityPanel.setBorder(new TitledBorder("Properties"));	
		JLabel nameLabel = new JLabel("Name: ");
		JLabel abilityLabel = new JLabel("Ability: ");
		JLabel costLabel = new JLabel("Price: ");
		nameField = new JTextField(15);
		abilityBox = new JComboBox<Ability>(Ability.values());
		abilitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel abilityHelpLabel = HelpLabels.getTattooAbilityHelpLabel();
		frame.add(abilityPanel, BorderLayout.CENTER);
		
		GroupLayout layout = new GroupLayout(abilityPanel);
		abilityPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(abilityLabel).addComponent(abilityBox).addComponent(abilitySpinner)
						.addComponent(abilityHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(costLabel).addComponent(costField)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(abilityLabel).addComponent(costLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(abilityBox).addComponent(costField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(abilitySpinner))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(abilityHelpLabel)));
	}

	protected void save() {
		tattoo.name = nameField.getText();
		tattoo.ability = (Ability)abilityBox.getSelectedItem();
		tattoo.magnitude = (Integer)abilitySpinner.getValue();
		tattoo.cost = Integer.parseInt(costField.getText());
		tattoo.setPath(Editor.getStore().getActive().get("id"));
	}

	protected void load() {
		nameField.setText(tattoo.name);
		costField.setValue(tattoo.cost);
		abilityBox.setSelectedItem(tattoo.ability);
		abilitySpinner.setValue(tattoo.magnitude);
	}
}
