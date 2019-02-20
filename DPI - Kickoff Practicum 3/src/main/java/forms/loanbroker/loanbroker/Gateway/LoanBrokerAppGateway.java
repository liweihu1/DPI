package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.BankInterestSerializer;
import utilities.Constants;
import utilities.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;

public class LoanBrokerAppGateway {
    private MessageSenderGateway sender;
    private LoanSerializer loanSerializer;

    protected LoanBrokerAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REQUEST, Constants.BANK_INTEREST_REQUEST_QUEUE);
        MessageReceiverGateway loanRequestReceiver = new MessageReceiverGateway(Constants.LOAN_REQUEST, Constants.LOAN_REQUEST_QUEUE);
        loanSerializer = new LoanSerializer();

        loanRequestReceiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.LOAN_REQUEST)){
                    String jsonString = message.getStringProperty(Constants.LOAN_REQUEST_JSON_STRING);
                    onLoanRequestArrived(loanSerializer.jsonStringToLoanRequest(jsonString));
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void convertBankReplyAndSendReply(BankInterestReply reply, String id){
        sendLoanReplyToLoanClient(loanSerializer.bankInterestReplyToLoanReply(reply), id);
    }

    private void sendLoanReplyToLoanClient(LoanReply reply, String id){
        Message message = sender.createMessageWithContent(Constants.LOAN_REPLY_JSON_STRING, loanSerializer.loanReplyToJsonString(reply), id, Constants.LOAN_REPLY);
        sender.send(message);
    }

    public void onLoanRequestArrived(LoanRequest request) {
        // ??
    }
}
