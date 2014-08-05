package lukfor.radio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class RadioLcd implements Runnable {

	public final static int LCD_ROWS = 2;
	public final static int LCD_ROW_1 = 0;
	public final static int LCD_ROW_2 = 1;
	public final static int LCD_COLUMNS = 16;
	public final static int LCD_BITS = 4;

	public final boolean DEBUG = true;

	public final int SCROLL_SPEED = 300;

	private GpioLcdDisplay lcd;

	private String lineA;

	private String lineB;

	private boolean scrollA = false;

	private boolean scrollB = false;

	private static final Log log = LogFactory.getLog(RadioLcd.class);

	public RadioLcd(Pin rs, Pin e, Pin d1, Pin d2, Pin d3, Pin d4) {
		if (!DEBUG) {
			lcd = new GpioLcdDisplay(LCD_ROWS, LCD_COLUMNS, rs, e, d1, d2, d3,
					d4);
		}
	}

	public void clear() {
		if (!DEBUG) {
			lcd.clear();
		}
	}

	public void writeLineA(String text, boolean scroll) {
		this.lineA = text;
		this.scrollA = scroll;
		writeA(text);
	}

	public void writeLineB(String text, boolean scroll) {
		this.lineB = text;
		this.scrollB = scroll;
		writeB(text);
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (scrollA) {
					lineA = lineA.substring(1) + lineA.substring(0, 1);
					writeA(lineA);
				}
				if (scrollB) {
					lineB = lineB.substring(1) + lineB.substring(0, 1);
					writeB(lineB);
				}

				Thread.sleep(SCROLL_SPEED);

			}
		} catch (InterruptedException e) {
			log.error("Lcd Display shutdown.", e);
		}
	}

	protected void writeA(String text) {

		int length = Math.min(LCD_COLUMNS, text.length());

		String output = text.subSequence(0, length - 1).toString();

		if (!DEBUG) {
			lcd.writeln(LCD_ROW_1, output);
		} else {
			log.debug(output);
		}

	}

	protected void writeB(String text) {

		int length = Math.min(LCD_COLUMNS, text.length());
		String output = text.subSequence(0, length - 1).toString();

		if (!DEBUG) {
			lcd.writeln(LCD_ROW_2, output);
		} else {
			log.debug(output);
		}

	}

}
