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

package neon.resources;

import neon.systems.files.FileSystem;
import neon.systems.files.StringTranslator;

public class RText extends Resource {
	private String text;
	private FileSystem files;
	
	public RText(String id, FileSystem files, String... path) {
		super(id, path);
		this.files = files;
	}
	
	public void load() {
		text = files.getFile(new StringTranslator(), path);
	}

	public void unload() {
		text = null;
	}

	public String getText() {
		if(text == null) {
			load();
		}
		return text;
	}
}
