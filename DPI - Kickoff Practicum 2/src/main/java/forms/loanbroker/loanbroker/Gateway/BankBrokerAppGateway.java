package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.loan.LoanReply;
import utilities.BankInterestSerializer;
import utilities.Constants;
import utilities.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;

public class BankBrokerAppGateway {
    private MessageSenderGateway sender;
    private BankInterestSerializer bankInterestSerializer;
    private LoanSerializer loanSerializer;

    protected BankBrokerAppGateway(){
        sender = new MessageSenderGateway(Constants.LOAN_REPLY, Constants.LOAN_REPLY_QUEUE);
        MessageReceiverGateway bankInterestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REPLY, Constants.BANK_INTEREST_REPLY_QUEUE);
        bankInterestSerializer = new BankInterestSerializer();
        loanSerializer = new LoanSerializer();

        bankInterestReceiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REPLY)){
                    String jsonString = message.getStringProperty(Constants.BANK_INTEREST_REPLY_JSON_STRING);
                    onBankInterestReplyArrived(bankInterestSerializer.stringToBankInterestReply(jsonString), message.getJMSCorrelationID());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void onBankInterestReplyArrived(BankInterestReply reply, String id) {
        sendLoanReplyToLoanClient(loanSerializer.bankInterestReplyToLoanReply(reply), id);
    }

    private void sendLoanReplyToLoanClient(LoanReply reply, String id){
        Message message = sender.createMessageWithContent(Constants.LOAN_REPLY_JSON_STRING, loanSerializer.loanReplyToJsonString(reply), id, Constants.LOAN_REPLY);
        sender.send(message);
    }
}
