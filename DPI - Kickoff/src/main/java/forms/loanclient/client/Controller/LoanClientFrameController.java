package forms.loanclient.client.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import messaging.MessageBroker;
import mix.messaging.requestreply.RequestReply;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.Constants;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class LoanClientFrameController extends Application implements MessageListener {

    @FXML
    private TextField tfSsn;
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfTime;
    @FXML
    private ListView<RequestReply> lvRequestReply;

    private Connection connection;
    private Session session;
    private Destination receiveDestination;
    private MessageConsumer consumer;

    private MessageBroker broker;

    private Scene scene;

    public LoanClientFrameController(){
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put((Constants.LOAN_REPLY_QUEUE), Constants.LOAN_REPLY);
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            receiveDestination = (Destination) jndiContext.lookup(Constants.LOAN_REPLY);
            consumer = session.createConsumer(receiveDestination);
            connection.start();

            consumer.setMessageListener(this);

            broker = MessageBroker.getInstance();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.LOAN_CLIENT_FRAME_VIEW)));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Loan Client");
        primaryStage.show();
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.LOAN_REPLY)) {
                LoanReply reply = new LoanReply();
                reply.setInterest(message.getDoubleProperty(Constants.INTEREST));
                reply.setQuoteID(message.getStringProperty(Constants.BANK_NAME));
                updateRequestReply(reply, Integer.parseInt(message.getJMSCorrelationID()));
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onButtonSendLoanRequestClick(ActionEvent actionEvent) {
        if (checkFields()) {
            LoanRequest request = new LoanRequest();
            request.setSsn(Integer.parseInt(tfSsn.getText()));
            request.setAmount(Integer.parseInt(tfAmount.getText()));
            request.setTime(Integer.parseInt(tfTime.getText()));
            RequestReply requestReply = new RequestReply(request, null);
            broker.sendMessage(requestReply, Constants.LOAN_REQUEST);
            addItemToListView(requestReply);
        }
    }

    public void addItemToListView(RequestReply requestReply){
        Platform.runLater(() -> {
            if (lvRequestReply != null){
                lvRequestReply.getItems().add(requestReply);
            }
        });
    }

    public void updateRequestReply(LoanReply reply, int correlationId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RequestReply receivingRR = lvRequestReply.getItems().stream().filter(rr -> correlationId == (((LoanRequest) rr.getRequest()).getSsn())).findAny().orElse(null);
                if (receivingRR != null) {
                    receivingRR.setReply(reply);
                    lvRequestReply.refresh();
                }
            }
        });
    }

    public boolean checkFields(){
        String ssn = tfSsn.getText();
        String amount = tfAmount.getText();
        String time = tfTime.getText();
        String regex = "^\\d+(\\.\\d+)?";

        RequestReply receivingRR = this.lvRequestReply.getItems().stream().filter(rr -> Integer.parseInt(ssn) == (((LoanRequest)rr.getRequest()).getSsn())).findAny().orElse(null);
        return !ssn.isEmpty() && !amount.isEmpty() && !time.isEmpty() && ssn.matches(regex) && amount.matches(regex) && time.matches(regex) && receivingRR == null;
    }
}
