package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bplustree.BPlusTree;
import fileio.BitMap;

public class TableWithIndex extends Table {
	public static final String IndexDir = "index";
	
	public static final String CfgFileExt = ".indexcfg";
	public static final String TreeFileExt = ".indextree";
	public static int indexTreeMaxKeyCount = 4;
	
	String indexName;
	String indexFieldName;	//ֻ֧�ֵ����ֶε�����
	BPlusTree indexTree;	//ֻ֧�������ֶε�����
	
	/**
	 * �չ��췽��
	 */
	public TableWithIndex(){
		super();
		indexName = null;
		indexFieldName = null;
		indexTree = null;
	}
	
	/**
	 * ָ�������Ĺ��췽��
	 * @param tableName
	 */
	public TableWithIndex(String tableName){
		super(tableName);
		indexName = null;
		indexFieldName = null;
		indexTree = null;
		checkIndex();
	}
	
	/**
	 * ָ���������������Ĺ��췽��
	 * @param tableName
	 * @param indexName
	 */
	public TableWithIndex(String tableName, String indexName){
		super(tableName);
		this.indexName= indexName.toUpperCase();
		indexFieldName = null;
		indexTree = null;
		updateIndexInfo();
	}
	
	/**
	 * ������Ƿ��������Ѿ�����
	 */
	private void checkIndex(){
		if(!this.isTableExist()){
			return;
		}
		String indexRootDirName = this.getTablePath()+File.separator+IndexDir;
		File indexRootDir = new File(indexRootDirName);
		if(indexRootDir.isDirectory()==false){
			//System.out.println("û�д�������Ŀ¼");
			return;
		}
		String[] indexNames = indexRootDir.list();
		if(indexNames.length==0){
			//System.out.println("û�д����κ�����");
			return;
		}
		this.indexName = indexNames[0];
		//System.out.println("�Ѿ�����������"+this.indexName);
		updateIndexInfo();
	}
	
	/**
	 * �õ���������
	 * @return
	 */
	public String getIndexName(){
		return this.indexName;
	}
	
	/**
	 * �趨��������
	 * @param indexName
	 */
	public void setIndexName(String indexName){
		this.indexName = indexName.toUpperCase();
		this.updateIndexInfo();
	}
	
	/**
	 * ����this.indexName����������Ϣ�������ֶ���������B+����
	 */
	private void updateIndexInfo(){
		if (this.indexName == null) {
			System.out.println("��û��ָ������������");
			return;
		}
		// Ϊÿ�������Ա���������Ŀ¼directory
		File indexDir = new File(this.getIndexPath());
		boolean isDirectory = indexDir.isDirectory();
		if (isDirectory) {
			//System.out.println("������" + tableName+"."+indexName+"�Ѵ���");
			this.indexFieldName = this.getIndexFieldName();
			this.indexTree = this.getIndexTree();
			if(this.indexTree==null || this.indexFieldName==null){
				this.indexName = null;
				this.indexFieldName = null;
				this.indexTree = null;
				indexDir.delete();
			}
		}else{
			this.indexName = null;
			this.indexFieldName = null;
			this.indexTree = null;
			indexDir.delete();
		}
	}
	
	/**
	 * �õ���������·��
	 * @return
	 */
	public String getIndexPath(){
		String path = super.getTablePath()+File.separator;
		path += IndexDir+File.separator+this.indexName;
		return path;
	}
	
