package httpStreaming;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Cliente {
	public final static Logger logger = Logger.getLogger(Cliente.class.toString());
    //Informações do client
	String serverName;
	int socketPort;
	Socket clientSocket;
    InputStream inputServer;
    OutputStream outputServer;
    
    
    //Informaçoes do video
    String videoFileName;
    
	
	//Versão do protocolo utilizada
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
        //Informações do cliente
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
        setupButton.addActionListener(new setupButtonListener());
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

  //------------------------------------
    //Handler for buttons
    //------------------------------------
    
    //.............
    //TO COMPLETE
    //.............
    
    //Handler for Setup button
    //-----------------------
    class setupButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            
            logger.info("Setup Button pressed !");
            
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

    
    public static void main(String argv[]) throws Exception
    {
        //Create a Client object
        Cliente cliente = new Cliente(argv[0], Integer.parseInt(argv[1]));
        

    }
}


