package table;

import java.io.*;
import java.util.*;

import fileio.*;

public class Table {
	public static final int initialRecordNum = 45;
	public static final int recordNumIncrement = 10;
	public static final String rootDir = "./table/";

	public static final String CfgFileExt = ".cfg";
	public static final String DataFileExt = ".dat";
	public static final String MapFileExt = ".map";

	String tableName;
	Field[] tableFields;

	/**
	 * �յĹ��췽��
	 */
	public Table() {
		this.tableName = null;
		this.tableFields = null;
	}

	/**
	 * ���췽��
	 * 
	 * @param tableName
	 */
	public Table(String tableName) {
		this.tableName = tableName.toUpperCase();
		this.tableFields = null;
	}

	/**
	 * �õ��������
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * �õ��ֶζ�����Ϣ
	 * 
	 * @return
	 */
	public Field[] getTableFields() {
		if (tableFields == null) {
			String tableFilesPrefix = rootDir + tableName + File.separator
					+ tableName;
			String cfgFileName = tableFilesPrefix + CfgFileExt;
			tableFields = getFieldCfg(cfgFileName);
		}
		return tableFields;
	}

	/**
	 * �õ�����λͼ
	 * 
	 * @return
	 */
	public BitMap getTableBitMap() {
		/* ��ȡλͼ�ļ� */
		String tableFilesPrefix = rootDir + tableName + File.separator
				+ tableName;
		String mapFileName = tableFilesPrefix + MapFileExt;
		BitMap map = BitMap.getFormFile(mapFileName);
		return map;
	}

	/**
	 * ��������λͼ
	 * 
	 * @param map
	 * @return
	 */
	public boolean updateBitMap(BitMap map) {
		String tableFilesPrefix = rootDir + tableName + File.separator
				+ tableName;
		String mapFileName = tableFilesPrefix + MapFileExt;
		return map.toFile(mapFileName);
	}

