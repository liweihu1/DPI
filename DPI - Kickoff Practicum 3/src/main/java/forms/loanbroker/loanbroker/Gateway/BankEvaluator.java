package forms.loanbroker.loanbroker.Gateway;

import mix.model.bank.BankInterestRequest;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import utilities.Constants;

public class BankEvaluator {
    private Evaluator evaluator;

    public BankEvaluator(){
        this.evaluator = new Evaluator();
    }

    public boolean evaluateBank(BankInterestRequest request, String bank) throws EvaluationException {
        // set values of variables amount and time
        evaluator.putVariable("amount", Integer.toString(request.getAmount()));
        evaluator.putVariable("time", Integer.toString(request.getTime()));

        switch(bank){
            case Constants.ABN:
                String ABN_AMRO = "#{amount} >= 200000 && #{amount} <= 300000  && #{time} <= 20";
                return evaluator.evaluate(ABN_AMRO).equals("1.0"); // 1.0 means TRUE, otherwise it is FALSE
            case Constants.ING:
                String ING = "#{amount} <= 100000 && #{time} <= 10";
                return evaluator.evaluate(ING).equals("1.0");
            case Constants.RABO:
                String RABO_BANK = "#{amount} <= 250000 && #{time} <= 15";
                return evaluator.evaluate(RABO_BANK).equals("1.0"); // 1.0 means TRUE, otherwise it is FALSE
            default:
                return false;
        }
    }
}
