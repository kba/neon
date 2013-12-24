/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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

package neon.util.fsm;

/**
 * Transitions must be added to the (nested) state or state machine that contains the 
 * 'from' state.
 * 
 * @author mdriesen
 */
public class Transition {
	private String condition;
	private boolean local;
	private Action action;
	private State next;
	private State from;
	
	public Transition(State from, State next, String condition) {
		this(from, next, condition, null);
	}
	
	public Transition(State from, State next, String condition, Action action) {
		this(from, next, condition, action, false);
	}
	
	public Transition(State from, State next, String condition, boolean isLocal) {
		this(from, next, condition, null, isLocal);
	}
	
	public Transition(State from, State next, String condition, Action action, boolean isLocal) {
		this.condition = condition;
		local = isLocal;
		this.action = action;
		this.next = next;
		this.from = from;
	}

	public State getNext() {
		return next;
	}
	
	public State getFrom() {
		return from;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public boolean isLocal() {
		return local;
	}
}
