package fr.skyforce77.pocketlang;

import java.util.Arrays;
import java.util.List;

public enum Instruction {

	/* Brainfuck instructions */
	
	INCREMENT_POINTER('>', "xerneas", "xerneas", "xerneas"),
	DECREMENT_POINTER('<', "yveltal", "yveltal", "yveltal"),
	
	INCREMENT_VALUE('+', "posipi", "plusle", "purasle"),
	DECREMENT_VALUE('-', "negapi", "minun", "minun"),
	
	OUTPUT_CHAR('.', "queulorior", "smeargle", "doburu"),
	INPUT_CHAR(',', "pikachu", "pikachu", "pikachuu"),
	
	WHILE_START('[', "reshiram", "reshiram", "reshiram"),
	WHILE_END(']', "zekrom", "zekrom", "zekrom"),
	
	/* PocketLang instructions */
	
	CLEAR_VALUE("porygon", "porygon", "porygon"),
	
	NANOSLEEP("goinfrex", "munchlax", "gonbe"),
	SLEEP("ronflex", "snorlax", "kabigon"),
	
	SYNTHESIZER("pijako", "chatot", "perap"),
	
	PUSH_BUFFER("groudon", "groudon", "groudon"),
	POP_BUFFER("kyogre", "kyogre", "kyogre"),
	CLEAR_BUFFER("porygon2", "porygon2", "porygon2"),
	
	DISPLAY_MESSAGE_BUFFER_POPUP("ramboum", "loudred", "dogoomu"),
	DISPLAY_PROMPT_BUFFER_POPUP("brouhabam", "exploud", "bakuong"),
	
	/* TODO */
	
	DISPLAY_IMAGE_BUFFER("zorua", "zorua", "zorua"),
	DISPLAY_IMAGE_URL_BUFFER("zoroark", "zoroark", "zoroark"),
	
	SOUND_URL_BUFFER("sonistrelle", "noibat", "onbatto");
	
	private List<String> as;
	private char brainfuck = 0x00;
	
	Instruction(String... as) {
		this.as = Arrays.asList(as);
	}
	
	Instruction(char brainfuck, String... as) {
		this.as = Arrays.asList(as);
		this.brainfuck = brainfuck;
	}
	
	public char getChar() {
		return brainfuck;
	}
	
	public List<String> getStrings() {
		return as;
	}
	
	public boolean is(String inst) {
		return as.contains(inst.toLowerCase());
	}
	
	public boolean is(char c) {
		return brainfuck == c && c != 0x00;
	}
	
	public static Instruction fromString(String inst) {
		for(Instruction in : values()) {
			if(in.is(inst))
				return in;
		}
		return null;
	}
	
	public static Instruction fromChar(char inst) {
		for(Instruction in : values()) {
			if(in.is(inst))
				return in;
		}
		return null;
	}
}