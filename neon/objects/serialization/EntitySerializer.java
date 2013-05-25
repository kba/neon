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

package neon.objects.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import neon.objects.entities.Creature;
import neon.objects.entities.Entity;
import neon.objects.entities.Item;
import org.apache.jdbm.Serializer;

public class EntitySerializer implements Serializer<Entity>, Serializable {
	private static final long serialVersionUID = 4682346337753485512L;
	private ItemSerializer itemSerializer = new ItemSerializer();
	private CreatureSerializer creatureSerializer = new CreatureSerializer();

	public Entity deserialize(DataInput input) throws IOException, ClassNotFoundException {
		switch(input.readUTF()) {
		case "item":
			return itemSerializer.deserialize(input);
		case "creature":
			return creatureSerializer.deserialize(input);
		default:
			return null;
		}		
	}

	public void serialize(DataOutput output, Entity entity) throws IOException {
		if(entity instanceof Item) {
			output.writeUTF("item");
			itemSerializer.serialize(output, (Item)entity);
		} else if(entity instanceof Creature) {
			output.writeUTF("creature");
			creatureSerializer.serialize(output, (Creature)entity);
		}
	}
}
