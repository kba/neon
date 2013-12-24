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

import neon.editor.ColorCellRenderer;
import neon.editor.Editor;
import neon.editor.NeonFormat;
import neon.editor.help.HelpLabels;
import neon.entities.property.Habitat;

import java.awt.*;
import java.util.Vector;
import javax.swing.border.*;

import neon.resources.RCreature;
import neon.resources.RCreature.Size;
import neon.resources.RCreature.Type;

public class CreatureEditor extends ObjectEditor {
	private RCreature data;
	private JTextField nameField;
	private JComboBox<String> colorBox;
	private JFormattedTextField charField;
	private JFormattedTextField speedField, defenseField, rangeField, manaField;
	private JTextField hitField;
	private JComboBox<Type> typeBox;
	private JComboBox<Size> sizeBox;
	private JComboBox<Habitat> habitatBox;
	private JTextField attackField;
	private JFormattedTextField strField, conField, dexField, intField, wisField, chaField;
	private JComboBox<RCreature.AIType> aiTypeBox;
	private JSpinner aggressionSpinner, confidenceSpinner;
	
	public CreatureEditor(JFrame parent, RCreature rCreature) {
		super(parent, "Creature Editor: " + rCreature.id);
		this.data = rCreature;
				
		// algemeen
		JPanel propPanel = new JPanel();
		GroupLayout propsLayout = new GroupLayout(propPanel);
		propPanel.setLayout(propsLayout);
		propsLayout.setAutoCreateGaps(true);
		propPanel.setBorder(new TitledBorder("General"));

		JLabel nameLabel = new JLabel("Name: ");
		JLabel colorLabel = new JLabel("Color: ");
		JLabel charLabel = new JLabel("Character: ");
		JLabel speedLabel = new JLabel("Speed: ");
		JLabel hitLabel = new JLabel("Hit dice: ");
		JLabel typeLabel = new JLabel("Type: ");
		JLabel sizeLabel = new JLabel("Size: ");
		JLabel habitatLabel = new JLabel("Habitat: ");
		JLabel strLabel = new JLabel("Str: ");
		JLabel conLabel = new JLabel("Con: ");
		JLabel dexLabel = new JLabel("Dex: ");
		JLabel intLabel = new JLabel("Int: ");
		JLabel wisLabel = new JLabel("Wis: ");
		JLabel chaLabel = new JLabel("Cha: ");
		JLabel manaLabel = new JLabel("Mana: ");
		JLabel attackLabel = new JLabel("Attack: ");
		JLabel defenseLabel = new JLabel("Defense: ");
		JLabel aiTypeLabel = new JLabel("AI type: ");
		JLabel aggressionLabel = new JLabel("Aggression: ");
		JLabel confidenceLabel = new JLabel("Confidence: ");
		JLabel rangeLabel = new JLabel("Territory: ");
		nameField = new JTextField(15);
		colorBox = new JComboBox<String>(neon.util.ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		speedField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		hitField = new JTextField(10);
		typeBox = new JComboBox<Type>(loadTypes());	// geen Type.values() vanwege player type
		sizeBox = new JComboBox<Size>(Size.values());
		habitatBox = new JComboBox<Habitat>(Habitat.values());
		strField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		conField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		dexField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		intField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		wisField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		chaField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		wisField.setColumns(10);
		manaField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		defenseField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		attackField = new JTextField(10);
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel speedHelpLabel = HelpLabels.getSpeedHelpLabel();
		JLabel hitHelpLabel = HelpLabels.getHitHelpLabel();
		JLabel typeHelpLabel = HelpLabels.getCreatureTypeHelpLabel();
		JLabel sizeHelpLabel = HelpLabels.getSizeHelpLabel();
		JLabel strHelpLabel = HelpLabels.getStrHelpLabel();
		JLabel conHelpLabel = HelpLabels.getConHelpLabel();
		JLabel dexHelpLabel = HelpLabels.getDexHelpLabel();
		JLabel intHelpLabel = HelpLabels.getIntHelpLabel();
		JLabel wisHelpLabel = HelpLabels.getWisHelpLabel();
		JLabel chaHelpLabel = HelpLabels.getChaHelpLabel();
		JLabel manaHelpLabel = HelpLabels.getManaHelpLabel();
		JLabel attackHelpLabel = HelpLabels.getAttackHelpLabel();
		JLabel defenseHelpLabel = HelpLabels.getDefenseHelpLabel();
		JLabel habitatHelpLabel = HelpLabels.getHabitatHelpLabel();
		propsLayout.setVerticalGroup(
				propsLayout.createSequentialGroup()
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(charLabel).addComponent(charField).addComponent(charHelpLabel)
						.addComponent(colorLabel).addComponent(colorBox).addComponent(colorHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(speedLabel).addComponent(speedField).addComponent(speedHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(hitLabel).addComponent(hitField).addComponent(hitHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(typeLabel).addComponent(typeBox).addComponent(typeHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sizeLabel).addComponent(sizeBox).addComponent(sizeHelpLabel)
						.addComponent(habitatLabel).addComponent(habitatBox).addComponent(habitatHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(strLabel).addComponent(strField).addComponent(strHelpLabel)
						.addComponent(intLabel).addComponent(intField).addComponent(intHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(conLabel).addComponent(conField).addComponent(conHelpLabel)
						.addComponent(wisLabel).addComponent(wisField).addComponent(wisHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(dexLabel).addComponent(dexField).addComponent(dexHelpLabel)
						.addComponent(chaLabel).addComponent(chaField).addComponent(chaHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(manaLabel).addComponent(manaField).addComponent(manaHelpLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(attackLabel).addComponent(attackField).addComponent(attackHelpLabel)
						.addComponent(defenseLabel).addComponent(defenseField).addComponent(defenseHelpLabel)));
		propsLayout.setHorizontalGroup(
				propsLayout.createSequentialGroup()
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(charLabel).addComponent(speedLabel)
						.addComponent(hitLabel).addComponent(typeLabel).addComponent(sizeLabel).addComponent(strLabel)
						.addComponent(conLabel).addComponent(dexLabel).addComponent(manaLabel).addComponent(attackLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(charField).addComponent(speedField).addComponent(hitField)
						.addComponent(typeBox).addComponent(sizeBox).addComponent(strField).addComponent(conField).addComponent(dexField)
						.addComponent(manaField).addComponent(attackField))
				.addGap(10)
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(charHelpLabel).addComponent(speedHelpLabel).addComponent(hitHelpLabel)
						.addComponent(typeHelpLabel).addComponent(sizeHelpLabel).addComponent(strHelpLabel).addComponent(conHelpLabel)
						.addComponent(dexHelpLabel).addComponent(manaHelpLabel).addComponent(attackHelpLabel))
				.addGap(10)
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(colorLabel).addComponent(habitatLabel).addComponent(intLabel)
						.addComponent(wisLabel).addComponent(chaLabel).addComponent(defenseLabel))
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(colorBox).addComponent(habitatBox).
						addComponent(dexField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(intField).addComponent(wisField).addComponent(chaField).addComponent(defenseField))
				.addGap(10)
				.addGroup(propsLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(colorHelpLabel).addComponent(habitatHelpLabel).addComponent(intHelpLabel)
						.addComponent(wisHelpLabel).addComponent(chaHelpLabel).addComponent(defenseHelpLabel)));
		
		// AI
		JPanel AIPanel = new JPanel();	
		GroupLayout AILayout = new GroupLayout(AIPanel);
		AIPanel.setLayout(AILayout);
		AILayout.setAutoCreateGaps(true);
		AIPanel.setBorder(new TitledBorder("AI"));
		aiTypeBox = new JComboBox<RCreature.AIType>(RCreature.AIType.values());
		aggressionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		confidenceSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		rangeField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		JLabel aiHelpLabel = HelpLabels.getAITypeHelpLabel();
		JLabel confidenceHelpLabel = HelpLabels.getConfidenceHelpLabel();
		JLabel aggressionHelpLabel = HelpLabels.getAggressionHelpLabel();
		JLabel rangeHelpLabel = HelpLabels.getRangeHelpLabel();
		rangeField.setValue(0);
		AILayout.setVerticalGroup(
				AILayout.createSequentialGroup()
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(aiTypeLabel).addComponent(aiTypeBox).addComponent(aiHelpLabel)
						.addComponent(aggressionLabel).addComponent(aggressionSpinner).addComponent(aggressionHelpLabel))
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(confidenceLabel).addComponent(confidenceSpinner).addComponent(confidenceHelpLabel)
						.addComponent(rangeLabel).addComponent(rangeField).addComponent(rangeHelpLabel)));
		AILayout.setHorizontalGroup(
				AILayout.createSequentialGroup()
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aiTypeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(confidenceLabel))
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(aiTypeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(confidenceSpinner))
				.addGap(10)
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aiHelpLabel).addComponent(confidenceHelpLabel))
				.addGap(10)
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aggressionLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(rangeLabel))
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(aggressionSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(rangeField))
				.addGap(10)
				.addGroup(AILayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aggressionHelpLabel).addComponent(rangeHelpLabel)));
		JPanel creatureProps = new JPanel(new BorderLayout());
		creatureProps.add(propPanel, BorderLayout.CENTER);
		creatureProps.add(AIPanel, BorderLayout.PAGE_END);

