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

package neon.editor.maps;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.editor.resources.IDoor;
import neon.editor.resources.RMap;
import neon.editor.resources.RZone;
import neon.resources.RDungeonTheme;
import neon.resources.RItem;
import neon.resources.RSpell;
import neon.resources.RItem.Type;

import java.awt.event.*;

public class DoorInstanceEditor implements ActionListener, ItemListener {
	private IDoor door;
	private JDialog frame;
	private JComboBox<RZone> zoneBox;
	private JTextField textField;
	private JFormattedTextField xField, yField;
	private JComboBox<RDungeonTheme> themeBox;
	private JCheckBox destBox, lockBox, trapBox;
	private JSpinner lockSpinner, trapSpinner;
	private JComboBox<RMap> mapBox;
	private JComboBox<IDoor.State> stateBox;
	private JComboBox<RItem> keyBox;
	private JComboBox<RSpell.Enchantment> spellBox;
	
	public DoorInstanceEditor(IDoor door, JFrame parent) {
		this.door = door;
		frame = new JDialog(parent, "Door instance editor: " + door.resource.id);
		JPanel content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
				
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
		
		JPanel lockPanel = new JPanel();
		lockPanel.setBorder(new TitledBorder("Lock"));
		GroupLayout lockLayout = new GroupLayout(lockPanel);
		lockPanel.setLayout(lockLayout);
		lockLayout.setAutoCreateGaps(true);
		
		JLabel stateLabel = new JLabel("State: ");
		JLabel lockLabel = new JLabel("Lock difficulty: ");
		JLabel keyLabel = new JLabel("Key: ");
		stateBox = new JComboBox<IDoor.State>(IDoor.State.values());
		lockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		keyBox = new JComboBox<RItem>();
		keyBox.addItem(null);
		JLabel stateHelpLabel = HelpLabels.getLockStateHelpLabel();
		JLabel lockHelpLabel = HelpLabels.getLockDCHelpLabel();
		JLabel keyHelpLabel = HelpLabels.getKeyHelpLabel();
		lockLayout.setVerticalGroup(
				lockLayout.createSequentialGroup()
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(stateLabel).addComponent(stateBox).addComponent(stateHelpLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lockLabel).addComponent(lockSpinner).addComponent(lockHelpLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(keyLabel).addComponent(keyBox).addComponent(keyHelpLabel)));
		lockLayout.setHorizontalGroup(
				lockLayout.createSequentialGroup()
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(stateLabel).addComponent(lockLabel).addComponent(keyLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(stateBox).addComponent(lockSpinner).addComponent(keyBox))
				.addGap(10)
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(stateHelpLabel).addComponent(lockHelpLabel).addComponent(keyHelpLabel)));

		JPanel trapPanel = new JPanel();
		trapPanel.setBorder(new TitledBorder("Trap"));
		GroupLayout trapLayout = new GroupLayout(trapPanel);
		trapPanel.setLayout(trapLayout);
		trapLayout.setAutoCreateGaps(true);
		
