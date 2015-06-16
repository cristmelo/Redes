package httpStreaming.response;

import httpStreaming.request.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SuccessResponse implements Response{
	private Request request;

	protected static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

	public SuccessResponse(Request request) {
		this.request = request;
	}
	
	@Override
	public String respond() {
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 200 OK").append("\r\n");
		sb.append("Date: ").append(HTTP_DATE_FORMAT.format(new Date())).append("\r\n");
		sb.append("Server: localhost").append("\r\n");
		sb.append("Connection: Keep-Alive").append("\r\n");
		sb.append("Content-Type: text; charset=UTF-8").append("\r\n");
		sb.append("\r\n");
		sb.append("Success Response").append("\r\n");
		return sb.toString();

	}

}
