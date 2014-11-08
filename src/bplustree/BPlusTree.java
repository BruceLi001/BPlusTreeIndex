package bplustree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import table.WhereCondition;

public class BPlusTree implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	int maxKeyCount;	//���ؼ��ָ���������B+����·��m_ways
							//��С�ؼ��ָ���=(maxKeyCount+1)/2;
	BPlusTreeNode root;	//��
	BPlusTreeNode first;//��һ���ӽ��
	
	/**
	 * BPlusTree���캯��
	 * @param m BPlusTree�Ľ���
	 */
	public BPlusTree(int maxKeyCount){
		if(maxKeyCount < 2) maxKeyCount=2;
		this.maxKeyCount = maxKeyCount;
		this.root = null;
	    this.first = null;
	}
	
	/**
	 * @return ���ؼ��ָ���,��B+����·��
	 */
	public int getMaxKeyCount(){
		return maxKeyCount;
	}
	
	public BPlusTreeNode getRoot(){
		return root;
	}
	
	public BPlusTreeNode getFirst(){
		return first;
	}
	
	/**
	 * �����ؼ���key��ָ�������λ��
	 * @param key �ؼ��֣���Ϊ����������
	 * @return int �ؼ���key��ָ�������λ��,-1���ʾδ�ҵ�
	 */
	public long search(int key){
		if (this.root == null || key > this.root.key[this.root.num - 1]) {
			// rootΪnull �� key�����������key
			return -1;
		}
		BPlusTreeNode node = this.root;
		int i;

		while (node!=null) {
			for (i = 0; node.key[i] < key; i++);
			if (node.isLeaf == false) {// �м���
				node = node.children[i];
			} else {
				if (node.key[i] == key) {// �ҵ�
					//System.out.println("�ҵ��ؼ���"+key);
					return node.addr[i];
				} else {// δ�ҵ�
					//System.out.println("δ�ҵ�" + key);
					// BPlusTreeNode.print(stack[top-3],0);
					return -1;
				}
			}
		}
		return -1;
	}
	
	/**
	 * ������key�Ƚ�ֵΪtrue�Ĺؼ�����ָ�������λ��
	 * @param key ��Ϊ����������
	 * @param compareOp �ȽϱȽϲ�����{@link table.WhereCondition}
	 * @return
	 */
	public List<Long> search(int key, int compareOp) {
		if (this.root == null || key > this.root.key[this.root.num - 1]) {
			// rootΪnull �� key�����������key
			return null;
		}
		BPlusTreeNode node = this.root;
		int i;

		while (node!=null) {
			for (i = 0; node.key[i] < key; i++);
			if (node.isLeaf == false) {// �м���
				node = node.children[i];
			} else {
				//System.out.println("�ҵ��ؼ��ֲο�ֵ"+node.key[i]);
				List<Long> addrList = new ArrayList<Long>();
				switch (compareOp) {
					case WhereCondition.LikeOp:
					case WhereCondition.EqualOp:
						gatherEqualKey(key, i, node, addrList);
						break;
					case WhereCondition.LessEqualOp:
						gatherEqualKey(key, i, node, addrList);
					case WhereCondition.LessThanOp:
						gatherLessKey(key, i, node, addrList);
						break;
					case WhereCondition.GreatEqualOp:
						gatherEqualKey(key, i, node, addrList);
					case WhereCondition.GreatThanOp:
						gatherGreaterKey(key, i, node, addrList);
						break;
					case WhereCondition.NotEqualOp:
						gatherLessKey(key, i, node, addrList);
						gatherGreaterKey(key, i, node, addrList);
						break;
					default:
						break;
				}
				return addrList;
			}
		}
		return null;
	}
		
	private void gatherEqualKey(int key, int i, BPlusTreeNode node, List<Long> addrList) {
		for (int t = i; t < node.num && node.key[t] == key; t++) {
			addrList.add(Long.valueOf(node.addr[t]));
		}
		BPlusTreeNode tmp = node.next;
		ge: while (tmp!=null && tmp!=this.first) {
			for (int t = 0; t < tmp.num; t++) {
				if (tmp.key[t] == key) {
					addrList.add(Long.valueOf(tmp.addr[t]));
				} else {
					break ge;
				}
			}
			tmp = tmp.next;
		}
	}
	
	private void gatherLessKey(int key, int i, BPlusTreeNode node, List<Long> addrList) {
		BPlusTreeNode tmpLt = this.first;
		while (tmpLt!=null && tmpLt!=node) {
			for (int t = 0; t < tmpLt.num; t++) {
				addrList.add(Long.valueOf(tmpLt.addr[t]));
			}
			tmpLt = tmpLt.next;
		}
		for (int t = 0; t < tmpLt.num && tmpLt.key[t] < key; t++) {
			addrList.add(Long.valueOf(tmpLt.addr[t]));
		}
	}
	
	private void gatherGreaterKey(int key, int i, BPlusTreeNode node, List<Long> addrList) {
		BPlusTreeNode tmpGt = node;
		for (int t = i+1; t < tmpGt.num && tmpGt.key[t] > key; t++) {
			addrList.add(Long.valueOf(tmpGt.addr[t]));
		}
		tmpGt = tmpGt.next;
		while (tmpGt!=null && tmpGt!=this.first) {
			for (int t = 0; t < tmpGt.num; t++) {
				addrList.add(Long.valueOf(tmpGt.addr[t]));
			}
			tmpGt = tmpGt.next;
		}
	}
	
	/**
	 * ��BPlusTree��insert�ؼ���Ϊkey�ļ�¼����趨����ָ��Ϊaddr
	 * @param key �ؼ���
	 * @param addr ����ָ��
	 */
	public void insert(int key, int addr){
		//System.out.println("����insert(" + key + "," + addr + ")����");
		BPlusTreeNode node = this.root;
		int i;
		Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
		Stack<Integer> indexStack = new Stack<Integer>();

		nodeStack.push(null);
		indexStack.push(0);
		//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));

		if (node != null) {
			//System.out.println("root!=null");
			i = node.num;
			if (key < node.key[i - 1]) {
				for (;;) {
					for (i = 0; node.key[i] < key; i++)
						;
					nodeStack.push(node);
					indexStack.push(i);
					//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
					if (node.isLeaf == false) {// ���ն˽��
						node = node.children[i];
					} else {
						break;
					}
				}
			} else {
				for (;;) {
					nodeStack.push(node);
					if (node.isLeaf == false) {
						node.key[i - 1] = key;// �޸��ϼ������������ֵ
						indexStack.push(i-1);

						node = node.children[i - 1];
						i = node.num;
					} else {
						indexStack.push(i);
						break;
					}
				}
				//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
			}
		} else {
			//System.out.println("root==null");
			node = new BPlusTreeNode(this.maxKeyCount);
			this.first = this.root = node;
			indexStack.push(0);
			nodeStack.push(node);
			node.num = 0;
			node.isLeaf = true;
			node.next = null;
		}
		//�ݹ����
		this.insert(key, addr, null, nodeStack, indexStack);
		//System.out.println("��ɲ���");
	}
	
	/**
	 * �ݹ����(key,addr)
	 * @param key �ؼ���
	 * @param addr ����ָ��
	 * @param child ָʾ�ӽڵ�
	 * @param nodeStack ����ջ
	 * @param indexStack ������ջ
	 */
	private void insert(int key, int addr, BPlusTreeNode child,
			Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack) 
	{
		//System.out.println("����insert�ݹ鷽��");
		//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
		BPlusTreeNode node = nodeStack.peek(), parent, sibling;
		int j = indexStack.peek();
		//System.out.println("index[top]��j="+j);
		int i, m, k;
		if (node.num < this.maxKeyCount) {// ��ʣ��ռ�
			//System.out.println("��ʣ��ռ�");
			for (i = node.num; i > j; i--) {
				node.children[i] = node.children[i - 1];
				node.key[i] = node.key[i - 1];
				node.addr[i] = node.addr[i - 1];
			}
			node.children[j] = child;
			node.key[j] = key;
			node.addr[j] = addr;
			node.num++;
			return;
		}

		// ���ѽ��
		//System.out.println("���ѽ��");
		sibling = new BPlusTreeNode(this.maxKeyCount);
		m = (this.maxKeyCount + 1) >> 1; // M=(KEY_COUNT+1)/2;
		//System.out.println("m=" + m);
		sibling.next = node.next;
		sibling.isLeaf = node.isLeaf;
		sibling.num = this.maxKeyCount + 1 - m;
		node.next = sibling;
		node.num = m;
		if (j < m) {
			//System.out.println("j(" + j + ")<m(" + m + ")");
			for (i = m - 1, k = 0; i < this.maxKeyCount; i++, k++) {
				sibling.key[k] = node.key[i];
				sibling.addr[k] = node.addr[i];
				sibling.children[k] = node.children[i];
			}
			for (i = m - 2; i >= j; i--) {
				node.key[i + 1] = node.key[i];
				node.addr[i + 1] = node.addr[i];
				node.children[i + 1] = node.children[i];
			}
			node.key[j] = key;
			node.addr[j] = addr;
			node.children[j] = child;
		} else {
			//System.out.println("j(" + j + ")>=m(" + m + ")");
			for (i = m, k = 0; i < j; i++, k++) {
				sibling.key[k] = node.key[i];
				sibling.addr[k] = node.addr[i];
				sibling.children[k] = node.children[i];
			}
			sibling.key[k] = key;
			sibling.addr[k] = addr;
			sibling.children[k] = child;
			k++;
			for (; i < this.maxKeyCount; i++, k++) {
				sibling.key[k] = node.key[i];
				sibling.addr[k] = node.addr[i];
				sibling.children[k] = node.children[i];
			}
		}

		// �޸��ϼ�����
		//System.out.println("�޸��ϼ�����");
		nodeStack.pop();
		indexStack.pop();
		//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
		parent = nodeStack.peek();
		j = indexStack.peek();
		if (parent != null) {
			//System.out.println("�޸��ϼ�����parent!=null");
			parent.children[j] = sibling;
			key = node.key[m - 1];
			this.insert(key, addr, node, nodeStack, indexStack);
		} else {
			//System.out.println("�µĸ����root");
			this.root = parent = new BPlusTreeNode(this.maxKeyCount);
			parent.num = 2;
			parent.next = null;
			parent.isLeaf = false;
			//nodeStack.setElementAt(parent, nodeStack.size()-1);

			parent.key[0] = node.key[m - 1];
			parent.children[0] = node;
			parent.key[1] = sibling.key[this.maxKeyCount - m];
			parent.children[1] = sibling;
		}
	}

	/**
	 * ��BPlusTree���Ƴ��ؼ���key
	 * @param key ��ɾ���Ĺؼ���
	 * @return ɾ���ɹ�����true�����򷵻�false
	 */
	public boolean delete(long key) {
		//System.out.println("���뷽��delete(" + key + ")");
		if (this.root == null || key > this.root.key[this.root.num - 1]) {
			// rootΪnull �� key�����������key
			return false;
		}
		BPlusTreeNode node = this.root;
		int i, new_key;
		boolean key_found = false;
		Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
		Stack<Integer> indexStack = new Stack<Integer>();

		//int top = 0;
		//System.out.println("top="+top);
		nodeStack.push(null);
		indexStack.push(0);
		//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
		
		for (;;) {
			for (i = 0; node.key[i] < key; i++)
				;
			//top++;
			//System.out.println("top="+top);
			nodeStack.push(node);
			indexStack.push(i);
			//System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));

			if (node.isLeaf == false) {// �м���
				node = node.children[i];
			} else {
				if (node.key[i] == key) {// �ҵ�
					//System.out.println("�ҵ��ؼ���");
					key_found = true;
					node.num--;
					for (; i < node.num; i++) {// ɾ��k[i]
						node.key[i] = node.key[i + 1];
						node.addr[i] = node.addr[i + 1];
						node.children[i] = node.children[i + 1];
					}
					new_key = node.key[node.num - 1];

					// �޸��ϼ�����key
					//System.out.println("�޸��ϼ�����key");
					//i = top - 1;
					i = nodeStack.size()-2;//top == nodeStack.size()-1;
					BPlusTreeNode node_i= nodeStack.elementAt(i);
					BPlusTreeNode node_i1= nodeStack.peek();//ǡ��i+1==nodeStack.size()-1;
					int index_i = indexStack.elementAt(i);
					int index_i1 = indexStack.peek();
					if (i > 0 && node_i.key[index_i] == key
							&& node_i1.num == index_i1) {

						node_i.key[index_i] = new_key;
						//i = top - 2;
						for (i--; i > 0; i--) {
							node_i = nodeStack.elementAt(i);
							node_i1 = nodeStack.elementAt(i + 1);
							index_i = indexStack.elementAt(i);
							index_i1 = indexStack.elementAt(i + 1);
							if (node_i.key[index_i] == key
									&& node_i1.num - 1 == index_i1) {
								node_i.key[index_i] = new_key;
							} else {
								break;
							}
						}
					}
				} else {// δ�ҵ�
					//System.out.println("δ�ҵ�" + key);
					// BPlusTreeNode.print(stack[top-3],0);
				}
				break;
			}
		}
		if (key_found) {
			/*if (!this.check_node(this.root)) {
				System.out.println("ERROR_1");
			}*/
			this.check(nodeStack, indexStack);
			/*if (!this.check_node(this.root)) {
				System.out.println("ERROR_2");
			}*/
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * ������Ƿ���Ҫ �ϲ�
	 * @param nodeStack
	 * @param indexStack
	 */
	private void check(Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack){
		BPlusTreeNode node = nodeStack.peek();
		BPlusTreeNode parent, lchild, rchild;
		int pos;

		while (node.num < (this.maxKeyCount + 1) / 2) {// �����Ԫ��̫��
			nodeStack.pop();
			indexStack.pop();
			pos = indexStack.peek();
			parent = nodeStack.peek();
			if (parent == null) {// �����
				if (node.num <= 1) {
					// �޸ĸ����
					if (node.children[0] != null) {
						this.root = node.children[0];
						// ���ո����
						node = null;
					} else {
						if (node.num == 0) {
							this.root = null;
							// ���ս��
							node = null;
						}
					}
				}
				break;
			}

			if (pos == 0) {// ����
				lchild = node;
				rchild = parent.children[pos + 1];
			} else {
				pos--;
				rchild = node;
				lchild = parent.children[pos];
			}
			if (this.merge(lchild, pos, rchild, parent)) {
				//System.out.println("�ϲ����ɹ�");
			} else {
				//System.out.println("ƽ�ֽ���е�Ԫ��");
			}
			node = parent;
		}
	}
	
	/**
	 * �ϲ����
	 * @param lchild
	 * @param index indexΪ lchild ��parent�е�����λ��
	 * @param rchild
	 * @param parent
	 */
	private boolean merge(BPlusTreeNode lchild, int index,
			BPlusTreeNode rchild, BPlusTreeNode parent) {
		int i, j, m;
		int k = lchild.num + rchild.num;
		if (k <= this.maxKeyCount) {
			// �ϲ�Ϊһ�����
			System.out.println("�ϲ�Ϊһ�����");
			// rchild�е�Ԫ�غϲ���lchild��
			for (i = lchild.num, j = 0; j < rchild.num; j++, i++) {
				lchild.key[i] = rchild.key[j];
				lchild.addr[i] = rchild.addr[j];
				lchild.children[i] = rchild.children[j];
			}
			lchild.next = rchild.next;
			lchild.num = k;

			// �����ϼ�����
			parent.num--;
			parent.key[index] = lchild.key[k - 1];
			for (i = index + 1; i < parent.num; i++) {
				parent.children[i] = parent.children[i + 1];
				parent.key[i] = parent.key[i + 1];
			}

			// ���ս��
			rchild = null;
			return true;
		} else {
			// ƽ�ֽ���е�Ԫ��
			System.out.println("ƽ�ֽ���е�Ԫ��");
			BPlusTreeNode[] children = new BPlusTreeNode[this.maxKeyCount * 2];
			int[] key = new int[this.maxKeyCount * 2];
			int[] addr = new int[this.maxKeyCount * 2];

			// �ռ�
			for (i = 0; i < lchild.num; i++) {
				children[i] = lchild.children[i];
				key[i] = lchild.key[i];
				addr[i] = lchild.addr[i];
			}
			for (j = 0; j < rchild.num; i++, j++) {
				children[i] = rchild.children[j];
				key[i] = rchild.key[j];
				addr[i] = rchild.addr[j];
			}

			// ƽ��
			m = k >> 1;// m = (lchild->n + rchild->n) / 2
			for (i = 0; i < m; i++) {
				lchild.children[i] = children[i];
				lchild.key[i] = key[i];
				lchild.addr[i] = addr[i];
			}
			lchild.num = m;

			for (j = 0; i < k; i++, j++) {
				rchild.children[j] = children[i];
				rchild.key[j] = key[i];
				rchild.addr[j] = addr[i];
			}
			rchild.num = k - m;

			// �����ϼ�����
			parent.key[index] = key[m - 1];
			return false;
		}
	}
	
	/* �����,���߼������򷵻�false */
	boolean check_node(BPlusTreeNode node) {
		if (node.isLeaf) {// Ҷ�ӽ��
			return true;
		}
		int i;
		BPlusTreeNode child;
		// ���ڷ�Ҷ�ӽ�㣨�м��㣩
		for (i = 0; i < node.num; i++) {
			child = node.children[i];
			if (child == null) {
				System.out.println("child[" + i + "]==null");
				return false;
			}
			if (node.key[i] != child.key[child.num - 1]) {
				System.out.println("�ؼ��ֲ���["+i+"]");
				BPlusTreeNode.print_2(node, 0);
				return false;
			}
			if (!this.check_node(child)) {
				System.out.println("�ӽڵ㲻��["+i+"]");
				BPlusTreeNode.print_2(child, 0);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * �õ����ĸ߶�
	 * @return
	 */
	public int getHeight(){
		int h=0;
		BPlusTreeNode node = this.root;
		while(node!=null){
			node = node.children[0];
			h++;
		}
		return h;
	}
	
	/**
	 * �õ�BPlusTreeNode������
	 * @return
	 */
	public int getNodeTotalCount(){
		return getNodeCount(this.root);
	}
	
	/**
	 * �õ���nodeΪ���������Ľڵ���
	 * @param node
	 * @return
	 */
	private int getNodeCount(BPlusTreeNode node){
		int count = 0;
		if(node==null){
			return 0;
		}else if(node.isLeaf){
			return 1;
		}else{
			for(int i=0;i<node.num;i++){
				count += getNodeCount(node.children[i]);
			}
			count++;
		}
		return count;
	}
	
	/**
	 * �õ�Ҷ�ӽ�����
	 * @return
	 */
	public int getLeafNodeCount(){
		int leafCount = 0;
		BPlusTreeNode node = this.first;

		while (node!=null) {
			leafCount++;
			node = node.next;
		}
		return leafCount;
	}
	
	/**
	 * �õ��ؼ��ָ�������Ҷ�ӽ���ϵ����йؼ��ָ����ܺ�
	 * @return
	 */
	public int getKeyTotalCount(){
		int keyCount = 0;
		BPlusTreeNode node = this.first;

		while (node!=null) {
			keyCount+=node.num;
			node = node.next;
		}
		return keyCount;
	}
	
	/**
	 * д���ļ�
	 * 
	 * @param treeFileName
	 * @return
	 */
	public boolean toFile(String treeFileName) {
		return toFile(new File(treeFileName));
	}

	/**
	 * д���ļ�
	 * 
	 * @param treeFile
	 * @return
	 */
	public boolean toFile(File treeFile) {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(treeFile));
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
	 * @param treeFileName
	 * @return
	 */
	public static BPlusTree getFormFile(String treeFileName) {
		return getFormFile(new File(treeFileName));
	}

	/**
	 * ���ļ��ж�������
	 * 
	 * @param treeFile
	 * @return
	 */
	public static BPlusTree getFormFile(File treeFile) {
		ObjectInputStream stream = null;
		BPlusTree treeFromFile = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(treeFile));
			treeFromFile = (BPlusTree) (stream.readObject());
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeFromFile;
	}
	
	/**
	 * չʾ���B+��
	 */
	@Deprecated
	public void showTree(){
		if(root!=null)
			BPlusTreeNode.show(root, 0);
	}
	
	/**
	 * ����������ṹ
	 */
	@Deprecated
	public void print_tree(){
		// System.out.println();
		if(this.root!=null)
			BPlusTreeNode.print(this.root,0);
		// System.out.println();
	}
	
	/**
	 * ����������ṹ
	 */
	public void print_tree_2(){
		// System.out.println();
		if(this.root!=null)
			BPlusTreeNode.print_2(this.root,0);
		//System.out.println();
	}
	
	/**
	 * ����print_2��ֻ��ת��Ϊ�ַ���
	 */
	public String toString(){
		if(this.root!=null)
			return this.root.toString();
		else
			return "null";
	}
	
	/**
	 * ���Ҷ�ӽ���
	 */
	public void print_leaf_layer() {
		int i;
		BPlusTreeNode node = this.first;

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
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		BPlusTree btree1 = new BPlusTree(3);
		btree1.insert(23, 23);
		btree1.showTree();
		btree1.insert(22, 22);
		btree1.showTree();
		btree1.insert(21, 21);
		btree1.showTree();
		*/
		
		int list[]={52,52,187,98,180,148,88,122,8,58,129,184,87,74,71,111,198,74,181,185,86};
		//int list[] = { 52, 187, 98, 180, 148, 88, 122, 8, 58, 129, 184 };
		int n = list.length;

		BPlusTree b = new BPlusTree(4);
		System.out.println("Height:"+b.getHeight());
		System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
		System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
		System.out.println("KeyCount:"+b.getKeyTotalCount());

		//��B+���в���ؼ���
		for (int i = 0; i < n; i++) {
			b.insert(list[i], i);
		}
		//b.print_tree_2();
		//b.print_leaf_layer();
		System.out.println("Height:"+b.getHeight());
		System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
		System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
		System.out.println("KeyCount:"+b.getKeyTotalCount());

		//������ṹ
		b.print_tree_2();
		System.out.println("------------------------------------");
		b.print_leaf_layer();
		//System.out.println("------------------------------------");
		System.out.println("һ��" + n + "���ؼ���\n");

		long addr = b.search(74);
		System.out.println("address="+addr);
		addr = b.search(47);
		System.out.println("address="+addr);
		addr = b.search(129);
		System.out.println("address="+addr);
		List<Long> l = b.search(74, WhereCondition.EqualOp);
		System.out.println(l);
		l = b.search(74, WhereCondition.LessEqualOp);
		System.out.println(l);
		l = b.search(74, WhereCondition.LessThanOp);
		System.out.println(l);
		l = b.search(74, WhereCondition.GreatThanOp);
		System.out.println(l);
		if (true) {
			return;
		}
		
		//��B+�����Ƴ��ؼ���
		boolean flag;
		flag = b.delete(74);
		System.out.println("ɾ��"+(flag?"�ɹ�":"ʧ��"));
		flag = b.delete(47);
		System.out.println("ɾ��"+(flag?"�ɹ�":"ʧ��"));
		flag = b.delete(129);
		System.out.println("ɾ��"+(flag?"�ɹ�":"ʧ��"));

		//������ṹ
		b.print_tree_2();
		System.out.println("------------------------------------");
		//b.print_leaf_layer();
		//System.out.println("------------------------------------");
		
		addr = b.search(74);
		System.out.println("address="+addr);
		addr = b.search(47);
		System.out.println("address="+addr);
		addr = b.search(129);
		System.out.println("address="+addr);

		System.out.println("Height:"+b.getHeight());
		System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
		System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
		System.out.println("KeyCount:"+b.getKeyTotalCount());
		
		b.print_tree();
		
		/*
		String nodeFileName = "root.btnode";
		b.root.toFile(nodeFileName);
		BPlusTreeNode node = BPlusTreeNode.getFormFile(nodeFileName);
		node.show(0);
		*/
		
		/*
		String treeFileName = "test.bptree";
		b.toFile(treeFileName);
		BPlusTree test = BPlusTree.getFormFile(treeFileName);
		test.showTree();
		*/
	}
}
