package forms.loanbroker.loanbroker;

import messaging.MessageBroker;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;
import mix.model.loan.LoanReply;
import mix.model.loan.LoanRequest;
import utilities.Constants;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class LoanBrokerFrame extends JFrame implements MessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;

    private Connection connection;
    private Session session;
    private Destination loanRequestDestination;
    private Destination bankInterestReplyDestination;
    private MessageConsumer loanRequestConsumer;
    private MessageConsumer bankInterestReplyConsumer;

    private MessageBroker broker;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);
		initLoanBroker();
	}

	private void initLoanBroker(){
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put((Constants.LOAN_REQUEST_QUEUE), Constants.LOAN_REQUEST);
            props.put((Constants.BANK_INTEREST_REPLY_QUEUE), Constants.BANK_INTEREST_REPLY);
            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            loanRequestDestination = (Destination) jndiContext.lookup(Constants.LOAN_REQUEST);
            bankInterestReplyDestination = (Destination) jndiContext.lookup(Constants.BANK_INTEREST_REPLY);

            loanRequestConsumer = session.createConsumer(loanRequestDestination);
            bankInterestReplyConsumer = session.createConsumer(bankInterestReplyDestination);

            loanRequestConsumer.setMessageListener(this);
            bankInterestReplyConsumer.setMessageListener(this);
            broker = MessageBroker.getInstance();
            connection.start();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
	
	 private JListLine getRequestReply(LoanRequest request){
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	}

    private JListLine getRequestReplyBySsn(int ssn){
        for (int i = 0; i < listModel.getSize(); i++){
            JListLine rr =listModel.get(i);
            if (rr.getLoanRequest().getSsn() == ssn){
                return rr;
            }
        }

        return null;
    }
	
	public void add(LoanRequest loanRequest){		
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}

    @Override
    public void onMessage(Message message) {
        try {
            if (message.getStringProperty(Constants.REQUEST_TYPE).equals(Constants.BANK_INTEREST_REPLY)) {
                int ssn = Integer.parseInt(message.getJMSCorrelationID());
                String quoteId = message.getStringProperty(Constants.BANK_NAME);
                double interest = message.getDoubleProperty(Constants.INTEREST);
                LoanReply reply = new LoanReply();
                reply.setQuoteID(quoteId);
                reply.setInterest(interest);
                reply.setSsn(ssn);
                broker.sendMessage(new RequestReply(null, reply), Constants.LOAN_REPLY);

                getRequestReplyBySsn(Integer.parseInt(message.getJMSCorrelationID())).setBankReply(new BankInterestReply(ssn, interest, quoteId));
                list.repaint();
            } else {
                int ssn = message.getIntProperty(Constants.SSN);
                int amount = message.getIntProperty(Constants.AMOUNT);
                int time = message.getIntProperty(Constants.TIME);
                add(new LoanRequest(ssn, amount, time));

                BankInterestRequest request = new BankInterestRequest();
                request.setSsn(ssn);
                request.setAmount(amount);
                request.setTime(time);
                RequestReply requestReply = new RequestReply(request, null);
                broker.sendMessage(requestReply, Constants.BANK_INTEREST_REQUEST);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
