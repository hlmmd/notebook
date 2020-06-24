import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.Socket;

/*每一个连上的客户端，服务器都有一个线程为之服务*/
public class ClientSocket extends Thread {
    Socket socket;
    private  boolean exit = false;

    public ClientSocket(Socket s) {
        this.socket = s;

    }

    //发送数据
    public void sendmsg(String out) {
        if(out==null || out.length()==0)
            return ;
        try {

            //socket.getOutputStream().write(out.getBytes("UTF-8"));
            socket.getOutputStream().write(out.getBytes());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("客户断开");
            exit = true;
            ClientManager.getChatManager().remove(this);
         //   e.printStackTrace();
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @return
     */
    public Boolean isSocketClose(){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            exit = true;
            ClientManager.getChatManager().remove(this);
            return true;
        }
    }

    //服务器会不断地从客户端读取内容，把读取到的内容发给集合内所有的客户端。
    public void run() {
        try {

            while(!exit)
            {

            }

//            //接收数据
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(
//                            socket.getInputStream(), "UTF-8"));
//            String line=null;
//            //发送读到的内容
//            while (true) {
//                if(isSocketClose())
//                {
//
//                    break;
//                }
//                if((line = br.readLine())!=null){
//
//                    System.sendmsg.println(line);
//                    ClientManager.getChatManager().publish(this, line);
//                }
//            }
//            br.close();
        //} catch (IOException e) {
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}