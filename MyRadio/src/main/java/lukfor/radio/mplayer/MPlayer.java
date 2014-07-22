package lukfor.radio.mplayer;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lukfor.radio.mplayer.command.Command;
import lukfor.radio.mplayer.command.IStdOutListener;

public class MPlayer implements IStdOutListener {

	private String filename;

	Command command = null;

	private String streamTitle = "*** unknown ***";

	private List<IMPlayerListener> listeners = new Vector<IMPlayerListener>();

	public MPlayer(String filename) {
		this.filename = filename;
	}

	public boolean init() {

		command = new Command(filename, "-slave", "-quiet", "-idle");
		command.addStdOutListener(this);
		command.setSilent(true);
		command.execute(false);

		return true;

	}

	public boolean play(String url) throws IOException {

		command.sendCommand("loadfile " + url + " 0");

		return true;

	}

	public String getStreamTitle() {

		return streamTitle;

	}

	public boolean stop() {

		return true;

	}
	
	public boolean close() throws InterruptedException, IOException {
		command.sendCommand("quit");
		command.close();
		return true;
	}

	@Override
	public void onNewLine(String line) {
		// System.out.println("[StdOut] " + line);

		if (line.startsWith("ICY Info:")) {

			Pattern pattern = Pattern.compile("StreamTitle=\\'(.*?)\\';");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {

				String newStreamTitle = matcher.group(1).trim();
				if (!streamTitle.equals(newStreamTitle)) {
					streamTitle = newStreamTitle;
					fireChange(IMPlayerListener.STREAM_TITLE_CHANGED);
				}
			}
		}

	}

	protected void fireChange(int type) {
		for (IMPlayerListener listener : listeners) {
			listener.onChange(type);
		}
	}

	public void addListener(IMPlayerListener listener) {
		listeners.add(listener);
	}

}
