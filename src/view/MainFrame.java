package view;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import bplustree.*;
import table.*;
import fileio.*;

public class MainFrame extends JFrame implements ActionListener,TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private TableWithIndex dataTable = null;
	private JTable tableDataTable;
	private JTextArea sqlTextArea = null;
	private JTextArea sqlRunTextArea = null;
	private JTree tablesListTree = null;
	//private BPlusTreePanel treePanel;

	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public MainFrame() {
		super();
		initGUI();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initGUI() {
		// get screen dimensions
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		// center frame in screen
		this.setSize(screenWidth / 2, screenHeight / 2);
		this.setLocation(screenWidth / 4, screenHeight / 4);
		// set frame icon and title
		Image img = kit.getImage("res/mainicon.png");
		this.setIconImage(img);
		this.setTitle("Table");

		// ��Ӳ˵�
		this.addMenu();
		/*���sqlִ���ı���*/
		this.addSQLTextArea();
		//��ӱ���б�
		this.addTableListPanel();
		//��ӱ��������ʾPanel,��sqlRunTextArea
		this.addCenterPanel();
		//���B+����ʾPanel
		//this.addBPlusTreePanel();
	}
	
	/**
	 * ���B+����ʾPanel
	private void addBPlusTreePanel(){
		treePanel = new BPlusTreePanel(null);
		JScrollPane bptpane = new JScrollPane(treePanel);
		//bptpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		//bptpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		bptpane.setPreferredSize(new Dimension(200,100));
		this.add(bptpane,BorderLayout.EAST);
	}
	 */
	
	/**
	 * �����������ʾPanel,��sqlRunTextArea
	 */
	private void addCenterPanel(){
		JPanel centerP = new JPanel(new GridLayout(2,1));
		//���������ʾPanel
		tableDataTable = new JTable();
		tableDataTable.setEnabled(false);
		centerP.add(new JScrollPane(tableDataTable));
		//sqlRunTextArea
		sqlRunTextArea = new JTextArea(10,40);
		sqlRunTextArea.setBackground(Color.WHITE);
		sqlRunTextArea.setEditable(false);
		centerP.add(new JScrollPane(sqlRunTextArea));
		
		add(centerP,BorderLayout.CENTER);
	}
	
	/**
	 * ��������б�Panel
	 */
	private void addTableListPanel() {
		File tablesDir = new File(Table.rootDir);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tables");
		if (tablesDir.isDirectory()) {
			String[] tableNames = tablesDir.list();
			//System.out.println("table������"+tableNames.length);
			for (int i = 0; i < tableNames.length; i++) {
				// System.out.println(tableNames[i]);
				DefaultMutableTreeNode curTable = new DefaultMutableTreeNode(
						tableNames[i]);
				root.add(curTable);
			}
		}
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		tablesListTree = new JTree(treeModel);
		tablesListTree.addTreeSelectionListener(this);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		tablesListTree.getSelectionModel().setSelectionMode(mode);
		JScrollPane tableListPanel = new JScrollPane(tablesListTree);
		add(tableListPanel, BorderLayout.WEST);
	}
	
	/**
	 * ���sqlִ���ı���
	 */
	private void addSQLTextArea(){
		JPanel sqlPanel = new JPanel(new BorderLayout());
		
		sqlTextArea = new JTextArea(3,30);
		sqlTextArea.setBackground(Color.WHITE);
		Font f = sqlTextArea.getFont();
		sqlTextArea.setFont(new Font(f.getName(),f.getStyle(),f.getSize()+4));
		JScrollPane scrollPane = new JScrollPane(sqlTextArea);
		sqlPanel.add(scrollPane,BorderLayout.CENTER);
		
		JButton sqlRunButton = new JButton("ִ��");
		sqlRunButton.setActionCommand("runSQL");
		sqlRunButton.addActionListener(this);
		JPanel subPanel = new JPanel();
		subPanel.add(sqlRunButton);
		sqlPanel.add(subPanel,BorderLayout.EAST);
		
		add(sqlPanel, BorderLayout.SOUTH);
	}

	/**
	 * ��Ӳ˵�
	 */
	private void addMenu() {
		JMenuItem m;
		/*file�˵�*/
		JMenu fileMenu = new JMenu("�ļ�(F)");
		fileMenu.setMnemonic('F');
		m = new JMenuItem("Exit");
		m.addActionListener(this);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.ALT_MASK));
		fileMenu.add(m);
		/*�鿴�˵�*/
		JMenu viewMenu = new JMenu("�鿴(V)");
		viewMenu.setMnemonic('V');
		m = new JMenuItem("���±������");
		m.setActionCommand("viewTableData");
		m.addActionListener(this);
		viewMenu.add(m);
		m = new JMenuItem("B+��");
		m.setActionCommand("viewBPlusTree");
		m.addActionListener(this);
		viewMenu.add(m);
		m = new JMenuItem("����λͼ");
		m.setActionCommand("viewBitMap");
		m.addActionListener(this);
		viewMenu.add(m);
		/*Style�˵�*/
		JMenu styleMenu = new JMenu("���(S)");
		styleMenu.setMnemonic('S');
		m = new JMenuItem("Windows");
		m.setActionCommand("styleWindows");
		m.addActionListener(this);
		styleMenu.add(m);
		m = new JMenuItem("Metal");
		m.setActionCommand("styleMetal");
		m.addActionListener(this);
		styleMenu.add(m);
		m = new JMenuItem("Motif");
		m.setActionCommand("styleMotif");
		m.addActionListener(this);
		styleMenu.add(m);
		/*Help�˵�*/
		JMenu helpMenu = new JMenu("����(H)");
		helpMenu.setMnemonic('H');
		m = new JMenuItem("About");
		m.addActionListener(this);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				InputEvent.ALT_MASK));
		helpMenu.add(m);

		/*�趨�˵�*/
		JMenuBar mBar = new JMenuBar();
		mBar.add(fileMenu);
		mBar.add(viewMenu);
		mBar.add(styleMenu);
		mBar.add(helpMenu);
		this.setJMenuBar(mBar);
	}

	/**
	 * ����ִ�нӿ�ʵ��
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("runSQL")) {
			//ִ��SQL
			String sql = this.sqlTextArea.getText().trim();
			if(this.sqlTextArea.getLineCount()>1){
				sql = sql.replace('\n', ' ');
			}
			sqlRunTextArea.setText("");
			System.out.println(sql);
			sqlRunTextArea.append(sql+"\n");
			if(sql.length()==0){
				System.out.println("û���κ�����");
				sqlRunTextArea.append("û���κ�����\n");
				return;
			}
			this.parseSQL(sql);
			this.sqlTextArea.setText("");
			if(dataTable!=null){
				updateTableDataPanel();
				//updateTreePanel();
			}
		} else if (actionCommand.equals("viewTableData")) {
			//���±������
			if(dataTable!=null){
				updateTableDataPanel();
			}
		} else if (actionCommand.equals("viewBPlusTree")) {
			//��ʾ����B+��
			if(dataTable!=null){
				BPlusTree bpt = dataTable.getIndexTree();	//�õ�B+��
				//bpt.print_tree_2();
				String tableName = dataTable.getTableName();//�������
				
				this.sqlRunTextArea.setText("");
				sqlRunTextArea.append("���" + tableName);
				if (bpt != null) {
					String colName = dataTable.getIndexFieldName();
					sqlRunTextArea.append(",���ֶ�" + colName + "��������:\n");
					sqlRunTextArea.append(bpt.toString());
					//��ʾB+���ṹͼ
					BPlusTreeDialog bptf = new BPlusTreeDialog(bpt,tableName);
					bptf.setLocation(100+this.getLocation().x, 100+this.getLocation().y);
					bptf.setVisible(true);
				} else {
					sqlRunTextArea.append(",û������B+��!\n");
					JOptionPane.showMessageDialog(this, "���" + tableName
							+ ",û������!", "��ʾ", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (actionCommand.equals("viewBitMap")) {
			//��ʾ����λͼ
			if(dataTable!=null){
				BitMap bitmap = dataTable.getTableBitMap();
				BitMapDialog bmd = new BitMapDialog(bitmap,dataTable.getTableName());
				bmd.setLocation(100+this.getLocation().x, 100+this.getLocation().y);
				bmd.setVisible(true);
			}
		} else if (actionCommand.equals("Exit")) {
			//�˳�����
			System.exit(0);
		} else if (actionCommand.equals("styleWindows")) {
			try {// Windows���
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("styleMetal")) {
			try {// Metal���
				UIManager
						.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("styleMotif")) {
			try {// Motif���
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception ex) {
			}
		} else if (actionCommand.equals("About")) {
			JOptionPane.showMessageDialog(this, "B+�����ݿ�����ʵ��չʾϵͳ��","Info",JOptionPane.INFORMATION_MESSAGE);
		} else {
		}
	}

	/**
	 * TreeSelectionListener ʵ��ѡ����
	 */
	public void valueChanged(TreeSelectionEvent e) {
		TreePath curTreePath = e.getPath();
		//System.out.println(curTreePath.getLastPathComponent().toString());
		int pathCount = curTreePath.getPathCount();
		//System.out.println(pathCount);
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) curTreePath.getLastPathComponent();
		if(pathCount==2){
			String tableName = selectedNode.toString();
			if(dataTable!=null && dataTable.getTableName().equalsIgnoreCase(tableName))
				return;
			//System.out.println(tableName);
			dataTable = new TableWithIndex(tableName);
			this.setTitle("Table - "+tableName);
			updateTableDataPanel();
			//updateTreePanel();
		}
	}

	/**
	 * ���±��������ʾPanel
	 */
	public void updateTableDataPanel(){
		String[][] data = dataTable.selectAllFromTable();
		Field[] fields = dataTable.getTableFields();
		String[] columnNames = new String[fields.length];
		for(int i=0;i<columnNames.length;i++){
			columnNames[i] = fields[i].getFieldName();
		}
		((DefaultTableModel) tableDataTable.getModel()).setDataVector(data, columnNames);
	}
	
	/**
	 *���£��ػ�B+��Panel
	public void updateTreePanel(){
		treePanel.setBPlusTree(dataTable.getIndexTree());
		int width=this.getWidth(),height=this.getHeight();
		this.pack();
		this.setSize(width, height);
	}
	 */
	
	/**
	 * ����sql���
	 * @param sql
	 */
	void parseSQL(String sql){
		String sqlUpper = sql.toUpperCase();
		String opTitle;
		int p1;
		
		p1 = sql.indexOf(' ');//��һ���ո�
		if(p1==-1){
			System.out.println("��ʽ����");
			sqlRunTextArea.append("��ʽ����\n");
			return;
		}
		opTitle = sqlUpper.substring(0,p1);
		p1++;
		while(sql.charAt(p1)==' '){	p1++; }//�����ո�
		if(opTitle.equals("CREATE")){
			/*����table��index*/
			int p2 = sql.indexOf(' ', p1);//�ڶ����ո�
			if(p2==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			String createType = sqlUpper.substring(p1, p2);
			p2++;
			while(sql.charAt(p2)==' '){	p2++; }//�����ո�
			if(createType.equals("TABLE")){
				/*����table*/
				int p3 = sql.indexOf('(',p2);
				if(p3==-1){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				String tableName = sqlUpper.substring(p2,p3).trim();
				if(sql.charAt(sql.length()-1)!=')'){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				String fieldDDLStr = sql.substring(p3+1,sql.length()-1).trim();
				System.out.println("fieldDDLStr:"+fieldDDLStr);
				Field[] fields = Field.getFieldsFromDDLString(fieldDDLStr);
				System.out.println("�������"+tableName);
				sqlRunTextArea.append("�������"+tableName+"\n");
				for(int i=0;i<fields.length;i++){
					if(fields[i]==null){
						System.out.println("������");
						sqlRunTextArea.append("������\n");
						return;
					}
					System.out.println(fields[i].toString()+",��:"+fields[i].toDDLString());
				}
				TableWithIndex theTable = new TableWithIndex(tableName);
				boolean flag = theTable.createTable(tableName, fields);
				if(flag){
					DefaultTreeModel tm = (DefaultTreeModel)(tablesListTree.getModel());
					DefaultMutableTreeNode root = (DefaultMutableTreeNode)(tm.getRoot());
					DefaultMutableTreeNode newTableNode = new DefaultMutableTreeNode(tableName);
					root.add(newTableNode);
					tm.setRoot(root);
					dataTable = theTable;
					this.setTitle("Table - "+tableName);
					sqlRunTextArea.append("�������"+tableName+"�ɹ�\n");
				}else{
					sqlRunTextArea.append("�������"+tableName+"ʧ��\n");
				}
				System.out.println("����"+(flag?"�ɹ�":"ʧ��"));
			}else if(createType.equals("INDEX")){
				/*����index*/
				int p3 = sql.indexOf(' ',p2);
				if(p3==-1){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				String indexName = sql.substring(p2,p3).trim();
				p3++;
				while(sql.charAt(p3)==' '){	p3++; }//�����ո�
				int p4 = sql.indexOf(' ',p3);
				if(p4==-1){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				if(!sqlUpper.substring(p3, p4).equals("ON")){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				p4++;
				while(sql.charAt(p4)==' '){	p4++; }//�����ո�
				int p5 = sql.indexOf('(',p4);
				if(p5==-1){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				String tableName = sql.substring(p4,p5).trim();
				if(sql.charAt(sql.length()-1)!=')'){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				int p6 = sql.indexOf(')',p5+1);
				if(p6==-1){
					System.out.println("��ʽ����");
					sqlRunTextArea.append("��ʽ����\n");
					return;
				}
				int p60 = sql.indexOf(',',p5+1);
				if(p60!=-1){
					System.out.println("ֻ֧��һ���ֶ�!!!");
					sqlRunTextArea.append("ֻ֧��һ���ֶ�!!!\n");
					p6 = p60;
				}
				String indexFieldName = sql.substring(p5+1, p6).trim();
				System.out.println("����index��"+indexName+",on table:"+tableName+" ���ֶΣ�"+indexFieldName);
				sqlRunTextArea.append("����index��"+indexName+",on table:"+tableName+" ���ֶΣ�"+indexFieldName+"\n");
				TableWithIndex theTable = new TableWithIndex(tableName);
				if(theTable.isTableExist()==false){
					sqlRunTextArea.append("���"+tableName+"������!\n");
				}
				boolean flag = theTable.createIndex(indexName, indexFieldName);
				if(flag){
					dataTable = theTable;
					this.setTitle("Table - "+tableName);
					sqlRunTextArea.append("����������"+indexName+"�ɹ�\n");
				}else{
					sqlRunTextArea.append("����������"+indexName+"ʧ��\n");
				}
			}else{
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
		}else if(opTitle.equals("SELECT")){
			/*��ѯ���*/
			int pFrom = sqlUpper.indexOf("FROM", p1);
			if(pFrom==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			int p2 = sql.indexOf(' ',pFrom);
			if(p2==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			p2++;
			while(sql.charAt(p2)==' '){	p2++; }//�����ո�
			int p3 = sql.indexOf(' ',p2);
			String tableName;
			if(p3==-1){
				tableName = sqlUpper.substring(p2);
				TableWithIndex theTable = new TableWithIndex(tableName);
				System.out.println("select all from table:"+tableName);
				sqlRunTextArea.append("select all from table:"+tableName+"\n");
				String[][] data = theTable.selectAllFromTable();
				if(data!=null){
					System.out.println("�ܼ�¼����" + data.length);
					sqlRunTextArea.append("�ܼ�¼����" + data.length+"\n");
					for (int i = 0; i < data.length; i++) {
						for (int j = 0; j < data[i].length; j++) {
							System.out.print(data[i][j] + " ");
							sqlRunTextArea.append(data[i][j] + " ");
						}
						System.out.println();
						sqlRunTextArea.append("\n");
					}
				}else{
					System.out.println("���ִ���!");
					sqlRunTextArea.append("���ִ���\n");
				}
			}else{
				tableName = sqlUpper.substring(p2,p3);
				TableWithIndex theTable = new TableWithIndex(tableName);
				String wheresSql = sql.substring(p3).trim();
				ArrayList<Integer> andOrsList = new ArrayList<Integer>(5);
				WhereCondition[] wcs = WhereCondition.getWheresFromDDLString(wheresSql, andOrsList);
				int[] andOrs = WhereCondition.getAndOrs(andOrsList);
				//��ʼselect
				System.out.println("select all from table:" + tableName	+ ", where:");
				sqlRunTextArea.append("select all from table:" + tableName + "\n");
				int i;
				// ������ʽ
				for (i = 0; i < andOrs.length; i++) {
					System.out.print(wcs[i].toString() + " "
						+ ((andOrs[i] == WhereCondition.RelationAndOp) ? "And" : "Or")
						+ " ");
				}
				System.out.println(wcs[i].toString());
				String[][] data = theTable.selectFromTable(wcs, andOrs);
				if (data != null) {
					System.out.println("�ܼ�¼����" + data.length);
					sqlRunTextArea.append("�ܼ�¼����" + data.length + "\n");
					for (i = 0; i < data.length; i++) {
						for (int j = 0; j < data[i].length; j++) {
							System.out.print(data[i][j] + " ");
							sqlRunTextArea.append(data[i][j] + " ");
						}
						System.out.println();
						sqlRunTextArea.append("\n");
					}
				} else {
					System.out.println("���ִ���!");
					sqlRunTextArea.append("���ִ���\n");
				}
			}
			//end select
		}else if(opTitle.equals("INSERT")){
			/*insert into table_name values(dsf,df,df)*/
			int p2 = sql.indexOf(' ',p1);
			if(p2==-1 || !sqlUpper.substring(p1,p2).equals("INTO")){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			p2++;
			while(sql.charAt(p2)==' '){	p2++; }//�����ո�
			int p3 = sql.indexOf(' ',p2);
			if(p3==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			String tableName = sqlUpper.substring(p2, p3);
			p3++;
			while(sql.charAt(p3)==' '){	p3++; }//�����ո�
			int p4 = sql.indexOf('(',p3);
			if(p4==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			String val = sqlUpper.substring(p3, p4).trim();
			if(val.equals("VALUES")==false){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			p4++;
			int p5 = sql.indexOf(')',p4);
			if(p5==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			String vals = sql.substring(p4, p5).trim();
			String[] oneRecord = vals.split(",");
			if(oneRecord.length==1 && oneRecord[0].length()==0){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			System.out.println("�����"+tableName);
			for(int i=0;i<oneRecord.length;i++){
				oneRecord[i] = oneRecord[i].trim();
				System.out.print(oneRecord[i]+" ");
			}
			System.out.println();
			TableWithIndex theTable;
			if(tableName.equals(dataTable.getTableName())){
				theTable = dataTable;
			}else{
				theTable = new TableWithIndex(tableName);
			}
				
			boolean flag = theTable.insertIntoTable(oneRecord);
			if(flag){
				sqlRunTextArea.append("����ɹ�\n");
			}else{
				sqlRunTextArea.append("����ʧ��\n");
			}
			System.out.println("����"+(flag?"�ɹ�":"ʧ��"));
		}else if(opTitle.equals("UPDATE")){
			/*update table_name set col=sdf where ... */
			int p2 = sql.indexOf(' ', p1);//�ڶ����ո�
			if(p2==-1){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			String tableName = sqlUpper.substring(p1, p2);
			p2++;
			while(sql.charAt(p2)==' '){	p2++; }//�����ո�
			if(sqlUpper.substring(p2, p2+3).equals("SET")==false){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			int pWhere = sqlUpper.indexOf("WHERE");
			TableWithIndex theTable;
			if(tableName.equals(dataTable.getTableName())){
				theTable = dataTable;
			}else{
				theTable = new TableWithIndex(tableName);
			}
			if(pWhere==-1){
				//����Ҫ����
				System.out.println("����"+tableName+" ������");
				String setSql = sql.substring(p2);
				UpdateSetPair[] setPairs = UpdateSetPair.getPairsFromDDLString(setSql);
				int rowNum = theTable.updateTable(setPairs, null, null);
				System.out.println("Ӱ���¼��"+rowNum);
				sqlRunTextArea.append("Ӱ���¼��"+rowNum+"\n");
			}else{
				//��Ҫ����
				String setSql = sql.substring(p2,pWhere).trim();
				UpdateSetPair[] setPairs = UpdateSetPair.getPairsFromDDLString(setSql);
				String wheresSql = sql.substring(pWhere).trim();
				ArrayList<Integer> andOrsList = new ArrayList<Integer>(5);
				WhereCondition[] wcs = WhereCondition.getWheresFromDDLString(wheresSql, andOrsList);
				System.out.print("����"+tableName+" ����");
				
				int[] andOrs = WhereCondition.getAndOrs(andOrsList);
				System.out.println("where:");
				int i;
				//������ʽ
				for (i = 0; i < andOrs.length; i++) {
					System.out.print(wcs[i].toString() + " "
						+ ((andOrs[i] == WhereCondition.RelationAndOp) ? "And" : "Or")
						+ " ");
				}
				System.out.println(wcs[i].toString());
				int rowNum = theTable.updateTable(setPairs, wcs, andOrs);
				System.out.println("Ӱ���¼��"+rowNum);
				sqlRunTextArea.append("Ӱ���¼��"+rowNum+"\n");
				//end update��Ҫ����
			}
			//end update
		}else if(opTitle.equals("DELETE")){
			int p2 = sql.indexOf(' ',p1);
			if(p2==-1 || !sqlUpper.substring(p1,p2).equals("FROM")){
				System.out.println("��ʽ����");
				sqlRunTextArea.append("��ʽ����\n");
				return;
			}
			p2++;
			while(sql.charAt(p2)==' '){	p2++; }//�����ո�
			String tableName;
			int p3 = sql.indexOf(' ',p2);
			if(p3==-1){
				//������ɾ��
				tableName = sqlUpper.substring(p2);
				TableWithIndex theTable;
				if(tableName.equals(dataTable.getTableName())){
					theTable = dataTable;
				}else{
					theTable = new TableWithIndex(tableName);
				}
				System.out.println("ɾ������"+tableName+"��¼");
				sqlRunTextArea.append("ɾ������"+tableName+"��¼\n");
				int rowNum = theTable.deleteAllFromTable();
				System.out.println("Ӱ���¼��"+rowNum);
				sqlRunTextArea.append("Ӱ���¼��"+rowNum+"\n");
			}else{
				//������ɾ��
				tableName = sqlUpper.substring(p2,p3);
				TableWithIndex theTable;
				if(tableName.equals(dataTable.getTableName())){
					theTable = dataTable;
				}else{
					theTable = new TableWithIndex(tableName);
				}
				String wheresSql = sql.substring(p3).trim();
				ArrayList<Integer> andOrsList = new ArrayList<Integer>(5);
				WhereCondition[] wcs = WhereCondition.getWheresFromDDLString(wheresSql, andOrsList);
				System.out.print("ɾ��"+tableName+"��¼ ����");
				int[] andOrs = WhereCondition.getAndOrs(andOrsList);
				System.out.println("where:");
				int i;
				//������ʽ
				for (i = 0; i < andOrs.length; i++) {
					System.out.print(wcs[i].toString() + " "
						+ ((andOrs[i] == WhereCondition.RelationAndOp) ? "And" : "Or")
						+ " ");
				}
				System.out.println(wcs[i].toString());
				int rowNum = theTable.deleteFromTable(wcs, andOrs);
				System.out.println("Ӱ���¼��"+rowNum);
				sqlRunTextArea.append("Ӱ���¼��"+rowNum+"\n");
				//end ������ɾ��
			}
			//end delete
		}else{
			System.out.println("��ʽ����");
			sqlRunTextArea.append("��ʽ����\n");
			return;
		}
	}
}
