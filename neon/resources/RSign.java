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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jdom2.Element;

import neon.entities.property.Ability;

public class RSign extends RData {
	public ArrayList<String> powers = new ArrayList<String>();
	public EnumMap<Ability, Integer> abilities = new EnumMap<Ability, Integer>(Ability.class);
	
	public RSign(String id, String... path) {
		super(id, path);
	}

	public RSign(RSign sign) {
		super(sign.id, sign.path);
		for(String power : sign.powers) {
			powers.add(power);
		}
		for(Map.Entry<Ability, Integer> entry : sign.abilities.entrySet()) {
			abilities.put(entry.getKey(), entry.getValue());
		}
	}
	
	public RSign(Element sign, String... path) {
		super(sign, path);
		for(Element power : sign.getChildren("power")) {
			powers.add(power.getAttributeValue("id"));
		}
		for(Element ability : sign.getChildren("ability")) {
			abilities.put(Ability.valueOf(ability.getAttributeValue("id").toUpperCase()), 
					Integer.parseInt(ability.getAttributeValue("size")));
		}
	}

	public Element toElement() {
		Element sign = new Element("sign");
		sign.setAttribute("id", id);
		for(String power : powers) {
			sign.addContent(new Element("power").setAttribute("id", power));
		}
		for(Entry<Ability, Integer> entry : abilities.entrySet()) {
			if(entry.getValue() > 0) {
				Element ability = new Element("ability");
				ability.setAttribute("id", entry.getKey().toString());
				ability.setAttribute("size", Integer.toString(entry.getValue()));
				sign.addContent(ability);				
			}
		}
		return sign;
	}
}
