package httpStreaming.response;

import httpStreaming.request.Request;

/**
 * Fábrica de respostas
 */
public class ResponseFactory {
	public static Response createResponse(Request request, boolean deuCerto) {
		if(deuCerto){
			return new SuccessResponse(request);
		}else{
			return new FailureResponse(request);
		}
	}
}
