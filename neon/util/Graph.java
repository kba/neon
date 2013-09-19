/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Graph<T> implements Serializable {
	private static final long serialVersionUID = -6431348687813884897L;
	private HashMap<Integer, Node<T>> nodes = new HashMap<Integer, Node<T>>();
	
	/**
	 * Adds a node to the graph. Any existing node with the given index
	 * is overwritten. Connections to the node are kept.
	 * 
	 * @param index	the index of the new node
	 */
	public void addNode(int index, T content) {
		nodes.put(index, new Node<T>(content));
	}
	
	/**
	 * Adds a connection between two nodes.
	 * 
	 * @param from			the start node
	 * @param to			the end node
	 * @param bidirectional	whether the connection is bidirectional
	 */
	public void addConnection(int from, int to, boolean bidirectional) {
		if(nodes.get(from) != null && nodes.get(to) != null) {
			nodes.get(from).addConnection(to);
			if(bidirectional) {
				nodes.get(to).addConnection(from);
			}
		}
	}
	
	/**
	 * @param index
	 * @return	the content of the node with the given index
	 */
	public T getNode(int index) {
//		System.out.println(index);
		return nodes.get(index).content;
	}
	
	/**
	 * @param index
	 * @return	all connections leaving the node with the given index
	 */
	public Collection<Integer> getConnections(int index) {
		try {
			return nodes.get(index).connections;
		} catch(IndexOutOfBoundsException e) {
			return new ArrayList<Integer>();
		}
	}
	
	/**
	 * @return	a collection of all nodes in this graph
	 */
	public Collection<T> getNodes() {
		ArrayList<T> content = new ArrayList<T>();
		for(Node<T> node : nodes.values()) {
			content.add(node.content);
		}
		return content;
	}
	
	private static class Node<T> implements Serializable {
		private static final long serialVersionUID = 2326885959259937816L;
		private T content;
		private ArrayList<Integer> connections = new ArrayList<Integer>();
		
		private Node(T content) {
			this.content = content;
		}
		
		private void addConnection(int to) {
			connections.add(to);
		}
	}
}
