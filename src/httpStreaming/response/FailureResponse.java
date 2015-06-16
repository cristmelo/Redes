package httpStreaming.response;

import httpStreaming.request.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FailureResponse implements Response{
	private Request request;

	protected static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

	public FailureResponse(Request request) {
		this.request = request;
	}
	
	@Override
	public String respond() {
		StringBuilder sb = new StringBuilder();
		// Cria primeira linha do status code, no caso sempre 200 OK
		sb.append("HTTP/1.1 400 BAD-REQUEST").append("\r\n");
		
		// Cria os cabeçalhos
		sb.append("Date: ").append(HTTP_DATE_FORMAT.format(new Date())).append("\r\n");
		sb.append("Server: localhost").append("\r\n");
		sb.append("Connection: Keep-Alive").append("\r\n");
		sb.append("Content-Type: text; charset=UTF-8").append("\r\n");
		sb.append("\r\n");
		sb.append("Failure Response").append("\r\n");
		return sb.toString();

	}
}
