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

import org.jdom2.Element;

public class RCraft extends RData {
	public String raw;
	public int amount, cost;
	
	public RCraft(Element properties, String... path) {
		super(properties.getAttributeValue("id"), path);
		name = properties.getAttributeValue("result");
		raw = properties.getAttributeValue("raw");
		amount = Integer.parseInt(properties.getAttributeValue("amount"));
		cost = Integer.parseInt(properties.getAttributeValue("cost"));
	}

	public RCraft(String id, RItem item, String... path) {
		super(Double.toString(Math.random()), path);
		name = item.id;
		raw = item.id;
	}

	public String toString() {
		return name;
	}
	
	public RCraft(RCraft procedure) {
		super(procedure.id, procedure.path);
		raw = procedure.raw;
		name = procedure.name;
		amount = procedure.amount;
		cost = procedure.cost;
	}
	
	public Element toElement() {
		Element procedure = new Element("craft");
		procedure.setAttribute("id", id);
		procedure.setAttribute("result", name);
		procedure.setAttribute("raw", raw);
		procedure.setAttribute("amount", Integer.toString(amount));
		procedure.setAttribute("cost", Integer.toString(cost));
		return procedure;
	}
}
