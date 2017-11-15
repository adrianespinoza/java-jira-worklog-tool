package jira.tool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import jira.tool.connector.JiraConnector;
import jira.tool.main.Log;
import jira.tool.util.Constants;
import jira.tool.util.JiraHelper;
import jira.tool.util.UiHelper;

public class JiraUI {

	private JFrame frmJiraTool;
	private JTextField tfUsername;
	private JPasswordField pfPassword;
	private JTextField tfJiraUrl;
	private JTable tableOutput;
	private JTextField tfAssignees;
	
	private JLabel lblMessages;
	
	private JDateChooser dcWorklogDate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JiraUI window = new JiraUI();
					window.frmJiraTool.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JiraUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJiraTool = new JFrame();
		frmJiraTool.setTitle("JIRA Worklog Tool");
		frmJiraTool.setBounds(0, 59, 806, 572);
		frmJiraTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmJiraTool.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblUsername = new JLabel("Username*");
		lblUsername.setBounds(250, 45, 74, 14);
		
		tfUsername = new JTextField();
		tfUsername.setBounds(334, 42, 201, 20);
		tfUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password*");
		lblPassword.setBounds(250, 76, 72, 14);
		
		pfPassword = new JPasswordField();
		pfPassword.setBounds(334, 73, 201, 20);
		
		JLabel lblJiraUrl = new JLabel("Jira Url*");
		lblJiraUrl.setBounds(250, 14, 72, 14);
		
		JLabel lblAssignee = new JLabel("Assignee*");
		lblAssignee.setBounds(250, 162, 72, 14);
		
		lblMessages = new JLabel("");
		lblMessages.setForeground(Color.RED);
		lblMessages.setBounds(10, 234, 770, 21);
		
		tfJiraUrl = new JTextField();
		tfJiraUrl.setText("https://agile.jira.com");
		tfJiraUrl.setBounds(334, 11, 201, 20);
		tfJiraUrl.setColumns(10);
		
		tableOutput = new JTable();
		tableOutput.setBounds(10, 266, 578, 97);
		
		JScrollPane jScrollPane1 = new JScrollPane(tableOutput);
		jScrollPane1.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		jScrollPane1.setBounds(10, 266, 770, 256);
		
		JLabel lblNewLabel = new JLabel("Worklog Date*");
		lblNewLabel.setBounds(250, 127, 83, 14);
		
		dcWorklogDate = new JDateChooser();
		dcWorklogDate.setDateFormatString(Constants.DATE_FORMAT);
		dcWorklogDate.setBounds(334, 121, 201, 20);
		
		JButton btnGenerateReport = new JButton("Generate Report");
		btnGenerateReport.addActionListener(createReportButtonActionListener());
		btnGenerateReport.setBounds(250, 200, 285, 23);
		
		panel.setLayout(null);
		panel.add(lblUsername);
		panel.add(tfUsername);
		panel.add(lblPassword);
		panel.add(pfPassword);
		panel.add(lblJiraUrl);
		panel.add(tfJiraUrl);
		panel.add(jScrollPane1);
		panel.add(lblNewLabel);
		panel.add(dcWorklogDate);
		panel.add(btnGenerateReport);	
		panel.add(lblAssignee);
		
		tfAssignees = new JTextField();
		tfAssignees.setBounds(334, 159, 201, 20);
		panel.add(tfAssignees);
		tfAssignees.setColumns(10);		
		
		panel.add(lblMessages);
	}
	
	private boolean isValidForm()
	{
		return tfJiraUrl.getText() != null && 
				tfUsername.getText() != null && 
				pfPassword.getText() != null && 
				tfAssignees.getText() != null && 
				dcWorklogDate.getDate() != null;
	}
	
	private ActionListener createReportButtonActionListener() {
		return new ActionListener() {
			
			
			public void actionPerformed(ActionEvent arg0) 
			{	
				lblMessages.setText("");
				
				if(!isValidForm()) 
				{
					lblMessages.setText("Please fill the required fields.");
					
					return;
				}
				
				try {
					DefaultTableModel dtm = new DefaultTableModel(0, 0);

					// add header of the table
					String header[] = new String[] { "Key", "Author", "Time Spent", "Created", "Summary" };
					// add header in table model     
					dtm.setColumnIdentifiers(header);
					tableOutput.setModel(dtm);
					
					String url = tfJiraUrl.getText() + "/rest/api/2/search";
					String method = Constants.POST;
					String assignees = tfAssignees.getText();
					String username = tfUsername.getText();
					String password = pfPassword.getText();
					String worklogDate = JiraHelper.getDateFormatted(dcWorklogDate.getDate());
					String requestBody = JiraHelper.buildWorklogRequestBody(assignees, worklogDate);
					
					JiraConnector connector = new JiraConnector(url, username, password);
					String jsonString = connector.doRequest(method, requestBody);
					
					if(jsonString != null && jsonString.contains("Error:"))
					{
						lblMessages.setText(jsonString);
						return;
					}
					
					String warnings = JiraHelper.checkWorklogWarnings(jsonString);
					if(warnings != null) 
					{
						lblMessages.setText(warnings);
					}
					
					UiHelper.setWorklogTableModelRows(dtm, jsonString, worklogDate);
					
				} catch (ParseException e) {
					Log.error("JIRA Tool", e.getMessage());
					e.printStackTrace();
				}
			}
		};
	}
}
