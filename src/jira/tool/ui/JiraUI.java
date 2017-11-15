package jira.tool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import jira.tool.util.IndexedFocusTraversalPolicy;
import jira.tool.util.JiraHelper;
import jira.tool.util.UiHelper;

public class JiraUI {

	private JFrame frmJiraTool;
	private JTextField tfUsername;
	private JPasswordField pfPassword;
	private JTextField tfJiraServer;
	private JTable tableOutput;
	private JTextField tfAssignee;	
	private JLabel lblMessages;	
	private JDateChooser dcWorklogDate;
	private JLabel lblConnectionSettings;
	private JList<String> listIssueTypes;
	private JList<String> listAssignees;	
	private JiraConnector connector;
	private Vector<String> vectorAssignees;

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
		frmJiraTool.setTitle("JIRA Worklog Tool - v1.1");
		frmJiraTool.setBounds(0, 59, 806, 572);
		frmJiraTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmJiraTool.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblUsername = new JLabel("Username*");
		lblUsername.setBounds(25, 68, 74, 14);
		
		tfUsername = new JTextField();
		tfUsername.setBounds(109, 65, 167, 20);
		tfUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password*");
		lblPassword.setBounds(25, 99, 72, 14);
		
		pfPassword = new JPasswordField();
		pfPassword.setBounds(109, 96, 167, 20);
		
		JLabel lblJiraUrl = new JLabel("Server*");
		lblJiraUrl.setBounds(25, 37, 72, 14);
		
		JLabel lblAssignee = new JLabel("Assignee*");
		lblAssignee.setToolTipText("Jira user name");
		lblAssignee.setBounds(337, 68, 62, 14);
		
		lblMessages = new JLabel("");
		lblMessages.setForeground(Color.RED);
		lblMessages.setBounds(10, 234, 770, 21);
		
		tfJiraServer = new JTextField();
		tfJiraServer.setText("https://agile.jira.com");
		tfJiraServer.setBounds(109, 34, 167, 20);
		tfJiraServer.setColumns(10);
		
		tableOutput = new JTable();
		tableOutput.setBounds(10, 266, 578, 97);
		
		JScrollPane jScrollPane1Report = new JScrollPane(tableOutput);
		jScrollPane1Report.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		jScrollPane1Report.setBounds(10, 266, 770, 256);
		
		JLabel lblNewLabel = new JLabel("Worklog Date*");
		lblNewLabel.setBounds(337, 37, 83, 14);
		
		dcWorklogDate = new JDateChooser();
		dcWorklogDate.setDateFormatString(Constants.DATE_FORMAT);
		dcWorklogDate.setBounds(421, 34, 101, 20);
		
		JButton btnGenerateReport = new JButton("Generate Report");
		btnGenerateReport.addActionListener(createReportButtonActionListener());
		btnGenerateReport.setBounds(250, 200, 285, 23);
		
		panel.setLayout(null);
		panel.add(lblUsername);
		panel.add(tfUsername);
		panel.add(lblPassword);
		panel.add(pfPassword);
		panel.add(lblJiraUrl);
		panel.add(tfJiraServer);
		panel.add(jScrollPane1Report);
		panel.add(lblNewLabel);
		panel.add(dcWorklogDate);
		panel.add(btnGenerateReport);	
		panel.add(lblAssignee);
		
		tfAssignee = new JTextField();
		tfAssignee.setBounds(420, 63, 101, 20);
		panel.add(tfAssignee);
		tfAssignee.setColumns(10);		
		
		panel.add(lblMessages);
		
		lblConnectionSettings = new JLabel("CONNECTION SETTINGS");
		lblConnectionSettings.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblConnectionSettings.setBounds(25, 0, 285, 25);
		panel.add(lblConnectionSettings);
		
		Label label = new Label("FILTER SETTINGS");
		label.setFont(new Font("Dialog", Font.BOLD, 16));
		label.setBounds(337, 0, 285, 23);
		panel.add(label);
		
		listIssueTypes = new JList<String>();
		listIssueTypes.setBounds(679, 68, 101, 121);		
		panel.add(listIssueTypes);
		
		JScrollPane jScrollPane1IssueType = new JScrollPane(listIssueTypes);
		jScrollPane1IssueType.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		jScrollPane1IssueType.setBounds(679, 68, 101, 121);
		panel.add(jScrollPane1IssueType);
		
		JLabel lblIssueType = new JLabel("Issue Type");
		lblIssueType.setBounds(615, 37, 62, 14);
		panel.add(lblIssueType);
		
		JButton btnSync = new JButton("Sync");
		btnSync.addActionListener(createSyncActionListener());
		btnSync.setBounds(679, 34, 101, 23);
		panel.add(btnSync);
		
		vectorAssignees = new Vector<String>();
		listAssignees = new JList<String>(vectorAssignees);
		listAssignees.setBounds(420, 92, 101, 97);
		listAssignees.addKeyListener(createAssigneesKeyAdapter());
		panel.add(listAssignees);
		
		JScrollPane jScrollPane1Assignees = new JScrollPane(listAssignees);
		jScrollPane1Assignees.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		jScrollPane1Assignees.setBounds(420, 92, 101, 97);
		panel.add(jScrollPane1Assignees);			
		
