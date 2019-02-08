package utilities;

public final class Constants {
    public static final String JMS_BANK_FRAME_VIEW = "/views/JMSBankFrame.fxml";
    public static final String LOAN_BROKER_FRAME_VIEW = "/views/LoanBrokerFrame.fxml";
    public static final String LOAN_CLIENT_FRAME_VIEW = "/views/LoanClientFrame.fxml";

    public static final String SSN = "ssn";
    public static final String AMOUNT = "amount";
    public static final String TIME = "amount";
    public static final String INTEREST = "interest";
    public static final String BANK_NAME = "bankName";

    public static final String BANK_INTEREST_REPLY = "BankInterestReply";
    public static final String BANK_INTEREST_REQUEST = "BankInterestRequest";
    public static final String LOAN_REQUEST = "LoanRequest";
    public static final String LOAN_REPLY = "LoanReply";

    public static final String BANK_INTEREST_REPLY_QUEUE = "queue.BankInterestReply";
    public static final String BANK_INTEREST_REQUEST_QUEUE = "queue.BankInterestRequest";
    public static final String LOAN_REQUEST_QUEUE = "queue.LoanRequest";
    public static final String LOAN_REPLY_QUEUE = "queue.LoanReply";

    public static final String REQUEST_TYPE = "requestType";
    public static final String REQUEST_TYPE_BANK = "bankInterest";
    public static final String REQUEST_TYPE_LOAN = "loan";
    public static final String REQUEST_TYPE_LOAN_REPLY = "loanReply";
    public static final String REQUEST_TYPE_BANK_REPLY = "bankReply";
}
