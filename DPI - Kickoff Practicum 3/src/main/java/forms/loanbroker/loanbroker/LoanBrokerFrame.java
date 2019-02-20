package forms.loanbroker.loanbroker;

import forms.loanbroker.loanbroker.Gateway.BankBrokerAppGateway;
import forms.loanbroker.loanbroker.Gateway.LoanBrokerAppGateway;
import mix.model.bank.BankInterestReply;
import mix.model.loan.LoanRequest;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.UUID;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class LoanBrokerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<>();

    public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LoanBrokerFrame frame = new LoanBrokerFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
    private LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
        JPanel contentPane = new JPanel();
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

        JList<JListLine> list = new JList<>(listModel);
		scrollPane.setViewportView(list);

		initLoanBrokerGateways();
	}

    /**
     * Creates the gateways.
     */
    private void initLoanBrokerGateways(){
        new LoanBrokerAppGateway() {
            @Override
            public void onLoanRequestArrived(LoanRequest request) {
                super.onLoanRequestArrived(request);
                addLoanRequestToList(request);
            }
        };

        new BankBrokerAppGateway() {
            @Override
            public void onBankInterestReplyArrived(BankInterestReply reply, String id) {
                super.onBankInterestReplyArrived(reply, id);
                updateLoanRequestWithIdReply(reply, id);
            }
        };
    }

    private void updateLoanRequestWithIdReply(BankInterestReply reply, String id){
        Objects.requireNonNull(getRequestReplyById(id)).setBankReply(reply);
        repaint();
    }

	private JListLine getRequestReplyById(String id){
		for (int i = 0; i < listModel.getSize(); i++){
			JListLine rr =listModel.get(i);
			if (rr.getLoanRequest().getId().equals(UUID.fromString(id))){
				return rr;
			}
		}

		return null;
	}

	private void addLoanRequestToList(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));
		repaint();
	}
}
