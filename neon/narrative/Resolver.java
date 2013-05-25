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

package neon.narrative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import neon.core.Engine;
import neon.core.event.ScriptAction;
import neon.objects.resources.RCreature;
import neon.objects.resources.RItem;
import neon.util.Dice;
import org.jdom2.Element;

public class Resolver {
	QuestTracker tracker;
	
	public Resolver(QuestTracker tracker) {
		this.tracker = tracker;
	}
	
	/**
	 * Resolves variables in a quest resource. Even elements are the original
	 * strings, odd elements are the resolved strings.
	 * 
	 * @param vars
	 * @return
	 */
	protected List<String> resolveVariables(Element vars) {
		ArrayList<String> strings = new ArrayList<String>();
		
		if(vars == null) {
			return strings;
		}
		
		for(Element var : vars.getChildren()) {
			if(var.getName().equals("item")) {
				addItem(var, strings);
			} else if(var.getName().equals("npc")) {
				addPerson(var, strings);
			} else if(var.getName().equals("creature")) {
				addCreature(var, strings);
			}
		}
		
		return strings;
	}
	
	private void addItem(Element var, List<String> strings) {
		Collection<RItem> items = Engine.getResources().getResources(RItem.class);
		if(var.getAttributeValue("type") != null) {
			for(RItem item : items) {
				if(item.type.name().equals(var.getAttributeValue("type"))) {
					strings.add("$" + var.getTextTrim() + "$");
					strings.add(item.toString());
					tracker.addObject(item.toString());
					break;	// uit de for loop halen
				}
			}
		} else if(var.getAttributeValue("id") != null) {
			String[] things = var.getAttributeValue("id").split(",");
			String item = things[Dice.roll(1, things.length, -1)];
			strings.add("$" + var.getTextTrim() + "$");
			strings.add(item.toString());
			tracker.addObject(item);
		} else {
			String item = items.toArray()[Dice.roll(1, items.size(), -1)].toString();
			strings.add("$" + var.getTextTrim() + "$");
			strings.add(item.toString());
			tracker.addObject(item.toString());
		}		
	}
	
	private void addPerson(Element var, List<String> strings) {
		String[] npcs = var.getAttributeValue("id").split(",");
		String npc = npcs[Dice.roll(1, npcs.length, -1)];
		strings.add("$" + var.getTextTrim() + "$");
		strings.add(npc);
		tracker.addObject(npc);		
	}
	
	private void addCreature(Element var, List<String> strings) {
		List<RCreature> creatures = Engine.getResources().getResources(RCreature.class);
		String creature = creatures.get(Dice.roll(1, creatures.size(), -1)).toString();
		strings.add("$" + var.getTextTrim() + "$");
		strings.add(creature);
		if(var.getChild("onDie") != null) {
			Engine.getEvents().addTask("die:" + creature, new ScriptAction(var.getChildText("onDie")));
		}
		tracker.addObject(creature);		
	}
}