	/**
	 * �������ļ��еõ��ֶ���Ϣ
	 * 
	 * @param cfgFileName
	 * @return
	 */
	Field[] getFieldCfg(String cfgFileName) {
		File cfgFile = new File(cfgFileName);
		if (!cfgFile.exists()) {
			System.out.println("�����ļ�" + cfgFileName + "��ʧ");
			return null;
		}
		BufferedReader stream = null;
		String line;
		int fieldCount;
		Field[] fields = null;
		try {
			stream = new BufferedReader(new FileReader(cfgFileName));
			// ��ȡ�ֶ���Ŀ
			line = stream.readLine();
			fieldCount = Integer.parseInt(line);
			// �����ȡ�ֶ���Ϣ
			fields = new Field[fieldCount];
			for (int i = 0; i < fieldCount; i++) {
				line = stream.readLine();
				fields[i] = Field.parseString(line);
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fields;
	}
	
	/**
	 * �õ��������·��
	 * @return
	 */
	public String getTablePath(){
		return (rootDir + tableName);
	}

	/**
	 * �������this.tableName���ֶ���fields�ж���
	 * 
	 * @param fields
	 * @return
	 */
	public boolean createTable(Field[] fields) {
		if (this.tableName == null) {
			System.out.println("��û��ָ���������");
			return false;
		}
		// Ϊÿ�������Ա���������Ŀ¼directory
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (isDirectory) {
			System.out.println("���" + tableName + "�Ѵ���");
			this.tableFields = this.getTableFields();
			return false;
		}
		boolean isExists = tableDir.exists();
		if (isExists) {
			System.out.println("ϵͳ�����и��ļ�"+getTablePath()+"����");
			System.out.println("�޷��������" + tableName);
			return false;
		}
		if(Field.checkFieldsNamesUnique(fields)==false){
			System.out.println("�ֶ������ظ�");
			return false;
		}
		// ����table���ڵ�Ŀ¼
		if(!tableDir.mkdirs()){
			this.tableName = null;
		}
		this.tableFields = fields; // �趨����ֶζ���

		/* ���ļ�ͨ��ǰ׺ */
		String tableFilesPrefix = tableDir.getPath() + File.separator
				+ tableName;

		int fieldCount = fields.length;
		/* ���������ļ� */
		String cfgFileName = tableFilesPrefix + CfgFileExt;
		File cfgFile = new File(cfgFileName);
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(cfgFile));
			outputStream.println(fieldCount);
			for (int i = 0; i < fieldCount; i++) {
				outputStream.println(fields[i]);
			}
			outputStream.close();
			//System.out.println("���������ļ�" + cfgFileName + "����");
		} catch (IOException e) {
			e.printStackTrace();
			cfgFile.delete();
			this.tableFields = null;
			System.out.println("���������ļ�" + cfgFileName + "ʧ��");
			return false;
		}
		cfgFile.setReadOnly();// �趨�����ļ�Ϊ ��ֻ����

		/* ����λͼ�ļ� */
		String mapFileName = tableFilesPrefix + MapFileExt;
		File mapFile = new File(mapFileName);
		BitMap map = new BitMap(initialRecordNum);
		if (map.toFile(mapFile)) {
			//System.out.println("����λͼ�ļ�" + mapFileName + "����");
		} else {
			cfgFile.delete();
			mapFile.delete();
			System.out.println("����λͼ�ļ�" + mapFileName + "ʧ��");
			return false;
		}

		/* ���������ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		try {
			dataFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			cfgFile.delete();
			mapFile.delete();
			dataFile.delete();
			System.out.println("���������ļ�" + dataFileName + "ʧ��");
			return false;
		}
		//System.out.println("���������ļ�" + dataFileName + "����");

		//System.out.println("�������" + tableName + "����");
		return true;
	}

	/**
	 * �������tableName���ֶ���fields�ж���
	 * 
	 * @param tableName
	 * @param fields
	 */
	public boolean createTable(String tableName, Field[] fields) {
		this.tableName = tableName.toUpperCase();
		return createTable(fields);
	}
	
	/**
	 * �жϱ���Ƿ����
	 * @return
	 */
	public boolean isTableExist(){
		if (this.tableName == null) {
			System.out.println("��û��ָ���������");
			return false;
		}
		// Ϊÿ�������Ա���������Ŀ¼directory
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (isDirectory) {
			//System.out.println("���" + tableName + "�Ѵ���");
			this.tableFields = this.getTableFields();
			return true;
		}else{
			return false;
		}
	}

	/**
	 * ��ѯ���tableName��������
	 * 
	 * @return
	 */
	public String[][] selectAllFromTable() {
		if (this.tableName == null) {
			System.out.println("��û��ָ���������");
			return null;
		}
		String[][] resultSet = null;
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
		int totalRecord = map.getSetNum();// �õ��ܼ�¼��
		resultSet = new String[totalRecord][fieldCount];
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return resultSet;
		}

		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		try {
			dataStream = new RandomAccessFile(dataFile, "r");
			boolean[] bits = map.getMap();
			int sum = 0;
			byte[] buf = new byte[recordSize];
			String[] oneRecord;
			for (int i = 0; i < bits.length; i++) {
				if (bits[i]) {
					// ��ȡһ����¼������buf��
					dataStream.seek(i * recordSize);
					dataStream.readFully(buf);
					// ��buf����ת��ΪString[]
					oneRecord = Field.parseBytesToStrings(buf, fields);
					for (int j = 0; j < fieldCount; j++) {
						resultSet[sum][j] = oneRecord[j];
					}
					sum++;// ���ݼ�¼��Ŀ
				}
			}
			dataStream.close();
		} catch (IOException e) {
			System.out.println("��ȡ�����ļ�ʧ��");
			e.printStackTrace();
			return null;
		}

		return resultSet;
	}

