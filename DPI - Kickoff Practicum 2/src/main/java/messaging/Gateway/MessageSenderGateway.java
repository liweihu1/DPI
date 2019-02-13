package messaging.Gateway;


import javax.jms.*;

public class MessageSenderGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;

    public MessageSenderGateway(String channelName){

    }

    public Message createMessageWithContent(String property, String propertyValue, String correlationId){
        try {
            Message message = session.createMessage();
            message.setStringProperty(property, propertyValue);
            message.setJMSCorrelationID(correlationId);
            return message;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void send(Message msg){
        try {
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
