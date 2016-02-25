/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package agendador;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author alexandre
 */
public class Comunicador implements SerialPortEventListener
{
    SerialPort serialPort;
    public JTextArea Log;
    

	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 38400;
    
    
    
//-----------------------------------------------------------------------------------------------------
	public void Iniciar(CommPortIdentifier portId, JTextArea Log) 
    {
        
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
        	
		this.Log=Log;
		if (portId == null) 
        {
			System.out.println("Could not find COM port.");
			return;
		}

		try 
        {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener( this);
			serialPort.notifyOnDataAvailable(true);
		} 
        catch (Exception e) 
        {
			System.err.println(e.toString());
		}
	}
//-----------------------------------------------------------------------------------------------------
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() 
    {
		if (serialPort != null) 
        {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
//-----------------------------------------------------------------------------------------------------
	/**
	 * Handle an event on the serial port. Read the data and print it.
     * @param oEvent
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) 
    {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
        {
			try 
            {
				String inputLine=input.readLine();
                Log.append(inputLine);                
                Log.append("\n");          
                Log.setCaretPosition(Log.getDocument().getLength());
				System.out.println(inputLine);
			} 
            catch (Exception e) 
            {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
//-----------------------------------------------------------------------------------------------------
	public Enumeration pegarPortas()
    {
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
        return CommPortIdentifier.getPortIdentifiers();        
    }
//-----------------------------------------------------------------------------------------------------    
    public void EnviarRelogio(int dia, int mes, int ano, int hora, int minuto, int segundo)
    {
        try 
        {
            String saida=String.format("A%02d%02d%04d%02d%02d%02d\n", dia, mes+1, ano, hora, minuto, segundo);
            Log.append("Enviado: " + saida);
            Log.setCaretPosition(Log.getDocument().getLength());
            output.write(saida.getBytes());
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(Comunicador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//-----------------------------------------------------------------------------------------------------    
    public void AgendarData(int dia, int mes, int ano, int hora, int minuto, int TempoLigado, int Porta)
    {
        try 
        {
            String saida=String.format("B%02d%02d%04d%02d%02d%010d%02d\n", dia, mes+1, ano, 
                                                                      hora, minuto, TempoLigado, Porta);
            Log.append("Enviado: " + saida);
            Log.setCaretPosition(Log.getDocument().getLength());
            output.write(saida.getBytes());
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(Comunicador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
//-----------------------------------------------------------------------------------------------------    
    public void AgendarDiario(int hora, int minuto, int TempoLigado, int Porta)
    {
        try 
        {
            String saida=String.format("C%02d%02d%010d%02d\n", hora, minuto, TempoLigado, Porta);
            Log.append("Enviado: " + saida);
            Log.setCaretPosition(Log.getDocument().getLength());
            output.write(saida.getBytes());
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(Comunicador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
//-----------------------------------------------------------------------------------------------------        
}
