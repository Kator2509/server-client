public class start
{
    public static void main(String[] args)
    {
        //Create new object Server.
        Server server = new Server();
        //Start object Server in new Thread. That new stream working and your program can continue work.
        new Thread(server::onInitialize).start();
        //Console log.
        System.out.println("Server is start.");
    }
}
