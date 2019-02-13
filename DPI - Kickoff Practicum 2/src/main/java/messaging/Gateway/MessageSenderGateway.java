package messaging.Gateway;


import javax.jms.*;

public class MessageSenderGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public void messageSenderGateway(String channelName){

    }

    public Message createTextMessage(String body){
        return null;
    }

    public void send(Message msg){

    }
}
