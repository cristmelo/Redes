package httpStreaming;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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


public class Cliente {
	public final static Logger logger = Logger.getLogger(Cliente.class.toString());
    //Informa��es do client
	String serverName;
	int socketPort;
	Socket clientSocket;
	BufferedReader inputServer;
	BufferedWriter outputServer;
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
        playButton.addActionListener(new playButtonListener());
        pauseButton.addActionListener(new pauseButtonListener());
        tearButton.addActionListener(new tearButtonListener());
        
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
    
    
    //Pede pro servidor um arquivo de video
    public void askForVideo(String videoFileName){
    	
    }
    
    //------------------------------------
    //Handler for buttons
    //------------------------------------
    
    //.............
    //TO COMPLETE
    //.............
    
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
                c.getURIRawContent("setup/" + c.videoFileName);
				logger.info("Enviou msg!");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
        }    
    }
    
    
    
    
    //Handler for Play button
    //-----------------------
    class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            logger.info("Play Button pressed !");
            
        }
    }
    
    
    //Handler for Pause button
    //-----------------------
    class pauseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            
            logger.info("Pause Button pressed !");
                               
        }
    }
    
    //Handler for Teardown button
    //-----------------------
    class tearButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            
            logger.info("Teardown Button pressed !");
        }
    }
    
    
    //------------------------------------
    //Handler for timer
    //------------------------------------
    
    class timerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
        }
    }
    
    
    public String getURIRawContent(String path) throws UnknownHostException, IOException {
    	
		try {
			// Abre a conexão
			//PrintWriter out = new PrintWriter(outputServer, true);
		
			// Envia a requisição
			outputServer.write("GET " + path + " " + HTTP_VERSION + CRLF);
			outputServer.write("Host: " + this.serverName + CRLF);
			outputServer.write("Connection: Close"+ CRLF);
			outputServer.flush();
			/*
			boolean loop = true;
			StringBuffer sb = new StringBuffer();
		
			// recupera a resposta quando ela estiver disponível
			while (loop) {
				if (inputServer.ready()) {
					int i = 0;
					while ((i = inputServer.read()) != -1) {
						sb.append((char) i);
					}
					loop = false;
				}
			}*/
			return "";//sb.toString();
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
        logger.info(cliente.getURIRawContent("cad"));
    }
}


