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

import java.util.*;

/**
 * A simple finite state machine supporting nested states.
 * 
 * @author mdriesen
 */
public class FiniteStateMachine extends State {
	// String in hashmap is eventID + current state, dan kunnen eventIDs herbruikt worden
	private HashMap<String, Transition> transitions = new HashMap<>();
	private HashMap<String, Object> variables = new HashMap<>();
	private List<State> starts = new ArrayList<>();	// lijst met alle start states
	private Set<State> currents;	// lijst van alle current states
	
	public FiniteStateMachine() {
		super(null, "FSM");
	}
	
	public void addStartStates(State... states) {
		starts.addAll(Arrays.asList(states));
	}

	public void start(TransitionEvent e) {
		currents = new TreeSet<State>(new StateComparator());
		for(State state : starts) {
			if(state.parent.equals(this) || starts.contains(state.parent)) {
				currents.add(state);
				state.enter(e);
			}
		}
	}

	public void stop(TransitionEvent e) {
		for(State current : currents) {
			current.exit(e);
		}
	}

	@Override
	public void transition(TransitionEvent event) {
		// hier een kopie van currents maken, omdat currents kan aangepast worden
		for(State current : new ArrayList<State>(currents)) {
			if(currents.contains(current) && !current.isBlocked()) {
				if(transitions.containsKey(event.toString() + current)) {
					// dit wil zeggen dat transition.from zeker de current state is
					stateChanged(event, current);
				}
			}
		}
		for(State current : currents) {
			current.unblock();
		}
	}

	private void stateChanged(TransitionEvent e, State current) {
		Transition transition = transitions.get(e.toString() + current);
		State next = transition.getNext();
		State ancestor = getCommonAncestor(current, next);
//		System.out.println(current.getName() + " -> " + next.getName());
		// lokale transitie
		boolean isLocal = transition.isLocal();

		// alle nodige state exiten
		if(isLocal) {	// lokale transitie
			for(State state : new ArrayList<State>(currents)) {
				if(state.parent == ancestor) {
					exit(e, ancestor, state);
				}
			}
		} else {	// alle andere transities
			exit(e, ancestor, current);
		}

		// acties op transitie uitvoeren
		if(transition.getAction() != null) {
			transition.getAction().run(e);
		}

		// alle nodige states enteren
		if(isLocal) {	// lokale transitie
			enter(e, ancestor, next);
			for(State state : starts) {
				if(state.parent == ancestor) {
					enter(e, ancestor, state);
				}
			}
		} else {
			enter(e, ancestor, next);
		}
	}

	// current en alle super/substates exiten
	private void exit(TransitionEvent e, State ancestor, State current) {
		currents.remove(current);
		// substates exiten
		for(State state : new ArrayList<State>(currents)) {
			if(state.parent == current) {
				exit(e, current, state);
			}
		}
		// current exiten
		current.exit(e);
		// superstates exiten
		if(current.parent != ancestor && currents.contains(current.parent)) {
			exit(e, ancestor, current.parent);
		}
	}

	// current en alle super/substates enteren
	private void enter(TransitionEvent e, State ancestor, State next) {
		currents.add(next);

		// alle superstates enteren
		if(next != ancestor && next.parent != ancestor && !currents.contains(next.parent)) {
			enter(e, ancestor, next.parent);
		}
		// next enteren
		next.enter(e);
		// alle substates enteren
		for(State state : starts) {
			if(state.parent == next) {
				boolean check = true;

				// als huidige state al actieve state heeft, overslaan
				for(State other : currents) {
					if(other.parent == next) {
						check = false;
					}
				}

				if(check) {
					enter(e, next, state);
				}
			}
		}

		// aangeven dat deze state net geëntered is, en geen events meer mag handlen deze beurt
		next.block();
	}

	public void addTransition(Transition transition) {
		transitions.put(transition.getCondition() + transition.getFrom(), transition);
	}

	@Override
	public void setVariable(String name, Object value) {
		variables.put(name, value);
	}
	
	@Override
	public Object getVariable(String name) {
		return variables.get(name);
	}
	
	private State getCommonAncestor(State one, State two) {
		LinkedList<State> states = new LinkedList<State>();

		State next = one;
		while(next != null) {
			states.addLast(next);
			next = next.parent;
		}

		next = two;
		while(!states.contains(next)) {
			next = next.parent;
		}
		
		return next;
	}
	
	private class StateComparator implements Comparator<State> {
		public int compare(State one, State two) {
			if(one == two) {
				return 0;
			} else if(getCommonAncestor(one, two) == two) {
				return 1;
			} else if(getCommonAncestor(one, two) == one) {
				return -1;
			} else {
				return 1;	// om volgorde in set goed te krijgen...
			}
		}
	}
}
