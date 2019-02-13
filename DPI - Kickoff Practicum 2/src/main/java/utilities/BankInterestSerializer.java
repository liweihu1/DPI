package utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanRequest;

import java.io.IOException;

public class BankInterestSerializer {
    private ObjectMapper objectMapper;

    public BankInterestSerializer(){
        this.objectMapper = new ObjectMapper();
    }

    public String bankInterestRequestToString(BankInterestRequest request) {
        try {
            return this.objectMapper.writeValueAsString(request);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String bankInterestReplyToString(BankInterestReply reply) {
        try {
            return this.objectMapper.writeValueAsString(reply);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BankInterestRequest stringToBankInterestRequest(String jsonString){
        try {
            return this.objectMapper.readValue(jsonString, BankInterestRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BankInterestReply stringToBankInterestReply(String jsonString){
        try {
            return this.objectMapper.readValue(jsonString, BankInterestReply.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BankInterestRequest loanRequestToBankInterestRequest(LoanRequest request){
        return new BankInterestRequest(request.getSsn(), request.getAmount(), request.getTime());
    }
}
