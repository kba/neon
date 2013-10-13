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
import neon.entities.Player.Specialisation;
import neon.entities.property.Gender;
import neon.resources.RSign;

@SuppressWarnings("serial")
public class LoadEvent extends EventObject {
	public enum Mode {
		LOAD, NEW, DONE;
	}

	private Mode mode;
	private String save;
	
	// new mode variabelen
	public String race, name, profession;
	public Gender gender;
	public Specialisation specialisation;
	public RSign sign;
	
	/**
	 * Initialize in {@code LOAD} mode.
	 * 
	 * @param source
	 * @param save
	 */
	public LoadEvent(Object source, String save) {
		super(source);
		this.save = save;
		mode = Mode.LOAD;
	}
	
	/**
	 * Initialize in {@code DONE} mode.
	 * 
	 * @param source
	 */
	public LoadEvent(Object source) {
		super(source);
		mode = Mode.DONE;
	}
	
	public LoadEvent(Object source, String race, String name, Gender gender,
			Specialisation specialisation, String profession, RSign sign) {
		super(source);
		mode = Mode.NEW;
		this.race = race;
		this.name = name;
		this.gender = gender;
		this.specialisation = specialisation;
		this.profession = profession;
		this.sign = sign;
	}

	public Mode getMode() {
		return mode;
	}
	
	public String getSaveName() {
		return save;
	}
}
