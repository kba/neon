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

import java.util.HashMap;
import java.util.EventObject;

@SuppressWarnings("serial")
public class TransitionEvent extends EventObject {
	private String eventID;
	private HashMap<String, Object> parameters;
	private boolean consumed;
	
	public TransitionEvent(String id) {
		super(id);
		parameters = new HashMap<String, Object>();
		consumed = false;
		eventID = id;
	}
	
	public TransitionEvent(String id, Object... args) {
		this(id);
		if(args.length%2 == 0) {
			for(int i = 0; i < args.length; i+=2) {
				parameters.put(args[i].toString(), args[i+1]);
			}
		}
	}
	
	@Override
	public String toString() {
		return eventID;
	}
	
	public void setParameter(String param, Object object) {
		parameters.put(param, object);
	}
	
	public Object getParameter(String param) {
		return parameters.get(param);
	}
	
	public void consume() {
		consumed = true;
	}
	
	public boolean isConsumed() {
		return consumed;
	}
}
