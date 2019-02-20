package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;
import utilities.BankInterestSerializer;
import utilities.Constants;
import utilities.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;

public class LoanBrokerAppGateway {
    private MessageSenderGateway sender;
    private LoanSerializer loanSerializer;
    private BankInterestSerializer bankInterestSerializer;

    protected LoanBrokerAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REQUEST, Constants.BANK_INTEREST_REQUEST_QUEUE);
        MessageReceiverGateway loanRequestReceiver = new MessageReceiverGateway(Constants.LOAN_REQUEST, Constants.LOAN_REQUEST_QUEUE);
        loanSerializer = new LoanSerializer();
        bankInterestSerializer = new BankInterestSerializer();

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

    public void onLoanRequestArrived(LoanRequest request) {
        sendBankRequestToBank(bankInterestSerializer.loanRequestToBankInterestRequest(request), String.valueOf(request.getId()));
    }

    private void sendBankRequestToBank(BankInterestRequest request, String id){
        String jsonString = bankInterestSerializer.bankInterestRequestToString(request);
        Message message = sender.createMessageWithContent(Constants.BANK_INTEREST_REQUEST_JSON_STRING, jsonString, id, Constants.BANK_INTEREST_REQUEST);
        sender.send(message);
    }
}
