package forms.bank.rabobank;

import forms.bank.rabobank.Gateway.BankClientAppGateway;
import mix.messaging.requestreply.RequestReply;
import mix.model.bank.BankInterestReply;
import mix.model.bank.BankInterestRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JMSBankFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<>();

	private BankClientAppGateway bankClientAppGateway;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            try {
                JMSBankFrame frame = new JMSBankFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	}

	/**
	 * Create the frame.
	 */
	private JMSBankFrame() {
		setTitle("JMS Bank - Rabo Bank");
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
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		final JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<>(listModel);
		scrollPane.setViewportView(list);
		
		JLabel lblNewLabel = new JLabel("type reply");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		tfReply = new JTextField();
		GridBagConstraints gbc_tfReply = new GridBagConstraints();
		gbc_tfReply.gridwidth = 2;
		gbc_tfReply.insets = new Insets(0, 0, 0, 5);
		gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfReply.gridx = 1;
		gbc_tfReply.gridy = 1;
		contentPane.add(tfReply, gbc_tfReply);
		tfReply.setColumns(10);
		
		JButton btnSendReply = new JButton("send reply");
		btnSendReply.addActionListener(e -> {
            RequestReply<BankInterestRequest, BankInterestReply> rr = list.getSelectedValue();
            if (rr != null){
                double interest = Double.parseDouble((tfReply.getText()));
                BankInterestReply reply = new BankInterestReply(interest,"RABOBANK");
                rr.setReply(reply);
                list.repaint();
                bankClientAppGateway.returnBankInterestReply(reply, String.valueOf(rr.getRequest().getId()));
            }
        });
		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);

		initJMSBankFrameGateway();
	}

	private void initJMSBankFrameGateway() {
		bankClientAppGateway = new BankClientAppGateway(){
			@Override
			public void onBankInterestRequestArrived(BankInterestRequest request, String id) {
				super.onBankInterestRequestArrived(request, id);
				addRequestToList(request);
			}
		};
	}

	private void addRequestToList(BankInterestRequest request){
		RequestReply<BankInterestRequest, BankInterestReply> rr = new RequestReply<>(request, null);
		listModel.add(listModel.getSize(), rr);
		repaint();
	}
}
