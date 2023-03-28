package cn.annacode.org;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketProcessor implements Runnable {

    private Socket socket;

    public SocketProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        processSocket(socket);
    }

    private void processSocket(Socket socket) {
        // 处理 Socket 连接
        try {
            InputStream inputStream = socket.getInputStream();

            byte[] bytes = new byte[1024];
            inputStream.read(bytes);

            // 解析字节流
            // 解析请求方式
            int pos = 0;
            int begin = 0, end = 0;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == ' ') break;
            }

            // 祝贺空格之前的字节流，转换成字符串就是请求方法
            StringBuffer method = new StringBuffer();
            for (; begin < end; begin++) {
                method.append((char) bytes[begin]);
            }
            System.out.print(method.toString() + " ");

            // 解析url
            pos++;
            begin++;
            end++;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == ' ') break;
            }
            StringBuffer url = new StringBuffer();
            for (; begin < end; begin++) {
                url.append((char) bytes[begin]);
            }
            System.out.print(url.toString() + " ");

            // 解析协议版本
            pos++;
            begin++;
            end++;
            for (; pos < bytes.length; pos++, end++) {
                if (bytes[pos] == '\r') break;
            }
            StringBuffer protocol = new StringBuffer();
            for (; begin < end; begin++) {
                protocol.append((char) bytes[begin]);
            }
            System.out.print(protocol.toString() + " ");

            Request request = new Request(method.toString(), url.toString(), protocol.toString(), socket);
            Response response = new Response(request);
            // 匹配 servlet
            Servlet servlet = new Servlet();
            servlet.service(request, response);

            // 发送响应
            response.complete();
        } catch (IOException e) {

        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