		JButton btnAdd = new JButton("+");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnAdd.setToolTipText("Add assignee for filtering");
		btnAdd.addActionListener(createAddAssigneeActionListener());
		btnAdd.setBounds(531, 64, 47, 23);
		panel.add(btnAdd);
		
		JButton btnRemove = new JButton("-");
		btnRemove.addActionListener(createRemoveAssigneeActionListener());
		btnRemove.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnRemove.setToolTipText("Remove the selected items");
		btnRemove.setBounds(531, 95, 47, 23);
		panel.add(btnRemove);
		
		IndexedFocusTraversalPolicy policy = new IndexedFocusTraversalPolicy();
		policy.addIndexedComponent(tfJiraServer);
		policy.addIndexedComponent(tfUsername);
		policy.addIndexedComponent(pfPassword);
		policy.addIndexedComponent(dcWorklogDate);
		policy.addIndexedComponent(tfAssignee);
		policy.addIndexedComponent(btnAdd);
		policy.addIndexedComponent(btnRemove);
		policy.addIndexedComponent(listAssignees);
		policy.addIndexedComponent(btnSync);
		policy.addIndexedComponent(listIssueTypes);
		policy.addIndexedComponent(btnGenerateReport);
		
		frmJiraTool.setFocusTraversalPolicy(policy);
	}
	
	private void removeSelectedAssignees()
	{
		List<String> selectedValues = listAssignees.getSelectedValuesList();					
		for(String selected : selectedValues) 
		{
			vectorAssignees.remove(selected);
		}
		listAssignees.setListData(vectorAssignees);
	}
	
	private boolean isConnectionSettingsValid()
	{
		return !UiHelper.isEmpty(tfJiraServer.getText()) && 
				!UiHelper.isEmpty(tfUsername.getText()) && 
				!UiHelper.isEmpty(pfPassword.getText());
	}
	
	private boolean isValidForm()
	{
		return !UiHelper.isEmpty(tfJiraServer.getText()) && 
				!UiHelper.isEmpty(tfUsername.getText()) && 
				!UiHelper.isEmpty(pfPassword.getText()) && 
				listAssignees.getModel().getSize() > 0 && 
				dcWorklogDate.getDate() != null;
	}
	
	private KeyAdapter createAssigneesKeyAdapter()
	{
		return new KeyAdapter() {
			public void keyPressed(KeyEvent ke)
			{
				if(ke.getKeyCode()==KeyEvent.VK_DELETE)
				{
					removeSelectedAssignees();
				}
			}
		};
	}
	
	private ActionListener createSyncActionListener()
	{
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				lblMessages.setText("");
				if(isConnectionSettingsValid() == false)
				{
					lblMessages.setText("Please provide the connection settings.");
					return;
				}
				String url = tfJiraServer.getText() + "/rest/api/2/issuetype";
				String method = Constants.GET;
				String username = tfUsername.getText();
				String password = pfPassword.getText();
				connector = new JiraConnector(url, username, password);
				String jsonString = connector.doRequest(method, null);
				if(jsonString != null && jsonString.contains("Error:"))
				{
					lblMessages.setText(jsonString);
					return;
				}				
				DefaultListModel<String> model = UiHelper.buildIssueTypeDefaultListModel(jsonString);
				listIssueTypes.setModel(model);
			}
		};
	}
	
	private ActionListener createAddAssigneeActionListener() 
	{
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				if(UiHelper.isEmpty(tfAssignee.getText()) || vectorAssignees.contains(tfAssignee.getText()))
				{
					return;
				}
				vectorAssignees.add(tfAssignee.getText());
				listAssignees.setListData(vectorAssignees);
				tfAssignee.setText("");
			}
		};
	}
	
	private ActionListener createRemoveAssigneeActionListener() 
	{
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				if(listAssignees.isSelectionEmpty()) 
				{
					return;
				}
				removeSelectedAssignees();
			}
		};
	}
	
	private ActionListener createReportButtonActionListener() 
	{
		return new ActionListener() {
			
			
			public void actionPerformed(ActionEvent arg0) 
			{	
				lblMessages.setText("");
				
				System.out.println(isValidForm());
				
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
					
					String url = tfJiraServer.getText() + "/rest/api/2/search";
					listAssignees.setSelectionInterval(0, listAssignees.getModel().getSize() - 1);
					String assignees = String.join(Constants.DELIMITER, listAssignees.getSelectedValuesList());
					listAssignees.clearSelection();
					String worklogDate = JiraHelper.getDateFormatted(dcWorklogDate.getDate());
					java.util.List<String> issueTypeSelected = listIssueTypes.getSelectedValuesList();
					String issueType = issueTypeSelected.size() > 0 ? String.join(Constants.DELIMITER, issueTypeSelected) : null;
					String requestBody = JiraHelper.buildWorklogRequestBody(assignees, worklogDate, issueType);
					
					connector = new JiraConnector(url, tfUsername.getText(), pfPassword.getText());
					String jsonString = connector.doRequest(Constants.POST, requestBody);
					
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