		JLabel trapLabel = new JLabel("Trap difficulty: ");
		trapSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		JLabel spellLabel = new JLabel("Spell: ");
		spellBox = new JComboBox<RSpell.Enchantment>();
		trapLayout.setVerticalGroup(
				trapLayout.createSequentialGroup()
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(trapLabel).addComponent(trapSpinner))
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(spellLabel).addComponent(spellBox)));
		trapLayout.setHorizontalGroup(
				trapLayout.createSequentialGroup()
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(trapLabel).addComponent(spellLabel))
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(trapSpinner).addComponent(spellBox)));

		JPanel destPanel = new JPanel();
		destPanel.setBorder(new TitledBorder("Destination"));
		GroupLayout destLayout = new GroupLayout(destPanel);
		destPanel.setLayout(destLayout);
		destLayout.setAutoCreateGaps(true);
		
		JLabel xLabel = new JLabel("X: ");
		JLabel yLabel = new JLabel("Y: ");
		JLabel zoneLabel = new JLabel("Zone: ");
		JLabel mapLabel = new JLabel("Map: ");
		JLabel themeLabel = new JLabel("Theme: ");
		JLabel textLabel = new JLabel("Door sign: ");
		xField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		yField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		zoneBox = new JComboBox<RZone>();
		mapBox = new JComboBox<RMap>();
		mapBox.addItemListener(this);
		mapBox.addItem(null);
		themeBox = new JComboBox<RDungeonTheme>();
		themeBox.addItem(null);
		textField = new JTextField();
		destLayout.setVerticalGroup(
				destLayout.createSequentialGroup()
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(xLabel).addComponent(xField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(yLabel).addComponent(yField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(zoneLabel).addComponent(zoneBox))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(mapLabel).addComponent(mapBox))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(textLabel).addComponent(textField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(themeLabel).addComponent(themeBox)));
		destLayout.setHorizontalGroup(
				destLayout.createSequentialGroup()
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(xLabel).addComponent(yLabel)
						.addComponent(zoneLabel).addComponent(mapLabel).addComponent(textLabel).addComponent(themeLabel))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(xField).addComponent(yField).addComponent(zoneBox)
						.addComponent(mapBox).addComponent(textField).addComponent(themeBox)));

		JPanel optionPanel = new JPanel();
		lockBox = new JCheckBox("lockable");
		lockBox.addItemListener(this);
		trapBox = new JCheckBox("trapped");
		trapBox.addItemListener(this);
		destBox = new JCheckBox("portal");
		destBox.addItemListener(this);
		optionPanel.add(lockBox);
		optionPanel.add(trapBox);
		optionPanel.add(destBox);
		
		JPanel leftPanel = new JPanel(new GridLayout(0, 1));
		leftPanel.add(lockPanel);
		leftPanel.add(trapPanel);
		JPanel propPanel = new JPanel(new GridLayout(0, 2));
		propPanel.add(leftPanel);
		propPanel.add(destPanel);
		
		content.setBorder(new TitledBorder("Properties"));
		content.add(propPanel, BorderLayout.CENTER);
		content.add(optionPanel, BorderLayout.PAGE_START);
		
		initDoor();	
	}

	public void show() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initDoor() {
		// maps laden
		for(RMap map : Editor.resources.getResources(RMap.class)) {
			mapBox.addItem(map);	
		}
		
		// zones laden
		if(door.destMap != null) {
			for(RZone zone : door.destMap.zones.values()) {
				zoneBox.addItem(zone);
			}
		}
		
		// themes laden
		for(RDungeonTheme theme : Editor.resources.getResources(RDungeonTheme.class)) {
			themeBox.addItem(theme);	
		}
		
		// destination
		destBox.setSelected(door.isPortal());
		xField.setEditable(door.isPortal());
		yField.setEditable(door.isPortal());
		mapBox.setEnabled(door.isPortal());
		zoneBox.setEnabled(door.isPortal());
		textField.setEditable(door.isPortal());
		themeBox.setEnabled(door.isPortal());
		themeBox.setSelectedItem(door.destTheme);
		mapBox.setSelectedItem(door.destMap);
		zoneBox.setSelectedItem(door.destZone);
		textField.setText(door.text);
		if(door.destPos != null) {
			xField.setValue(door.destPos.x);
			yField.setValue(door.destPos.y);
		}
		
		// keys laden
		for(RItem ri : Editor.resources.getResources(RItem.class)) {
			if(ri.type == Type.item) {
				keyBox.addItem(ri);
			}
		}
		// lockable
		lockBox.setSelected(door.lock > 0);
		lockSpinner.setEnabled(door.lock > 0);
		keyBox.setEnabled(door.lock > 0);
		stateBox.setEnabled(door.lock > 0);
		lockSpinner.setValue(door.lock);
		keyBox.setSelectedItem(door.key);
		stateBox.setSelectedItem(door.state);
		// spells laden
		for(RSpell.Enchantment rs : Editor.resources.getResources(RSpell.Enchantment.class)) {
			if(rs.item.equals("trap")) {
				spellBox.addItem(rs);
			}
		}
		// trapped
		trapBox.setSelected(door.trap > 0);
		trapSpinner.setEnabled(door.trap > 0);
		trapSpinner.setValue(door.trap);
		spellBox.setSelectedItem(door.spell);
		spellBox.setEnabled(door.trap > 0);
	}

	public void itemStateChanged(ItemEvent e) {
		 Object source = e.getItemSelectable();

		 if(source == lockBox) {
			 if(e.getStateChange() == ItemEvent.DESELECTED) {
				 stateBox.setEnabled(false);
				 lockSpinner.setEnabled(false);
				 keyBox.setEnabled(false);
			 } else {
				 stateBox.setEnabled(true);
				 lockSpinner.setEnabled(true);
				 keyBox.setEnabled(true);				 
			 }
		 } else if(source == destBox) {
			 if(e.getStateChange() == ItemEvent.DESELECTED) {
					xField.setEditable(false);
					yField.setEditable(false);
					mapBox.setEnabled(false);
					zoneBox.setEnabled(false);
					textField.setEditable(false);
					themeBox.setEnabled(false);
			 } else {
					xField.setEditable(true);
					yField.setEditable(true);
					mapBox.setEnabled(true);
					zoneBox.setEnabled(true);
					textField.setEditable(true);
					themeBox.setEnabled(true);
			 }
		 } else if(source == mapBox) {
			 // zones laden
			 zoneBox.setModel(new DefaultComboBoxModel<RZone>());
			 RMap map = (RMap)mapBox.getSelectedItem();
			 if(map != null) {
				 for(RZone zone : map.zones.values()) {
					 zoneBox.addItem(zone);
				 }
			 }
			 frame.pack();
		 } else if(source == trapBox) {
			 if(e.getStateChange() == ItemEvent.DESELECTED) {
				 trapSpinner.setEnabled(false);
				 spellBox.setEnabled(false);
			 } else {
				 trapSpinner.setEnabled(true);
				 spellBox.setEnabled(true);				 
			 }			 
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
	
	public void save() {
		// lock
		door.state = (IDoor.State)stateBox.getSelectedItem();
		if(lockBox.isSelected()) {
			door.lock = (Integer)lockSpinner.getValue();
			door.key = (RItem)keyBox.getSelectedItem();
		} else {
			door.lock = 0;
			door.key = null;
		}

		// trap
		door.state = (IDoor.State)stateBox.getSelectedItem();
		if(trapBox.isSelected()) {
			door.trap = (Integer)trapSpinner.getValue();
			door.spell = (RSpell.Enchantment)spellBox.getSelectedItem();
		} else {
			door.trap = 0;
			door.spell = null;
		}

		// bestemming
		if(destBox.isSelected()) {
			if(!textField.getText().isEmpty()) {
				door.text = textField.getText();
			} else {
				door.text = null;
			}
			if(themeBox.getSelectedItem() != null) {
				door.destTheme = (RDungeonTheme)themeBox.getSelectedItem();
				door.destMap = null;
				door.destZone = null;
				door.destPos = null;
			} else {
				door.destTheme = null;
				door.destMap = (RMap)mapBox.getSelectedItem();
				door.destZone = (RZone)zoneBox.getSelectedItem();
				if(!xField.getText().isEmpty() && !yField.getText().isEmpty()) {
					door.destPos = new Point((Integer)xField.getValue(), (Integer)yField.getValue());
				}
			}
		}
	}
}
