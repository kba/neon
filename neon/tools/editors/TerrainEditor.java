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
import neon.maps.Region.Modifier;
import neon.objects.resources.RTerrain;
import neon.tools.ColorCellRenderer;
import neon.tools.Editor;
import neon.tools.help.HelpLabels;
import neon.util.ColorFactory;

public class TerrainEditor extends ObjectEditor {
	private JFormattedTextField charField;
	private JComboBox<String> colorBox;
	private JComboBox<Modifier> modBox;
	private RTerrain data;
	
	public TerrainEditor(JFrame parent, RTerrain data) {
		super(parent, "Terrain Editor: " + data.id);
		this.data = data;
				
		JPanel terrainProps = new JPanel();
		GroupLayout layout = new GroupLayout(terrainProps);
		terrainProps.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel colorLabel = new JLabel("Color: ");
		JLabel charLabel = new JLabel("Character: ");
		JLabel modLabel = new JLabel("Modifier: ");
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		modBox = new JComboBox<Modifier>(Modifier.values());
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		JLabel modHelpLabel = HelpLabels.getTerrainHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(colorLabel).addComponent(colorBox).addComponent(colorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(charLabel).addComponent(charField).addComponent(charHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(modLabel).addComponent(modBox).addComponent(modHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(colorLabel)
						.addComponent(charLabel).addComponent(modLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(colorBox).addComponent(charField).addComponent(modBox))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(colorHelpLabel).addComponent(charHelpLabel).addComponent(modHelpLabel)));
		JScrollPane propScroller = new JScrollPane(terrainProps);
		propScroller.setBorder(new TitledBorder("Properties"));
		frame.add(propScroller, BorderLayout.CENTER);
	}
	
	protected void load() {
		colorBox.setSelectedItem(data.color);
		charField.setValue(data.text);
		modBox.setSelectedItem(data.modifier);
	}
	
	protected void save() {
		data.color = colorBox.getSelectedItem().toString();
		data.text = charField.getText();
		data.modifier = (Modifier)modBox.getSelectedItem();
		data.setPath(Editor.getStore().getActive().get("id"));
	}
}