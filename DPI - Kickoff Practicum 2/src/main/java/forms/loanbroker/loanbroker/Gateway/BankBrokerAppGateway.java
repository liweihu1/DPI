package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import utilities.BankInterestSerializer;
import utilities.Constants;

public class BankBrokerAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway bankInterestReceiver;
    private BankInterestSerializer bankInterestSerializer;

    public BankBrokerAppGateway(){
        sender = new MessageSenderGateway();
        bankInterestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REPLY);
        bankInterestSerializer = new BankInterestSerializer();

        bankInterestReceiver.setListener(message -> {
            BankInterestReply reply = bankInterestSerializer.messageToBankInterestReply(message);
            onBankInterestReplyArrived(reply);
        });
    }

    public void onBankInterestReplyArrived(BankInterestReply reply) {
        sender.send(bankInterestSerializer.bankInterestReplyToMessage(reply));
    }
}