		JScrollPane scroller = new JScrollPane(creatureProps);
		scroller.setBorder(new TitledBorder("Properties"));		
		
		frame.add(scroller, BorderLayout.CENTER);
	}
	
	protected void save() {
		data.name = nameField.getText();
		data.hit = hitField.getText();
		data.color = colorBox.getSelectedItem().toString();
		data.speed = Integer.parseInt(speedField.getText());
		data.text = charField.getText();
		data.size = sizeBox.getItemAt(sizeBox.getSelectedIndex());
		data.type = typeBox.getItemAt(typeBox.getSelectedIndex());
		data.habitat = habitatBox.getItemAt(habitatBox.getSelectedIndex());
		data.mana = Integer.parseInt(manaField.getText());
		data.str = Integer.parseInt(strField.getText());
		data.con = Integer.parseInt(conField.getText());
		data.dex = Integer.parseInt(dexField.getText());
		data.iq = Integer.parseInt(intField.getText());
		data.wis = Integer.parseInt(wisField.getText());
		data.cha = Integer.parseInt(chaField.getText());
		data.av = attackField.getText();
		data.dv = Integer.parseInt(defenseField.getText());
		data.aiType = aiTypeBox.getItemAt(aiTypeBox.getSelectedIndex());
		data.aiRange = Integer.parseInt(rangeField.getText());
		data.aiAggr = (Integer)aggressionSpinner.getValue();
		data.aiConf = (Integer)confidenceSpinner.getValue();
		
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		nameField.setText(data.name);
		hitField.setText(data.hit);
		colorBox.setSelectedItem(data.color);
		speedField.setValue(data.speed);
		charField.setValue(data.text);
		sizeBox.setSelectedItem(data.size);
		typeBox.setSelectedItem(data.type);
		habitatBox.setSelectedItem(data.habitat);
		manaField.setValue(data.mana);
		strField.setValue(data.str);
		conField.setValue(data.con);
		dexField.setValue(data.dex);
		intField.setValue(data.iq);
		wisField.setValue(data.wis);
		chaField.setValue(data.cha);
		attackField.setText(data.av);
		defenseField.setValue(data.dv);
		aiTypeBox.setSelectedItem(data.aiType);
		rangeField.setValue(data.aiRange);
		aggressionSpinner.setValue(data.aiAggr);
		confidenceSpinner.setValue(data.aiConf);
	}

	private Vector<Type> loadTypes() {
		Vector<Type> types = new Vector<Type>();
		types.add(Type.animal);
		types.add(Type.construct);
		types.add(Type.daemon);
		types.add(Type.dragon);
		types.add(Type.goblin);
		types.add(Type.humanoid);
		types.add(Type.monster);
		return types;
	}
}
