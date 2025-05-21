public class start
{
    public static void main(String[] args)
    {
        Server server = new Server();
        new Thread(server::onInitialize).start();
        System.out.println("Server is start.");
    }
}
