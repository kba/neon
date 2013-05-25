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

package neon.objects;

import java.util.*;
import neon.ai.*;
import neon.core.Engine;
import neon.core.handlers.InventoryHandler;
import neon.graphics.shapes.JVShape;
import neon.graphics.svg.SVGLoader;
import neon.magic.SpellFactory;
import neon.objects.components.Enchantment;
import neon.objects.entities.*;
import neon.objects.property.Gender;
import neon.objects.resources.*;
import neon.util.Dice;

public class EntityFactory {
	private static AIFactory aiFactory = new AIFactory();
	
	public static Item getItem(String id, long uid) {
		Item item = getItem(id, -1, -1, uid);
		return item;
	}
	
	public static Item getItem(String id, int x, int y, long uid) {
		// item aanmaken
		RItem resource;
		if(Engine.getResources().getResource(id) instanceof LItem) {
			LItem li = (LItem)Engine.getResources().getResource(id);
			ArrayList<String> items = new ArrayList<String>(li.items.keySet());
			resource = (RItem)Engine.getResources().getResource(items.get(Dice.roll(1, items.size(), -1)));
		} else {
			resource = (RItem)Engine.getResources().getResource(id);
		}
		Item item = getItem(resource, uid);
		
		// positie
		item.getBounds().setLocation(x, y);
		item.getRenderer().setZ(resource.top ? Byte.MAX_VALUE : Byte.MAX_VALUE - 2);
		
		if(resource.svg != null) {	// svg custom look gedefinieerd
			JVShape shape = SVGLoader.loadShape(resource.svg);
			shape.setX(x);
			shape.setY(y);
			shape.setZ(item.getRenderer().getZ());
			item.renderer = shape;
			item.getBounds().setWidth(shape.getBounds().width);
			item.getBounds().setHeight(shape.getBounds().height);
		} 

		if(resource.spell != null) {
			int mana = 0;
			if(resource instanceof RWeapon) {
				mana = ((RWeapon) resource).mana;
			}
			item.enchantment = new Enchantment(SpellFactory.getSpell(resource.spell), mana, item.getUID());
		}
		
		return item;
	}
	
	private static Item getItem(RItem resource, long uid) {
		// item aanmaken
		switch(resource.type) {
		case container: return new Container(uid, (RItem.Container)resource);
		case food: return new Item.Food(uid, resource);
		case aid: return new Item.Aid(uid, resource);
		case book: return new Item.Book(uid, (RItem.Text)resource);
		case clothing: return new Clothing(uid, (RClothing)resource);
		case armor: return new Armor(uid, (RClothing)resource);
		case coin: return new Item.Coin(uid, resource);
		case door: return new Door(uid, resource);
		case light: return new Light(uid, resource);
		case potion: return new Item.Potion(uid, resource);
		case scroll: return new Item.Scroll(uid, (RItem.Text)resource);
		case weapon: return new Weapon(uid, (RWeapon)resource);
		default: return new Item(uid, resource);
		}
	}

	/*
	 * Returns a person with the given uid, position and properties.
	 */
	private static Creature getPerson(String id, int x, int y, long uid, RCreature species) {
		String name = id;
		RPerson person = (RPerson)Engine.getResources().getResource(id);
		if(person.name != null) {
			name = person.name;
		}
		Creature creature = new Hominid(id, uid, name, species, Gender.OTHER);
		creature.getBounds().setLocation(x, y);
		for(String i : person.items) {
			long itemUID = Engine.getStore().createNewEntityUID();
			Item item = EntityFactory.getItem(i, itemUID);
			Engine.getStore().addEntity(item);
			InventoryHandler.addItem(creature, itemUID);
		}
		for(String s : person.spells) {
			creature.animus.addSpell(neon.magic.SpellFactory.getSpell(s));
		}
		for(String f : person.factions.keySet()) {
			creature.addFaction(f, person.factions.get(f));
		}
		return creature;
	}
	
	public static Creature getCreature(String id, int x, int y, long uid) {
		Creature creature;
		Resource resource = Engine.getResources().getResource(id);
		if(resource instanceof RPerson) {
			RPerson rp = (RPerson)resource;
			RCreature species = (RCreature)Engine.getResources().getResource(rp.species);
			creature = getPerson(id, x, y, uid, species);
			creature.brain = aiFactory.getAI(creature, rp);
		} else if(resource instanceof LCreature){
			LCreature lc = (LCreature)resource;
			ArrayList<String> creatures = new ArrayList<String>(lc.creatures.keySet());
			return getCreature(creatures.get(Dice.roll(1, creatures.size(), -1)), x, y, uid);
		} else {
			RCreature rc = (RCreature)Engine.getResources().getResource(id);
			switch(rc.type) {
			case animal: creature = new Animal(x, y, id, uid, rc); break;
			case monster: creature = new Monster(x, y, id, uid, Gender.OTHER, rc); break;
			case construct: creature = new Construct(x, y, id, uid, rc); break;
			case humanoid: creature = new Hominid(id, uid, rc); break;
			case daemon: creature = new Daemon(x, y, id, uid, rc); break;
			case dragon: creature = new Dragon(x, y, id, uid, rc); break;
			case goblin: creature = new Hominid.Goblin(x, y, id, uid, rc); break;
			default: creature = new Animal(x, y, id, uid, rc); break;
			}
			
			// positie
			creature.getBounds().setLocation(x, y);
			
			// brain
			creature.brain = aiFactory.getAI(creature);
		}
		
		return creature;
	}
}
