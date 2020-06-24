import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String args[])throws Exception{
        Socket socket = new Socket("127.0.0.1", 8888);
        socket.setOOBInline(false);
        System.out.println("连接成功");

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream() ));
        while(true){

            String line = br.readLine();
            if(line!=null)
                System.out.println(line);
            else
                break;
        }
        System.out.println("server down");
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        PrintWriter pw = new PrintWriter(socket.getOutputStream());
//        while(true){
//            pw.println(br.readLine());
//          pw.flush();
//        }
    }
}