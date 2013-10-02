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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import neon.editor.Editor;
import neon.resources.RTerrain;
import neon.resources.RZoneTheme;

public class LevelDialog implements ActionListener {
	private JSpinner xSpinner;
	private JSpinner ySpinner;
	private JDialog dialog;
	private JComboBox<String> terrainBox, randomBox;
	private boolean cancelled;
	private JTextField nameField;

	public Properties showInputDialog(JFrame frame){
		dialog = new JDialog(frame, "Create new zone", true);
		dialog.setLocationRelativeTo(null);
		JPanel content = new JPanel(new BorderLayout());
		JPanel mapPanel = new JPanel(new GridLayout(0, 2));
		mapPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		nameField = new JTextField();
		mapPanel.add(new JLabel("Name: "));
		mapPanel.add(nameField);
		xSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		mapPanel.add(new JLabel("Width: "));
		mapPanel.add(xSpinner);
		ySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		mapPanel.add(new JLabel("Height: "));
		mapPanel.add(ySpinner);

		// terrain kiezen
		terrainBox = new JComboBox<String>();
		for(RTerrain terrain : Editor.resources.getResources(RTerrain.class)) {
			terrainBox.addItem(terrain.id);
		}
		mapPanel.add(new JLabel("Base terrain: "));
		mapPanel.add(terrainBox);
		
		// random dungeon
		randomBox = new JComboBox<String>();
		randomBox.addItem("none");
		for(RZoneTheme theme : Editor.resources.getResources(RZoneTheme.class)) {
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
		
		int width = (Integer)xSpinner.getValue();
		int height = (Integer)ySpinner.getValue();
		String terrain = terrainBox.getSelectedItem().toString();
		String name = nameField.getText();
		String theme = randomBox.getSelectedItem().toString();
		return new Properties(cancelled, width, height, terrain, name, theme);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Ok")) {
			if(!nameField.getText().equals("")) {
				dialog.setVisible(false);
			}
		} else if(e.getActionCommand().equals("Cancel")) {
			cancelled = true;
			dialog.setVisible(false);
		}
	}

	public static class Properties {
		private boolean ok;
		private int width, height;
		private String terrain, name, theme;
		
		public Properties(boolean cancelled, int width, int height, String terrain, String name, String theme) {
			this.ok = !cancelled;
			this.width = width;
			this.height = height;
			this.terrain = terrain;
			this.name = name;
			this.theme = theme;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean cancelled() {
			return !ok;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public String getTerrain() {
			return terrain;
		}
		
		public String getTheme() {
			return theme;
		}
	}
}
