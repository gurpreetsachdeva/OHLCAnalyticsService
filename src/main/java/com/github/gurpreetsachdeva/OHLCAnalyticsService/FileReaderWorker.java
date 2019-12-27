package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;

public class FileReaderWorker implements Runnable {

	private BlockingQueue<TradesData> queue;

	private String filePath;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Read File and keep on appending to the Concurrent Queue,if file gets modified
		// , keep blocking and pushing
		System.out.println("Inside FileReaderWorker");

		JSONParser jsonParser = new JSONParser();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = readEachLineGracefully(reader);
			JSONObject obj =null;
			if (line != null) {
				 obj = parseEachLineGracefully(jsonParser,line);
			}
			while (line != null) {
				parseTradeLine(obj);
				obj = parseEachLineGracefully(jsonParser,line);
				line = readEachLineGracefully(reader);
			}

			// Add the Watcher Service

			final Path path = FileSystems.getDefault().getPath(getDirectory(filePath));
			// System.out.println(path);
			try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
				final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
				while (true) {
					// System.out.println("Upstream Feed will write and I will do my work ;)");
					final WatchKey wk = watchService.take();
					for (WatchEvent<?> event : wk.pollEvents()) {
						// we only register "ENTRY_MODIFY" so the context is always a Path.
						final Path changed = (Path) event.context();
						// System.out.println(changed);
						if (changed.endsWith(getFileName(filePath))) {
							line = readEachLineGracefully(reader);
							System.out.println("line Entered" + line);
							if (line != null) {
								obj = parseEachLineGracefully(jsonParser,line);

							}

							while (line != null) {

								parseTradeLine(obj);

								obj = parseEachLineGracefully(jsonParser,line);
								line = readEachLineGracefully(reader);
							}
						}
					}
					// reset the key
					boolean valid = wk.reset();
					if (!valid) {
						System.out.println("Key has been unregisterede");
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Exiting FileReaderWorker");

	}

	private void parseTradeLine(JSONObject obj) {
		if (obj != null) {
			Double price = Double.valueOf(String.valueOf(obj.get("P")));
			Long time = Long.valueOf(String.valueOf((obj.get("TS2"))));
			String sym = (String) obj.get("sym");
			Double quantity = Double.valueOf(String.valueOf(obj.get("Q")));
			TradesData td = new TradesData(sym, price, quantity, time);

			try {
				queue.put(td);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public FileReaderWorker(BlockingQueue<TradesData> queue, String filePath) {
		super();
		this.queue = queue;
		this.filePath = filePath;
	}

	public String getDirectory(String fullPath) {

		int index = fullPath.lastIndexOf("/");
		return fullPath.substring(0, index);

	}

	public String getFileName(String fullPath) {

		int index = fullPath.lastIndexOf("/");

		return fullPath.substring(index + 1);
	}

	private JSONObject parseEachLineGracefully(JSONParser jsonParser,String line) {
		
		try {
			return (JSONObject) jsonParser.parse(line);
		}
		
		catch (ParseException e) {
			// TODO: handle exception
			System.out.println("See this line carefully , it threw a parsing exception:"+line);
			e.printStackTrace();
		}
		return null;
		
		
	}
	
private String readEachLineGracefully(BufferedReader reader) throws IOException {
		
		String line= reader.readLine();
		while(!line.endsWith("}")) {
			line=line+reader.readLine();
		}
		return line;
		
	}
}
