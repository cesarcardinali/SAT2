package views.secondarypanes;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import core.SharedObjs;


@SuppressWarnings("serial")
public class NotificationDialog extends JDialog
{
	private final JPanel contentPanel = new JPanel();
	private JTextPane    txtpnCheckingAllThe;
	
	public NotificationDialog(String title, String text)
	{
		setType(Type.POPUP);
		setBackground(UIManager.getColor("Panel.background"));
		setTitle(title);
		getContentPane().setBackground(UIManager.getColor("Panel.background"));
		setBounds(100, 100, 732, 271);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(UIManager.getColor("Panel.background"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		txtpnCheckingAllThe = new JTextPane();
		txtpnCheckingAllThe.setEditable(false);
		txtpnCheckingAllThe.setBackground(UIManager.getColor("Panel.background"));
		txtpnCheckingAllThe.setPreferredSize(new Dimension(300, 100));
		txtpnCheckingAllThe.setMinimumSize(new Dimension(300, 100));
		txtpnCheckingAllThe.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtpnCheckingAllThe.setText(text);
		
		contentPanel.add(txtpnCheckingAllThe);
		setLocationRelativeTo(SharedObjs.satFrame);
		
		setModal(true);
	}
	
	// Getters and Setters
	public void setText(String text)
	{
		txtpnCheckingAllThe.setText(text);
	}
	
	public String getText()
	{
		return txtpnCheckingAllThe.getText();
	}
}
