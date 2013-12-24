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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class RItem extends RData {
	public enum Type {
		aid, armor, book, clothing, coin, container, door, food, item, light, potion, scroll, weapon;
	}

	private static XMLOutputter outputter = new XMLOutputter();	
	private static SAXBuilder builder = new SAXBuilder();
	
	public int cost;
	public float weight;
	public boolean top;
	public Type type;
	public String spell;
	public String svg;

	public RItem(Element item, String... path) {
		super(item, path);
		type = Type.valueOf(item.getName());
		if(item.getAttribute("cost") != null) {
			cost = Integer.parseInt(item.getAttributeValue("cost"));
		} 
		if(item.getAttribute("weight") != null) {
			weight = Float.parseFloat(item.getAttributeValue("weight"));
		} 
		top = item.getAttribute("z") != null;		
		if(item.getAttribute("spell") != null) {
			spell = item.getAttributeValue("spell");
		}
		if(item.getChild("svg") !=  null) {
			svg = outputter.outputString((Element)item.getChild("svg").getChildren().get(0));
		}
	}
	
	public RItem(String id, Type type, String... path) {
		super(id, path);
		this.type = type;
	}

	public Element toElement() {
		Element item = new Element(type.toString());
		item.setAttribute("id", id);
		if(svg != null) {
			try {
				Element graphics = new Element("svg");
				ByteArrayInputStream stream = new ByteArrayInputStream(svg.getBytes("UTF-8"));
				Element shape = (Element)builder.build(stream).getRootElement().detach();
				graphics.addContent(shape);
				item.addContent(graphics);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			item.setAttribute("char", text);
			item.setAttribute("color", color);
		}
		
		if(top) {
			item.setAttribute("z", "top");
		}
		if(cost > 0) {
			item.setAttribute("cost", Integer.toString(cost));
		}
		if(weight > 0) {
			item.setAttribute("weight", Float.toString(weight));
		}
		if(name != null && !name.isEmpty()) {
			item.setAttribute("name", name);
		}
		if(spell != null) {
			item.setAttribute("spell", spell);
		}
		
		return item;
	}	
	
	public static class Door extends RItem {
		public String closed = " ";
		public String locked = " ";

		public Door(Element door, String... path) {
			super(door, path);
			Element states = door.getChild("states");
			if(states != null) {
				if(states.getAttribute("closed") != null) {
					closed = states.getAttributeValue("closed");
				} else {
					closed = text;
				}
				if(states.getAttribute("locked") != null) {
					locked = states.getAttributeValue("locked");
				} else {
					locked = closed;
				}
			}
		}

		public Door(String id, Type type, String... path) {
			super(id, type, path);
		}
		
		@Override
		public Element toElement() {
			Element door = super.toElement();
			if((!closed.equals(text) && !closed.equals(" ")) || (!locked.equals(closed) && !locked.equals(" "))) {
				Element states = new Element("states");
				if(!closed.equals(text) && !closed.equals(" ")) {
					states.setAttribute("closed", closed);
				}
				if(!locked.equals(closed) && !locked.equals(" ")) {
					states.setAttribute("locked", locked);
				}
				door.addContent(states);
			}
			return door;
		}	
	}
	
	public static class Potion extends RItem {
		public Potion(Element potion, String... path) {
			super(potion, path);
		}
	}

	public static class Container extends RItem {
		public ArrayList<String> contents = new ArrayList<String>();
		
		public Container(Element container, String... path) {
			super(container, path);
			for(Element item : container.getChildren("item")) {
				contents.add(item.getText());
			}
		}
	}
	
	public static class Text extends RItem {
		public String content;
		
		public Text(Element text, String... path) {
			super(text, path);
			content = text.getText();
		}
		
		public Text(String id, Type type, String... path) {
			super(id, type, path);
		}
		
		public Element toElement() {
			Element book = super.toElement();
			book.setText(content);
			return book;
		}
	}
}
