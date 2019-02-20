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
    private LoanSerializer serializer;

    protected LoanClientAppGateway(){
        sender = new MessageSenderGateway(Constants.LOAN_REQUEST, Constants.LOAN_REQUEST_QUEUE);
        MessageReceiverGateway receiver = new MessageReceiverGateway(Constants.LOAN_REPLY, Constants.LOAN_REPLY_QUEUE);
        serializer = new LoanSerializer();

        receiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.LOAN_REPLY)){
                    LoanReply reply = serializer.jsonStringToLoanReply(message.getStringProperty(Constants.LOAN_REPLY_JSON_STRING));
                    onLoanReplyArrived(reply, message.getJMSCorrelationID());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    public void applyForLoan(LoanRequest request){
        String jsonLoanRequest = serializer.loanRequestToJsonString(request);
        Message message = sender.createMessageWithContent(Constants.LOAN_REQUEST_JSON_STRING, jsonLoanRequest, String.valueOf(request.getId()), Constants.LOAN_REQUEST);
        sender.send(message);
    }

    public void onLoanReplyArrived(LoanReply reply, String requestId){
        //DO NOTHING
    }

}
