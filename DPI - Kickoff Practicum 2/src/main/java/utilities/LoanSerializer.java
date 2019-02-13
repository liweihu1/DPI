package utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import mix.model.bank.BankInterestReply;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;

import java.io.IOException;

public class LoanSerializer {

    private ObjectMapper objectMapper;

    public LoanSerializer(){
        this.objectMapper = new ObjectMapper();
    }


    public String loanRequestToJsonString(LoanRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String loanReplyToJsonString(LoanReply reply) {
        try {
            return objectMapper.writeValueAsString(reply);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoanRequest jsonStringToLoanRequest(String jsonString){
        try {
            return objectMapper.readValue(jsonString, LoanRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoanReply jsonStringToLoanReply(String jsonString){
        try {
            return objectMapper.readValue(jsonString, LoanReply.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoanReply bankInterestReplyToLoanReply(BankInterestReply reply){
        return new LoanReply(reply.getInterest(), reply.getQuoteId());
    }
}
