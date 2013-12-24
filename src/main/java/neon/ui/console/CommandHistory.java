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

package neon.ui.console;
/**
 * This code was adapted from the following forum post: 
 * http://www.javaprogrammingforums.com/java-swing-tutorials/4907-java-tip-jul-29-2010-swing-console-component.html
 * 
 * @author Andrew
 */

public class CommandHistory {
	private static class Node {
		public String command;
		public Node	next;
		public Node	prev;

		public Node(String command) {
			this.command = command;
			next = null;
			prev = null;
		}
	}

	private int	length;
	private Node top;	// the top command with an empty string
	private Node current;
	private int	capacity;

	/**
	 * Creates a CommandHistory with the default capacity of 64
	 */
	protected CommandHistory() {
		this(64);
	}

	/**
	 * Creates a CommandHistory with a specified capacity
	 * 
	 * @param capacity
	 */
	protected CommandHistory(int capacity) {
		top = new Node("");
		current = top;
		top.next = top;
		top.prev = top;
		length = 1;
		this.capacity = capacity;
	}

	/**
	 * @return
	 */
	protected String getPrevCommand() {
		current = current.prev;
		return current.command;
	}

	/**
	 * @return
	 */
	protected String getNextCommand() {
		current = current.next;
		return current.command;
	}

	/**
	 * Adds a command to this command history manager. Resets the command
	 * counter for which command to select next/prev.<br>
	 * If the number of remembered commands exceeds the capacity, the oldest
	 * item is removed.<br>
	 * Duplicate checking only for most recent item.
	 * 
	 * @param command
	 */
	protected void add(String command) {
		// move back to the top
		current = top;
		// see if we even need to insert
		if (top.prev.command.equals(command)) {
			// don't insert
			return;
		}
		// insert before top.next
		Node temp = new Node(command);
		Node oldPrev = top.prev;
		temp.prev = oldPrev;
		oldPrev.next = temp;
		temp.next = top;
		top.prev = temp;
		length++;
		if (length > capacity) {
			// delete oldest command
			Node newNext = top.next.next;
			top.next = newNext;
			newNext.prev = top;
		}
	}
}
