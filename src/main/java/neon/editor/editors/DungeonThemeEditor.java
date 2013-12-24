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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import neon.editor.Editor;
import neon.editor.NeonFormat;
import neon.editor.help.HelpLabels;
import neon.resources.RDungeonTheme;

public class DungeonThemeEditor extends ObjectEditor {
	private JTextField zoneField;
	private JFormattedTextField minField, maxField, branchField;
	private RDungeonTheme theme;
	
	public DungeonThemeEditor(JFrame parent, RDungeonTheme theme) {
		super(parent, "Dungeon theme: " + theme.id);
		this.theme = theme;
		
		JPanel props = new JPanel(); 
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);
		props.setBorder(new TitledBorder("Properties"));
		zoneField = new JTextField(15);
		minField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		maxField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		branchField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		JLabel zoneLabel = new JLabel("Zones: ");
		JLabel minLabel = new JLabel("Min. zones: ");
		JLabel maxLabel = new JLabel("Max. zones: ");
		JLabel branchLabel = new JLabel("Branch factor: ");
		JLabel zoneHelpLabel = HelpLabels.getZoneHelpLabel();
		JLabel minHelpLabel = HelpLabels.getMinZoneHelpLabel();
		JLabel maxHelpLabel = HelpLabels.getMaxZoneHelpLabel();
		JLabel branchHelpLabel = HelpLabels.getBranchHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(zoneLabel).addComponent(zoneField).addComponent(zoneHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(minLabel).addComponent(minField).addComponent(minHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(maxLabel).addComponent(maxField).addComponent(maxHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(branchLabel).addComponent(branchField).addComponent(branchHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(zoneLabel).addComponent(minLabel).addComponent(maxLabel)
						.addComponent(branchLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(zoneField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(minField).addComponent(maxField).addComponent(branchField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(zoneHelpLabel).addComponent(minHelpLabel).addComponent(maxHelpLabel)
						.addComponent(branchHelpLabel)));
		frame.add(props, BorderLayout.CENTER);
	}
	
	protected void save() {
		theme.min = Integer.parseInt(minField.getText());
		theme.max = Integer.parseInt(maxField.getText());
		theme.branching = Integer.parseInt(branchField.getText());
		theme.zones = zoneField.getText();
		theme.setPath(Editor.getStore().getActive().get("id"));
	}

	protected void load() {
		zoneField.setText(theme.zones);
		minField.setValue(theme.min);
		maxField.setValue(theme.max);
		branchField.setValue(theme.branching);
	}
}
