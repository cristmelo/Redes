public void serve() {
    ServerSocket serverSocket = null;
 
    logger.info("Iniciando servidor no endereço: " + this.host
            + ":" + this.port);
 
    try {
        // Cria a conexão servidora
        serverSocket = new ServerSocket(port, 1,
                InetAddress.getByName(host));
    } catch (IOException e) {
        logger.log(Level.SEVERE, "Erro ao iniciar servidor!", e);
        return;
    }
    logger.info("Conexão com o servidor aberta no endereço: " + this.host
            + ":" + this.port);
 
    // Fica esperando pela conexão cliente
    while (true) {
        logger.info("Aguardando conexões...");
        Socket socket = null;
        InputStream input = null;
        OutputStream output = null;
        try {
            socket = serverSocket.accept();
            input = socket.getInputStream();
            output = socket.getOutputStream();
 
            // Realiza o parse da requisição recebida
            String requestString = convertStreamToString(input);
            logger.info("Conexão recebida. Conteúdo: " + requestString);
            Request request = new Request();
            request.parse(requestString);
 
            // recupera a resposta de acordo com a requisicao
            Response response = ResponseFactory.createResponse(request);
            String responseString = response.respond();
            logger.info("Resposta enviada. Conteúdo: " + responseString);
            output.write(responseString.getBytes());
 
            // Fecha a conexão
            socket.close();
 
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao executar servidor!", e);
            continue;
        }
    }
}