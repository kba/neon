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

package neon.editor.maps;

import javax.swing.tree.DefaultMutableTreeNode;

import neon.editor.Editor;
import neon.editor.resources.RMap;

@SuppressWarnings("serial")
public class MapTreeNode extends DefaultMutableTreeNode {
	private RMap map;

	public MapTreeNode(RMap map) {
		super(map.toString());
		this.map = map;
	}
	
	public String toString() {
		if(!map.getPath()[0].equals(Editor.getStore().getActive().get("id"))) {
			// niet-actieve data is cursief weergegeven
			return "<html><i>" + map.id + "</i></html>";
		} else {
			return map.id;
		}
	}

	public RMap getMap() {
		return map;
	}
	
	public short getUID() {
		return map.getUID();
	}	
}
