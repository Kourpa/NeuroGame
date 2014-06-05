///**
// * NeuroGame.
// * CS 351, Project 3
// * 
// * Team members:
// * Ramon A. Lovato
// * Danny Gomez
// * Marcos Lemus
// */
//
//package neurogame.io;
//
//import jssc.SerialPort;
//import jssc.SerialPortException;
//import jssc.SerialPortList;
//
///**
// * A class for handling serial port communication for NeuroGame. Since
// * NeuroGame only needs to send data over serial (the amp can't transmit back),
// * and since receiving data requires special handling, this module *only*
// * provides functionality for opening a single serial connection at a time and
// * transmitting data through it.
// * 
// * This class utilizes the open-source jSSC (Java Simple Serial Connector)
// * library to interface with the port:
// *     https://code.google.com/p/java-simple-serial-connector/
// * 
// * @author Ramon A. Lovato
// * @team Danny Gomez
// * @team Marcos Lemus
// */
//public class Communicator {
//	public static final int DEFAULT_PORT_NUMBER = 0;
//	public static final int DEFAULT_BAUDRATE = SerialPort.BAUDRATE_9600;
//	public static final int DEFAULT_DATABITS = SerialPort.DATABITS_8;
//	public static final int DEFAULT_STOPBITS = SerialPort.STOPBITS_1;
//	public static final int DEFAULT_PARITY_BITS = SerialPort.PARITY_NONE;
//	
//	private SerialPort serialPort;
//	
//	/**
//	 * Instantiate a new Communicator with serialPort null.
//	 */
//	public Communicator() {
//		serialPort = null;
//	}
//	
//	/**
//	 * Instantiate a new Communicator with the specified serialPort and
//	 * configuration. This does not guarantee that the port will be available
//	 * when instantiation completes. If any of the settings failed to
//	 * initialize, the serialPort is closed and reset to null.
//	 * 
//	 * @param name String containing the name of the port to open.
//	 * @param baudRate The baud rate at which to communicate over the port.
//	 * @param dataBits The number of data bits used by the connected device.
//	 * @param stopBits The number of stop bits used by the connected device.
//	 * @param parityBits The number of parity bits used by the connected device.
//	 */
//	public Communicator (String name, int baudRate, int dataBits,
//			int stopBits, int parityBits) {
//			serialPort = new SerialPort(name);
//		
//		try {
//			serialPort.openPort();
//			serialPort.setParams(baudRate, dataBits, stopBits, parityBits);
//		} catch (SerialPortException ex) {
//			try {
//				serialPort.closePort();
//			} catch (SerialPortException ex2) {
//				// Does nothing since we're setting the port to null anyway.
//			} finally {
//				serialPort = null;				
//			}
//		}
//	}
//	
//	/**
//	 * Get the names of all available comm ports.
//	 * 
//	 * @return ports String[] containing the names of all available comm ports.
//	 */
//	public String[] getPortNames() {
//		return SerialPortList.getPortNames();
//	}
//	
//	/**
//	 * Open the specified port without configuring it. Only one port may be
//	 * open at a time. If a port is already open, closes it first.
//	 * 
//	 * @param name String containing the name of the port to open.
//	 * @return True if opening the connection succeeded, else false.
//	 */
//	public boolean openPort(String name) {
//		if (serialPort != null) {
//			closePort();
//		}
//		serialPort = new SerialPort(name);
//		
//		try {
//			return serialPort.openPort();
//		} catch (SerialPortException ex) {
//			return false;
//		}
//	}
//	
//	/**
//	 * Open the specified port and configure it. Only one port may be open at
//	 * a time. If a port is already open, closes it first.
//	 * 
//	 * @param name String containing the name of the port to open.
//	 * @param baudRate The baud rate at which to communicate over the port.
//	 * @param dataBits The number of data bits used by the connected device.
//	 * @param stopBits The number of stop bits used by the connected device.
//	 * @param parityBits The number of parity bits used by the connected device.
//	 * @return True if opening the connection succeeded, else false.
//	 */
//	public boolean openPort(String name, int baudRate, int dataBits,
//			int stopBits, int parityBits) {
//		if (serialPort != null) {
//			closePort();
//		}
//		serialPort = new SerialPort(name);
//		
//		try {
//			serialPort.openPort();
//			serialPort.setParams(baudRate, dataBits, stopBits, parityBits);
//		} catch (SerialPortException ex) {
//			return false;
//		}
//		
//		return true;
//	}
//	
//	/**
//	 * Configure the currently open port.
//	 * 
//	 * @param baudRate The baud rate at which to communicate over the port.
//	 * @param dataBits The number of data bits used by the connected device.
//	 * @param stopBits The number of stop bits used by the connected device.
//	 * @param parityBits The number of parity bits used by the connected device.
//	 * @return True if configuring the connection succeeded, else false (also
//	 *             false if the port is currently null).
//	 */
//	public boolean configurePort(int baudRate, int dataBits, int stopBits,
//			int parityBits) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.setParams(
//						baudRate, dataBits, stopBits, parityBits);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Write the specified String to the port.
//	 * 
//	 * @param s String to write to the port.
//	 * @return True if the write was successful (this does not mean the data
//	 *             were received, only that they were sent over the line
//	 *             without throwing an exception), else false.
//	 */
//	public boolean send(String s) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.writeString(s);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Write the specified int to the port.
//	 * 
//	 * @param i Int to write to the port.
//	 * @return True if the write was successful (this does not mean the data
//	 *             were received, only that they were sent over the line
//	 *             without throwing an exception), else false.
//	 */
//	public boolean send(int i) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.writeInt(i);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Write the specified ints to the port.
//	 * 
//	 * @param i Int array to write to the port.
//	 * @return True if the write was successful (this does not mean the data
//	 *             were received, only that they were sent over the line
//	 *             without throwing an exception), else false.
//	 */
//	public boolean send(int[] i) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.writeIntArray(i);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Write the specified byte to the port.
//	 * 
//	 * @param b Byte to write to the port.
//	 * @return True if the write was successful (this does not mean the data
//	 *             were received, only that they were sent over the line
//	 *             without throwing an exception), else false.
//	 */
//	public boolean send(byte b) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.writeByte(b);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Write the specified byte to the port.
//	 * 
//	 * @param b Byte array to write to the port.
//	 * @return True if the write was successful (this does not mean the data
//	 *             were received, only that they were sent over the line
//	 *             without throwing an exception), else false.
//	 */
//	public boolean send(byte[] b) {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.writeBytes(b);
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Close the currently open serial port.
//	 * 
//	 * @return True if successful, else false (also false if no port is open).
//	 */
//	public boolean closePort() {
//		if (serialPort == null) {
//			return false;
//		} else {
//			try {
//				return serialPort.closePort();
//			} catch (SerialPortException ex) {
//				return false;
//			}
//		}
//	}
//	
//	/**
//	 * Check if a port is currently *open* (not just assigned).
//	 * 
//	 * @return True if serialPort is open (not just assigned), else false.
//	 */
//	public boolean isPortOpen() {
//		return (serialPort == null ? false : serialPort.isOpened());
//	}
//	
//	/**
//	 * Getter for serialPort.
//	 * 
//	 * @return The current serialPort or null if unassigned.
//	 */
//	public SerialPort getSerialPort() {
//		return serialPort;
//	}
//	
//	/**
//	 * Getter for serialPort's name.
//	 * 
//	 * @return The current serialPort's name or an empty String if unassigned.
//	 */
//	public String getSerialPortName() {
//		return (serialPort == null ? "" : serialPort.getPortName());
//	}
//	
//	/**
//	 * Main method for debugging. Prints to the console a list of all currently
//	 * available serial ports.
//	 * 
//	 * @param args Command-line arguments.
//	 */
//	public static void main(String[] args) {
//		Communicator comm = new Communicator();
//		String[] portNames = comm.getPortNames();
//		System.out.println("Serial ports:");
//		if (portNames.length == 0) {
//			System.out.println("No ports found.");
//		} else {
//			for (String s : portNames) {
//				System.out.println(s);
//			}
//			comm.openPort(portNames[0]);
//			comm.configurePort(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
//					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//			comm.send("Hello");
//		}
//	}
//	
//}
