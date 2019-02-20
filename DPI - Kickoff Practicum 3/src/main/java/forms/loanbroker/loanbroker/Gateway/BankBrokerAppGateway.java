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

public class BankBrokerAppGateway {
    private MessageSenderGateway sender;
    private BankInterestSerializer bankInterestSerializer;

    protected BankBrokerAppGateway(){
        sender = new MessageSenderGateway(Constants.LOAN_REPLY, Constants.LOAN_REPLY_QUEUE);
        MessageReceiverGateway bankInterestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REPLY, Constants.BANK_INTEREST_REPLY_QUEUE);
        bankInterestSerializer = new BankInterestSerializer();
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
        //
    }

    public void convertLoanRequestAndSend(LoanRequest request){
        sendBankRequestToBank(bankInterestSerializer.loanRequestToBankInterestRequest(request), String.valueOf(request.getId()));
    }

    private void sendBankRequestToBank(BankInterestRequest request, String id){
        String jsonString = bankInterestSerializer.bankInterestRequestToString(request);
        Message message = sender.createMessageWithContent(Constants.BANK_INTEREST_REQUEST_JSON_STRING, jsonString, id, Constants.BANK_INTEREST_REQUEST);
        sender.send(message);
    }
}
