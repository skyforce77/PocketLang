package fr.skyforce77.pocketlang;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.swing.JOptionPane;

public class Interpreter extends Thread{

	private int pointer = 0;
	private int[] bytes = new int[30000];
	private ArrayList<Integer> buffer = new ArrayList<Integer>();
	
	private String[] instructions = null;
	private int index = 0;
	
	private Synthesizer synth = null;
	
	public Interpreter(String[] instructions) {
		this.instructions = instructions;
	}
	
	public Interpreter(String[] instructions, int index) {
		this.instructions = instructions;
		this.index = index;
	}
	
	public Interpreter(String[] instructions, int index, Interpreter parent) {
		this.instructions = instructions;
		this.index = index;
		this.buffer = parent.getBuffer();
		this.pointer = parent.getPointer();
		this.bytes = parent.getBytes();
	}
	
	public ArrayList<Integer> getBuffer() {
		return buffer;
	}
	
	public int getPointer() {
		return pointer;
	}
	
	public int[] getBytes() {
		return bytes;
	}
	
	public void run() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		
		while(index < instructions.length) {
			Instruction inst = Instruction.fromString(instructions[index]);
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
						int i = (int)(buffer.size()-1);
						bytes[pointer] = buffer.get(i);
						buffer.remove(i);
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
						while(!Instruction.WHILE_END.equals(Instruction.fromString(instructions[index]))) {
							index++;
						}
					}
				} else if(inst.equals(Instruction.WHILE_END)) {
					if(bytes[pointer] != 0) {
						while(!Instruction.WHILE_START.equals(Instruction.fromString(instructions[index]))) {
							index--;
						}
						index--;
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
					if(result == null)
						result = "";
					buffer.clear();
					for(Byte i : result.getBytes()) {
						buffer.add((int)(char)(byte)i);
					}
				} else if(inst.equals(Instruction.FORK)) {
					buffer.add(Integer.valueOf(0));
					Interpreter child = new Interpreter(instructions, index+1, this);
					child.getBuffer().add(Integer.valueOf(1));
					child.start();
				}
			}
			index++;
		}
	}
	
}
