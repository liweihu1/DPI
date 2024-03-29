package mix.model.loan;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * This class stores all information about a
 * request that a client submits to get a loan.
 *
 */
public class LoanRequest implements Serializable {

    private UUID id;
    private int ssn; // unique client number.
    private int amount; // the ammount to borrow
    private int time; // the time-span of the loan

    public LoanRequest() {
        super();
        this.ssn = 0;
        this.amount = 0;
        this.time = 0;
    }

    public LoanRequest(UUID id, int ssn, int amount, int time) {
        super();
        this.id = id;
        this.ssn = ssn;
        this.amount = amount;
        this.time = time;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ssn=" + String.valueOf(ssn) + " amount=" + String.valueOf(amount) + " time=" + String.valueOf(time);
    }
}
