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

package neon.objects.resources;

import java.util.Vector;
import org.jdom2.Element;

public class RRecipe extends RData {
	public Vector<String> ingredients = new Vector<String>();
	public int cost = 10;
	
	public RRecipe(Element properties, String... path) {
		super(properties.getAttributeValue("id"), path);
		name = properties.getChild("out").getText();
		if(properties.getAttribute("cost") != null) {
			cost = Integer.parseInt(properties.getAttributeValue("cost"));
		}
		for(Element in : properties.getChildren("in")) {
			ingredients.add(in.getText());
		}
	}
	
	public RRecipe(String id, RItem item, String... path) {
		super(id, path);
		name = item.id;
	}
	
	public String toString() {
		return name;
	}
	
	public Element toElement() {
		Element recipe = new Element("recipe");
		if(cost != 10) {
			recipe.setAttribute("cost", Integer.toString(cost));
		}
		recipe.addContent(new Element("out").setText(name));
		for(String item : ingredients) {
			recipe.addContent(new Element("in").setText(item));
		}
		return recipe;
	}
}
