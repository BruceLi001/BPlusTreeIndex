package view;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import table.*;
import bplustree.*;

public class BPlusTreeDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private BPlusTree bPlusTree = null;

	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		TableWithIndex stuTable = new TableWithIndex("student");
		/*
		// ����һЩ��¼
		String[] oneRecord = new String[2];
		for(int i=0;i<5;i++){
			oneRecord[0] = String.valueOf((int)(Math.random()*100.0));
			oneRecord[1] = String.valueOf((char)('a'+(Math.random()*25)));
			boolean insertFlag = stuTable.insertIntoTable(oneRecord);
			System.out.println("insert("+oneRecord[0]+","+oneRecord[1]+"):"+insertFlag);
		}
		*/
		BPlusTree bpt = stuTable.getIndexTree();
		bpt.print_tree_2();
		
		BPlusTreeDialog diag = new BPlusTreeDialog(bpt,stuTable.getTableName());
		diag.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		diag.setVisible(true);
	}
	
	/**
	 * ���췽��������B+��
	 * @param bPlusTree
	 */
	public BPlusTreeDialog(BPlusTree bPlusTree,String tableName){
		super();
		this.setModal(true);
		this.setSize(400, 300);
		this.bPlusTree = bPlusTree;
		this.setTitle(tableName+" -- Index B+��");
		initBPlusTree();
	}
	
	/**
	 * ��ʼ��B+���ṹͼ
	 */
	private void initBPlusTree(){
		if(this.bPlusTree==null){
			JLabel noTree = new JLabel("û��B+����Ҫ��ʾ");
			noTree.setBorder(new EtchedBorder());
			this.add(noTree);
		}else{
			BPlusTreePanel treePanel = new BPlusTreePanel(this.bPlusTree);
			JScrollPane pane = new JScrollPane(treePanel);
			this.add(pane);
		}
	}

}
