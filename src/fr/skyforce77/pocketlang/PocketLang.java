package fr.skyforce77.pocketlang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.JOptionPane;

public class PocketLang {
    
	private static int pointer = 0;
	private static int[] bytes = new int[30000];
	private static ArrayList<Integer> buffer = new ArrayList<Integer>();
	
	public static void main(String[] args) {
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
			run(toRun);
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
	
	public static void run(String toRun) {
		String[] insts = toRun.split(" ");
		int at = 0;
		
		Synthesizer synth = null;
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		
		while(at < insts.length) {
			Instruction inst = Instruction.fromString(insts[at]);
			if(inst != null) {
				if(inst.equals(Instruction.INCREMENT_POINTER)) {
					if(pointer < bytes.length-1)
						pointer++;
					else
						pointer = 0;
				} else if(inst.equals(Instruction.DECREMENT_POINTER)) {
					if(pointer != 0)
						pointer--;
					else
						pointer = bytes.length-1;
				} else if(inst.equals(Instruction.INCREMENT_VALUE)) {
					if(bytes[pointer] < 256)
						bytes[pointer]++;
					else
						bytes[pointer] = 0;
				} else if(inst.equals(Instruction.DECREMENT_VALUE)) {
					if(bytes[pointer] != 0)
						bytes[pointer]--;
					else
						bytes[pointer] = 255;
				} else if(inst.equals(Instruction.CLEAR_VALUE)) {
					bytes[pointer] = 0;
				} else if(inst.equals(Instruction.PUSH_BUFFER)) {
					buffer.add(bytes[pointer]);
				} else if(inst.equals(Instruction.POP_BUFFER)) {
					if(buffer.size() != 0) {
						int index = (int)(buffer.size()-1);
						bytes[pointer] = buffer.get(index);
						buffer.remove(index);
					} else {
						bytes[pointer] = 0;
					}
				} else if(inst.equals(Instruction.CLEAR_BUFFER)) {
					buffer.clear();
				} else if(inst.equals(Instruction.OUTPUT_CHAR)) {
					System.out.print((char)bytes[pointer]);
				} else if(inst.equals(Instruction.INPUT_CHAR)) {
					try {
						bytes[pointer] = System.in.read();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if(inst.equals(Instruction.WHILE_START)) {
					if(bytes[pointer] == 0) {
						while(!Instruction.WHILE_END.equals(Instruction.fromString(insts[at]))) {
							at++;
						}
					}
				} else if(inst.equals(Instruction.WHILE_END)) {
					if(bytes[pointer] != 0) {
						while(!Instruction.WHILE_START.equals(Instruction.fromString(insts[at]))) {
							at--;
						}
						at--;
					}
				} else if(inst.equals(Instruction.SLEEP)) {
					try {
						Thread.sleep(1000*bytes[pointer]);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if(inst.equals(Instruction.NANOSLEEP)) {
					try {
						Thread.sleep(bytes[pointer]);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if(inst.equals(Instruction.SYNTHESIZER)) {
					try {
						ShortMessage myMsg = new ShortMessage();
					    myMsg.setMessage(ShortMessage.NOTE_ON, ((bytes[pointer] & 0x80) == 128) ? 9 : 4, bytes[pointer] & 0x7F, 90); 
					    Receiver synthRcvr = synth.getReceiver();
					    synthRcvr.send(myMsg, -1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(inst.equals(Instruction.DISPLAY_MESSAGE_BUFFER_POPUP)) {
					String text = "";
					for(Integer i : buffer) {
						text += (char)(int)i;
					}
					JOptionPane.showMessageDialog(null, text);
				} else if(inst.equals(Instruction.DISPLAY_PROMPT_BUFFER_POPUP)) {
					String text = "";
					for(Integer i : buffer) {
						text += (char)(int)i;
					}
					String result = JOptionPane.showInputDialog(null, text.equals("") ? "Input requested" : text);
					for(Byte i : result.getBytes()) {
						buffer.add((int)(char)(byte)i);
					}
				}
			}
			at++;
		}
	}
	
}