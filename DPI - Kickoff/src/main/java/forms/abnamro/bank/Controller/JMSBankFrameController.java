package forms.abnamro.bank.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import messaging.MessageBroker;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import utilities.Constants;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMSBankFrameController extends Application implements MessageListener {
    @FXML
    private ListView<RequestReply> lvRequestReply;

    @FXML
    private TextField tfAmount;

    private Connection connection;
    private Session session;
    private Destination receiveDestination;
    private MessageConsumer consumer;

    private MessageBroker broker;

    private Scene scene;

    private ObservableList<RequestReply> itemList;

    public JMSBankFrameController() {
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put((Constants.BANK_INTEREST_REQUEST_QUEUE), Constants.BANK_INTEREST_REQUEST);
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            receiveDestination = (Destination) jndiContext.lookup(Constants.BANK_INTEREST_REQUEST);
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
        scene = new Scene(FXMLLoader.load(getClass().getResource(Constants.JMS_BANK_FRAME_VIEW)));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank");
        primaryStage.show();
    }

    @FXML
    public void onBtnSendReplyClick(ActionEvent event) {
        RequestReply selectedItem = lvRequestReply.getSelectionModel().getSelectedItem();
        if (!tfAmount.getText().isEmpty() && selectedItem != null && checkText(tfAmount.getText())) {
            BankInterestReply reply = new BankInterestReply();
            reply.setInterest(Double.parseDouble(tfAmount.getText()));
            reply.setQuoteId("abn");
            reply.setSsn(((BankInterestRequest)selectedItem.getRequest()).getSsn());
            selectedItem.setReply(reply);
            lvRequestReply.refresh();
            broker.sendMessage(new RequestReply(null, reply), Constants.BANK_INTEREST_REPLY);
        }
    }

    @Override
    public void onMessage(Message message) {
        Platform.runLater(() -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REQUEST)) {
                    BankInterestRequest request = new BankInterestRequest();
                    request.setSsn(message.getIntProperty(Constants.SSN));
                    request.setAmount(message.getIntProperty(Constants.AMOUNT));
                    request.setTime(message.getIntProperty(Constants.TIME));
                    RequestReply requestReply = new RequestReply(request, null);
                    addMessageToList(requestReply);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void addMessageToList(RequestReply request){
        itemList.add(request);
    }

    public boolean checkText(String text){
        return text.matches("^\\d+(\\.\\d+)?");
    }
}
