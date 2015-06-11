package httpStreaming;

import httpStreaming.request.Request;
import httpStreaming.response.Response;
import httpStreaming.response.ResponseFactory;

import java.awt.BorderLayout;
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
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.Receiver;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import udpStreaming.VideoStream;

public class Servidor extends JFrame implements ActionListener{
	
	private final static Logger logger = Logger.getLogger(Servidor.class.toString());
	
	//Atributos do servidor
	String hostname;
	int sourcePort;
	ServerSocket serverSocket;
	Socket socketClient;
	BufferedReader inputServer;
	BufferedWriter outputServer;
	
	
	//Video variables:
    //----------------
    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames
	
	
	//GUI
	JLabel label;
	Timer timer; 
	byte[] buf; //buffer used to store the images to send to the client
	
	

	Servidor(String hostname, int sourcePort){
        super("Server");	//Inicializa o frame
        
        //Inicializa informaï¿½ï¿½es do servidor
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
	
	//-- 
	public void listenSocket(){
		try{
			serverSocket = new ServerSocket(sourcePort, 1, InetAddress.getByName(hostname));
		}catch(IOException e){
			logger.log(Level.SEVERE, "Erro ao iniciar servidor!", e);
			return;
		}
		logger.info("Conexï¿½o com o servidor aberta no endereï¿½o: " + this.hostname + ":" + this.sourcePort);
	}
	
	public void stopListenSocket(){
		try {
			socketClient.close();
		} catch (IOException e) {
			logger.info("Deu erro no close client!");
		}
	}
	
	//--
	
	//--
	public void makeConnectionWithRequester(){
		
		try{
			socketClient = serverSocket.accept();
			inputServer = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		    outputServer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		}
		catch(Exception e){
			logger.log(Level.SEVERE, "Deu muito ruim!", e);
		}
		
		logger.info("Recebeu uma ConexÃ£o marota!");
	}
	
	public Request receiveRequest(){
		try {
		    String requestString = convertStreamToString();
			logger.info("ConexÃ£o recebida. ConteÃºdo:\n" + requestString);
			Request request = new Request();
			request.parse(requestString);
			return request;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Deu muito ruim!", e);
			return null;
		}
	}
	
	public void sendString(String packet){
		try {
			outputServer.write(packet);
			outputServer.flush();
		} catch (IOException e) {
			logger.info("Deu erro ao enviar!");
		}
	}
	
	public void closeServerConnectioWithClient(){
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			logger.info("Deu erro no close server!");
		}
	}
	//--
	
	
	//-- Utilitarias
	private String convertStreamToString() {
		String stringResultante = "";
		Writer writer = new StringWriter();
		char[] buffer = new char[2048];
		
		try {
			int i = inputServer.read(buffer);
			writer.write(buffer, 0, i);
			stringResultante = writer.toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao converter stream para string", e);
			return "";
		}
	
		return stringResultante;
	}
		
	//Algo Interessante: Enquanto o timer estiver rodando esta função vai ser chamada, então so precisamos controlar o timer para 
	//controlar o envio.
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Eae");
		
	}
	
	//--

	
	
	//-- isSetupRequest, isStartRequest, isPauseRequest, isTearDownRequest
	
	public static boolean isSetupRequest(Request request){
		String[] uri = request.getUri().split("/");
		logger.info("Aqui: "+uri[0]);
		if(uri[0].compareTo("setup")==0 && uri.length > 1)
			return true;		
		else
			return false;
	}
	
	
	private static boolean isStartRequest(Request requestReceived) {
		return false;
	}
	
	
	private static boolean isPauseRequest(Request requestReceived) {
		return false;
	}
	
	
	private static boolean isTeardownRequest(Request requestReceived) {
		return false;
	}
	
	
	//--
	
	public static void main(String[] args) throws Exception {
		//show GUI:
		Servidor server = new Servidor(args[0],Integer.parseInt(args[1]));
		
		
		//Coisas Da GUI
		//-------
		server.pack();
		server.setVisible(true);
		//-------
		Request requestReceived;
		Response responseBuilted;
		
		String responseString;
		String tipoDaRequisicao;
		String videoFileName;
		
		
		server.listenSocket(); //Inicializa a conexao TCP
		server.makeConnectionWithRequester(); 
				
		//Espera pelo requisição setup/nomeDoVideo
		
		
		
		boolean recebeuSetupRequest = false;
		boolean	recebeuTearDownRequest = false;
		while(!recebeuTearDownRequest){
			
			requestReceived = server.receiveRequest();
			responseBuilted = ResponseFactory.createResponse(requestReceived);
			responseString = responseBuilted.respond();
			server.sendString(responseString);
			
			if(!recebeuSetupRequest){
				if(isSetupRequest(requestReceived)){
					try{
						videoFileName = requestReceived.getUri().split("/")[1];
						server.video = new VideoStream(videoFileName);
						recebeuSetupRequest = true;
					}catch(Exception e){
						recebeuSetupRequest = false;
					}
				}
				else if(isStartRequest(requestReceived)){
					server.timer.start();
				}
				else if(isPauseRequest(requestReceived)){
					server.timer.stop();
				}
				else if(isTeardownRequest(requestReceived)){
					recebeuTearDownRequest = true;
					server.timer.stop();
				}
				else{
					
				}
			}
		
		

		//logger.info("Acabou");
		//server.closeClientSocket();
		
		//server.closeServer();
		//server.dispose();
		
		
		}
	}

}