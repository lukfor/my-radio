package lukfor.radio.menu;

import java.util.List;
import java.util.Vector;

public class MenuItem {

	private String text = "";

	private List<MenuItem> items;

	private List<ISelectionListener> listeners;

	private MenuItem parent = null;

	public MenuItem(String text) {
		this.text = text;
		items = new Vector<MenuItem>();
		listeners = new Vector<ISelectionListener>();
	}

	public void addMenuItem(MenuItem item) {
		items.add(item);
		item.setParent(this);
	}

	public List<MenuItem> getItems() {
		return items;
	}

	public void addSelectionListener(ISelectionListener listener) {
		listeners.add(listener);
	}

	protected void fireSelection() {
		for (ISelectionListener listener : listeners) {
			listener.selection(this);
		}
		System.out.println("Fire selection " + toString());
	}

	protected void setParent(MenuItem parent) {
		this.parent = parent;
	}

	public MenuItem getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return text;
	}
}
