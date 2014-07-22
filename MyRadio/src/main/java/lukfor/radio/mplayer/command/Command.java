package lukfor.radio.mplayer.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Command {

	protected String cmd;

	private String[] params;

	private boolean silent = false;

	private boolean deleteInput = false;

	private String directory = null;

	private Process process;

	private List<IStdOutListener> listeners = new Vector<IStdOutListener>();

	private Thread errorStreamHandler;

	private Thread inputStreamHandler;

	public Command(String cmd, String... params) {
		this.cmd = cmd;
		this.params = params;
	}

	public Command(String cmd) {
		this.cmd = cmd;
	}

	public void setParams(String... params) {
		this.params = params;
	}

	public void setParams(List<String> params) {
		this.params = new String[params.size()];
		for (int i = 0; i < params.size(); i++) {
			this.params[i] = params.get(i);
		}
	}

	public int execute(boolean wait) {

		List<String> command = new ArrayList<String>();

		command.add(cmd);
		if (params != null) {
			for (String param : params) {
				command.add(param);
			}
		}

		try {

			ProcessBuilder builder = new ProcessBuilder(command);
			// builder.redirectErrorStream(true);
			if (directory != null) {
				builder.directory(new File(directory));
			}

			process = builder.start();

			inputStreamHandler = null;

			CommandStreamHandler handler = new CommandStreamHandler(
					process.getInputStream(), null);
			handler.setSilent(silent);
			for (IStdOutListener listener : listeners) {
				handler.addStdOutListener(listener);
			}
			inputStreamHandler = new Thread(handler);
			inputStreamHandler.start();

			/*
			 * CommandStreamPipeHandler handler2 = new CommandStreamPipeHandler(
			 * inputStream, process.getOutputStream()); Thread pipeHandler = new
			 * Thread(handler2); pipeHandler.start();
			 */

			errorStreamHandler = new Thread(new CommandStreamHandler(
					process.getErrorStream()));

			errorStreamHandler.start();

			if (wait) {

				process.waitFor();

				inputStreamHandler.interrupt();
				errorStreamHandler.interrupt();
				inputStreamHandler.join();
				errorStreamHandler.join();

				if (process.exitValue() != 0) {
					return process.exitValue();
				} else {
					process.destroy();
				}

				if (deleteInput) {
					new File(cmd).delete();
				}
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public InputStream getOutputStream() {
		return process.getInputStream();
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isDeleteInput() {
		return deleteInput;
	}

	public void setDeleteInput(boolean deleteInput) {
		this.deleteInput = deleteInput;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDirectory() {
		return directory;
	}

	@Override
	public String toString() {
		String result = cmd;
		if (params != null) {
			for (String param : params) {
				result += " " + param;
			}
		}
		return result;
	}

	public String getName() {
		return cmd;
	}

	public boolean sendCommand(String command) throws IOException {
		process.getOutputStream().write(command.getBytes());
		process.getOutputStream().write("\n".getBytes());
		process.getOutputStream().flush();
		return true;

	}

	public void addStdOutListener(IStdOutListener listener) {
		listeners.add(listener);
	}

	public int close() throws InterruptedException {

		inputStreamHandler.interrupt();
		errorStreamHandler.interrupt();
		inputStreamHandler.join();
		errorStreamHandler.join();

		if (process.exitValue() != 0) {
			return process.exitValue();
		} else {
			process.destroy();
		}

		if (deleteInput) {
			new File(cmd).delete();
		}

		return 0;
	}

}
