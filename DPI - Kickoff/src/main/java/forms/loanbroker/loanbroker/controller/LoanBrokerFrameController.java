package forms.loanbroker.loanbroker.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import messaging.MessageBroker;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import utilities.Constants;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class LoanBrokerFrameController extends Application implements MessageListener {

    @FXML
    private ListView<RequestReply> lvRequestReply;

    private Scene scene;

    private Connection connection;
    private Session session;
    private Destination loanRequestDestination;
    private Destination bankInterestReplyDestination;
    private MessageConsumer loanRequestConsumer;
    private MessageConsumer bankInterestReplyConsumer;

    private MessageBroker broker;

    public LoanBrokerFrameController() {
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put((Constants.LOAN_REQUEST_QUEUE), Constants.LOAN_REQUEST);
            props.put((Constants.BANK_INTEREST_REPLY_QUEUE), Constants.BANK_INTEREST_REPLY);
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            loanRequestDestination = (Destination) jndiContext.lookup(Constants.LOAN_REQUEST);
            bankInterestReplyDestination = (Destination) jndiContext.lookup(Constants.BANK_INTEREST_REPLY);

            loanRequestConsumer = session.createConsumer(loanRequestDestination);
            bankInterestReplyConsumer = session.createConsumer(bankInterestReplyDestination);

            loanRequestConsumer.setMessageListener(this);
            bankInterestReplyConsumer.setMessageListener(this);
            broker = MessageBroker.getInstance();
            connection.start();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.LOAN_BROKER_FRAME_VIEW)));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Loan Broker");
        primaryStage.show();
    }

    @Override
    public void onMessage(Message message) {
        Platform.runLater(() -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REPLY)) {
                    LoanReply reply = new LoanReply();
                    reply.setQuoteID(message.getStringProperty(Constants.BANK_NAME));
                    reply.setInterest(message.getDoubleProperty(Constants.INTEREST));
                    reply.setSsn(Integer.parseInt(message.getJMSCorrelationID()));
                    RequestReply receivingRR = this.lvRequestReply.getItems().stream().filter(rr -> {
                        try {
                            return Integer.parseInt(message.getJMSCorrelationID()) == (((BankInterestRequest)rr.getRequest()).getSsn());
                        } catch (JMSException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }).findAny().orElse(null);
                    if (receivingRR != null) {
                        receivingRR.setReply(reply);
                        lvRequestReply.refresh();
                    }
                    broker.sendMessage(new RequestReply(null, reply), Constants.LOAN_REPLY);
                } else {
                    BankInterestRequest request = new BankInterestRequest();
                    request.setSsn(Integer.parseInt(message.getJMSCorrelationID()));
                    request.setAmount(message.getIntProperty(Constants.AMOUNT));
                    request.setTime(message.getIntProperty(Constants.TIME));
                    RequestReply requestReply = new RequestReply(request, null);
                    System.out.println(requestReply);
                    broker.sendMessage(requestReply, Constants.BANK_INTEREST_REQUEST);
                    addRequestToList(requestReply);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void addRequestToList(RequestReply request) {
        this.lvRequestReply.getItems().add(request);
    }
}
