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
    private MessageReceiverGateway loanRequestReceiver;
    private LoanSerializer loanSerializer;
    private BankInterestSerializer bankInterestSerializer;

    public LoanBrokerAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REQUEST);
        loanRequestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REQUEST);
        loanSerializer = new LoanSerializer();
        bankInterestSerializer = new BankInterestSerializer();

        loanRequestReceiver.setListener(message -> {
            try {
                String jsonString = message.getStringProperty(Constants.LOAN_REQUEST_JSON_STRING);
                onLoanRequestArrived(loanSerializer.jsonStringToLoanRequest(jsonString));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void onLoanRequestArrived(LoanRequest request) {
        sendBankRequestToBank(bankInterestSerializer.loanRequestToBankInterestRequest(request));
    }

    public void sendBankRequestToBank(BankInterestRequest request){
        String jsonString = bankInterestSerializer.bankInterestRequestToString(request);
        Message message = sender.createMessageWithContent(Constants.BANK_INTEREST_REQUEST_JSON_STRING, jsonString, String.valueOf(request.getSsn()));
        sender.send(message);
    }
}
