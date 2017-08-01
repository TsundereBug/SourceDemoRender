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
	public static final File DATA_DIR = new File(System.getProperty("user.home"), ".sourcerender");

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
							File f = new File(DATA_DIR, u.getPath().substring(u.getPath().lastIndexOf('/') + 1) + System.currentTimeMillis() + ".dem");
							f.createNewFile();
							FileOutputStream fos = new FileOutputStream(f);
							HttpURLConnection huc = (HttpURLConnection) u.openConnection();
							huc.addRequestProperty("User-Agent", "Mozilla/5.0 DemoRender/1.0");
							InputStream is = huc.getInputStream();
							byte[] buffer = new byte[1048576];
							while(is.read(buffer) > 0) {
								fos.write(buffer);
							}
							fos.close();
							is.close();
							g = Game.fromRemote(g);
							String extension = ".exe";
							String os = System.getProperty("os.name");
							if(os.contains("mac") || os.contains("darwin")) {
								extension = "_mac";
							} else if (os.contains("win")) {
								extension = ".exe";
							} else if (os.contains("nix") || os.contains("nux")) {
								extension = "_linux";
							}
							String executable = g.getPath() + "/" + g.getCode() + extension;
							Process p = new ProcessBuilder(executable, "-hijack", "-textmode", "-console", "+sv_cheats 1", "+host_framerate 30", "+demo_quitafterplayback 1", "+startmovie\"" + f.getAbsolutePath() + "_\" raw", "+playdemo \"" + f.getAbsolutePath() + "\"").redirectOutput(ProcessBuilder.Redirect.INHERIT).start();
							p.waitFor();
						} catch (MalformedURLException e) {
							b.write("MALFORMED");
							b.newLine();
							b.flush();
							s.close();
							br.close();
							b.close();
						} catch (InterruptedException e) {
							b.write("INTERRUPT");
							b.newLine();
							b.flush();
							s.close();
							br.close();
							b.close();
							Thread.currentThread().interrupt();
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
