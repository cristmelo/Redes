package httpStreaming.response;

import httpStreaming.request.Request;

/**
 * Fábrica de respostas
 */
public class ResponseFactory {
	/**
	 * Retorna a resposta adequada ao request
	 * @param request request
	 * @return resposta de acordo com o request
	 */
	public static Response createResponse(Request request) {
		// TODO: Colocar outros tipos de response. Ex: FileResponse, DBResponse,
		// CacheResponse

		return new DummyResponse(request);
	}
}