	/**
	 * ��ѯ���tableName��������conditions������
	 * 
	 * @param conditions
	 *            WhereCondition[]
	 * @return
	 */
	@Deprecated
	public String[][] selectFromTable(WhereCondition[] conditions) {
		if (conditions == null || conditions.length == 0)
			return selectAllFromTable();
		if (this.tableName == null) {
			System.out.println("��û��ָ���������");
			return null;
		}

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

		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		ArrayList<String[]> resultList = new ArrayList<String[]>(totalRecord);
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
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions))
						resultList.add(oneRecord);
					// System.out.println(resultList.size());
				}
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
	 * ��ѯ���tableName��������conditions AndOrs��ɵ�����������
	 * 
	 * @param conditions
	 * @param AndOrs
	 * @return
	 */
	public String[][] selectFromTable(WhereCondition[] conditions, int[] AndOrs) {
		if (conditions == null || conditions.length == 0)
			return selectAllFromTable();
		if (AndOrs == null)// ����ҪAnd Or����
			return selectFromTable(conditions);
		if (this.tableName == null) {
			System.out.println("��û��ָ���������");
			return null;
		}

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

		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		ArrayList<String[]> resultList = new ArrayList<String[]>(totalRecord);
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
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions, AndOrs))
						resultList.add(oneRecord);
					// System.out.println(resultList.size());
				}
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
	 * ����в���һ������
	 * 
	 * @param oneRecord
	 * @return boolean success or not
	 */
	public boolean insertIntoTable(String[] oneRecord) {
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return false;
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
			return false;
		}
		String[] legalRecord = Field.checkRecordByFields(oneRecord, fields);
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
			return false;
		}
		// �õ���һ�����ô洢����λ��
		int newDataLocation = map.getFirstAvailable() - 1;
		// System.out.println("newDataLocation:"+newDataLocation);
		if (newDataLocation < 0) {
			newDataLocation = map.getSize();
			map.setSize(newDataLocation + recordNumIncrement);
			map.toFile(mapFileName);
		}

		/* д�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		// System.out.println("dataFileName:"+dataFileName);
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		try {
			dataStream = new RandomAccessFile(dataFile, "rw");
			dataStream.seek(newDataLocation * recordSize);
			byte[] buf = Field.parseStringsToBytes(legalRecord, fields);
			dataStream.write(buf);
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		map.set(newDataLocation + 1);
		map.toFile(mapFileName);
		return true;
	}

	/**
	 * ɾ�������������
	 * 
	 * @return ɾ����¼����
	 */
	public int deleteAllFromTable() {
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return 0;
		}

		/* ���ļ�ͨ��ǰ׺ */
		String tableFilesPrefix = tableDir.getPath() + File.separator
				+ tableName;
		/* ��ȡλͼ�ļ� */
		String mapFileName = tableFilesPrefix + MapFileExt;
		BitMap map = BitMap.getFormFile(mapFileName);
		if (map == null) {
			System.out.println("��ȡλͼ�ļ�" + mapFileName + "ʧ��");
			return 0;
		}
		int sum = map.getSetNum();// �õ��ܼ�¼��
		map = null;
		map = new BitMap(initialRecordNum);
		if (map.toFile(mapFileName)) {
			return sum;
		}
		return 0;
	}

	/**
	 * ɾ�����tableName��������conditions������
	 * 
	 * @param conditions
	 * @return
	 */
	@Deprecated
	public int deleteFromTable(WhereCondition[] conditions) {
		if (conditions == null || conditions.length == 0)
			return deleteAllFromTable();
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return 0;
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
			return 0;
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
			return 0;
		}
		int totalRecord = map.getSetNum();
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return 0;
		}

		int sum = 0;// Ӱ���¼��
		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
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
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions)) {
						map.clear(i + 1);
						sum++;
					}
				}
			}
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		if (map.toFile(mapFileName)) {
			return sum;
		}
		return 0;
	}

	/**
	 * ɾ�����tableName��������(conditions AndOrs��ɵ�����)������
	 * 
	 * @param conditions
	 * @param AndOrs
	 * @return ɾ����¼����
	 */
	public int deleteFromTable(WhereCondition[] conditions, int[] AndOrs) {
		if (AndOrs == null)// ����ҪAnd Or����
			return deleteFromTable(conditions);
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return 0;
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
			return 0;
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
			return 0;
		}
		int totalRecord = map.getSetNum();
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return 0;
		}

		int sum = 0;// Ӱ���¼��
		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
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
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions,AndOrs)) {
						map.clear(i + 1);
						sum++;
					}
				}
			}
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		if (map.toFile(mapFileName)) {
			return sum;
		}
		return 0;
	}

	
	/**
	 * update���tableName��������conditions�ļ�¼
	 * 
	 * @param setPairs
	 * @param conditions
	 * @return
	 */
	@Deprecated
	public int updateTable(UpdateSetPair[] setPairs, WhereCondition[] conditions) {
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return 0;
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
			return 0;
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
			return 0;
		}
		int totalRecord = map.getSetNum();
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return 0;
		}

		int sum = 0;// Ӱ��ļ�¼��
		/* ��ȡ�����ļ� */
		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		try {
			dataStream = new RandomAccessFile(dataFile, "rw");
			boolean[] bits = map.getMap();
			byte[] buf = new byte[recordSize];
			String[] oneRecord;
			byte[] newRecordBuf;
			for (int i = 0; i < bits.length; i++) {
				if (bits[i]) {
					// ��ȡһ����¼������buf��
					dataStream.seek(i * recordSize);
					dataStream.readFully(buf);
					// ��buf����ת��ΪString[]
					oneRecord = Field.parseBytesToStrings(buf, fields);
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions)) {
						UpdateSetPair.updateRecordByFields(oneRecord, fields,
								setPairs);
						newRecordBuf = Field.parseStringsToBytes(oneRecord,
								fields);
						dataStream.seek(i * recordSize);
						dataStream.write(newRecordBuf);
						sum++;
					}
				}
			}
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return sum;
	}

	/**
	 * update���tableName��������(conditions AndOrs��ɵ�����)�ļ�¼
	 * 
	 * @param setPairs
	 * @param conditions
	 * @param AndOrs
	 * @return Ӱ���¼����
	 */
	public int updateTable(UpdateSetPair[] setPairs,
			WhereCondition[] conditions, int[] AndOrs) {
		if (AndOrs == null)// ����ҪAnd Or����
			return updateTable(setPairs, conditions);
		// �������Ŀ¼
		File tableDir = new File(rootDir + tableName);
		boolean isDirectory = tableDir.isDirectory();
		if (!isDirectory) {
			System.out.println("���" + tableName + "������");
			return 0;
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
			return 0;
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
			return 0;
		}
		int totalRecord = map.getSetNum();
		if (totalRecord == 0) {
			// �ܼ�¼��Ϊ0����ֱ�ӷ���
			return 0;
		}

		int sum = 0;// Ӱ��ļ�¼��
		/* ��ȡ�����ļ� */

		String dataFileName = tableFilesPrefix + DataFileExt;
		File dataFile = new File(dataFileName);
		RandomAccessFile dataStream = null;
		try {
			dataStream = new RandomAccessFile(dataFile, "rw");
			boolean[] bits = map.getMap();
			byte[] buf = new byte[recordSize];
			String[] oneRecord;
			byte[] newRecordBuf;
			for (int i = 0; i < bits.length; i++) {
				if (bits[i]) {
					// ��ȡһ����¼������buf��
					dataStream.seek(i * recordSize);
					dataStream.readFully(buf);
					// ��buf����ת��ΪString[]
					oneRecord = Field.parseBytesToStrings(buf, fields);
					if (WhereCondition.checkRecordByFields(oneRecord, fields,
							conditions,AndOrs)) {
						UpdateSetPair.updateRecordByFields(oneRecord, fields,
								setPairs);
						newRecordBuf = Field.parseStringsToBytes(oneRecord,
								fields);
						dataStream.seek(i * recordSize);
						dataStream.write(newRecordBuf);
						sum++;
					}
				}
			}
			dataStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return sum;
	}

	/**
	 * ת��ΪDDL��Data Definition Language���ݿ�ģʽ�������ԣ�
	 * 
	 * @return create_table_sql
	 */
	public String toDDLString() {
		if (this.tableName == null) {
			return "��û��ָ���������";
		}
		String ddl = "Create Table " + this.tableName + "( ";
		if (this.tableFields != null) {
			int i = 0;
			for (i = 0; i < this.tableFields.length - 1; i++)
				ddl += this.tableFields[i].toDDLString() + ", ";
			ddl += this.tableFields[i].toDDLString();
		}
		ddl += " );";
		return ddl;
	}

	/**
	 * ����Table������
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String stuTableName = "student";
		Field[] fields = { new Field("ID", Field.BigintType),
				new Field("Name", Field.VarcharType) };
		Table stuTable = new Table();

		boolean createFlag;
		System.out.println(stuTable.toDDLString());
		createFlag = stuTable.createTable(stuTableName, fields);
		System.out.println(stuTable.toDDLString());
		System.out.println("createFlag:" + createFlag);

		BitMap map;
		// System.out.println("����bitmapλͼ��");
		// map = stuTable.getTableBitMap();
		// map.showMap();

		boolean insertFlag;
		String[] oneRecord = new String[2];
		// ����һЩ��¼
		oneRecord[0] = "123";
		oneRecord[1] = "hao";
		insertFlag = stuTable.insertIntoTable(oneRecord);
		System.out.println("insertFlag:" + insertFlag);
		oneRecord[0] = "12";
		oneRecord[1] = "asdf";
		insertFlag = stuTable.insertIntoTable(oneRecord);
		System.out.println("insertFlag:" + insertFlag);
		oneRecord[0] = "1";
		oneRecord[1] = "liu";
		insertFlag = stuTable.insertIntoTable(oneRecord);
		System.out.println("insertFlag:" + insertFlag);
		oneRecord[0] = "4";
		oneRecord[1] = "helloworld";
		insertFlag = stuTable.insertIntoTable(oneRecord);
		System.out.println("insertFlag:" + insertFlag);

		System.out.println("����bitmapλͼ��");
		map = stuTable.getTableBitMap();
		map.showMap();

		String[][] students;

		System.out.println("selectAllFromTable");
		students = stuTable.selectAllFromTable();
		System.out.println("�ܼ�¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}

		WhereCondition[] conditions = new WhereCondition[3];
		conditions[0] = new WhereCondition("id", WhereCondition.GreatThanOp,
				"3");
		conditions[1] = new WhereCondition("name", WhereCondition.LessEqualOp,
				"liu");
		conditions[2] = new WhereCondition("name", WhereCondition.LikeOp,
				"hao", true);
		System.out.println("Where Conditions:");
		for (int i = 0; i < conditions.length; i++) {
			System.out.println("    condition[" + i + "]"
					+ conditions[i].toString());
		}

		System.out.println("selectFromTable Where Conditions:");
		students = stuTable.selectFromTable(conditions);
		System.out.println("��¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}

		int num = 0;
		UpdateSetPair setPairs[] = new UpdateSetPair[2];
		setPairs[0] = new UpdateSetPair("id", "234");
		setPairs[1] = new UpdateSetPair("name", "ahaha12345678901234567890");
		for (int i = 0; i < setPairs.length; i++) {
			System.out.println(setPairs[i].toString());
		}
		System.out.println("updateTable Where Conditions:");
		num = stuTable.updateTable(setPairs, conditions);
		System.out.println("���¼�¼��:" + num);

		System.out.println("�ٴ�selectAllFromTable");
		students = stuTable.selectAllFromTable();
		System.out.println("�ܼ�¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("�ٴ�selectFromTable Where Conditions:");
		students = stuTable.selectFromTable(conditions);
		System.out.println("��¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("deleteFromTable Where Conditions:");
		num = stuTable.deleteFromTable(conditions);
		System.out.println("ɾ����¼��:" + num);

		System.out.println("����bitmapλͼ��");
		map = stuTable.getTableBitMap();
		map.showMap();

		students = stuTable.selectAllFromTable();
		System.out.println("now�ܼ�¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}

		/*
		System.out.println("deleteAllFromTable");
		num = stuTable.deleteAllFromTable();
		System.out.println("ɾ����¼��:" + num);
		*/
		
		System.out.println("����bitmapλͼ��");
		map = stuTable.getTableBitMap();
		map.showMap();

		students = stuTable.selectAllFromTable();
		System.out.println("now�ܼ�¼����" + students.length);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < students[i].length; j++) {
				System.out.print(students[i][j] + " ");
			}
			System.out.println();
		}
	}
}
