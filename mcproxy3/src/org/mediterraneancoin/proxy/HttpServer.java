package org.mediterraneancoin.proxy;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.nio.NetworkTrafficSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.mediterraneancoin.proxy.net.RPCUtils;

/**
 *
 * @author test
 */
public class HttpServer {
    public static void main(String[] args) throws Exception
    {
        
        int localport;
        String hostname;
        int port;

        hostname = "localhost";
        port = 9372;
        
        String bindAddress = "localhost";
        localport = 8080;        

        int i = 0;
         

         while (i < args.length) {

             if (args[i].equals("-s")) {
                 i++;
                 hostname = args[i];
             } else if (args[i].equals("-p")) {
                 i++;
                 port = Integer.parseInt(args[i]);
             }  else if (args[i].equals("-b")) {
                 i++;
                 bindAddress = args[i];
             }  else if (args[i].equals("-l")) {
                 i++;
                 localport = Integer.parseInt(args[i]);
             } else if (args[i].equals("-h") || args[i].equals("--help")) {
                   System.out.println("parameters:\n" +
                           "-s: hostname of wallet/pool (default: localhost)\n" + 
                           "-p: port of wallet/pool (default: 9372)\n" + 
                           "-b: bind to local address (default: )\n" +
                           "-l: local proxy port (default: 8080)\n" + 
                           "-v: verbose"
                           );
                   return;                 
             } else if (args[i].equals("-v")) {
                 McproxyHandler.DEBUG = true;
             }
  
             i++;
         }       
        
        System.out.println("MediterraneanCoin Proxy3");
        System.out.println("parameters:\n" + 
                "wallet hostname: " + hostname + "\n" +
                "wallet port: " + port + "\n" +
                "bind to local address: " + bindAddress + "\n" +
                "local proxy port: " + localport + "\n"
                );
    
        /*
        Server server = new Server();
        AbstractConnector connector = new NetworkTrafficSelectChannelConnector(server);
        
        connector.setPort(Integer.getInteger("jetty.port",8080).intValue());
        connector.setThreadPool(new QueuedThreadPool(Integer.getInteger("jetty.threadpool.size",Runtime.getRuntime().availableProcessors()+1)));
        
        server.setConnectors(new Connector[]{connector});
        server.setHandler(webapp);
        server.start();
        server.join();        
        */
        
        HttpConfiguration config = new HttpConfiguration();
 
        
  
        QueuedThreadPool threadPool = new QueuedThreadPool(200,16);
        
        //ExecutorThreadPool threadPool = new ExecutorThreadPool(
        
        
        //threadPool.setMaxThreads(32);
        System.out.println( "threads: " + threadPool.getThreads() );
        
       
        // default port: 8080
        Server server = new Server(threadPool);
        
        //server.addBean(new ScheduledExecutorScheduler());
        server.manage(threadPool);
 
        
        ExecutorService pool;
        pool = Executors.  newFixedThreadPool(32);
    
        ServerConnector connector = new ServerConnector(server, 8, 8);
                //new ServerConnector(server, null, null, null, 16, 16, new HttpConnectionFactory(config));
    
                //
        connector.setHost(bindAddress);
        connector.setPort(localport);
        connector.setIdleTimeout(30000);
        connector.setStopTimeout(40000);
        
        System.out.println( "connector.getAcceptors(): " +  connector.getAcceptors() );
        System.out.println( "connector.getAcceptQueueSize(): " + connector.getAcceptQueueSize()) ;
 
        
        //server.setHandler(new McproxyHandler());
        
        ServletHandler handler = new ServletHandler();
        

        
        server.setHandler(handler);
        handler.addServletWithMapping(McproxyServlet.class, "/*");        
        
        McproxyServlet.hostname = hostname;
        McproxyServlet.localport = localport;
        McproxyServlet.port = port;
        
        McproxyServlet.url = new URL("http", hostname, port, "/");        
        McproxyServlet.utils = new RPCUtils(McproxyServlet.url, "", "");            
        
        server.addConnector(connector);
         
        
        
        
        McproxyHandler.url = new URL("http", hostname, port, "/");        
        McproxyHandler.utils = new RPCUtils(McproxyHandler.url, "", "");        
        
        
        server.start();
         
        server.join();
       
    }    
}
