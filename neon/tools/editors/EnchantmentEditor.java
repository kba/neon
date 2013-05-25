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

import java.awt.event.*;
import javax.swing.*;

import java.awt.*;
import java.util.Vector;
import javax.swing.border.*;

import neon.magic.Effect;
import neon.tools.Editor;
import neon.tools.NeonFormat;
import neon.tools.help.HelpLabels;
import neon.objects.resources.RSpell;

public class EnchantmentEditor extends ObjectEditor implements ActionListener {
	private JTextField nameField;
	private JFormattedTextField areaField, sizeField, durationField;
	private JComboBox<Effect> effectBox;
	private JComboBox<String> itemBox;
	private JTextArea scriptArea;
	private RSpell.Enchantment data;
	
	public EnchantmentEditor(JFrame parent, RSpell.Enchantment data) {
		super(parent, "Enchantment: " + data.id);
		this.data = data;
				
		JPanel props = new JPanel();
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);
		props.setBorder(new TitledBorder("Properties"));
		
		JLabel nameLabel = new JLabel("Name: ");
		JLabel itemLabel = new JLabel("Item type: ");
		JLabel effectLabel = new JLabel("Effect: ");
		JLabel sizeLabel = new JLabel("Magnitude: ");
		JLabel areaLabel = new JLabel("Radius: ");
		JLabel durationLabel = new JLabel("Duration: ");
		nameField = new JTextField(15);
		itemBox = new JComboBox<String>(getTypes());
		itemBox.addActionListener(this);
		effectBox = new JComboBox<Effect>(Effect.values());
		effectBox.addActionListener(this);
		areaField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		sizeField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		durationField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		scriptArea = new JTextArea();
		scriptArea.setDisabledTextColor(Color.red);
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel itemHelpLabel = HelpLabels.getItemHelpLabel();
		JLabel effectHelpLabel = HelpLabels.getEffectHelpLabel();
		JLabel sizeHelpLabel = HelpLabels.getSpellSizeHelpLabel();
		JLabel areaHelpLabel = HelpLabels.getSpellRadiusHelpLabel();
		JLabel durationHelpLabel = HelpLabels.getDurationHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(itemLabel).addComponent(itemBox).addComponent(itemHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(effectLabel).addComponent(effectBox).addComponent(effectHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sizeLabel).addComponent(sizeField).addComponent(sizeHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(areaLabel).addComponent(areaField).addComponent(areaHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(durationLabel).addComponent(durationField).addComponent(durationHelpLabel))
				.addGap(10));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(itemLabel).addComponent(effectLabel)
						.addComponent(sizeLabel).addComponent(areaLabel).addComponent(durationLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField).addComponent(itemBox)
						.addComponent(effectBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(sizeField).addComponent(areaField).addComponent(durationField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(itemHelpLabel).addComponent(effectHelpLabel)
						.addComponent(sizeHelpLabel).addComponent(areaHelpLabel).addComponent(durationHelpLabel)));
		frame.add(props, BorderLayout.PAGE_START);

		scriptArea = new JTextArea(6, 0);
		scriptArea.setDisabledTextColor(Color.red);
		JScrollPane scriptScroller = new JScrollPane(scriptArea);
		scriptScroller.setBorder(new TitledBorder("Script"));
		frame.add(scriptScroller, BorderLayout.CENTER);
	}
	
	private Vector<String> getTypes() {
		Vector<String> types = new Vector<String>();
		types.add("clothing");
		types.add("trap");
		types.add("nutrition");
		types.add("weapon");
		return types;
	}
	
	protected void load() {
		nameField.setText(data.name);
		itemBox.setSelectedItem(data.item);
		effectBox.setSelectedItem(data.effect);
		sizeField.setValue(data.size);
		durationField.setValue(data.duration);
		areaField.setValue(data.radius);
		scriptArea.setText(data.script);
		scriptArea.setEditable(data.effect == Effect.SCRIPTED);
	}

	protected void save() {
		data.name = nameField.getText();
		data.item = itemBox.getSelectedItem().toString();
		data.size = Integer.parseInt(sizeField.getText());
		data.radius = Integer.parseInt(areaField.getText());
		data.effect = effectBox.getItemAt(effectBox.getSelectedIndex());
		data.duration = Integer.parseInt(durationField.getText());
		data.script = scriptArea.getText();
		data.setPath(Editor.getStore().getActive().get("id"));
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(effectBox)){
			scriptArea.setEditable(effectBox.getSelectedItem() == Effect.SCRIPTED);
		} else if(e.getSource().equals(itemBox)){
			
		}
	}
}
