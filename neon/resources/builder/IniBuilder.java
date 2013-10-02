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

package neon.resources.builder;

import neon.core.event.TaskQueue;
import neon.resources.CClient;
import neon.resources.CGame;
import neon.resources.CServer;
import neon.resources.RMod;
import neon.resources.ResourceManager;
import neon.systems.files.FileSystem;

public class IniBuilder extends Builder {
	private String ini;
	private TaskQueue queue;
	
	/**
	 * Initializes this {@code Builder}.
	 * 
	 * @param ini	a neon.ini file
	 * @param files
	 * @param queue
	 */
	public IniBuilder(String ini, FileSystem files, TaskQueue queue) {
		super(files);
		this.ini = ini;
		this.queue = queue;
	}

	@Override
	public void build(ResourceManager resources) {
		// server instellingen inlezen
		CServer server = new CServer(ini);
		resources.addResource(server, "config");
		
		// client instellingen inlezen
		CClient client = new CClient(ini);
		resources.addResource(client, "config");
		
		// spelgerelateerde instellingen
		CGame game = new CGame("game");
		resources.addResource(game, "config");

		// alle data dirs en jars afgaan
		for(String file : server.getMods()) {
			RMod mod = new ModLoader(file, queue, files).loadMod(game, client);
			resources.addResource(mod, "mods");
		}		
	}
}
