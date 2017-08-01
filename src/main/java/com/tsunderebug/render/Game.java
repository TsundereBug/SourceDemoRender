package com.tsunderebug.render;

public class Game {

	private String code;
	private String name;
	private String path;

	public Game(String code, String name, String path) {
		this.code = code;
		this.name = name;
		this.path = path;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return code.equals(obj);
	}

}
