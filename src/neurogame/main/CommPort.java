package neurogame.main;

import gnu.io.CommPortIdentifier;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
/**
 *
 * Created by Marcos on 7/19/2014.
 */
public class CommPort{
  private OutputStream outputSteam;
  private boolean foundPort = false;
  private byte[] buffer;

  public CommPort(){
    buffer = new byte[1];

    ParallelPort port;
    Enumeration portIdentidiers = CommPortIdentifier.getPortIdentifiers();
    while(portIdentidiers.hasMoreElements()){
      CommPortIdentifier id = (CommPortIdentifier)portIdentidiers.nextElement();
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

