package lukfor.radio.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import lukfor.radio.RadioLcd;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;

public class RadioMenu {

	private RadioLcd lcd;

	private List<MenuItem> items = new Vector<MenuItem>();

	private MenuItem item = null;

	private int currentSelection = 0;

	public RadioMenu(RadioLcd lcd) {
		this.lcd = lcd;
	}

	public void addMenuItem(MenuItem item) {
		items.add(item);
	}

	public void left() {
		if (currentSelection > 0) {
			currentSelection--;
		}
		updateLcd();
	}

	public void right() {
		if (currentSelection < items.size() - 1) {
			currentSelection++;
		}
		updateLcd();
	}

	public void enter() {
		if (item == null) {
			item = items.get(currentSelection);
			if (item.getItems().size() == 0) {
				item.fireSelection();
			} else {
				currentSelection = 0;
				updateLcd();
			}
		} else {
			if (item.getItems().size() > 0) {
				item = item.getItems().get(currentSelection);
				if (item.getItems().size() == 0) {
					item.fireSelection();
				} else {
					currentSelection = 0;
					updateLcd();
				}
			}
		}

	}

	public void back() {
		if (item != null) {
			// item = item.getParent();

			if (item.getParent() != null) {
				currentSelection = item.getParent().getItems().indexOf(item);
				item = item.getParent().getItems().get(currentSelection);
			} else {
				currentSelection = items.indexOf(item);
				item = items.get(currentSelection);
			}
			updateLcd();
		} else {
			close();
		}
	}

	public void show() {
		currentSelection = 0;
		item = null;
		updateLcd();
	}

	protected void updateLcd() {

		List<MenuItem> myItems = null;

		if (item != null) {
			myItems = item.getItems();
		} else {
			myItems = items;
		}

		if (currentSelection == 0) {
			System.out.println("-> " + myItems.get(currentSelection));
			System.out.println("  " + myItems.get(currentSelection + 1));
		} else {
			System.out.println("  " + myItems.get(currentSelection - 1));
			System.out.println("-> " + myItems.get(currentSelection));
		}

	}

	protected void close() {

	}

	public static void main(String[] args) throws IOException {

		MenuItem settings = new MenuItem("Settings...");
		settings.addMenuItem(new MenuItem("Sound"));
		settings.addMenuItem(new MenuItem("Grafik"));
		settings.addMenuItem(new MenuItem("Other"));

		RadioMenu ui = new RadioMenu(null);
		ui.addMenuItem(new MenuItem("Info"));
		ui.addMenuItem(settings);
		ui.addMenuItem(new MenuItem("Exit"));
		ui.show();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s;
		while ((s = in.readLine()) != null) {
			if (s.equals("l")) {
				ui.left();
			}
			if (s.equals("r")) {
				ui.right();
			}
			if (s.equals("e")) {
				ui.enter();
			}
			if (s.equals("b")) {
				ui.back();
			}
			if (s.equals("q")) {
				break;
			}
		}

	}

}
