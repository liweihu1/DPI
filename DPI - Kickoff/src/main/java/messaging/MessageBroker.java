package messaging;

import jdk.nashorn.internal.runtime.JSONFunctions;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

public final class MessageBroker {
    private static MessageBroker messageBroker;

    private Connection connection; // to connect to the JMS
    private Session session; // session for creating consumers

    private Destination receiveDestination; //reference to a queue/topic destination
    private Destination sendDestination;
    private Properties props;

    private Context jndiContext;

    public MessageBroker(String queueName) {
        try {
            props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            session.createQueue(queueName);

            // connect to the receiver destination
            receiveDestination = (Destination) jndiContext.lookup("DankBank");
            // connect to the sender destination
            sendDestination = (Destination) jndiContext.lookup("DankBank");

            connection.start(); // this is needed to start receiving messages
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(RequestReply rr) {
        try {
            MessageProducer producer = session.createProducer(sendDestination);
            // create a text message
            Message msg = convertRequestReplyToMessage(rr);
            // send the message
            producer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        try {
            MessageConsumer consumer = session.createConsumer(receiveDestination);
            consumer.setMessageListener(new CustomMessageListener());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Message convertRequestReplyToMessage(RequestReply reply) {
        try {
            if (reply.getRequest() != null) {
                Message message = session.createMessage();
                if (reply.getRequest() instanceof LoanRequest) {
                    message.setStringProperty("type", "LOAN");
                    message.setIntProperty("ssn", ((LoanRequest)reply.getRequest()).getSsn());
                    message.setIntProperty("amount", ((LoanRequest)reply.getRequest()).getAmount());
                    message.setIntProperty("time", ((LoanRequest)reply.getRequest()).getTime());
                    message.setJMSReplyTo(sendDestination);
                } else {
                    message.setStringProperty("type", "BANK");
                    message.setIntProperty("amount", ((BankInterestRequest)reply.getRequest()).getAmount());
                    message.setIntProperty("time", ((BankInterestRequest)reply.getRequest()).getTime());

                }
                return message;
            }
            return null;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    public RequestReply convertMessageToRequestReply(Message message){
        try {
            if (message.getStringProperty("type").contains("BANK")) {

            } else {

            }
            return null;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MessageBroker getInstance(String queueName){
        if (messageBroker == null) {
            messageBroker = new MessageBroker(queueName);
        }
        return messageBroker;
    }
}
