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

import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import neon.core.Engine;
import neon.util.fsm.*;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

/**
 * This class implements a primitive event bus. Incoming events are dispatched 
 * to appropriate {@code EventHandlers}, which decode the event and call the
 * individual {@code EventListeners} that were registered to listen to an 
 * event type.
 * 
 * A secondary function of the class is to implement a task queue. A task can
 * be registered to be run on receiving a certain event.
 * 
 * @author mdriesen
 */
public class EventTracker {
	private Multimap<Class<? extends EventObject>, EventListener> listeners = ArrayListMultimap.create();
	private HashMap<Class<? extends EventObject>, EventHandler<?, ?>> handlers = new HashMap<>();
	
	private Queue queue = new Queue(Engine.getTimer());
	private Timer timer = new Timer("Event tracker");
	
	/**
	 * Posts a new {@code EventObject}.
	 * 
	 * @param event
	 */
	public synchronized void post(EventObject event) {
		timer.schedule(new EventTask(event), 0);
	}

	// laat listeners event behandelen
	private void handle(EventObject event) {
		queue.check(event);

		EventHandler eh = handlers.get(event.getClass());
		for(EventListener listener : listeners.get(event.getClass())) {
			eh.dispatch(event, listener);
		}
	}

	/**
	 * Adds an {@code EventListener} for the given event type.
	 * 
	 * @param type
	 * @param listener
	 */
	public <T extends EventObject> void addListener(Class<T> type, EventListener listener) {
		listeners.put(type, listener);
	}
	
	/**
	 * Registers an {@code EventHandler}. A single {@code EventHandler} can be
	 * registered for each event type. Registering a second one will overwrite
	 * the existing handler.
	 * 
	 * @param handler
	 */
	public void addHandler(EventHandler<?, ?> handler) {
		handlers.put(handler.getEventType(), handler);
	}
	
	/**
	 * Add a task to be executed when an event with the given description is posted.
	 * 
	 * @param description	the description of the event
	 * @param a				the task to be run
	 */
	public void addTask(String description, Action a) {
		queue.add(description, a);
	}
	
	/**
	 * @return	a map with all queued tasks
	 */
	public Multimap<String, Action> getTasks() {
		return queue.getTasks();
	}
	
	public void addTimerTask(String script, Integer start, Integer period, Integer stop) {
		queue.add(script, start, period, stop);
	}
	
	public void addTimerTask(Action task, Integer start, Integer period, Integer stop) {
		queue.add(task, start, period, stop);
	}
	
	public Multimap<Integer, Queue.RepeatEntry> getTimerTasks() {
		return queue.getTimerTasks();
	}

	private class EventTask extends TimerTask {
		private EventObject event;
		
		private EventTask(EventObject event) {
			this.event = event;
		}
		
		public void run() {
			handle(event);
		}
	}
}
