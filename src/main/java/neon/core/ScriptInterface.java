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

package neon.core;

import java.io.InputStream;
import java.util.Scanner;
import neon.entities.Entity;
import neon.ui.GamePanel;

public class ScriptInterface {
	private GamePanel panel;
	
	public ScriptInterface(GamePanel panel) {
		this.panel = panel;
		InputStream input = Engine.class.getResourceAsStream("scripts.js");
		Scanner scanner = new Scanner(input, "UTF-8");
		Engine.execute(scanner.useDelimiter("\\A").next());
		scanner.close();
	}
	
	public void show(String text) {
		panel.print(text);
	}

	public Entity get(long uid) {
		return Engine.getStore().getEntity(uid);
	}
	
	public Entity getPlayer() {
		return Engine.getPlayer();
	}
}

