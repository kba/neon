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

package neon.tools;

import neon.objects.resources.RCraft;
import neon.objects.resources.RData;
import neon.objects.resources.RRecipe;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("serial")
public class ResourceNode extends DefaultMutableTreeNode {
	private static NodeComparator nodeComparator = new NodeComparator();
	private RData resource;
	private ResourceType type;
	private String name;
	
	public ResourceNode(RData r, ResourceType t) {
		resource = r;
		type = t;
	}
	
	public ResourceNode(String name, ResourceType t) {
		this.name = name;
		type = t;
	}
	
	public RData getResource() {
		return resource;
	}
	
	public ResourceType getType() {
		return type;
	}
	
	public String toString() {
		if(name != null) {	// top node
			return name;
		}
		
		String id = resource.id;	// leaf node
		if(resource instanceof RRecipe || resource instanceof RCraft) {
			id = resource.name;
		}
		
		if(!resource.getPath()[0].equals(Editor.getStore().getActive().get("id"))) {
			// niet-actieve data is cursief weergegeven
			return "<html><i>" + id + "</i></html>";
		} else {
			return id;
		}
	}
	
	public void insert(MutableTreeNode newChild, int childIndex)    {
	    super.insert(newChild, childIndex);
	    Collections.sort(children, nodeComparator);
	}

	private static class NodeComparator implements Comparator<MutableTreeNode> {
		public int compare(MutableTreeNode arg0, MutableTreeNode arg1) {
			if(arg0 instanceof ObjectNode && arg1 instanceof ObjectNode) {
				return ((ResourceNode)arg0).resource.id.compareTo(((ResourceNode)arg1).resource.id);
			} else {
				return arg0.toString().compareToIgnoreCase(arg1.toString());
			}
		}
	}
	
	public enum ResourceType {
		CRAFT, FACTION("faction"), SCRIPT, EVENT, REGION("theme"), DUNGEON("theme"), 
		ZONE("theme"), QUEST("quest"), SPELL("magic"), LEVEL_SPELL("magic"), 
		SIGN("magic"), POWER("magic"), CURSE("magic"), ENCHANTMENT("magic"), 
		POISON("magic"), TATTOO("magic"), DISEASE("magic"), RECIPE("magic");
		
		private String namespace;
		
		private ResourceType() {
			this(null);
		}
		
		private ResourceType(String namespace) {
			this.namespace = namespace;
		}
		
		public String getNamespace() {
			return namespace;
		}
	}
}
