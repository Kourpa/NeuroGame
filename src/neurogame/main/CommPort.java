package neurogame.main;

import gnu.io.CommPortIdentifier;
import gnu.io.ParallelPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Used http://www.coderanch.com/t/617885/java/java/rxtx-Parallel-Port-writing-Java as a reference.
 *
 * Created by Marcos on 7/19/2014.
 */
public class CommPort{
  private OutputStream outputSteam;
  private boolean foundPort = false;
  private byte[] buffer;

  /**
   * looks for the ports. If the parallel port is found its output stream i referenced.
   */
  public CommPort(){
    buffer = new byte[1];

    ParallelPort port;
    
    Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

    /**
     * Go through any ports and find the parallel port.
     */
    while(portIdentifiers.hasMoreElements()){

      CommPortIdentifier id = (CommPortIdentifier)portIdentifiers.nextElement();
      String typeStr = "";
      if ( id.getPortType() == CommPortIdentifier.PORT_PARALLEL) typeStr = "PORT_PARALLEL";
      
      System.out.println("====>Found CommPortIdentifier: " + id.getName() + ", Type="+id.getPortType()+"("+typeStr+")" );
      
      if(id.getName().equals("LPT3")){
        try {
          port = (ParallelPort) id.open("Comm", 0xC050);
        	//port = (ParallelPort) id.open("Comm", 0xC050);
          System.out.println(port.isPaperOut() + ": Paper Out");  
          System.out.println(port.isPrinterBusy()+ ": Parallel Port Busy");  
          System.out.println(port.isPrinterError()+ ": Parallel Port Error");  
          System.out.println(port.isPrinterSelected()+ ": Parallel Port Selected");  
          System.out.println(port.isPrinterTimedOut()+ ": Timed Out");  
        	//port = new ParallelPort();
          outputSteam = port.getOutputStream();
          foundPort = true;
        } catch (Exception e) {
          System.out.println("Opening Parallel port failed.");
        }
      }
    }
  }

  /**
   * writes the given byte to the output stream.
   * @param b
   */
  public void write(byte b){
    buffer[0] = b;
    if(foundPort){
      try {
        outputSteam.write(buffer);
        outputSteam.flush();
      } catch (IOException e) {
        System.out.println("failed to write to port: "+ e.getMessage());
      }
    }
    System.out.println("CommPort.write() Exit.");
  }

  /**
   * closes the output stream.
   */
  public void close(){
    if(foundPort) {
      try {
        outputSteam.flush();
        outputSteam.close();
      } catch (IOException e) {
        System.out.println("closing port failed");
      }
    }
  }


  /**
     * @param args the command line arguments
     */
  public static void main(String[] args) {
    
	  System.out.println("Opening Parallel port");
	  CommPort port = new CommPort();
    
	  System.out.println("Parallel port Send: 0x12");
    port.write((byte) 0x12);
    System.out.println("Parallel port: close");
    port.close();
  }
}

