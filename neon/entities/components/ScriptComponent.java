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

package neon.entities.components;

import java.util.ArrayList;
import java.util.Collection;

public class ScriptComponent implements Component {
	private final long uid;
	private ArrayList<String> scripts = new ArrayList<>();
	
	public ScriptComponent(long uid) {
		this.uid = uid;
	}
	
	@Override
	public long getUID() {
		return uid;
	}
	
	public void addScript(String script) {
		scripts.add(script);
	}
	
	public Collection<String> getScripts() {
		return scripts;
	}
	
	/*
	 * TODO: script object voor entities
	 * 
	 * in javascript: 
	 * function entity() {
	 * 	bounds = entity.getShapeComponent();
	 *  social = entity.getFactionComponent();
	 *  ...
	 * }
	 * 
	 * kijken hoe in java de juiste entity aan script gehangen moet worden
	 */
}
