package messaging;

import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.Constants;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public final class MessageBroker {
    private static MessageBroker messageBroker;

    private Connection connection; // to connect to the JMS
    private Session session; // session for creating consumers

    private Properties props;

    private Context jndiContext;

    public MessageBroker() {
        try {
            props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put((Constants.BANK_INTEREST_REQUEST_QUEUE), Constants.BANK_INTEREST_REQUEST);
            props.put((Constants.BANK_INTEREST_REPLY_QUEUE), Constants.BANK_INTEREST_REPLY);
            props.put((Constants.LOAN_REPLY_QUEUE), Constants.LOAN_REPLY);
            props.put((Constants.LOAN_REQUEST_QUEUE), Constants.LOAN_REQUEST);

            jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            connection.start(); // this is needed to start receiving messages
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(RequestReply rr, String destination) {
        try {
            Destination sendDestination = (Destination) jndiContext.lookup(destination);
            Message message = session.createMessage();
            if (destination.equals(Constants.LOAN_REQUEST)) {
                message.setStringProperty(Constants.REQUEST_TYPE, Constants.LOAN_REQUEST);
                message.setJMSCorrelationID(String.valueOf((((LoanRequest)rr.getRequest()).getSsn())));
                message.setIntProperty(Constants.SSN, (((LoanRequest)rr.getRequest()).getSsn()));
                message.setIntProperty(Constants.TIME, (((LoanRequest)rr.getRequest()).getTime()));
                message.setIntProperty(Constants.AMOUNT, (((LoanRequest)rr.getRequest()).getAmount()));
            } else if (destination.equals(Constants.BANK_INTEREST_REQUEST)){
                message.setStringProperty(Constants.REQUEST_TYPE, Constants.BANK_INTEREST_REQUEST);
                message.setJMSCorrelationID(String.valueOf((((BankInterestRequest)rr.getRequest()).getSsn())));
                message.setIntProperty(Constants.SSN, (((BankInterestRequest)rr.getRequest()).getSsn()));
                message.setIntProperty(Constants.AMOUNT, (((BankInterestRequest)rr.getRequest()).getAmount()));
                message.setIntProperty(Constants.TIME, (((BankInterestRequest)rr.getRequest()).getTime()));
            } else if (destination.equals(Constants.BANK_INTEREST_REPLY)){
                message.setStringProperty(Constants.REQUEST_TYPE, Constants.BANK_INTEREST_REPLY);
                message.setJMSCorrelationID(String.valueOf((((BankInterestReply)rr.getReply()).getSsn())));
                message.setDoubleProperty(Constants.INTEREST, (((BankInterestReply)rr.getReply()).getInterest()));
                message.setStringProperty(Constants.BANK_NAME, (((BankInterestReply)rr.getReply()).getQuoteId()));
            } else {
                message.setStringProperty(Constants.REQUEST_TYPE, Constants.LOAN_REPLY);
                message.setJMSCorrelationID(String.valueOf((((LoanReply)rr.getReply()).getSsn())));
                message.setDoubleProperty(Constants.INTEREST, (((LoanReply)rr.getReply()).getInterest()));
                message.setStringProperty(Constants.BANK_NAME, (((LoanReply)rr.getReply()).getQuoteID()));
            }
            MessageProducer producer = session.createProducer(sendDestination);
            producer.send(message);
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public static MessageBroker getInstance(){
        if (messageBroker == null) {
            messageBroker = new MessageBroker();
        }
        return messageBroker;
    }
}
