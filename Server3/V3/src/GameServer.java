import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private ServerSocket ss;
    private int numPlayers;
    private int maxPlayers;
    private Socket p1Solect;
    private Socket p2Solect;
    private  ReadFromClient p1ReadRunnble;
    private ReadFromClient p2ReadRunnble;
    private WriteToClient p1WriteRunnble;
    private WriteToClient p2WriteRunnble;

    private double p1x ,p1y ,p2x,p2y;
    public GameServer(){
        System.out.println("==========Game SERVER =============");
        numPlayers = 0;
        maxPlayers =2;

        p1x = 100;
        p1y = 350;
        p2x = 490;
        p2y = 350;
        try {
            ss = new ServerSocket(12345);
        }catch (IOException ex){
            System.out.println("IOException from gameSerVer");
        }
    }
    public void acceptConnections(){
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers<maxPlayers){
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("player #"+numPlayers +"has connected.");

                ReadFromClient rfc = new ReadFromClient(numPlayers,in);
                WriteToClient wtc = new WriteToClient(numPlayers,out);

                if (numPlayers ==1){
                    p1Solect =s;
                    p1ReadRunnble = rfc;
                    p1WriteRunnble = wtc;
                }else{
                    p2Solect =s;
                    p2ReadRunnble =rfc;
                    p2WriteRunnble =wtc;
                    p1WriteRunnble.sendStartMsg();
                    p2WriteRunnble.sendStartMsg();

                    Thread readTharead1 = new Thread(p1ReadRunnble);
                    Thread readTharead2 = new Thread(p2ReadRunnble);
                    readTharead1.start();
                    readTharead2.start();
                    Thread writeThread1 = new Thread(p1WriteRunnble);
                    Thread writeThread2 = new Thread(p2WriteRunnble);
                    writeThread1.start();
                    writeThread2.start();
                }
            }

            System.out.println("No longer accepting");
        }catch (IOException ex){
            System.out.println("IOException from gameSerVer");
        }
    }

    private class ReadFromClient implements Runnable{
        private  int playerID;
        private DataInputStream daataint;
        public  ReadFromClient(int pid,DataInputStream in){
            playerID = pid;
            daataint = in;
            System.out.println("RFC"+playerID+"Runable createn");
        }
        @Override
        public void run() {
            try {
                while (true){
                    if (playerID==1){
                        p1x = daataint.readDouble();
                        p1y = daataint.readDouble();
                    }else{
                        p2x = daataint.readDouble();
                        p2y = daataint.readDouble();
                    }
                }

            }catch (IOException ex){
                System.out.println("XX");
            }
        }
    }

    private class WriteToClient implements Runnable{
        private  int playerID;
        private DataOutputStream dataout;
        public  WriteToClient(int pid,DataOutputStream outputStream){
            playerID = pid;
            dataout = outputStream;
            System.out.println("WTC"+playerID+"Runable createn");
        }
        @Override
        public void run() {
            try {
                while (true){
                    if (playerID==1){
                        dataout.writeDouble(p2x);
                        dataout.writeDouble(p2y);
                        dataout.flush();
                    }else{
                        dataout.writeDouble(p1x);
                        dataout.writeDouble(p1y);
                        dataout.flush();
                    }
                    Thread.sleep(25);
                }
            }catch (IOException ex){
                System.out.println("XX");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        public void sendStartMsg(){
            try {
                dataout.writeUTF("We now have 2 palyer Go");

            }catch (Exception ex){

            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
