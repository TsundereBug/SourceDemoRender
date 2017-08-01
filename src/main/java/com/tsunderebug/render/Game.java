package com.tsunderebug.render;

import java.io.*;
import java.util.Properties;

public class Game {

	private String code;
	private String name;
	private String path;

	public Game(String code, String name, String path) {
		this.code = code;
		this.name = name;
		this.path = path;
	}

	public static Game fromRemote(Game remote) throws IOException {
		Properties p = new Properties();
		File cfg = new File(RenderService.DATA_DIR, "config.properties");
		if(!cfg.exists()) {
			cfg.createNewFile();
			String os = System.getProperty("os.name");
			if(os.contains("mac") || os.contains("darwin")) {
				p.setProperty("steam-common", System.getProperty("user.home") + "/Library/Application Support/Steam/steamapps/common");
			} else if (os.contains("win")) {
				p.setProperty("steam-common", "C:\\Program Files (x86)\\Steam\\steamapps\\common");
			} else if (os.contains("nix") || os.contains("nux")) {
				p.setProperty("steam-common", System.getProperty("user.home") + "/.local/share/Steam/steamapps/common");
			}
			System.out.print("Your Steam Common directory is set by default to `" + p.getProperty("steam-common") + "`. Enter a different path or press enter to keep this one: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String l = br.readLine();
			if(!"".equals(l)) {
				p.setProperty("steam-common", l);
				System.out.println("l");
			}
			FileWriter f = new FileWriter(cfg);
			p.store(f, "");
			f.close();
		} else {
			FileInputStream f = new FileInputStream(cfg);
			p.load(f);
			f.close();
		}
		return new Game(remote.getCode(), remote.getName(), p.getProperty("steam-common") + "/" + remote.getName());
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
