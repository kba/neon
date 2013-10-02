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

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.editor.resources.RFaction;

public class FactionEditor extends ObjectEditor {
	private RFaction faction;
	private JTextField nameField;
	
	public FactionEditor(JFrame parent, RFaction data) {
		super(parent, "Faction Editor: " + data.id);
		faction = data;
		
		JPanel props = new JPanel();
		props.add(new JLabel("Name: "));
		nameField = new JTextField(15);
		props.add(nameField);
		props.add(HelpLabels.getNameHelpLabel());
		props.setBorder(new TitledBorder("Properties"));	
		frame.add(props, BorderLayout.CENTER);
	}
	
	protected void save() {
		faction.name = nameField.getText();
		faction.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		nameField.setText(faction.name);
	}
}
