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

package neon.util.fsm;

/**
 * This class represents a state in the finite state machine. Every state can
 * contain substates.
 * 
 * @author mdriesen
 */
public class State {
	protected State parent;
	protected String name;
	private boolean blocked = false;
	
	public State(State parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public State(State parent) {
		this(parent, null);
	}
	
	// methodes overriden indien nodig
	public void enter(TransitionEvent e) {}
	public void exit(TransitionEvent e) {}

	public void setVariable(String name, Object value) {
		parent.setVariable(name, value);
	}
	
	public Object getVariable(String name) {
		return parent.getVariable(name);
	}
	
	public State getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	void block() {
		blocked = true;
	}
	
	void unblock() {
		blocked = false;
	}
	
	boolean isBlocked() {
		return blocked;
	}
}
