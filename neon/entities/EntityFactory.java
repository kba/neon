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

package neon.entities;

import java.util.*;
import neon.ai.*;
import neon.core.Engine;
import neon.core.handlers.InventoryHandler;
import neon.entities.components.Enchantment;
import neon.entities.components.FactionComponent;
import neon.entities.components.RenderComponent;
import neon.entities.components.ShapeComponent;
import neon.entities.property.Gender;
import neon.magic.SpellFactory;
import neon.resources.*;
import neon.ui.graphics.shapes.JVShape;
import neon.ui.graphics.svg.SVGLoader;
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
		ShapeComponent itemBounds = item.getComponent(ShapeComponent.class);
		itemBounds.setLocation(x, y);
		RenderComponent renderer = item.getComponent(RenderComponent.class);
		renderer.setZ(resource.top ? Byte.MAX_VALUE : Byte.MAX_VALUE - 2);
		
		if(resource.svg != null) {	// svg custom look gedefinieerd
			JVShape shape = SVGLoader.loadShape(resource.svg);
			shape.setX(x);
			shape.setY(y);
			shape.setZ(renderer.getZ());
			item.setComponent(RenderComponent.class, shape);
			itemBounds.setWidth(shape.getBounds().width);
			itemBounds.setHeight(shape.getBounds().height);
		} 

		if(resource.spell != null) {
			int mana = 0;
			if(resource instanceof RWeapon) {
				mana = ((RWeapon) resource).mana;
			}
			item.setComponent(Enchantment.class, new Enchantment(SpellFactory.getSpell(resource.spell), mana, item.getUID()));
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
		case light: return new Item.Light(uid, resource);
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
		creature.bounds.setLocation(x, y);
		for(String i : person.items) {
			long itemUID = Engine.getStore().createNewEntityUID();
			Item item = EntityFactory.getItem(i, itemUID);
			Engine.getStore().addEntity(item);
			InventoryHandler.addItem(creature, itemUID);
		}
		for(String s : person.spells) {
			creature.animus.addSpell(neon.magic.SpellFactory.getSpell(s));
		}
		FactionComponent factions = creature.getComponent(FactionComponent.class);
		for(String f : person.factions.keySet()) {
			factions.addFaction(f, person.factions.get(f));
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
			case construct: creature = new Construct(id, uid, rc); break;
			case humanoid: creature = new Hominid(id, uid, rc); break;
			case daemon: creature = new Daemon(id, uid, rc); break;
			case dragon: creature = new Dragon(id, uid, rc); break;
			case goblin: creature = new Hominid(id, uid, rc); break;
			default: creature = new Creature(id, uid, rc); break;
			}
			
			// positie
			creature.bounds.setLocation(x, y);
			
			// brain
			creature.brain = aiFactory.getAI(creature);
		}
		
		return creature;
	}
}
