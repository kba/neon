/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2007 - Maarten Driesen
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

import java.util.*;

public class Journal {
	private HashMap<String, Integer> quests;
	private HashMap<String, String> subjects;
	
	public Journal() {
		quests = new HashMap<String, Integer>();
		subjects = new HashMap<String, String>();
	}
	
	public HashMap<String, Integer> getQuests() {
		return quests;
	}
	
	public HashMap<String, String> getSubjects() {
		return subjects;
	}
	
	public boolean hasQuest(String name) {
//		System.out.println("NoteBook.hasQuest(" + name + ")");
		return quests.containsKey(name);
	}
	
	public void addQuest(String name, String description) {
//		System.out.println("NoteBook.addQuest(" + name + ")");
		quests.put(name, 0);
		subjects.put(name, description);
	}
	
	public void finishQuest(String name) {
//		System.out.println("NoteBook.finishQuest(" + name + ")");
		quests.put(name, 100);
	}
	
	public void updateQuest(String name, int status, String description) {
		quests.put(name, status);
		subjects.put(name, subjects.get(name) + "<br />" + description);
	}
	
	public void updateQuest(String name, int status) {
		quests.put(name, status);
	}
	
	public boolean finishedQuest(String name) {
//		System.out.println("NoteBook.finishedQuest(" + name + ")");
//		System.out.println(quests.get(name));
		if(quests.get(name) != null && quests.containsKey(name)) {
			return quests.get(name) == 100;
		} else {
			return false;
		}
	}
	
	public int questStatus(String name) {
//		System.out.println(name);
		if(quests.containsKey(name)) {
//			System.out.println(quests.get(name));
			return quests.get(name);
		} else {
			return 0;
		}
	}
	
	public void removeQuest(String name) {
		quests.remove(name);
		subjects.remove(name);
	}
	
	public void addText(String subject, String text) {
		subjects.put(subject, subjects.get(subject) + "<br />" + text);
	}
}
