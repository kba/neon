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

package neon.systems.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class FileUtils {
	
	/**
	 * Copies all contents of a source directory to a destination directory
	 * @param from	the source directory
	 * @param to	the destination directory
	 */
	public static void copy(Path from, Path to) {
		try {
			Files.walkFileTree(from, new Visitor(from, to));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class Visitor implements FileVisitor<Path> {
        private final Path source;
        private final Path target;

        private Visitor(Path source, Path target) {
            this.source = source;
            this.target = target;
        }
        
	    private void copyFile(Path source, Path target) {
	    	try {
	    		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }

	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
	    	try {
	    		Files.copy(dir, target.resolve(source.relativize(dir)));
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	return FileVisitResult.CONTINUE;
	    }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return FileVisitResult.CONTINUE;
        }

	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
	    	copyFile(file, target.resolve(source.relativize(file)));
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return FileVisitResult.CONTINUE;
        }
	}
	
	
	/**
	 * Packs a directory into a jar file of the same name.
	 * 
	 * @param path	the path to the directory that has to be packed
	 * @throws IOException 
	 */
	public static JarFile pack(String path, String modID) throws IOException {
		// alle oudermappen wegknippen
		String name = path.substring(Math.max(0, path.lastIndexOf(File.separator)));

		File mod = new File(path);
		File jar = new File(name + ".jar");

		byte buffer[] = new byte[1024];
		// open jar file
		FileOutputStream stream = new FileOutputStream(jar);
		JarOutputStream out = new JarOutputStream(stream);

		for(File file : listFiles(mod)) {
			String entry = file.getPath().replace(path + File.separator, "").replace(File.separator, "/");
			System.out.println("Adding " + entry);

			// add jar entry
			out.putNextEntry(new JarEntry(entry));

			// write file to jar
			FileInputStream in = new FileInputStream(file);
			while(true) {
				int nRead = in.read(buffer, 0, buffer.length);
				if(nRead <= 0) { break; }
				out.write(buffer, 0, nRead);
			}
			in.close();
		}
		// manifest schrijven
		Manifest mf = new Manifest();
		mf.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mf.getMainAttributes().putValue("Mod-ID", modID);
		System.out.println(mf.getMainAttributes().getValue("Mod-ID"));

		JarEntry manifest = new JarEntry("META-INF/MANIFEST.MF");
		out.putNextEntry(manifest);
		mf.write(out);

		out.close();
		stream.close();
		System.out.println("Adding completed!");
		return new JarFile(jar);
	}
	
	/**
	 * Unpacks a jar file to a directory of the same name.
	 * 
	 * @param path	the path to the jar file that has to be unpacked
	 */
	public static void unpack(String path) {
		try(JarFile jar = new JarFile(new File(path))) {
			File dir = new File(jar.getName().replace(".jar", ""));
			dir.mkdir();
						
			Enumeration<JarEntry> entries = jar.entries();
			
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				File file = new File(dir.getPath() + File.separator + name);
				if(!entry.isDirectory()) {
					File parent = new File(file.getParent());
					// directories worden niet uitgepakt, maar worden voor elke file gecontroleerd
					if(!parent.exists()) {
						parent.mkdir();
					}
					InputStream in = jar.getInputStream(jar.getEntry(entry.getName()));
					OutputStream out = new FileOutputStream(file);
					int nextByte;	// in.read leest een byte, maar steekt dat in een int?????
					while((nextByte = in.read()) != -1) {
						out.write((byte)nextByte);
					}
					out.write( '\n' );
					out.flush();
					out.close();
				} 
			}
		} catch(IOException e) {
			System.out.println("Error in FileSystem.unpack: " + e.getMessage());			
		} 
	}
	
	// geeft lijst terug van alle files in een directory en zijn subdirectories
	private static List<File> listFiles(File dir) {
		ArrayList<File> files = new ArrayList<File>();
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				files.addAll(listFiles(file));
			} else {
				files.add(file);
			}
		}
		return files;
	}
}
