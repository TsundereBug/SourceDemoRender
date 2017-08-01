package com.tsunderebug.render;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RenderService {

	public static final int PORT = 12983;

	private static RenderService ourInstance;

	public static RenderService getRenderService() throws IOException {
		if(ourInstance == null) {
			ourInstance = new RenderService();
		}
		return ourInstance;
	}

	private ServerSocket server;
	private Map<Game, Deque<Socket>> clients;

	private RenderService() throws IOException {
		server = new ServerSocket(PORT, 500);
		clients = new HashMap<>();
	}

	private Thread acceptanceThread;
	private boolean accept = false;

	public void startAcceptance() {
		accept = true;
		acceptanceThread = new Thread(() -> {
			while (accept) {
				try {
					Socket s = server.accept();
					BufferedWriter b = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
					InputStreamReader r = new InputStreamReader(s.getInputStream());
					b.write("GAME?");
					b.newLine();
					b.flush();
					Gson gs = new Gson();
					Game g = gs.fromJson(r, Game.class);
					if(!clients.containsKey(g)) {
						clients.put(g, new ArrayDeque<>());
					}
					b.write("QUEUED: " + clients.get(g).size());
					b.newLine();
					b.flush();
					BufferedReader br = new BufferedReader(r);
					String l = br.readLine();
					if("CLOSE".equals(l)) {
						s.close();
						br.close();
						b.close();
					} else {
						try {
							URL u = new URL(l);
							HttpURLConnection huc = (HttpURLConnection) u.openConnection();
							huc.addRequestProperty("User-Agent", "Mozilla/5.0 DemoRender/1.0");
							// TODO
						} catch (MalformedURLException e) {
							b.write("MALFORMED");
							b.newLine();
							b.flush();
							s.close();
							br.close();
							b.close();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void stopAcceptance() {

	}

}
