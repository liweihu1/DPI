package forms.bank.rabobank.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import utilities.BankInterestSerializer;
import utilities.Constants;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankClientAppGateway {
    private MessageSenderGateway sender;
    private BankInterestSerializer serializer;
    private Map<UUID, String> aggregationIds;

    protected BankClientAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REPLY, Constants.BANK_INTEREST_REPLY_QUEUE);
        MessageReceiverGateway receiver = new MessageReceiverGateway(Constants.RABO_BANK_INTEREST_REQUEST, Constants.RABO_BANK_INTEREST_REQUEST_QUEUE);
        serializer = new BankInterestSerializer();
        aggregationIds = new HashMap<>();

        receiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REQUEST)){
                    BankInterestRequest request = serializer.stringToBankInterestRequest(message.getStringProperty(Constants.BANK_INTEREST_REQUEST_JSON_STRING));
                    onBankInterestRequestArrived(request, message.getJMSCorrelationID());
                    aggregationIds.put(request.getId(), message.getStringProperty(Constants.GROUP_ID));
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void returnBankInterestReply(BankInterestReply reply, String id){
        String replyString = serializer.bankInterestReplyToString(reply);
        Message message = sender.createMessageWithContent(Constants.BANK_INTEREST_REPLY_JSON_STRING, replyString, id, Constants.BANK_INTEREST_REPLY, aggregationIds.get(UUID.fromString(id)));
        sender.send(message);
    }

    public void onBankInterestRequestArrived(BankInterestRequest request, String id){
        // DO NOTHING
    }
}
