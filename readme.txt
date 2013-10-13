Instructions when using code from the git repository
----------------------------------------------------
The repository contains two separate parts:
	neon: the main game engine and editor
	darkness: a sample game for neon

To download the source code of a certain revision, choose the required branch or tag and click 'Download Snapshot'. 

Neon uses some external libraries, which should be in your java classpath before running. These are:
	guava.jar
		- java collections not included in the standard collections framework
		- available at code.google.com/p/guava-libraries/‎
	jdbm.jar
		- java collections backed by disk storage
		- available at github.com/jankotek/JDBM3
	jdom-2.0.5.jar
		- used for xml reading and writing
		- available at www.jdom.org
	jtexgen.jar
		- procedural texture generation
		- available at kenai.com/projects/jtexgen
	mbassador-1.1.7.jar
		- a fast event bus implementation
		- available at github.com/bennidi/mbassador
	phys2d.jar
		- 2d java physics engine
		- available at www.cokeandcode.com/phys2d/
	tinylaf-1.4.0.jar
		- alternative look-and-feel
		- available at www.muntjak.de/hans/java/tinylaf/
All these libraries are included in the latest neon release.

Also required is the DejaVuSansMono.ttf file. This provides a font to make the game look the same on any platform.
