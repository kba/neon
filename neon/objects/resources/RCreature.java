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

package neon.objects.resources;

import java.util.ArrayList;
import java.util.EnumMap;
import neon.objects.property.Habitat;
import neon.objects.property.Skill;
import neon.objects.property.Subtype;
import org.jdom2.Element;

public class RCreature extends RData {
	public enum Size {
		tiny, small, medium, large, huge;
	}
	
	public enum Type {
		animal, construct, daemon, dragon, goblin, humanoid, monster, player;
	}

	public enum AIType {
		wander, guard, schedule;
	}
	
	public final EnumMap<Skill, Float> skills;
	public final ArrayList<Subtype> subtypes;
	public String hit, av;
	public int speed, mana, dv;	
	public float str, dex, con, iq, wis, cha;
	public AIType aiType = AIType.guard;	// default
	public int aiRange = 10, aiConf = 0, aiAggr = 0;
	public Size size = Size.medium;			// default
	public Type type = Type.animal;			// default
	public Habitat habitat = Habitat.LAND;	// default
	
	public RCreature(String id, String... path) {
		super(id, path);
		subtypes = new ArrayList<Subtype>();
		skills = new EnumMap<Skill, Float>(Skill.class);
		hit = "1d1";
		av = "1d1";
	}
	
	public RCreature(Element properties, String... path) {
		super(properties, path);
		subtypes = new ArrayList<Subtype>();
		skills = initSkills(properties.getChild("skills"));
		
		color = properties.getAttributeValue("color");
		hit = properties.getAttributeValue("hit");
		av = properties.getChild("av").getText();
		text = properties.getAttributeValue("char");
		
		size = Size.valueOf(properties.getAttributeValue("size"));
		type = Type.valueOf(properties.getName());
		
		str = Integer.parseInt(properties.getChild("stats").getAttributeValue("str"));
		con = Integer.parseInt(properties.getChild("stats").getAttributeValue("con"));
		dex = Integer.parseInt(properties.getChild("stats").getAttributeValue("dex"));
		iq = Integer.parseInt(properties.getChild("stats").getAttributeValue("int"));
		wis = Integer.parseInt(properties.getChild("stats").getAttributeValue("wis"));
		cha = Integer.parseInt(properties.getChild("stats").getAttributeValue("cha"));

		speed = Integer.parseInt(properties.getAttributeValue("speed"));
		if(properties.getAttributeValue("mana") != null) {	// niet altijd aanwezig
			mana = Integer.parseInt(properties.getAttributeValue("mana"));
		}
		if(properties.getChild("dv") != null) {				// niet altijd aanwezig
			dv = Integer.parseInt(properties.getChild("dv").getText());
		}
		
		if(properties.getAttribute("habitat") != null) {
			habitat = Habitat.valueOf(properties.getAttributeValue("habitat").toUpperCase());
		}
		
		Element brain = properties.getChild("ai");
		if(brain != null) {
			if(!brain.getText().isEmpty()) {
				aiType = AIType.valueOf(brain.getText());
			}
			if(brain.getAttributeValue("r") !=  null) {
				aiRange = Integer.parseInt(brain.getAttributeValue("r"));
			}
			if(brain.getAttributeValue("a") !=  null) {
				aiAggr = Integer.parseInt(brain.getAttributeValue("a"));
			}
			if(brain.getAttributeValue("c") !=  null) {
				aiConf = Integer.parseInt(brain.getAttributeValue("c"));	
			}
		}
	}
	
	public String getName() {
		return name != null ? name : id;
	}
	
	private static EnumMap<Skill, Float> initSkills(Element skills) {
		EnumMap<Skill, Float> list = new EnumMap<Skill, Float>(Skill.class);
		for(Skill skill : Skill.values()) {
			if(skills != null && skills.getAttribute(skill.toString().toLowerCase()) != null) {
				list.put(skill, Float.parseFloat(skills.getAttributeValue(skill.toString().toLowerCase())));				
			} else {
				list.put(skill, 0f);
			}
		}
		return list;
	}
	
	public Element toElement() {
		Element creature = new Element(type.toString());
		
		creature.setAttribute("id", id);
		creature.setAttribute("size", size.toString());
		creature.setAttribute("char", text);
		creature.setAttribute("color", color);
		creature.setAttribute("hit", hit);
		creature.setAttribute("speed", Integer.toString(speed));

		if(mana > 0) {
			creature.setAttribute("mana", Integer.toString(mana));
		}
		if(name != null && !name.isEmpty()) {
			creature.setAttribute("name", name);
		}
		if(habitat != Habitat.LAND) {
			creature.setAttribute("habitat", habitat.name());
		}
		
		Element stats = new Element("stats");
		stats.setAttribute("str", Integer.toString((int)str));
		stats.setAttribute("con", Integer.toString((int)con));
		stats.setAttribute("dex", Integer.toString((int)dex));
		stats.setAttribute("int", Integer.toString((int)iq));
		stats.setAttribute("wis", Integer.toString((int)wis));
		stats.setAttribute("cha", Integer.toString((int)cha));
		creature.addContent(stats);
		
		if(av != null && !av.isEmpty()) {
			Element avElement = new Element("av");
			avElement.setText(av);
			creature.addContent(avElement);
		}
		if(dv > 0) {
			Element dvElement = new Element("dv");
			dvElement.setText(Integer.toString(dv));
			creature.addContent(dvElement);
		}
		
		if(aiAggr > 0 || aiConf > 0 || aiRange > 0 || aiType != null) {
			Element ai = new Element("ai");
			if(aiType != null) {
				ai.setText(aiType.toString());
			}
			if(aiAggr > 0) {
				ai.setAttribute("a", Integer.toString(aiAggr));
			}
			if(aiConf > 0) {
				ai.setAttribute("c", Integer.toString(aiConf));
			}
			if(aiRange > 0) {
				ai.setAttribute("r", Integer.toString(aiRange));
			}
			creature.addContent(ai);
		}
		
		return creature;
	}
}
