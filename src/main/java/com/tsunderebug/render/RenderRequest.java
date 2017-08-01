package com.tsunderebug.render;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.net.URL;

public class RenderRequest {

	public static void requestRender(Game game, URL demo) throws IOException {
		try(Socket c = new Socket("localhost", RenderService.PORT)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
			String l = br.readLine();
			if("GAME?".equals(l)) {
				String j = new Gson().toJson(game);
				bw.write(j);
				bw.newLine();
				bw.flush();
				int queued = Integer.parseInt(br.readLine().replaceAll("QUEUED: (\\d+)", "$1"));
				bw.write(demo.toString());
				bw.flush();
			}
		}
	}

}
