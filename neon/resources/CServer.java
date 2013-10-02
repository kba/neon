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

package neon.resources;

import java.io.FileInputStream;
import java.util.ArrayList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * A resource that keeps track of all configuration settings in neon.ini.
 * 
 * @author mdriesen
 */
public class CServer extends Resource {
	private ArrayList<String> mods = new ArrayList<String>();
	private String log = "FINEST";
	private boolean gThread = true;
//	private boolean audio = false;
//	private int ai = 20;
	
	public CServer(String... path) {
		super("ini", path);
		
		// file inladen
		Document doc = new Document();
		try (FileInputStream in = new FileInputStream(path[0])){
			doc = new SAXBuilder().build(in);
		} catch(Exception e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		
		// mods
		Element files = root.getChild("files");
		for(Element file : files.getChildren("file")) {
			mods.add(file.getText());			
		}
		
		// logging
		log = root.getChildText("log").toUpperCase();
		
		// map generation thread
		gThread = root.getChild("threads").getAttributeValue("generate").equals("on");
	}


	@Override
	public void load() {}	// loaden niet mogelijk

	@Override
	public void unload() {}	// unloaden niet mogelijk

	public String getLogLevel() {
		return log;
	}
	
	public ArrayList<String> getMods() {
		return mods;
	}
	
	public boolean isMapThreaded() {
		return gThread;
	}
}
