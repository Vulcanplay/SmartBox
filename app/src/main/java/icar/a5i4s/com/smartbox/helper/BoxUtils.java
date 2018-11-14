package icar.a5i4s.com.smartbox.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.uartdemo.SerialPort;


public class BoxUtils {
	private static SerialPort serialPort;
	private static InputStream is;
	private static OutputStream os;
	public static final String OPENDOOR = "55aa0101";
	public static final String CLOSEDOOR = "55aa0102";
	public static final String PUSHOUT = "55aa0105";
	public static final String PUSHIN = "55aa0106";
	public static final String RESET = "55aa0103";
	public static final String STEPRUNCCW = "55aa0104";
	public static final int totalCell = 40;

	public static SerialPort initSerialPort() {
		if (serialPort == null) {
			try {
				serialPort = new SerialPort(12, 115200, 0);

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return serialPort;
	}

	public static void closeSerialPort() {
		if (serialPort != null) {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serialPort.close(12);
			serialPort = null;
		}
	}

	public static void executeCmd(String cmd, long time) {
		if (serialPort != null && os != null && time > 0) {
			try {
				os.write(Tools.HexString2Bytes(cmd));
				Thread.sleep(time);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void openCmd(int inputCell, int beforeCell) {

		executeCmd(STEPRUNCCW.concat(Tools.encodeHex(inputCell)),
				getExecuteTime(inputCell, beforeCell));
		executeCmd(OPENDOOR, 7000); // 开门
		executeCmd(PUSHOUT, 7000); // 弹出箱格
	}

	public static void closeCmd() {
		executeCmd(PUSHIN, 8000);// 弹入箱格
		executeCmd(CLOSEDOOR, 7000);// 关门
	}

	public static long getExecuteTime(int inputCell, int beforeCell) {
		int intervalNum = Math.abs(beforeCell - inputCell);
		int intervalNum2 = beforeCell > inputCell ? (totalCell - beforeCell + inputCell)
				: (totalCell - inputCell + beforeCell);
		long time = 0;
		if (intervalNum > intervalNum2) {
			time = intervalNum2 * 1000;
		} else {
			time = intervalNum * 1000;
		}
		if (time > 0 && time <= 8000) {
			time += 2000;
		}
		return time;
	}
}
