package httpStreaming;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import udpStreaming.RTPpacket;


public class Cliente {
	public final static Logger logger = Logger.getLogger(Cliente.class.toString());
    //Informa��es do client
	String serverName;
	int socketPort;
	
	Socket clientSocket;
	BufferedReader inputServer;
	BufferedWriter outputServer;
	
	Socket videoDataSocket;
	InputStream dataInputServer;
	OutputStream dataOutputServer;
	boolean isAlreadySetup=false;
	
	final static String CRLF = "\r\n";

	
	
    //Informa�oes do video
    String videoFileName;
    
	
	//Vers�o do protocolo utilizada
	public final static String HTTP_VERSION = "HTTP/1.1";
	
	//GUI
    //----
    JFrame f = new JFrame("Client");
    JButton setupButton = new JButton("Setup");
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton tearButton = new JButton("Teardown");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel iconLabel = new JLabel();
    ImageIcon icon;
    
    Timer timer; //timer used to receive data from the UDP socket
    byte[] buf; //buffer used to store data received from the server
	
    
    
    public Cliente(String serverName, int socketPort) {        
        //Informa��es do cliente
    	this.serverName = serverName;
    	this.socketPort = socketPort;
    	
    	//build GUI
        //--------------------------
        //Frame
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        //Buttons
        buttonPanel.setLayout(new GridLayout(1,0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);
        setupButton.addActionListener(new setupButtonListener(this));
        playButton.addActionListener(new playButtonListener(this));
        pauseButton.addActionListener(new pauseButtonListener(this));
        tearButton.addActionListener(new tearButtonListener(this));
        
        //Image display label
        iconLabel.setIcon(null);
        
        //frame layout
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0,0,380,280);
        buttonPanel.setBounds(0,280,380,50);
        
        f.getContentPane().add(mainPanel, BorderLayout.CENTER);
        f.setSize(new Dimension(390,370));
        f.setVisible(true);
        
        //init timer
        //--------------------------
        timer = new Timer(20, new timerListener());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
        buf = new byte[15000];

    } 

    //--
    public void makeConnectionWithServer(){
    	try {
			clientSocket = new Socket(this.serverName, this.socketPort);
			inputServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outputServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "DeuRuim:", e );
		}
    }
    
    public void closeConnectionWithServer(){
    	try {
			clientSocket.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE,"Deu ruim ao fechar conexão com servidor",e);
		}
    }
    //--
    
    //--
    public void makeDataConnection(){
    	try {
			videoDataSocket = new Socket(this.serverName, this.socketPort+1);
			dataInputServer = videoDataSocket.getInputStream();
			dataOutputServer = videoDataSocket.getOutputStream();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "DeuRuim:", e );
		}
    }
    
    public void closeDataConnection(){
    	try {
			videoDataSocket.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE,"Deu ruim ao fechar conexão com servidor",e);
		}
    }
    
    
    //--
    
    
    //------------------------------------
    //Handler for buttons
    //------------------------------------
    
    
    //Handler for Setup button
    //-----------------------
    class setupButtonListener implements ActionListener{
    	Cliente c;
    	setupButtonListener(Cliente c){
    		this.c = c;
    	}
    	
        public void actionPerformed(ActionEvent e){
            logger.info("Setup Button pressed !");
            try {
            	if(!isAlreadySetup){
            		c.getURIRawContent("setup/" + c.videoFileName);
            		makeDataConnection();
            		isAlreadySetup = true;
            	}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
        }    
    }
    
    
    
    
    //Handler for Play button
    //-----------------------
    class playButtonListener implements ActionListener {
    	Cliente c;
    	playButtonListener(Cliente c){
    		this.c = c;
    	}
        public void actionPerformed(ActionEvent e){
            logger.info("Play Button pressed !");
            try {
            	if(isAlreadySetup){
            		c.getURIRawContent("play/");
            		c.timer.start();
            	}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
    
    
    //Handler for Pause button
    //-----------------------
    class pauseButtonListener implements ActionListener {
    	Cliente c;
    	public pauseButtonListener(Cliente c){
    		this.c = c;
    	}
        public void actionPerformed(ActionEvent e){
            logger.info("Pause Button pressed !");
            try {
            	if(isAlreadySetup){
            		c.getURIRawContent("pause/");
            		c.timer.stop();
            	}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}                   
        }
    }
    
    //Handler for Teardown button
    //-----------------------
    class tearButtonListener implements ActionListener {
    	Cliente c;
    	public tearButtonListener(Cliente c) {
    		this.c = c;
    	}
        public void actionPerformed(ActionEvent e){
            logger.info("Teardown Button pressed !");
            try {
                c.getURIRawContent("teardown/");
                c.closeConnectionWithServer();
                c.closeDataConnection();
                c.timer.stop();
                System.exit(0);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
        }
    }
    
    
    //------------------------------------
    //Handler for timer
    //------------------------------------
    
    class timerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	try{
        		dataInputServer.read(buf);
	            //get an Image object from the payload bitstream
	            Toolkit toolkit = Toolkit.getDefaultToolkit();
	            Image image = toolkit.createImage(buf, 0, buf.length);
	            
	            //display the image as an ImageIcon object
	            icon = new ImageIcon(image);
	            iconLabel.setIcon(icon);
        	
        	}
	        catch (InterruptedIOException iioe){
	            System.out.println("Nothing to read");
	        }
	        catch (IOException ioe) {
	            System.out.println("Exception caught: "+ioe);
	        }
        
        }
    }
    
    
    public String getURIRawContent(String path) throws UnknownHostException, IOException {
    	
		try {
		
			// Envia a requisição
			outputServer.write("GET " + path + " " + HTTP_VERSION + CRLF);
			outputServer.write("Host: " + this.serverName + CRLF);
			outputServer.write("Connection: Close"+ CRLF);
			outputServer.flush();

			return "";
		} catch(Exception e){
			return "";
		}
	}
    
    

	public static void main(String argv[]) throws Exception
    {
        //Create a Client object
        Cliente cliente = new Cliente(argv[0], Integer.parseInt(argv[1]));
        cliente.videoFileName = "movie.Mjpeg";
        cliente.makeConnectionWithServer();
        logger.info("Pronto");
    }
}