	/**
	 * �������� -- ������(��һ�ֶ�����)
	 * 1,���˱��Ѿ���һ�����������ڴ���
	 * 2,����ֻ֧��int��bigInt�ֶ�
	 * @param indexName
	 * @param indexFieldName
	 * @return
	 */
	public boolean createIndex(String indexName,String indexFieldName){
		if (isTableExist()==false){//��񲻴���
			return false;
		}
		if(this.indexName!=null){
			System.out.println("�Ѿ�����һ������,���ܴ����ڶ�������Ǹ��");
			return false;
		}
		Field[] fields = getTableFields();
		if (fields == null) {
			return false;
		}
		/*����ֶ�����*/
		int indexFiledType = Field.getTypeByNameFromFields(fields, indexFieldName);
		if(indexFiledType == Field.TypeError){
			System.out.println("�����ڵ��ֶ��������⣡");
			return false;
		}
		if(indexFiledType!=Field.IntType && indexFiledType!=Field.BigintType){
			System.out.println("��֧�ֵ������ֶ����ͣ���Ǹ��");
			return false;
		}
		//��ʼ��������
		this.indexName = indexName.toUpperCase();
		// Ϊÿ������������"����/index/indexName"������Ŀ¼
		File indexDir = new File(getIndexPath());
		boolean isExists = indexDir.exists();
		if (isExists) {
			if(indexDir.isDirectory()){
				System.out.println("������" + indexName + "�Ѵ���");
				updateIndexInfo();
			}else{
				System.out.println("ϵͳ���������ļ���\""+getIndexPath()+"\"����");
				System.out.println("�޷���������" + indexName);
				this.indexName = null;
			}
			return false;
		}
		// ����index���ڵ�Ŀ¼
		if(!indexDir.mkdirs()){
			this.indexName = null;
		}
		this.indexFieldName = indexFieldName.toUpperCase();
		
		/*дindexFieldName�������ļ�*/
		String cfgFileName = this.getIndexPath() + File.separator
				+ this.indexName + CfgFileExt;
		File cfgFile = new File(cfgFileName);
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(cfgFile));
			outputStream.println(this.indexFieldName);
			outputStream.close();
			//System.out.println("���������ļ�" + cfgFileName + "����");
		} catch (IOException e) {
			System.out.println("���������ļ�" + cfgFileName + "ʧ��");
			e.printStackTrace();
			cfgFile.delete();
			this.indexName = null;
			this.indexFieldName = null;
			return false;
		}
		cfgFile.setReadOnly();// �趨�����ļ�Ϊ ��ֻ����
		
		/* ��ȡλͼ�ļ� */
		BitMap map = getTableBitMap();
		if (map == null) {
			cfgFile.delete();
			this.indexName = null;
			this.indexFieldName = null;
			System.out.println("��ȡλͼ�ļ�ʧ��");
			return false;
		}
		
		/*�������ļ���ʼ��this.indexTree*/
		this.initIndexTree(map,fields);
		updateIndexTreeToFile();//��������B+��to�ļ�
		
		return true;
	}
	
	/**
	 * �������ļ���ʼ��this.indexTree
	 * @return
	 */
	private void initIndexTree(BitMap map, Field[] fields){
		this.indexTree = new BPlusTree(indexTreeMaxKeyCount);
		int totalRecord = map.getSetNum();// �õ��ܼ�¼��
		if (totalRecord > 0) {
			int fieldCount = fields.length;
			int recordSize = 0;
			for (int i = 0; i < fieldCount; i++) {
				recordSize += fields[i].fieldSize;
			}
			/* ��ȡ�����ļ� */
			String dataFileName = getTablePath() + File.separator + tableName + DataFileExt;
			File dataFile = new File(dataFileName);
			RandomAccessFile dataStream = null;
			try {
				dataStream = new RandomAccessFile(dataFile, "r");
				boolean[] bits = map.getMap();
				byte[] buf = new byte[recordSize];
				String[] oneRecord;
				for (int i = 0; i < bits.length; i++) {
					if (bits[i]) {
						// ��ȡһ����¼������buf��
						dataStream.seek(i * recordSize);
						dataStream.readFully(buf);
						// ��buf����ת��ΪString[]
						oneRecord = Field.parseBytesToStrings(buf, fields);
						String keyValue = Field.getValueByFieldName(oneRecord, fields, this.indexFieldName);
						//��������B+����keyValue��Ϊ�ؼ��֣�i��Ϊ����ָ�룩
						this.indexTree.insert(Integer.parseInt(keyValue), i);
					}
				}
				dataStream.close();
			} catch (IOException e) {
				System.out.println("��ȡ�����ļ�ʧ��");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �����ݿ�������³�ʼ��B+��
	 */
	public void updateIndexTree(){
		if(!isIndexExist()){//���,������������
			return;
		}
		/* ��ȡλͼ�ļ� */
		BitMap map = getTableBitMap();
		if (map == null) {
			return;
		}
		Field[] fields = getTableFields();
		if (fields == null) {
			return;
		}
		this.initIndexTree(map,fields);
		updateIndexTreeToFile();//��������B+��to�ļ�
	}
	
	/**
	 * �ж������Ƿ����
	 * @return
	 */
	public boolean isIndexExist(){
		if (isTableExist()==false){
			//��񲻴���
			return false;
		}
		if (this.indexName == null) {
			System.out.println("��û��ָ������������");
			return false;
		}
		// Ϊÿ�������Ա���������Ŀ¼directory
		File indexDir = new File(this.getIndexPath());
		boolean isDirectory = indexDir.isDirectory();
		if (isDirectory) {
			//System.out.println("������" + tableName+"."+indexName+"�Ѵ���");
			this.indexFieldName = this.getIndexFieldName();
			this.indexTree = this.getIndexTree();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * �õ������ֶ���
	 * @return
	 */
	public String getIndexFieldName(){
		if (indexFieldName == null) {
			String cfgFileName = this.getIndexPath() + File.separator
					+ this.indexName + CfgFileExt;
			File cfgFile = new File(cfgFileName);
			if (!cfgFile.exists()) {
				//System.out.println("�����ļ�" + cfgFileName + "��ʧ");
				return null;
			}
			BufferedReader stream = null;
			String line;
			try {
				stream = new BufferedReader(new FileReader(cfgFileName));
				// ��ȡindexFieldName
				line = stream.readLine();
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			indexFieldName = line.toUpperCase();
		}
		return indexFieldName;
	}
	
	/**
	 * �õ�����B+��
	 * @return
	 */
	public BPlusTree getIndexTree() {
		if (indexTree == null) {
			String treeFileName = this.getIndexPath() + File.separator
					+ this.indexName + TreeFileExt;
			File treeFile = new File(treeFileName);
			if (!treeFile.exists()) {
				//System.out.println("����B+���ļ�" + treeFileName + "��ʧ");
				return null;
			}
			indexTree = BPlusTree.getFormFile(treeFileName);
		}
		return indexTree;
	}
	
	/**
	 * ��������B+��to�ļ�
	 */
	private void updateIndexTreeToFile(){
		if (indexTree != null) {
			String treeFileName = this.getIndexPath() + File.separator
					+ this.indexName + TreeFileExt;
			indexTree.toFile(treeFileName);
		}
	}

	/**
	 * ��ѯ���tableName��������conditions AndOrs��ɵ�����������
	 * 
	 * @param conditions
	 * @param AndOrs
	 * @return
	 */
	public String[][] selectFromTable(WhereCondition[] conditions, int[] AndOrs) {
		int condFiledIsIndex = -1; // ��ʱֻ֧��һ���������ʽ��������
		if (conditions != null && conditions.length > 0) {
			// ����������ʽ�ֶ�Ϊ�����ֶΣ����������ȡ������������
			for (int i=0; i<conditions.length; i++) {
				if (conditions[i].fieldName.equalsIgnoreCase(this.indexFieldName)) {
					if (AndOrs==null || AndOrs.length==0
							|| (i==0 && AndOrs[0]==WhereCondition.RelationAndOp)
							|| (AndOrs[i-1]==WhereCondition.RelationAndOp && (AndOrs.length<=i || (AndOrs[i]==WhereCondition.RelationAndOp)))
							) { // ǰ�������and����
						condFiledIsIndex = i;
						break; // ��ʱֻ֧��һ���������ʽ��������
					}
				}
			}
		}
		if (condFiledIsIndex == -1 || indexTree==null) {
			return super.selectFromTable(conditions, AndOrs);
		}
		resetIOCount();
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return null;
		}

		/* ���ļ�ͨ��ǰ׺ */
		String tableFilesPrefix = tableDir.getPath() + File.separator
				+ tableName;

		/* ��ȡ�����ļ� */
		String cfgFileName = tableFilesPrefix + CfgFileExt;
		Field[] fields;
		if (tableFields != null)
			fields = tableFields;
		else {
			fields = getFieldCfg(cfgFileName);
			tableFields = fields;
		}
		if (fields == null) {
			System.out.println("��ȡ�����ļ�" + cfgFileName + "ʧ��");
			return null;
		}
		int fieldCount = fields.length;
		int recordSize = 0;
		for (int i = 0; i < fieldCount; i++) {
			recordSize += fields[i].fieldSize;
		}

		/* ��ȡλͼ�ļ� */
		String mapFileName = tableFilesPrefix + MapFileExt;
		BitMap map = BitMap.getFormFile(mapFileName);
		if (map == null) {
			System.out.println("��ȡλͼ�ļ�" + mapFileName + "ʧ��");
			return null;
		}
		int totalRecord = map.getSetNum();
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return new String[0][fieldCount];
		}
		
		/* ��ȡ������*/
		WhereCondition wc = conditions[condFiledIsIndex];
		List<Long> addrList = indexTree.search(Integer.valueOf(wc.referencValue), wc.comparisonOp);
		if (addrList == null) {
			return null;
		}
		
		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		ArrayList<String[]> resultList = new ArrayList<String[]>(totalRecord);
		try {
			dataStream = new RandomAccessFile(dataFile, "r");
			//boolean[] bits = map.getMap();
			byte[] buf = new byte[recordSize];
			String[] oneRecord;
			Collections.sort(addrList);
			for (int i = 0; i < addrList.size(); i++) {
				// ��ȡһ����¼������buf��
				dataStream.seek(addrList.get(i) * recordSize);
				dataStream.readFully(buf);
				incIOCount();
				// ��buf����ת��ΪString[]
				oneRecord = Field.parseBytesToStrings(buf, fields);
				if (WhereCondition.checkRecordByFields(oneRecord, fields,
						conditions, AndOrs))
					resultList.add(oneRecord);
				// System.out.println(resultList.size());
			}
			dataStream.close();
		} catch (IOException e) {
			System.out.println("��ȡ�����ļ�ʧ��");
			e.printStackTrace();
			return null;
		}
		/*ת��*/
		String[][] resultSet = new String[resultList.size()][];
		resultList.toArray(resultSet);

		return resultSet;
	}
	
	/**
	 * ����в���һ������:
	 * 
	 * @param oneRecord
	 * @return boolean success or not
	 * @see table.Table#insertIntoTable(java.lang.String[])
	 */
	public boolean insertIntoTable(String[] oneRecord){
		boolean flag = super.insertIntoTable(oneRecord);
		if(flag){
			//System.out.println("����һ�������ݣ�updateIndexTree");
			this.updateIndexTree();
		}
		return flag;
	}
	
	/**
	 * ɾ�������������
	 * 
	 * @return ɾ����¼����
	 * @see table.Table#deleteAllFromTable()
	 */
	public int deleteAllFromTable(){
		int num = super.deleteAllFromTable();
		if(num>0){
			this.updateIndexTree();
		}
		return num;
	}
	
	/**
	 * ɾ�����tableName��������(conditions AndOrs��ɵ�����)������
	 * 
	 * @param conditions
	 * @param AndOrs
	 * @return ɾ����¼����
	 * @see table.Table#deleteFromTable(table.WhereCondition[], int[])
	 */
	public int deleteFromTable(WhereCondition[] conditions, int[] AndOrs){
		int num = super.deleteFromTable(conditions,AndOrs);
		if(num>0){
			this.updateIndexTree();
		}
		return num;
	}
	
	/**
	 * update���tableName��������(conditions AndOrs��ɵ�����)�ļ�¼
	 * 
	 * @param setPairs
	 * @param conditions
	 * @param AndOrs
	 * @return Ӱ���¼����
	 * @see table.Table#updateTable(table.UpdateSetPair[], table.WhereCondition[], int[])
	 */
	public int updateTable(UpdateSetPair[] setPairs,
			WhereCondition[] conditions, int[] AndOrs){
		int num = super.updateTable(setPairs,conditions,AndOrs);
		if(num>0){
			this.updateIndexTree();
		}
		return num;
	}
	
	/**
	 * ת��ΪDDL��Data Definition Language���ݿ�ģʽ�������ԣ�
	 * @return create_table_sql �� create_index_sql
	 * @see table.Table#toDDLString()
	 */
	public String toDDLString() {
		if (this.tableName == null) {
			return "��û��ָ���������";
		}
		StringBuffer ddl = new StringBuffer("Create Table " + this.tableName + "( ");
		if (this.tableFields != null) {
			int i = 0;
			for (i = 0; i < this.tableFields.length - 1; i++)
				ddl.append(this.tableFields[i].toDDLString() + ", ");
			ddl.append(this.tableFields[i].toDDLString());
		}
		ddl.append(" );\n");
		
		if(this.indexName == null){
			ddl.append("��û�д�������");
			return ddl.toString();
		}
		ddl.append("Create index "+this.indexName+" ON "+
				this.tableName+"("+this.indexFieldName+");");
		
		return ddl.toString();
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		TableWithIndex test = new TableWithIndex("test");
		boolean flag = test.createIndex("asf", "id");
		System.out.println("��������"+flag);
		//test.setIndexName("idindex");
		String tablePath = test.getTablePath();
		String indexPath = test.getIndexPath();
		System.out.println(tablePath);
		System.out.println(indexPath);
		boolean isTableExist = test.isTableExist();
		System.out.println("isTableExist:"+isTableExist);
		boolean isIndexExist = test.isIndexExist();
		System.out.println("isIndexExist:"+isIndexExist);
		String indexFieldName = test.getIndexFieldName();
		System.out.println("indexFieldName:"+indexFieldName+",");
		BPlusTree bplustree = test.getIndexTree();
		if(bplustree!=null){
			bplustree.print_tree_2();
		}else{
			System.out.println("IndexTree��ʧ");
		}
		System.out.println(test.toDDLString());
		
		TableWithIndex studentIndexTable = new TableWithIndex("student");
		System.out.println(studentIndexTable.toDDLString());
		System.out.println(studentIndexTable.toString());
	}
}
