package bplustree;

import java.io.*;

public class BPlusTreeNode implements Serializable {
	private static final long serialVersionUID = 1L;

	static int pos = 0;
	int position; // ��¼��������������ļ������
	int[] posChildren;	// ָ�������������ļ�����š����ӽ�㲻���ڴ棬����ļ��������浽children
	int posNext;		// ͬ�����һ���������ļ������

	int max; //B+���Ľ���,�������(�ӽ��,�ؼ���)�������Ŀ
	/* (�ӽ��,�ؼ�����Ŀ��ͬ) */
	boolean isLeaf;	// �Ƿ���Ҷ���
	int num;	// �ؼ��ָ���,���Ϊmax��
	int[] key;	// num���ؼ�ֵ
	int[] addr;	// �ؼ�ֵ��Ӧ������ָ��()
	/* ���ָ�� */
	BPlusTreeNode[] children;	// fork�¼����,�ӽ�㡣���ӽ�������ڴ�ʱ����������
	BPlusTreeNode next;			// ͬ�����һ���,���ڱ���

	/**
	 * BTNode���캯��
	 * @param m BTNode�Ľ���
	 */
	public BPlusTreeNode(int m) {
		position = pos++;
		//int max;
		max = m;

		isLeaf = true;
		num = 0;

		key = new int[max];
		addr = new int[max];
		children = new BPlusTreeNode[max];
		posChildren = new int[max];
		for (int i = 0; i < max; i++) {
			key[i] = -1;
			addr[i] = -1;
			children[i] = null;
			posChildren[i] = -1;
		}
		next = null;
		posNext = -1;
	}
	
	public int getMax(){
		return max;
	}
	
	/**
	 * @return boolean Ҷ����true;internel��false
	 */
	public boolean isLeaf() {
		return isLeaf;
	}
	
	/**
	 * @return �ؼ��ָ���
	 */
	public int getNum(){
		return num;
	}

	public int getKeyAt(int i){
		if(i<0 || i>num)
			return -1;
		return key[i];
	}
	
	public int getAddrAt(int i){
		if(i<0 || i>num)
			return -1;
		return addr[i];
	}
	
	public BPlusTreeNode getChildAt(int i){
		if(i<0 || i>num)
			return null;
		return children[i];
	}
	
	public BPlusTreeNode getNext(){
		return next;
	}

	/**
	 * �жϽ���Ƿ���һ�����Ľ�㣨��max���ؼ��֣�
	 * 
	 * @return boolean
	 */
	public boolean isFull() {
		if (num == max)
			return true;
		else
			return false;
	}

	/**
	 * д���ļ�
	 * 
	 * @param nodeFileName
	 * @return
	 */
	public boolean toFile(String nodeFileName) {
		return toFile(new File(nodeFileName));
	}

	/**
	 * д���ļ�
	 * 
	 * @param nodeFile
	 * @return
	 */
	public boolean toFile(File nodeFile) {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(nodeFile));
			stream.writeObject(this);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ���ļ��ж�������
	 * 
	 * @param nodeFileName
	 * @return
	 */
	public static BPlusTreeNode getFormFile(String nodeFileName) {
		return getFormFile(new File(nodeFileName));
	}

