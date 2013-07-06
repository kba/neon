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

package neon.core.event;

import java.util.EventObject;
import neon.objects.property.Skill;

@SuppressWarnings("serial")
public class SkillEvent extends EventObject {
	private boolean levelled;
	private int stat;
	
	public SkillEvent(Skill skill, int stat) {
		this(skill, false);
		this.stat = stat;
	}
	
	public SkillEvent(Skill skill, boolean levelled) {
		super(skill);
		this.levelled = levelled;
	}
	
	public SkillEvent(Skill skill) {
		this(skill, false);
	}
	
	public boolean hasLevelled() {
		return levelled;
	}
	
	public Skill getSkill() {
		return (Skill)source;
	}
	
	@Override
	public String toString() {
		return source.toString();
	}
	
	public int getStat() {
		return stat;
	}
}
