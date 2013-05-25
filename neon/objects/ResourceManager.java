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

package neon.objects;

import java.util.HashMap;
import java.util.Vector;
import neon.core.Engine;
import neon.objects.resources.RData;
import neon.objects.resources.RText;
import neon.objects.resources.Resource;

public class ResourceManager {
	private HashMap<String, Resource> resources = new HashMap<String, Resource>();
	
	public Resource getResource(String id) {
		return resources.get(id);
	}
	
	public Resource getResource(String id, String namespace) {
		return resources.get(namespace + ":" + id);
	}
	
	public <T extends Resource> Vector<T> getResources(Class<T> cl) {
		Vector<T> list = new Vector<T>();
		for(Resource r : resources.values()) {
			if(cl.isInstance(r)) {
				list.add((T)r);
			}
		}
		return list;
	}
	
	public void clear() {
		for(Resource resource : resources.values()) {
			resource.unload();
		}
		resources.clear();
	}
	
	public void addResource(Resource resource) {
		resources.put(resource.id, resource);
	}
	
	public void addResource(Resource resource, String namespace) {
		resources.put(namespace + ":" + resource.id, resource);
	}
	
	public void removeResource(Resource resource) {
		resources.remove(resource.id);
	}
	
	public void removeResource(Resource resource, String namespace) {
		resources.remove(namespace + ":" + resource.id);
	}
	
	public void removeResource(String id) {
		resources.remove(id);
	}
	
	public void addResource(String id, Class<? extends Resource> type, String... path) {
		if(type.equals(RText.class)) {
			Resource resource = new RText(id, Engine.getFileSystem(), path);
			resources.put(id, resource);
		} else if(type.equals(RData.class)) {
			// doorverwijzen naar entityfactory?
		}
	}
}