	/**
	 * ���ļ��ж�������
	 * 
	 * @param nodeFile
	 * @return
	 */
	public static BPlusTreeNode getFormFile(File nodeFile) {
		ObjectInputStream stream = null;
		BPlusTreeNode nodeFromFile = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(nodeFile));
			nodeFromFile = (BPlusTreeNode) (stream.readObject());
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeFromFile;
	}

	/**
	 * ��ӡ��this�����Ϣ��
	 * 
	 * @param depth
	 */
	@Deprecated
	public void show(int depth) {
		for (int i = 0; i < depth; i++) {
			System.out.print("    ");
		}
		System.out.print("node[" + this.position + "]:{");
		for (int i = 0; i < this.num; i++) {
			System.out.print("(" + this.key[i] + "," + this.addr[i] + ")");
		}
		System.out.println("}");
		for (int i = 0; i < this.num; i++) {
			if (this.children[i] != null)
				this.children[i].show(depth + 1);
		}
	}

	/**
	 * ��ӡ�������Ϣ��:��node.show(depth)�����������
	 * 
	 * @param node
	 * @param depth
	 */
	@Deprecated
	public static void show(BPlusTreeNode node, int depth) {
		if (node == null)
			return;
		for (int i = 0; i < depth; i++) {
			System.out.print("    ");
		}
		System.out.print("node[" + node.position + "]:{");
		for (int i = 0; i < node.num; i++) {
			System.out.print("(" + node.key[i] + "," + node.addr[i] + ")");
		}
		System.out.println("}");
		for (int i = 0; i < node.num; i++) {
			show(node.children[i], depth + 1);
		}
	}
	
	/**
	 * ����������
	 * @param node
	 * @param level
	 */
	@Deprecated
	public static void print(BPlusTreeNode node, int level) {
		int i, j;
		if (node.isLeaf) {
			for (i = 0; i < node.num; i++) {
				for (j = 0; j < level; j++) {
					System.out.print(" ");
				}
				System.out.println("("+node.key[i]+","+node.addr[i]+")");
			}
		} else {
			for (i = 0; i < node.num/*-1*/; i++) {
				print(node.children[i], level + 4);
				for (j = 0; j < level; j++) {
					System.out.print(" ");
				}
				System.out.println(node.key[i]);
			}
		}
	}
	
	/**
	 * ����print_2��ֻ��ת��Ϊ�ַ���
	 */
	public String toString(){
		return this.toString(0);
	}
	/*�ݹ鷽��:��toString����*/
	private String toString(int level){
		StringBuffer str = new StringBuffer(1024);
		int i, j;
		if (this.isLeaf) {
			for (j = 0; j < level; j++) {
				str.append(" ");
			}
			str.append("{");
			for (i = 0; i < this.num - 1; i++) {
				str.append("("+this.key[i]+","+this.addr[i]+"),");
			}
			str.append("("+this.key[i]+","+this.addr[i]+")}\n");
		} else {
			for (j = 0; j < level; j++) {
				str.append(" ");
			}
			str.append("{");
			for (i = 0; i < this.num - 1; i++) {
				str.append(this.key[i]+",");
			}
			str.append(this.key[i] + "}\n");
			for (i = 0; i < this.num/*-1*/; i++) {
				str.append(this.children[i].toString(level + 4));
			}
		}
		return str.toString();
	}
	
	/**
	 * ����������
	 * @param node
	 * @param level
	 */
	public static void print_2(BPlusTreeNode node, int level) {
		int i, j;
		if (node.isLeaf) {
			for (j = 0; j < level; j++) {
				System.out.print(" ");
			}
			System.out.print("{");
			for (i = 0; i < node.num - 1; i++) {
				System.out.print("("+node.key[i]+","+node.addr[i]+"),");
			}
			System.out.println("("+node.key[i]+","+node.addr[i]+")}");
		} else {
			for (j = 0; j < level; j++) {
				System.out.print(" ");
			}
			System.out.print("{");
			for (i = 0; i < node.num - 1; i++) {
				System.out.print(node.key[i]+",");
			}
			System.out.println(node.key[i] + "}");
			for (i = 0; i < node.num/*-1*/; i++) {
				print_2(node.children[i], level + 4);
			}
		}
	}

	/**
	 * ����
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String nodeFileName = "test.btnode";
		
		int m = 3;
		BPlusTreeNode node1 = new BPlusTreeNode(m);

		node1.show(0);
		node1.toFile(nodeFileName);
		File file = new File(nodeFileName);
		System.out.println(nodeFileName+" length:"+file.length());
		
		BPlusTreeNode[] node1Child = new BPlusTreeNode[m];
		node1.isLeaf = false;
		node1.num = m;
		for(int i=0;i<node1.num;i++){
			node1.key[i]=i+1;
		}
		for(int i=0;i<node1Child.length;i++){
			node1Child[i] = new BPlusTreeNode(m);
			node1Child[i].num = m;
			for(int j=0;j<node1Child[i].num;j++){
				node1Child[i].key[j]=j+1+(i+1)*3;
			}
			node1.children[i] = node1Child[i];
			node1.posChildren[i] = node1Child[i].position;
		}
		for(int i=0;i<node1Child.length-1;i++){
			node1Child[i].next = node1Child[i+1];
			node1Child[i].posNext = node1Child[i+1].position;
		}
		
		node1.show(0);
		node1.toFile(nodeFileName);
		System.out.println(nodeFileName+" length:"+file.length());
		
		BPlusTreeNode.print(node1, 0);
		BPlusTreeNode.print_2(node1, 0);
		int i;
		BPlusTreeNode node = node1Child[0];

		while (node!=null && node.next!=null) {
			for (i = 0; i < node.num; i++) {
				System.out.print("("+node.key[i]+","+node.addr[i]+"),");
			}
			node = node.next;
		}
		if (node != null) {
			for (i = 0; i < node.num - 1; i++) {
				System.out.print("(" + node.key[i] + "," + node.addr[i] + "),");
			}
			System.out.println("(" + node.key[i] + "," + node.addr[i] + ")");
		}
		
		/*
		BPlusTreeNode node2 = BPlusTreeNode.getFormFile(nodeFileName);
		node2.show(0);
		*/
	}
}
