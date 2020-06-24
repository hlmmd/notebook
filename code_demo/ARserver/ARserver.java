import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ARserver {
    public static void main(String[] args) {
        try {
            System.out.println("启动服务器");
            //创建server
            ServerSocket serverSocket = new ServerSocket(8888);
            ClientManager.getChatManager();

            while (true) {

                // block
                Socket socket = serverSocket.accept();
                // 建立链接
                System.out.println("客户已连接"+socket.getRemoteSocketAddress());
                //将socket传递给新的线程
                ClientSocket cs= new ClientSocket(socket);
                cs.start();
                ClientManager.getChatManager().add(cs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
