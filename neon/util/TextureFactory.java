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

package neon.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class implements methods for procedural texture generation and 
 * buffering the generated textures. 
 * When someone requests a texture, a character and a color are used to 
 * generate a square texture with the given font size. This allows a renderer
 * to work with different zoom levels. This texture is then buffered for fast 
 * retrieval at a later time.
 * 
 * @author mdriesen
 */
public class TextureFactory {
	private static HashMap<Integer, HashMap<String, TexturePaint>> textures = new HashMap<>();
	private static HashMap<Integer, HashMap<String, Image>> images = new HashMap<>();

	private static Font base = new Font("Lucida Sans Typewriter Regular", Font.PLAIN, 12);
	
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice gs = ge.getDefaultScreenDevice();
	private static GraphicsConfiguration gc = gs.getDefaultConfiguration();
	
	static {	// try to load DejaVu font, fallback is Lucida
		try {
			base = Font.createFont(Font.TRUETYPE_FONT, new File("lib/DejaVuSansMono.ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param text	the text of texture (only one character)
	 * @param size	the size of the text on the texture
	 * @param color	the color of the text on the texture
	 * @return		a texture with the given size
	 */
	public static TexturePaint getTexture(String text, int size, String color) {
		return getTexture(text, size, ColorFactory.getColor(color), Font.PLAIN);
	}
	
	/**
	 * @param text	the text of texture (only one character)
	 * @param size	the size of the text on the texture
	 * @param color	the color of the text on the texture
	 * @return		a texture with the given size
	 */
	public static TexturePaint getTexture(String text, int size, Color color) {
		return getTexture(text, size, color, Font.PLAIN);
	}

	/**
	 * @param text	the text of texture (only one character)
	 * @param size	the size of the text on the texture
	 * @param color	the color of the text on the texture
	 * @param style	the style of the text on the texture (bold, italic, ...)
	 * @return		a texture with the given size
	 */
	public static TexturePaint getTexture(String text, int size, Color color, int style) {
		if(text.length() != 1) {
			throw new IllegalArgumentException("String should be one character in length.");
		}
		
		size = Math.max(size, 1);
		String type = text + color + style;
		if(textures.containsKey(size)) {
			if(textures.get(size).containsKey(type)) {
				return textures.get(size).get(type);
			} 
		} else {
			textures.put(size, new HashMap<String, TexturePaint>());
		}
		
		BufferedImage image = gc.createCompatibleImage(size, size);
		Graphics2D g2i = image.createGraphics();
		
		Font font = base.deriveFont(style, size*6/7);
		
		g2i.setFont(font);
		g2i.setColor(Color.black);
		g2i.fillRect(0, 0, size, size);
		g2i.setColor(color);
		// 0.25 en 0.85 om letter mooi in midden van texture te krijgen
		g2i.drawString(text, (int)(size*0.25), (int)(size*0.85));	
		
		TexturePaint texture = new TexturePaint(image, new Rectangle(size, size));
		textures.get(size).put(type, texture);
		return texture;
	}
	
	/**
	 * @param text	the text on the image (only one character)
	 * @param size	the size of the text on the image
	 * @param color	the color of the text on the image
	 * @return		an image with the given size
	 */
	public static Image getImage(String text, int size, Color color) {
		if(text.length() != 1) {
			throw new IllegalArgumentException("String should be one character in length.");
		}

		size = Math.max(size, 1);
		String type = text + color;
		if(images.containsKey(size)) {
			if(images.get(size).containsKey(type)) {
				return images.get(size).get(type);
			} 
		} else {
			images.put(size, new HashMap<String, Image>());
		}

		BufferedImage image = gc.createCompatibleImage(size, size);
		Graphics2D g2i = image.createGraphics();
		
		Font font = base.deriveFont(Font.PLAIN, size*6/7);
		
		g2i.setFont(font);
		g2i.setColor(Color.black);
		g2i.fillRect(0, 0, size, size);
		g2i.setColor(color);
		// 0.25 en 0.85 om letter mooi in midden van texture te krijgen
		g2i.drawString(text, (int)(size*0.25), (int)(size*0.85));	
		
		images.get(size).put(type, image);
		return image;		
	}
}
