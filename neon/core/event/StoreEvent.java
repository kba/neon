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

package neon.core.event;

import java.util.EventObject;
import neon.entities.Entity;

@SuppressWarnings("serial")
public class StoreEvent extends EventObject {
	public enum Mode {
		ADD, REMOVE;
	}

	private Mode mode;
	private long uid;
	private Entity entity;
	
	/**
	 * Creates a {@code StoreEvent} in {@code REMOVE} mode.
	 * 
	 * @param source
	 * @param uid	the uid of the {@code Entity} to remove
	 */
	public StoreEvent(Object source, long uid) {
		super(source);
		mode = Mode.REMOVE;
		this.uid = uid;
	}

	/**
	 * Creates a {@code StoreEvent} in {@code ADD} mode.
	 * 
	 * @param source
	 * @param entity	the {@code Entity} to add
	 */
	public StoreEvent(Object source, Entity entity) {
		super(source);
		mode = Mode.ADD;
		this.entity = entity;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public long getUID() {
		return uid;
	}
	
	public Entity getEntity() {
		return entity;
	}
}
