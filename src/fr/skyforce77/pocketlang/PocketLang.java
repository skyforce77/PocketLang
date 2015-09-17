package fr.skyforce77.pocketlang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import javax.swing.UIManager;

public class PocketLang {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		if(args[0].equals("run")) {
			if(args.length >= 2) {
				runFile(args[1]);
			}
		} else if(args[0].equals("translate")) {
			if(args.length >= 3) {
				translateFile(args[1], args[2]);
			}
		} else if(args[0].equals("help")) {
			for(Instruction inst : Instruction.values()) {
				System.out.println(inst+": "+Arrays.toString(inst.getStrings().toArray()));
			}
		}
	}
	
	public static void runFile(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String toRun = "";
			String line = "";
			while(line != null) {
				toRun += " "+line;
				line = br.readLine();
			}
			br.close();
			new Interpreter(toRun.split(" ")).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void translateFile(String file, String to) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String toRun = "";
			String line = "";
			while(line != null) {
				toRun += line;
				line = br.readLine();
			}
			br.close();
			
			File f = new File(to);
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			Instruction last = null;
			for(int i = 0; i<toRun.length(); i++) {
				Instruction inst = Instruction.fromChar(toRun.charAt(i));
				
				if(inst != null) {
					String start = "";
					
					if(inst.equals(last)) {
						start = " ";
					} else if(last != null) {
						start = "\n";
					}
					bw.write(start+inst.getStrings().get(0));
				}
				last = inst;
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}