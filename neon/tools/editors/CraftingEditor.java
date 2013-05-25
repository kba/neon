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
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import neon.objects.resources.RCraft;
import neon.objects.resources.RItem;
import neon.tools.Editor;
import neon.tools.NeonFormat;
import neon.tools.help.HelpLabels;

public class CraftingEditor extends ObjectEditor {
	private RCraft craft;
	private JComboBox<RItem> rawBox;
	private JFormattedTextField costField, amountField;
	
	public CraftingEditor(JFrame parent, RCraft data) {
		super(parent, "Crafting Editor: " + data.id);
		craft = data;
		
		JPanel props = new JPanel();
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);
		props.setBorder(new TitledBorder("Properties"));

		JLabel rawLabel = new JLabel("Raw material: ");
		JLabel amountLabel = new JLabel("Amount: ");
		JLabel costLabel = new JLabel("Cost: ");
		rawBox = new JComboBox<RItem>(Editor.resources.getResources(RItem.class));
		amountField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		JLabel rawHelpLabel = HelpLabels.getRawHelpLabel();
		JLabel amountHelpLabel = HelpLabels.getAmountHelpLabel();
		JLabel costHelpLabel = HelpLabels.getCraftingCostHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rawLabel).addComponent(rawBox).addComponent(rawHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(amountLabel).addComponent(amountField).addComponent(amountHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(costLabel).addComponent(costField).addComponent(costHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(rawLabel).addComponent(amountLabel).addComponent(costLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(rawBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(amountField).addComponent(costField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(rawHelpLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(amountHelpLabel).addComponent(costHelpLabel)));

		frame.add(props, BorderLayout.CENTER);
	}
	
	protected void load() {
		RItem raw = (RItem)Editor.resources.getResource(craft.raw);
		rawBox.setSelectedItem(raw);
		amountField.setValue(craft.amount);
		costField.setValue(craft.cost);
	}
	
	protected void save() {
		craft.raw = ((RItem)rawBox.getSelectedItem()).id;
		craft.cost = Integer.parseInt(costField.getText());
		craft.amount = Integer.parseInt(amountField.getText());
		craft.setPath(Editor.getStore().getActive().get("id"));
	}
}
