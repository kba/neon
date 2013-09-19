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

package neon.narrative;

import java.util.Collection;
import java.util.HashMap;

import neon.resources.RQuest;

public class Quest {
	public RQuest template;
	private int stage = 0;
	private boolean finished = false;
	private Collection<Topic> topics;
	private HashMap<String, Object> objects = new HashMap<String, Object>();
	
	public Quest(RQuest template, Collection<Topic> topics) {
		this.template = template;
		this.topics = topics;
	}
	
	public Collection<Topic> getTopics() {
		return topics;
	}
	
	public void setStage(int stage) {
		this.stage = stage;
	}
	
	public int getStage() {
		return stage;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Finishes this quest.
	 */
	public void finish() {
		finished = true;
		objects.clear();
	}
	
	public void addObject(String name, Object object) {
		objects.put(name,  object);
	}
	
	public HashMap<String, Object> getObjects() {
		return objects;
	}
}
