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

package neon.editor;

import java.io.Serializable;
import java.util.*;

import neon.resources.RData;
import neon.resources.RMod;

import org.jdom2.Document;
import org.jdom2.Element;

public class XMLBuilder {
	private DataStore store;
	
	public XMLBuilder(DataStore store) {
		this.store = store;
	}
	
	public Document getEventsDoc() {
		Element root = new Element("events");
		for(String script : store.getEvents().keySet()) {
			for(String stamp : store.getEvents().get(script)) {
				Element event = new Element("event");
				event.setAttribute("script", script);
				event.setAttribute("tick", stamp);
				root.addContent(event);				
			}
		}
		return new Document(root);		
	}

	public Document getListDoc(Collection<? extends RData> elements, String name, RMod mod) {
		Element root = new Element(name);
		for(RData resource : elements) {
			if(resource.getPath()[0].equals(mod.get("id"))) {
				root.addContent(resource.toElement());
			}
		}
		return new Document(root);
	}
	
	public Document getResourceDoc(Collection<? extends RData> elements, String name, RMod mod) {
		ArrayList<RData> buffer = new ArrayList<RData>(elements);
		Collections.sort(buffer, new IDComparator());
		Element root = new Element(name);
		for(RData resource : buffer) {
			if(resource.getPath()[0].equals(mod.get("id"))) {
				root.addContent(resource.toElement());
			}
		}
		return new Document(root);				
	}

	@SuppressWarnings("serial")
	private static class IDComparator implements Comparator<RData>, Serializable {
		public int compare(RData arg0, RData arg1) {
			return arg0.id.compareTo(arg1.id);
		}
	}
}
