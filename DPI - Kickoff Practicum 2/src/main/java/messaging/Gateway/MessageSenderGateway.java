package messaging.Gateway;

import org.apache.camel.Producer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;

public class MessageSenderGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private Producer producer;

    public void messageSenderGateway(String channelName){

    }

    public Message createTextMessage(String body){
        return null;
    }

    public void send(Message msg){

    }
}
