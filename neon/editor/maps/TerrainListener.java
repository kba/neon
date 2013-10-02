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

import javax.swing.*;

import neon.editor.*;
import neon.editor.editors.TerrainEditor;
import neon.resources.RTerrain;

import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

public class TerrainListener implements ListSelectionListener, MouseListener {
	private MapEditor editor;
	private JList<RTerrain> list;
	
	public TerrainListener(MapEditor editor, JList<RTerrain> list) {
		this.editor = editor;
		this.list = list;
	}

	public void valueChanged(ListSelectionEvent e) {
		// blijkbaar worden er twee events gefired bij selectie
		// dus: altijd checken, anders louche dingen met hashmap
		if(e.getValueIsAdjusting()) {
			editor.setTerrain(list.getSelectedValue().toString());
		}
	}

	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount() == 2) {
				if(list.getSelectedValue() != null) {
					new TerrainEditor(Editor.getFrame(), list.getSelectedValue()).show();
				} 
			}
		}
	}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ClickAction("New terrain type"));
			menu.add(new ClickAction("Delete terrain type"));
			menu.show(e.getComponent(), e.getX(), e.getY());
			list.setSelectedIndex(list.locationToIndex(new Point(e.getX(), e.getY())));
		}
	}
	
	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			DefaultListModel<RTerrain> model = (DefaultListModel<RTerrain>)list.getModel();
			if(e.getActionCommand().equals("New terrain type")) {
				String s = (String)JOptionPane.showInputDialog(neon.editor.Editor.getFrame(), "New terrain name:",
						"New terrain", JOptionPane.QUESTION_MESSAGE);
				if((s != null) && (s.length() > 0)) {
					RTerrain type = new RTerrain(s, Editor.getStore().getActive().get("id"));
					model.addElement(type);
					list.setSelectedValue(type, true);
					Editor.resources.addResource(type, "terrain");
					new TerrainEditor(Editor.getFrame(), type).show();		    		
				}
			} else if(e.getActionCommand().equals("Delete terrain type")) {
				try {
					if(list.getSelectedIndex() >= 0) {
						Editor.resources.removeResource(list.getSelectedValue(), "terrain");
						model.removeElement(list.getSelectedValue());
					}
				} catch(ArrayIndexOutOfBoundsException a) {}
			}
		}
	}
}
