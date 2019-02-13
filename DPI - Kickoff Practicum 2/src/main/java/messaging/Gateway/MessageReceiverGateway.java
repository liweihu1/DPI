package messaging.Gateway;

import org.apache.camel.Producer;

import javax.jms.*;

public class MessageReceiverGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private Producer producer;

    public MessageReceiverGateway(String channelName){

    }

    public void setListener(MessageListener listener){

    }
}
