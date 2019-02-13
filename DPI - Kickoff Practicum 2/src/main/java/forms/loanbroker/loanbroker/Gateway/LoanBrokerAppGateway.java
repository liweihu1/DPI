package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.loan.LoanRequest;
import utilities.Constants;
import utilities.LoanSerializer;

public class LoanBrokerAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway loanRequestReceiver;
    private LoanSerializer loanSerializer;

    public LoanBrokerAppGateway(){
        sender = new MessageSenderGateway();
        loanRequestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REQUEST);
        loanSerializer = new LoanSerializer();

        loanRequestReceiver.setListener(message -> {
            onLoanRequestArrived(loanSerializer.messageToLoanRequest(message));
        });
    }

    public void onLoanRequestArrived(LoanRequest request) {
        sender.send(loanSerializer.loanRequestToMessage(request));
    }
}
