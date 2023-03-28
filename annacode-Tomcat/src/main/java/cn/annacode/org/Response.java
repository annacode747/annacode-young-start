package cn.annacode.org;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Response extends AbstractHttpServletResponse {

    private int statue = 200;
    private String message = "OK";
    private final byte SP = ' ';
    private final byte CR = '\r';
    private final byte LF = '\n';

    private ResponseServletOutputStream responseServletOutputStream = new ResponseServletOutputStream();
    private Map<String, String> headers = new HashMap<>();
    private Request request;
    private OutputStream socketOutputStream;

    public Response(Request request) {
        this.request = request;
        try {
            this.socketOutputStream = request.getSocket().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setStatus(int sc, String sm) {
        statue = sc;
        message = sm;
    }

    @Override
    public int getStatus() {
        return statue;
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public ResponseServletOutputStream getOutputStream() throws IOException {
        return responseServletOutputStream;
    }

    public void complete() {
        // 发送相应
        try {
            sendResponseLine();
            sendResponseHeader();
            sendResponseBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendResponseBody() throws IOException {
        socketOutputStream.write(getOutputStream().getBytes());
    }


    private void sendResponseHeader() throws IOException {
        if (!headers.containsKey("Content-Length")) {
            addHeader("Content-Length", String.valueOf(getOutputStream().getPos()));
        }
        if (!headers.containsKey("Content-Type")) {
            addHeader("Content-Type", "text/plain;charset=utf-8");
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            socketOutputStream.write(key.getBytes());
            socketOutputStream.write(':');
            socketOutputStream.write(value.getBytes());
            socketOutputStream.write(CR);
            socketOutputStream.write(LF);
        }
        socketOutputStream.write(CR);
        socketOutputStream.write(LF);
    }

    private void sendResponseLine() throws IOException {
        // 相应行
        socketOutputStream.write(request.getProtocol().getBytes());
        socketOutputStream.write(SP);
        socketOutputStream.write(statue);
        socketOutputStream.write(SP);
        socketOutputStream.write(message.getBytes());
        socketOutputStream.write(CR);
        socketOutputStream.write(LF);
    }
}
