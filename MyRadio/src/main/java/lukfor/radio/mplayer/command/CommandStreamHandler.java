package lukfor.radio.mplayer.command;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public class CommandStreamHandler implements Runnable {

	private InputStream is;

	private boolean silent = true;

	private String filename = null;

	private List<IStdOutListener> listeners = new Vector<IStdOutListener>();

	public CommandStreamHandler(InputStream is) {
		this.is = is;
	}

	public CommandStreamHandler(InputStream is, String filename) {
		this.is = is;
		this.filename = filename;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {

		try {

			boolean save = (filename != null && !filename.isEmpty());
			FileOutputStream writer = null;

			byte[] buffer = new byte[200];

			if (save) {
				writer = new FileOutputStream(filename);
			}

			int size = 0;

			while ((size = is.read(buffer)) > 0) {
				// stdout.append(line);
				String line = new String(buffer, 0, size);
				if (!silent) {
					System.out.println(line);
				}

				fireOnNewLine(line);

				if (save) {
					writer.write(buffer, 0, size);
				}
			}

			if (save) {
				writer.close();
			}

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addStdOutListener(IStdOutListener listener) {
		listeners.add(listener);
	}

	protected void fireOnNewLine(String line) {
		String[] lines = line.split("\n");
		for (String newLine : lines) {
			for (IStdOutListener listener : listeners) {
				listener.onNewLine(newLine);
			}
		}
	}

}
