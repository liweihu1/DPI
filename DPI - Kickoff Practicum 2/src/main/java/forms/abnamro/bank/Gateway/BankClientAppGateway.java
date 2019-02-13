package forms.abnamro.bank.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import utilities.BankInterestSerializer;
import utilities.Constants;

import javax.jms.JMSException;
import javax.jms.Message;

public class BankClientAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankInterestSerializer serializer;

    public BankClientAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REPLY, Constants.BANK_INTEREST_REPLY_QUEUE);
        receiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REQUEST, Constants.BANK_INTEREST_REQUEST_QUEUE);
        serializer = new BankInterestSerializer();

        receiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REQUEST)){
                    onBankInterestRequestArrived(serializer.stringToBankInterestRequest(message.getStringProperty(Constants.BANK_INTEREST_REQUEST_JSON_STRING)), message.getJMSCorrelationID());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void returnBankInterestReply(BankInterestReply reply, String id){
        String replyString = serializer.bankInterestReplyToString(reply);
        Message message = sender.createMessageWithContent(Constants.BANK_INTEREST_REPLY_JSON_STRING, replyString, id, Constants.BANK_INTEREST_REPLY);
        sender.send(message);
    }

    public void onBankInterestRequestArrived(BankInterestRequest request, String id){
        // DO NOTHING
    }
}
