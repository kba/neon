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

package neon.objects.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import neon.ai.AIFactory;
import neon.core.Engine;
import neon.magic.SpellFactory;
import neon.objects.entities.Animal;
import neon.objects.entities.Construct;
import neon.objects.entities.Creature;
import neon.objects.entities.Daemon;
import neon.objects.entities.Dragon;
import neon.objects.entities.Hominid;
import neon.objects.entities.Monster;
import neon.objects.property.Gender;
import neon.objects.property.Slot;
import neon.objects.resources.RCreature;
import org.apache.jdbm.Serializer;

public class CreatureSerializer implements Serializer<Creature>, Serializable {
	private static final long serialVersionUID = -2452444993764883434L;
	private static AIFactory aiFactory = new AIFactory();

	public Creature deserialize(DataInput in) throws IOException, ClassNotFoundException {
		String id = in.readUTF();
		String species = in.readUTF();
		int x = in.readInt();
		int y = in.readInt();
		long uid = in.readLong();
		Creature creature = getCreature(id, x, y, uid, species);
		creature.getBounds().setLocation(x, y);
		creature.brain = aiFactory.getAI(creature);
		
		creature.setHealth(in.readInt());
		creature.addBaseHealthMod(in.readFloat());
		creature.heal(in.readFloat());
		creature.addMoney(in.readInt());
		creature.animus.setBaseModifier(in.readFloat());
		creature.animus.setModifier(in.readFloat());
		String spell = in.readUTF();
		if(!spell.isEmpty()) {
			creature.animus.equipSpell(SpellFactory.getSpell(spell));
		}
		
		int date = in.readInt();
		if(date != 0) {
			creature.die(date);
		}
		
		byte iCount = in.readByte();
		for(int i = 0; i < iCount; i++) {
			creature.inventory.addItem(in.readLong());
		}
		
		byte sCount = in.readByte();
		for(int i = 0; i < sCount; i++) {
			creature.inventory.put(Slot.valueOf(in.readUTF()), in.readLong());
		}
		
		return creature;
	}

	public void serialize(DataOutput out, Creature creature) throws IOException {
		out.writeUTF(creature.getID());
		out.writeUTF(creature.species.id);
		out.writeInt(creature.getBounds().x);
		out.writeInt(creature.getBounds().y);
		out.writeLong(creature.getUID());
		
		out.writeInt(creature.getBaseHealth());
		out.writeFloat(creature.getBaseHealthMod());
		out.writeFloat(creature.getHealthMod());
		out.writeInt(creature.getMoney());
		out.writeFloat(creature.animus.getBaseModifier());
		out.writeFloat(creature.animus.getModifier());
		if(creature.animus.getSpell() != null) {
			out.writeUTF(creature.animus.getSpell().id);
		} else {
			out.writeUTF("");
		}
		out.writeInt(creature.getTimeOfDeath());
		
		out.writeByte(creature.inventory.getItems().size());
		for(long uid : creature.inventory) {
			out.writeLong(uid);
		}
		
		out.writeByte(creature.inventory.slots().size());
		for(Slot slot : creature.inventory.slots()) {
			out.writeUTF(slot.name());
			out.writeLong(creature.inventory.get(slot));
		}
	}
	
	private Creature getCreature(String id, int x, int y, long uid, String species) {
		Creature creature; 
		
		RCreature rc = (RCreature)Engine.getResources().getResource(species);
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
		
		return creature;
	}
}
