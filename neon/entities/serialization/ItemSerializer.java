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

package neon.entities.serialization;

import java.awt.Point;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import neon.core.Engine;
import neon.entities.Armor;
import neon.entities.Container;
import neon.entities.Door;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.Weapon;
import neon.entities.components.CPortal;
import neon.entities.components.Enchantment;
import neon.entities.components.Lock;
import neon.entities.components.Trap;
import neon.magic.SpellFactory;
import neon.resources.RItem;

import org.apache.jdbm.Serializer;

/**
 * This class takes care of (de)serialization of {@code Item}s.
 * 
 * @author mdriesen
 */
public class ItemSerializer implements Serializer<Item>, Serializable {
	private static final long serialVersionUID = 2138679015831709732L;

	public Item deserialize(DataInput input) throws IOException, ClassNotFoundException {
		// item aanmaken
		String id = input.readUTF();
		long uid = input.readLong();
		int x = input.readInt();
		int y = input.readInt();
		Item item = EntityFactory.getItem(id, x, y, uid);
		item.setOwner(input.readLong());
		
		if(input.readBoolean()) {
			readEnchantment(input, item, uid);
		}
		
		if(item instanceof Door) {
			Door door = (Door)item;
			door.setSign(input.readUTF());
			readPortal(input, door.portal);
			readLock(input, door.lock);
			readTrap(input, door.trap);
		} else if(item instanceof Container) {
			Container container = (Container)item;
			readLock(input, container.lock);
			readTrap(input, container.trap);
			readContents(input, container);
		} else if(item instanceof Armor) {
			((Armor)item).setState(input.readInt());
		} else if(item instanceof Weapon) {
			((Weapon)item).setState(input.readInt());
		}

		return item;
	}

	public void serialize(DataOutput output, Item item) throws IOException {
		output.writeUTF(item.resource.id);
		output.writeLong(item.getUID());
		output.writeInt(item.bounds.x);
		output.writeInt(item.bounds.y);
		output.writeLong(item.getOwner());
		
		if(item.enchantment != null) {
			output.writeBoolean(true);
			writeEnchantment(output, item.enchantment);
		} else {
			output.writeBoolean(false);
		}
		
		if(item instanceof Door) {
			Door door = (Door)item;
			output.writeUTF(door.toString());
			writePortal(output, door.portal);
			writeLock(output, door.lock);
			writeTrap(output, door.trap);
		} else if(item instanceof Container) {
			Container container = (Container)item;
			writeLock(output, container.lock);
			writeTrap(output, container.trap);
			writeContents(output, container);
		} else if(item instanceof Armor) {
			output.writeInt(((Armor)item).getState());
		} else if(item instanceof Weapon) {
			output.writeInt(((Weapon)item).getState());
		}
	}
	
	private void readEnchantment(DataInput input, Item item, long uid) throws IOException {
		String id = input.readUTF();
		int mana = input.readInt();
		float modifier = input.readFloat();
		Enchantment enchantment = new Enchantment(SpellFactory.getSpell(id), mana, uid);
		enchantment.setModifier(modifier);
		item.enchantment = enchantment;
	}
	
	private void writeEnchantment(DataOutput output, Enchantment enchantment) throws IOException {
		output.writeUTF(enchantment.getSpell().id);
		output.writeInt(enchantment.getBaseMana());
		output.writeFloat(enchantment.getModifier());
	}	
	
	private void readContents(DataInput input, Container container) throws IOException {
		int size = input.readInt();
		for(int i = 0; i < size; i++) {
			container.addItem(input.readLong());
		}	
	}
	
	private void writeContents(DataOutput output, Container container) throws IOException {
		output.writeInt(container.getItems().size());
		for(Long uid : container.getItems()) {
			output.writeLong(uid);
		}
	}	
	
	private void readTrap(DataInput input, Trap trap) throws IOException {
		trap.setState(input.readInt());
		trap.setTrapDC(input.readInt());
	}
	
	private void writeTrap(DataOutput output, Trap trap) throws IOException {
		output.writeInt(trap.getState());
		output.writeInt(trap.getTrapDC());
	}
	
	private void readPortal(DataInput input, CPortal portal) throws IOException {
		String destTheme = input.readUTF();
		portal.setDestTheme(destTheme.isEmpty() ? null : destTheme);
		portal.setDestMap(input.readInt());
		portal.setDestZone(input.readInt());
		if(!input.readUTF().isEmpty()) {
			portal.setDestPos(new Point(input.readInt(), input.readInt()));
		}		
	}
	
	private void writePortal(DataOutput output, CPortal portal) throws IOException {
		if(portal.getDestTheme() != null) {
			output.writeUTF(portal.getDestTheme());
		} else {
			output.writeUTF("");
		}
		output.writeInt(portal.getDestMap());
		output.writeInt(portal.getDestZone());
		if(portal.getDestPos() != null) {
			output.writeUTF("point");
			output.writeInt(portal.getDestPos().x);
			output.writeInt(portal.getDestPos().y);
		} else {
			output.writeUTF("");
		}		
	}
	
	private void readLock(DataInput input, Lock lock) throws IOException {
		lock.setLockDC(input.readInt());
		lock.setState(input.readInt());
		String id = input.readUTF();
		if(!id.isEmpty()) {
			lock.setKey((RItem)Engine.getResources().getResource(id));
		}
	}
	
	private void writeLock(DataOutput output, Lock lock) throws IOException {
		output.writeInt(lock.getLockDC());
		output.writeInt(lock.getState());
		if(lock.hasKey()) {
			output.writeUTF(lock.getKey().id);
		} else {
			output.writeUTF("");
		}		
	}
}
