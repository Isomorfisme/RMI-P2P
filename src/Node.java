import java.rmi.RemoteException;

public class Node extends NodeFunctionalities implements Runnable{
    //args[0] -> port; args[1] -> port to connect, args[2] -> IP to connect

    public Node(String[] args){
        String[] params = args;
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {
        Node node = new Node(args);
        Thread t1 = new Thread(node, "threadServer");//pass the runnable interference object and set the thread name
        t1.start();
        if (args.length > 1) {  //There is a port to connect
            Thread t2 = new Thread(node, "threadClient");//pass the runnable interference object and set the thread name
            t2.start();
        }



       /* while (true){
            Thread.sleep(5000);
            System.out.println("Node will list all files from all registered nodes");
            node.listFiles();*/
    }

    @Override
    public void run() {
        while (true) {
            String threadName = Thread.currentThread().getName();//get the current thread's name
            Node node = Thread.currentThread().;
            if (threadName.equals("threadServer")) {
                try{
                    node.startServer(args);
                }catch (RemoteException e){
                    System.err.println("Server exception: " + e.toString()); e.printStackTrace();
                }
            } else if (threadName.equals("threadClient")) {
                try{
                    node.connectToServer(args);
                }catch (RemoteException e){
                    System.err.println("Server exception: " + e.toString()); e.printStackTrace();
                }
            }
        }
    }
}



