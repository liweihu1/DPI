package forms.loanclient.client.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.Constants;
import utilities.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;

public class LoanClientAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private LoanSerializer serializer;

    public LoanClientAppGateway(){
        sender = new MessageSenderGateway(Constants.BANK_INTEREST_REQUEST);
        receiver = new MessageReceiverGateway(Constants.LOAN_REPLY);
        serializer = new LoanSerializer();

        receiver.setListener(message -> {
            try {
                serializer.jsonStringToLoanReply(message.getStringProperty(Constants.LOAN_REPLY_JSON_STRING));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void applyForLoan(LoanRequest request){
        String jsonLoanRequest = serializer.loanRequestToJsonString(request);
        Message message = sender.createMessageWithContent(Constants.LOAN_REQUEST_JSON_STRING, jsonLoanRequest, String.valueOf(request.getSsn()));
        sender.send(message);
    }

    public void onLoanReplyArrived(LoanRequest request, LoanReply reply){

    }

}
