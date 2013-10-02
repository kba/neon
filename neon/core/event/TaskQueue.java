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
import neon.core.Engine;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import neon.util.fsm.Action;
import net.engio.mbassy.listener.Handler;

public class TaskQueue {
	private Multimap<String, Action> tasks;
	private Multimap<Integer, RepeatEntry> repeat;
	
	public TaskQueue() {
		tasks = ArrayListMultimap.create();
		repeat = ArrayListMultimap.create();
	}
	
	@Handler public void check(EventObject e) {
		if(tasks.containsKey(e.toString())) {
			for(Action task : tasks.get(e.toString())) {
				task.run(e);
			}
		}
	}
	
	public void add(String description, Action task) {
		tasks.put(description, task);
	}
	
	public void add(String script, Integer start, Integer period, Integer stop) {
		RepeatEntry entry = new RepeatEntry(period, stop, script);
		repeat.put(start, entry);
	}
	
	public void add(Action task, Integer start, Integer period, Integer stop) {
		RepeatEntry entry = new RepeatEntry(period, stop, task);
		repeat.put(start, entry);
	}
	
	public Multimap<String, Action> getTasks() {
		return tasks;
	}
	
	public Multimap<Integer, RepeatEntry> getTimerTasks() {
		return repeat;
	}
	
	@Handler public void tick(TurnEvent te) {
		int time = te.getTime();
		
		if(repeat.containsKey(time)) {
			for(RepeatEntry entry : repeat.get(time)) {
				if(entry.script != null) {
					Engine.execute(entry.script);
				} else {
					entry.task.run(te);
				}
				if(entry.stop > 0 && time + entry.period <= entry.stop) {
					repeat.put(time + entry.period, entry);
				}
			}
			repeat.removeAll(time);
		}
	}
	
	public static class RepeatEntry {
		private Action task;
		private String script;
		private int period;
		private int stop;
		
		private RepeatEntry(int period, int stop, String script) {
			this.script = script;
			this.period = period;
			this.stop = stop;
		}

		private RepeatEntry(int period, int stop, Action task) {
			this.task = task;
			this.period = period;
			this.stop = stop;
		}
		
		public Action getTask() {
			return task;
		}
		
		public String getScript() {
			return script;
		}
		
		public int getPeriod() {
			return period;
		}

		public int getStop() {
			return stop;
		}
	}
}
