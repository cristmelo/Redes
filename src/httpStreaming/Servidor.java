package httpStreaming;

import http.request.Request;
import http.response.Response;
import http.response.ResponseFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Servidor extends JFrame implements ActionListener{
	
	private final static Logger logger = Logger.getLogger(Servidor.class.toString());
	
	
	//Atributos do servidor
	String hostname;
	int sourcePort;

	ServerSocket serverSocket;
	Socket socketClient;
    InputStream inputServer;
    OutputStream outputServer;
	
	
	//GUI
	JLabel label;
	Timer timer; 
	byte[] buf; //buffer used to store the images to send to the client
	
	//Caracteristicas relacionadas a execu�ao do video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
	

	Servidor(String hostname, int sourcePort){
        super("Server");	//Inicializa o frame
        
        //Inicializa informa��es do servidor
        this.hostname = hostname;
        this.sourcePort = sourcePort;
        this.serverSocket = null;
        
        //init Timer
        timer = new Timer(FRAME_PERIOD, this);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
        buf = new byte[15000];
                
        //Handler to close the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //stop the timer and exit
                timer.stop();
                System.exit(0);
            }});
        
        //GUI:
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);

	}
	
	
	public void initServer(){
		try{
			serverSocket = new ServerSocket(sourcePort, 1, InetAddress.getByName(hostname));
		}catch(IOException e){
			logger.log(Level.SEVERE, "Erro ao iniciar servidor!", e);
			return;
		}
		logger.info("Conex�o com o servidor aberta no endere�o: " + this.hostname + ":" + this.sourcePort);
	}
	
	public void waitForConnections(){
		
		try{
			socketClient = serverSocket.accept();
			inputServer = socketClient.getInputStream();
		    outputServer = socketClient.getOutputStream();
			
		    //recebeu uma requisi��o
		    String requestString = convertStreamToString(inputServer);
			logger.info("Conexão recebida. Conteúdo:\n" + requestString);
			Request request = new Request();
			request.parse(requestString);
			
			//respondeu a requisi��o
			Response response = ResponseFactory.createResponse(request);
			String responseString = response.respond();
			logger.info("Resposta enviada. Conteudo:\n" + responseString);
			outputServer.write(responseString.getBytes());
			socketClient.close();
		}
		catch(Exception e){
			logger.log(Level.SEVERE, "Deu muito ruim!", e);
		}
		
		logger.info("Acabou!");
	}
	
	
	//Converte um Stream em uma String, eu sei que � meio obvio, mas � basicamente isso.
	private String convertStreamToString(InputStream inputStream) {
		String stringResultante = "";
		if (inputStream != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[2048];
			
			try {
				Reader reader = new BufferedReader(new InputStreamReader(inputStream));
				int i = reader.read(buffer);
				writer.write(buffer, 0, i);
				stringResultante = writer.toString();
				
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Erro ao converter stream para string", e);
				return "";
			}
		}
		return stringResultante;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void closeServer(){
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			logger.info("Deu erro no close server!");
		}
	};
	
	public static void main(String[] args) {
		//show GUI:
		Servidor server = new Servidor(args[0],Integer.parseInt(args[1]));
		//Coisas Da GUI
		//--
		server.pack();
		server.setVisible(true);
		//--
		
		
		server.initServer();	//Inicializa o servidor
		server.waitForConnections(); //Espera pra alguem fazer um request e da um respon
		server.closeServer();	//fecha o server
		server.dispose();	//fecha a janela

	}
}
