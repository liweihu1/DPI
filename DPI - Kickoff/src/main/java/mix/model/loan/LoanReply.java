package mix.model.loan;

import java.io.Serializable;

/**
 *
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
public class LoanReply implements Serializable {

        private int ssn;
        private double interest; // the interest that the bank offers
        private String bankID; // the unique quote identification

    public LoanReply() {
        super();
        this.ssn = 0;
        this.interest = 0;
        this.bankID = "";
    }

    public LoanReply(int ssn, double interest, String quoteID) {
        super();
        this.ssn = ssn;
        this.interest = interest;
        this.bankID = quoteID;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getQuoteID() {
        return bankID;
    }

    public void setQuoteID(String quoteID) {
        this.bankID = quoteID;
    }
    
    @Override
    public String toString(){
        return " interest="+String.valueOf(interest) + " quoteID="+String.valueOf(bankID);
    }

}
