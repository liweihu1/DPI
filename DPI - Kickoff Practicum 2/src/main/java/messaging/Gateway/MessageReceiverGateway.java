package messaging.Gateway;

import javax.jms.*;

public class MessageReceiverGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public MessageReceiverGateway(String channelName){

    }

    public void setListener(MessageListener listener){

    }
}
