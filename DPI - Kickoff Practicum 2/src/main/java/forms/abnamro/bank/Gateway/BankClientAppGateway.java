package forms.abnamro.bank.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import utilities.BankInterestSerializer;
import utilities.Constants;

public class BankClientAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private BankInterestSerializer serializer;

    public BankClientAppGateway(){
        sender = new MessageSenderGateway();
        receiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REQUEST);
        serializer = new BankInterestSerializer();

        receiver.setListener(message -> {

        });
    }

    public void returnBankInterestReply(BankInterestRequest request, BankInterestReply reply){
        // TODO  send request
    }

    public void onBankInterestRequestArrived(BankInterestRequest request){
        // TODO Do something
    }
}
