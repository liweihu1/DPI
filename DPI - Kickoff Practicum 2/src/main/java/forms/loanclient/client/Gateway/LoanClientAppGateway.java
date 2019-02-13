package forms.loanclient.client.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.LoanSerializer;

public class LoanClientAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    public void applyForLoan(LoanRequest request){

    }

    public void onLoanReplyArrived(LoanRequest request, LoanReply reply){

    }

}
