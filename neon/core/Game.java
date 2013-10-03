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

package neon.core;

import neon.entities.Player;
import neon.entities.UIDStore;
import neon.maps.Atlas;
import neon.systems.files.FileSystem;
import neon.systems.timing.Timer;

public class Game {
	private final UIDStore store;
	private final Player player;
	private final Timer timer = new Timer();
	private final Atlas atlas;
	
	public Game(Player player, FileSystem files) {
		store = new UIDStore("temp/store");
		atlas = new Atlas(files, "temp/atlas");
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public UIDStore getStore() {
		return store;
	}
	
	public Atlas getAtlas() {
		return atlas;
	}
}
