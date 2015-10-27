package server;
import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class WebSocketServer {
    
	// This group will be joined to all WebSocket client connections 
	// So as to enable easy communication to all
    private final ChannelGroup group = new DefaultChannelGroup();
    private final int udpPort;
    private final int port;

    public WebSocketServer(int port, int udpPort) {
        this.port = port;
        this.udpPort = udpPort;
    }

    /**
     * Start the server.
     * 
     */
    public void startUp() {
        // Prepare the UDP / datagram Channel in the start
    	//
        ConnectionlessBootstrap udpBootstrap = new ConnectionlessBootstrap(new NioDatagramChannelFactory());

        // Put the WebSocketBroadcastPipelineFactory the sending UDP
        // By taking messages to the WebSocket clients
        udpBootstrap.setPipelineFactory(new WebSocketBroadcastPipelineFactory(group));

        // Accepts binding the sockets of the UDP messages
        udpBootstrap.bind(new InetSocketAddress(udpPort));
        
        // Prepare the Channel at the start
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory());

        // Put the WebSocketPipelineFactory did the editing of HTTP
        // and WebSocket requests will takeover
        bootstrap.setPipelineFactory(new WebSocketPipelineFactory(group));

        // Bind the socket which is now ready to receive requests to take
        bootstrap.bind(new InetSocketAddress(port));

    }

    public static void main(String[] args) {
        int wsPort;
        int udpPort;
        if (args.length < 2) {
            wsPort = 8888;
            udpPort = 49153;
        } else {
            wsPort = Integer.parseInt(args[0]);
            udpPort = Integer.parseInt(args[1]);
        }
        new WebSocketServer(wsPort, udpPort).startUp();
    }
}