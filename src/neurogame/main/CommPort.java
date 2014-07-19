package neurogame.main;

import gnu.io.CommPortIdentifier;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;
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

      // No idea what LPT1 is.
      if(id.getName().equals("LPT1")){
        try {
          port = (ParallelPort) id.open("Comm", 50);
          outputSteam = port.getOutputStream();
          foundPort = true;
        } catch (IOException | PortInUseException | NullPointerException e) {
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
        System.out.println("failed to write to port.");
      }
    }
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
    CommPort port = new CommPort();
    port.write((byte) 0x12);
    port.close();
  }
}

