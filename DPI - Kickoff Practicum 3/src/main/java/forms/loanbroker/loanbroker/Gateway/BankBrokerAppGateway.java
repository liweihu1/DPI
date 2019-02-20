package forms.loanbroker.loanbroker.Gateway;

import messaging.Gateway.MessageReceiverGateway;
import messaging.Gateway.MessageSenderGateway;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;
import net.sourceforge.jeval.EvaluationException;
import utilities.BankInterestSerializer;
import utilities.Constants;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

public class BankBrokerAppGateway {
    private MessageSenderGateway raboSender;
    private MessageSenderGateway abnSender;
    private MessageSenderGateway ingSender;
    private BankInterestSerializer bankInterestSerializer;
    private BankEvaluator evaluator;
    private Aggregator aggregator;

    protected BankBrokerAppGateway(){
        raboSender = new MessageSenderGateway(Constants.RABO_BANK_INTEREST_REQUEST, Constants.RABO_BANK_INTEREST_REQUEST_QUEUE);
        abnSender = new MessageSenderGateway(Constants.ABN_BANK_INTEREST_REQUEST, Constants.ABN_BANK_INTEREST_REQUEST_QUEUE);
        ingSender = new MessageSenderGateway(Constants.ING_BANK_INTEREST_REQUEST, Constants.ING_BANK_INTEREST_REQUEST_QUEUE);
        MessageReceiverGateway bankInterestReceiver = new MessageReceiverGateway(Constants.BANK_INTEREST_REPLY, Constants.BANK_INTEREST_REPLY_QUEUE);
        bankInterestSerializer = new BankInterestSerializer();
        evaluator = new BankEvaluator();
        aggregator = new Aggregator();

        bankInterestReceiver.setListener(message -> {
            try {
                if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REPLY)){
                    String jsonString = message.getStringProperty(Constants.BANK_INTEREST_REPLY_JSON_STRING);
                    UUID aggregatorId = UUID.fromString(message.getStringProperty(Constants.GROUP_ID));
                    aggregator.getReplies().get(aggregatorId).add(bankInterestSerializer.stringToBankInterestReply(jsonString));
                    if (aggregator.getReplies().get(aggregatorId).size() == aggregator.getExpectedAmountForId(aggregatorId)){
                        checkBestBank(message.getJMSCorrelationID(), aggregatorId);
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

    private void checkBestBank(String id, UUID aggregationId){
        BankInterestReply bestReply = null;
        for(BankInterestReply br : aggregator.getReplies().get(aggregationId)){
            if (bestReply == null || br.getInterest() < bestReply.getInterest()){
                bestReply = br;
            }
        }
        onBankInterestReplyArrived(bestReply, id);
    }

    public void onBankInterestReplyArrived(BankInterestReply reply, String id) {
        //
    }

    public void convertLoanRequestAndSend(LoanRequest request){
        sendBankRequestToBank(bankInterestSerializer.loanRequestToBankInterestRequest(request), String.valueOf(request.getId()));
    }

    private void sendBankRequestToBank(BankInterestRequest request, String id){
        try {
            String jsonString = bankInterestSerializer.bankInterestRequestToString(request);
            UUID aggregationId = UUID.randomUUID();
            int amount = 0;
            Message message = abnSender.createMessageWithContent(Constants.BANK_INTEREST_REQUEST_JSON_STRING, jsonString, id, Constants.BANK_INTEREST_REQUEST, aggregationId.toString());
            if (evaluator.evaluateBank(request, Constants.ING)){
                ingSender.send(message);
                amount++;
            }

            if (evaluator.evaluateBank(request, Constants.ABN)){
                abnSender.send(message);
                amount++;
            }

            if (evaluator.evaluateBank(request, Constants.RABO)){
                raboSender.send(message);
                amount++;
            }
            aggregator.addNewExpectedAmount(aggregationId, amount);
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
    }
}
