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

import java.util.*;

import neon.entities.property.Skill;
import neon.resources.RCreature.AIType;

import org.jdom2.Element;

public class RPerson extends RData {
	public HashMap<String, Integer> factions = new HashMap<String, Integer>();
	public AIType aiType;
	public int aiRange, aiConf, aiAggr;
	public HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
	public HashSet<String> spells = new HashSet<String>();
	public ArrayList<String> items = new ArrayList<String>();
	public List<Element> services = new ArrayList<Element>();
	public String species;

	public RPerson(Element person, String... path) {
		super(person.getAttributeValue("id"), path);
		name = person.getAttributeValue("name");
		species = person.getAttributeValue("race");
		
		if(person.getChild("factions") != null) {
			for(Element f : person.getChild("factions").getChildren()) {
				int rank = f.getAttribute("rank") != null ? Integer.parseInt(f.getAttributeValue("rank")) : 0;
				factions.put(f.getAttributeValue("id"), rank);
			}
		}
		
		Element brain = person.getChild("ai");
		if(brain != null && !brain.getText().isEmpty()) {
			aiType = AIType.valueOf(brain.getText());
		} else {
			aiType = null;
		}
		if(brain != null && brain.getAttribute("r") !=  null) {
			aiRange = Integer.parseInt(brain.getAttributeValue("r"));
		} else {
			aiRange = -1;
		}
		if(brain != null && brain.getAttribute("a") !=  null) {
			aiAggr = Integer.parseInt(brain.getAttributeValue("a"));
		} else {
			aiAggr = -1;
		}
		if(brain != null && brain.getAttribute("c") !=  null) {
			aiConf = Integer.parseInt(brain.getAttributeValue("c"));	
		} else {
			aiConf = -1;
		}

		Element skillList = person.getChild("skills");
		if(skillList != null) {
			for(Element skill : skillList.getChildren()) {
				skills.put(Skill.valueOf(skill.getAttributeValue("id").toUpperCase()), 
						Integer.parseInt(skill.getAttributeValue("rank")));
			}
		}
		
		Element itemList = person.getChild("items");
		if(itemList != null) {
			for(Element item : itemList.getChildren()) {
				items.add(item.getAttributeValue("id"));
			}
		}
		
		Element spellList = person.getChild("spells");
		if(spellList != null) {
			for(Element spell : spellList.getChildren()) {
				spells.add(spell.getAttributeValue("id"));
			}
		}
		
		// nieuwe arraylist om concurrentmodificationexceptions te vermijden
		for(Element service : new ArrayList<Element>(person.getChildren("service"))) {
			services.add(service.detach());
		}
	}
	
	public RPerson(String id, String... path) {
		super(id, path);
	}

	public Element toElement() {
		Element npc = new Element("npc");
		npc.setAttribute("race", species);
		npc.setAttribute("id", id);
		
		for(Element service : services) {
			service.detach();	// anders fout bij 2de keer saven
			npc.addContent(service);
		}
		
		if(!factions.isEmpty()) {
			Element factionList = new Element("factions");
			for(String f : factions.keySet()) {
				Element faction = new Element("faction");
				faction.setAttribute("id", f);
				faction.setAttribute("rank", Integer.toString(factions.get(f)));
				factionList.addContent(faction);
			}
			npc.addContent(factionList);
		}
		
		if(!items.isEmpty()) {
			Element itemList = new Element("items");
			for(String ri : items) {
				Element item = new Element("item");
				item.setAttribute("id", ri);
				itemList.addContent(item);
			}
			npc.addContent(itemList);
		}
		
		if(!spells.isEmpty()) {
			Element spellList = new Element("spells");
			for(String rs : spells) {
				Element spell = new Element("spell");
				spell.setAttribute("id", rs);
				spellList.addContent(spell);
			}
			npc.addContent(spellList);
		}
		
		if(aiAggr > -1 || aiConf > -1 || aiRange > -1 || aiType != null) {
			Element ai = new Element("ai");
			if(aiType != null) {
				ai.setText(aiType.toString());
			}
			if(aiAggr > -1) {
				ai.setAttribute("a", Integer.toString(aiAggr));
			}
			if(aiConf > -1) {
				ai.setAttribute("c", Integer.toString(aiConf));
			}
			if(aiRange > -1) {
				ai.setAttribute("r", Integer.toString(aiRange));
			}
			npc.addContent(ai);
		}

		return npc;
	}
}
