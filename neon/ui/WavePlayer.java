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

package neon.ui;

import java.io.*;
import javax.sound.sampled.*;
 
public class WavePlayer extends Thread {
    private static final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
    private String filename;
 
    public WavePlayer(String wavfile) {
        filename = wavfile;
    }
 
    public void run() {
        File soundFile = new File(filename);
        if(!soundFile.exists()) {
            System.err.println("Wave file not found: " + filename);
            return;
        }
 
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch(UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            return;
        } catch(IOException e1) {
            e1.printStackTrace();
            return;
        }
 
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try {
            auline = (SourceDataLine)AudioSystem.getLine(info);
            auline.open(format);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            return;
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
 
        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
        try {
            while(nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if(nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            return;
        } finally {
            auline.drain();
            auline.close();
        }
    }
}
