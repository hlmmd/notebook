import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

//一个聊天服务器只能有一个manager，要单例化处理
public class ClientManager {
    java.util.Random random = null;

    String[] strs = { "月下饮茶，念卿天涯", "世间安得双全法，不负如来不负卿", "寒灯纸上梨花雨凉，我等风雪又一年", "君之喜怒哀乐即吾之春夏秋冬", "三生有幸遇见你，纵使悲凉也是情",
            "落花倾兮，美御美兮，见卿一刻，胜花美矣", "人间纵有百媚千红，唯独你是情之所钟", "与君相向转相亲，与君双栖共一身", "温山软水繁星千万，不及你眉眼半分", "愿有岁月可回首，且以深情共白头",
            "风止于秋水，我止于你", "初见乍然，久处仍怦然", "愿我如星君如月，夜夜流光相皎洁", "金凤玉露一相逢，便胜却人间无数", "相思相见知何日，此时此夜难为情", "云想衣裳花想容，春风拂槛露华浓",
            "回眸一笑百媚生，六宫粉黛无颜色" };

    private ClientManager() {
        random = new java.util.Random();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                synchronized (vector) {
                    for (int i = 0; i < vector.size(); i++) {
                        ClientSocket csTemp = vector.get(i);

                        int index = random.nextInt(strs.length);

                        csTemp.sendmsg(strs[index] + "\n");
                    }
                }
                System.out.println("当前连接客户端数目：" + ClientManager.getChatManager().ClientNumber());
            }
        }, 1000, 5000);
    }

    private static final ClientManager CM = new ClientManager();

    public static ClientManager getChatManager() {
        return CM;
    }

    private Vector<ClientSocket> vector = new Vector<ClientSocket>();

    public int ClientNumber() {
        synchronized (vector) {
            return vector.size();
        }
    }

    /* 增加ChatSocket 实例到vector中 */
    public void add(ClientSocket cs) {
        synchronized (vector) {
            vector.add(cs);
        }

    }

    /* 删除断开连接的client */
    public void remove(ClientSocket cs) {
        synchronized ((vector)) {
            vector.remove(cs);
        }
    }

    /*
     * 发布消息给其他客户端 ChatSocket cs： 调用publish的线程 msg：要发送的信息
     */
    public void publish(ClientSocket cs, String msg) {
        for (int i = 0; i < vector.size(); i++) {
            ClientSocket csTemp = vector.get(i);
            if (!cs.equals(csTemp)) {
                // csTemp.sendmsg(msg+"\n");//不用发送给自己。
            }
        }
    }

    //
    // /*查看客户端连接是否正常
    // */
    // public void checkonline(){
    // for (int i = 0; i < vector.size(); i++) {
    //
    // ClientSocket csTemp = vector.get(i);
    // if (isSocketClose((csTemp))) {
    // csTemp.sendmsg("客户端退出");//不用发送给自己。
    // vector.remove((csTemp));
    // }
    // }
    // }

}
