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

package neon.tools.maps;

import javax.swing.*;
import javax.swing.border.*;
import neon.objects.resources.RDungeonTheme;
import neon.objects.resources.RTerrain;
import neon.tools.Editor;
import java.awt.event.*;
import java.awt.*;

public class MapDialog implements ActionListener {
	private JTextField nameField;
	private JTextField idField;
	private JComboBox<String> typeBox;	// dungeon = 1
	private JSpinner xSpinner;
	private JSpinner ySpinner;
	private JDialog dialog;
	private JComboBox<String> terrainBox;
	private JComboBox<String> randomBox;
	private boolean cancelled;

	public Properties showInputDialog(JFrame frame) {
		dialog = new JDialog(frame, "Create new map", true);
		dialog.setLocationRelativeTo(null);
		JPanel content = new JPanel(new BorderLayout());
		JPanel mapPanel = new JPanel(new GridLayout(0, 2));
		mapPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		nameField = new JTextField();
		mapPanel.add(new JLabel("Map name: "));
		mapPanel.add(nameField);
		idField = new JTextField();
		mapPanel.add(new JLabel("Map id: "));
		mapPanel.add(idField);
		String[] types = {"outdoor", "indoor"};
		typeBox = new JComboBox<String>(types);
		typeBox.addActionListener(this);
		mapPanel.add(new JLabel("Map type: "));
		mapPanel.add(typeBox);
		xSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		mapPanel.add(new JLabel("Width: "));
		mapPanel.add(xSpinner);
		ySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		mapPanel.add(new JLabel("Height: "));
		mapPanel.add(ySpinner);
		
		terrainBox = new JComboBox<String>();
		for(RTerrain terrain : Editor.resources.getResources(RTerrain.class)) {
			terrainBox.addItem(terrain.id);
		}
		mapPanel.add(new JLabel("Base terrain: "));
		mapPanel.add(terrainBox);

		// random map
		randomBox = new JComboBox<String>();
		randomBox.addItem("none");
		randomBox.setEnabled(false);
		for(RDungeonTheme theme : Editor.resources.getResources(RDungeonTheme.class)) {
			randomBox.addItem(theme.id);
		}
		mapPanel.add(new JLabel("Random"));
		mapPanel.add(randomBox);		
		
		content.add(mapPanel, BorderLayout.PAGE_START);
		cancelled = false;
		
		// knoppekes
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		buttons.add(ok);
		buttons.add(cancel);

		// dialog laten zien
		dialog.setContentPane(content);
		dialog.pack();
		dialog.setVisible(true);
		
		boolean type = typeBox.getSelectedItem().equals("indoor");
		String name = nameField.getText();
		String id = idField.getText();
		int width = (Integer)xSpinner.getValue();
		int height = (Integer)ySpinner.getValue();
		String terrain = (String)terrainBox.getSelectedItem();
		return new Properties(cancelled, name, type, id, width, height, terrain);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(typeBox)) {
			xSpinner.setEnabled(typeBox.getSelectedItem().equals("outdoor"));
			ySpinner.setEnabled(typeBox.getSelectedItem().equals("outdoor"));
			terrainBox.setEnabled(typeBox.getSelectedItem().equals("outdoor"));
			randomBox.setEnabled(typeBox.getSelectedItem().equals("indoor"));
		} else {
			if(e.getActionCommand().equals("Ok")) {
				if(!nameField.getText().equals("") && !idField.getText().equals("")) {
					dialog.setVisible(false);
				}
			} else if(e.getActionCommand().equals("Cancel")) {
				cancelled = true;
				dialog.setVisible(false);
			}
		}
	}

	public class Properties {
		private String name, path, terrain;
		private boolean ok;
		private boolean type;
		private int width, height;
		
		public Properties(boolean cancelled, String name, boolean type, String path, int width, int height, String terrain) {
			this.name = name;
			this.ok = !cancelled;
			this.type = type;
			this.path = path;
			this.width = width;
			this.height = height;
			this.terrain = terrain;
		}
		
		public String getID() {
			return path;
		}
		
		public boolean cancelled() {
			return !ok;
		}
		
		public boolean isDungeon() {
			return type;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public String getName() {
			return name;
		}
		
		public String getTerrain() {
			return terrain;
		}
	}
}
