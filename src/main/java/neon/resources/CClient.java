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

package neon.resources;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class CClient extends Resource {
	// keyboard settings
	public static final int NUMPAD = 0;
	public static final int AZERTY = 1;
	public static final int QWERTY = 2;
	public static final int QWERTZ = 3;

	public int up = KeyEvent.VK_NUMPAD8;
	public int upright = KeyEvent.VK_NUMPAD9;
	public int right = KeyEvent.VK_NUMPAD6;
	public int downright = KeyEvent.VK_NUMPAD3;
	public int down = KeyEvent.VK_NUMPAD2;
	public int downleft = KeyEvent.VK_NUMPAD1;
	public int left = KeyEvent.VK_NUMPAD4;
	public int upleft = KeyEvent.VK_NUMPAD7;
	public int wait = KeyEvent.VK_NUMPAD5;
	
	public int map = KeyEvent.VK_M;
	public int magic = KeyEvent.VK_G;
	public int shoot = KeyEvent.VK_F;
	public int look = KeyEvent.VK_L;
	public int act = KeyEvent.VK_SPACE;
	public int talk = KeyEvent.VK_T;
	public int unmount = KeyEvent.VK_U;
	public int sneak = KeyEvent.VK_V;
	public int journal = KeyEvent.VK_J;
	
	private int keys = NUMPAD;
	
	// language settings
	private Properties strings;
	
	// other settings
	private String bigCoin = "€";
	private String smallCoin = "c";
	private String title = "";
	
	public CClient(String... path) {
		super("client", path);
		
		// file inladen
		Document doc = new Document();
		try (FileInputStream in = new FileInputStream(path[0])){
			doc = new SAXBuilder().build(in);
		} catch(Exception e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();

		// keyboard
		setKeys(root.getChild("keys"));
		
		// taal
		Properties defaults = new Properties();	// locale.en laden als default
		try (FileInputStream stream = new FileInputStream("data/locale/locale.en"); 
				InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"))){
			defaults.load(reader);
		} catch(IOException e) {
			e.printStackTrace();
		} 

		String lang = root.getChild("lang").getText();
		strings = new Properties(defaults);		// locale initialiseren met 'en' defaults
		try (FileInputStream stream = new FileInputStream("data/locale/locale." + lang); 
				InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"))){
			strings.load(reader);
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void load() {}

	@Override
	public void unload() {}
	
	/**
	 * Return the string value with the given name.
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		return strings.getProperty(name);
	}
	
	public String getBig() {
		return bigCoin;
	}
	
	public void setBig(String name) {
		bigCoin = name;
	}
	
	public String getSmall() {
		return smallCoin;
	}
	
	public void setSmall(String name) {
		smallCoin = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getSettings() {
		return keys;
	}

	public void setKeys(Element settings) {
		if(settings != null) {
			// movement keys
			switch(settings.getText()) {
			case "azerty": setKeys(AZERTY); break;
			case "qwerty": setKeys(QWERTY); break;
			case "qwertz": setKeys(QWERTZ); break;
			}

			// andere keys
			if(settings.getAttribute("map") != null) {
				map = getKeyCode(settings.getAttributeValue("map"));
			}
			if(settings.getAttribute("act") != null) {
				act = getKeyCode(settings.getAttributeValue("act"));
			}
			if(settings.getAttribute("magic") != null) {
				magic = getKeyCode(settings.getAttributeValue("magic"));
			}
			if(settings.getAttribute("shoot") != null) {
				shoot = getKeyCode(settings.getAttributeValue("shoot"));
			}
			if(settings.getAttribute("look") != null) {
				look = getKeyCode(settings.getAttributeValue("look"));
			}
			if(settings.getAttribute("talk") != null) {
				talk = getKeyCode(settings.getAttributeValue("talk"));
			}
			if(settings.getAttribute("unmount") != null) {
				unmount = getKeyCode(settings.getAttributeValue("unmount"));
			}
			if(settings.getAttribute("sneak") != null) {
				sneak = getKeyCode(settings.getAttributeValue("sneak"));
			}
			if(settings.getAttribute("journal") != null) {
				journal = getKeyCode(settings.getAttributeValue("journal"));
			}
		}
	}
	
	public void setKeys(int choice) {
		keys = choice;
		switch(keys) {
		case NUMPAD: 
			up = KeyEvent.VK_NUMPAD8;
			upright = KeyEvent.VK_NUMPAD9;
			right = KeyEvent.VK_NUMPAD6;
			downright = KeyEvent.VK_NUMPAD3;
			down = KeyEvent.VK_NUMPAD2;
			downleft = KeyEvent.VK_NUMPAD1;
			left = KeyEvent.VK_NUMPAD4;
			upleft = KeyEvent.VK_NUMPAD7;
			wait = KeyEvent.VK_NUMPAD5;
			break;
		case AZERTY:
			up = KeyEvent.VK_Z;
			upright = KeyEvent.VK_E;
			right = KeyEvent.VK_D;
			downright = KeyEvent.VK_C;
			down = KeyEvent.VK_X;
			downleft = KeyEvent.VK_W;
			left = KeyEvent.VK_Q;
			upleft = KeyEvent.VK_A;
			wait = KeyEvent.VK_S;
			break;
		case QWERTY:
			up = KeyEvent.VK_W;
			upright = KeyEvent.VK_E;
			right = KeyEvent.VK_D;
			downright = KeyEvent.VK_C;
			down = KeyEvent.VK_X;
			downleft = KeyEvent.VK_Z;
			left = KeyEvent.VK_A;
			upleft = KeyEvent.VK_Q;
			wait = KeyEvent.VK_S;
			break;
		case QWERTZ:
			up = KeyEvent.VK_W;
			upright = KeyEvent.VK_E;
			right = KeyEvent.VK_D;
			downright = KeyEvent.VK_C;
			down = KeyEvent.VK_X;
			downleft = KeyEvent.VK_Y;
			left = KeyEvent.VK_A;
			upleft = KeyEvent.VK_Q;
			wait = KeyEvent.VK_S;
			break;
		}
	}
	
	private static int getKeyCode(String code) {
		switch(code) {
		case "VK_B": return KeyEvent.VK_B;
		case "VK_F": return KeyEvent.VK_F;
		case "VK_G": return KeyEvent.VK_G;
		case "VK_H": return KeyEvent.VK_H;
		case "VK_I": return KeyEvent.VK_I;
		case "VK_J": return KeyEvent.VK_J;
		case "VK_K": return KeyEvent.VK_K;
		case "VK_L": return KeyEvent.VK_L;
		case "VK_M": return KeyEvent.VK_M;
		case "VK_N": return KeyEvent.VK_N;
		case "VK_O": return KeyEvent.VK_O;
		case "VK_P": return KeyEvent.VK_P;
		case "VK_R": return KeyEvent.VK_R;
		case "VK_T": return KeyEvent.VK_T;
		case "VK_U": return KeyEvent.VK_U;
		case "VK_V": return KeyEvent.VK_V;
		case "VK_SPACE": return KeyEvent.VK_SPACE;
		default: return 0;
		}
	}
}
