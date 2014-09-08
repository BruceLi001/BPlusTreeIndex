package table;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Field {
	String fieldName;
	int fieldType;
	int fieldSize;
	
	public static final int TypeError = -1;
	public static final int IntType = 1;
	public static final int BigintType = 2;
	public static final int VarcharType = 3;
	public static final int CharType = 4;
	
	public static final int DefaultCharSize = 20;
	
	/**
	 * 
	 * @param name
	 * @param type
	 */
	public Field(String name,int type){
		fieldName = name.toUpperCase();
		fieldType = type;
		switch (fieldType) {
		case IntType:
			fieldSize = 4;
			break;
		case BigintType:
			fieldSize = 8;
			break;
		default:
			fieldSize = DefaultCharSize;
			break;
		}
	}
	
	/**
	 * �������췽��
	 * @param name
	 * @param type
	 * @param size
	 */
	public Field(String name,int type,int size){
		fieldName = name.toUpperCase();
		fieldType = type;
		if(size<=0)
			size = DefaultCharSize;
		fieldSize = size;
		switch (fieldType) {
		case IntType:
			fieldSize = 4;
			break;
		case BigintType:
			fieldSize = 8;
			break;
		}
	}
	
	/**
	 * �õ�fieldName
	 * @return
	 */
	public String getFieldName(){
		return fieldName;
	}
	
	/**
	 * ת�����ַ���fieldName-fieldType-fieldSize
	 */
	public String toString(){
		String temp="";
		temp += fieldName;
		temp += "-";
		temp += fieldType;
		temp += "-";
		temp += fieldSize;
		return temp;
	}
	
	/**
	 * ת��ΪDDL��Data Definition Language���ݿ�ģʽ�������ԣ�
	 * @return
	 */
	public String toDDLString(){
		String ddl = fieldName + " ";
		switch (fieldType) {
		case IntType:
			ddl += "Int";
			break;
		case BigintType:
			ddl += "BigInt";
			break;
		case VarcharType:
			ddl += "Varchar";
			break;
		case CharType:
			ddl += "Char";
			break;
		}
		ddl += "("+fieldSize+")";
		return ddl;
	}
	
	/**
	 * ��byte[]����ת��Ϊһ��String[]��¼
	 * @param buf
	 * @return
	 */
	public static String[] parseBytesToStrings(byte[] buf, Field[] fields){
		int fieldCount = fields.length;
		int recordSize = 0;
		for(int i=0;i<fieldCount;i++){
			recordSize+=fields[i].fieldSize;
		}
		String[] oneRecord = new String[fieldCount];
		int offset = 0;
		for(int i=0;i<fieldCount;i++){
			int value = 0;
			int tempInt = 0;
			long bigValue = 0;
			long tempLong = 0;
			int charNum = 0;
			switch (fields[i].fieldType) {
			case IntType:
				//System.out.println("parseBytesToStrings:IntType");
				value = 0;
				tempInt = 0;
				for(int j=0;j<4;j++){
					tempInt = 0xff & buf[offset+j];
					value |= tempInt << j*8;
					//value |= (buf[offset+j])<<(j*8);//С�ˣ���λ��ǰ����λ�ں�
				}
				//for(int j=0;j<4;j++)
				//	System.out.print(Integer.toHexString(0xff&buf[offset+j])+" ");
				//System.out.println();
				oneRecord[i] = String.valueOf(value);
				//System.out.println(oneRecord[i]);
				//System.out.println(Integer.toHexString(value));
				break;
			case BigintType:
				bigValue = 0;
				tempLong = 0;
				for(int j=0;j<8;j++){
					tempLong = 0xff & buf[offset+j];
					bigValue |= tempLong << j*8;
					//bigValue |= (buf[offset+j])<<(j*8);//С�ˣ���λ��ǰ����λ�ں�
				}
				oneRecord[i] = String.valueOf(bigValue);
				break;
			case VarcharType:
				byte[] varStr = new byte[fields[i].fieldSize];
				for(int j=0;j<varStr.length;j++)
					varStr[j] = buf[offset+j];
				charNum = 0;
				for(int j=0;j<varStr.length;j++){
					if(varStr[j]==0)
						break;
					charNum++;
				}
				oneRecord[i] = new String(varStr,0,charNum);
				break;
			case CharType:
				byte[] charStr = new byte[fields[i].fieldSize];
				for(int j=0;j<charStr.length;j++)
					charStr[j] = buf[offset+j];
				charNum = 0;
				for(int j=0;j<charStr.length;j++){
					if(charStr[j]==0)
						break;
					charNum++;
				}
				oneRecord[i] = new String(charStr,0,charNum);
				break;
			}
			offset+=fields[i].fieldSize;
		}
		return oneRecord;
	}
	
	/**
	 * ��һ��String[]��¼ת��Ϊbyte[]
	 * @param oneRecord
	 * @return
	 */
	public static byte[] parseStringsToBytes(String[] oneRecord, Field[] fields){
		int fieldCount = fields.length;
		int recordSize = 0;
		for(int i=0;i<fieldCount;i++){
			recordSize+=fields[i].fieldSize;
		}
		byte[] buf = new byte[recordSize];
		int offset = 0;
		for(int i=0;i<fieldCount;i++){
			switch (fields[i].fieldType) {
			case IntType:
				//System.out.println("parseStringsToBytes:IntType");
				int value = Integer.parseInt(oneRecord[i]);
				//System.out.println(oneRecord[i]);
				//System.out.println(Integer.toHexString(value));
				for(int j=0;j<4;j++)
					buf[offset+j] = (byte)(value>>(j*8));//С�ˣ���λ��ǰ����λ�ں�
				//for(int j=0;j<4;j++)
				//	System.out.print(Integer.toHexString(0xff&buf[offset+j])+" ");
				//System.out.println();
				break;
			case BigintType:
				long bigValue = Long.parseLong(oneRecord[i]);
				for(int j=0;j<8;j++)
					buf[offset+j] = (byte)(bigValue>>(j*8));//С�ˣ���λ��ǰ����λ�ں�
				break;
			case VarcharType:
				byte[] varStr = oneRecord[i].getBytes();
				for(int j=0;j<varStr.length;j++)
					buf[offset+j] = varStr[j];
				for(int j=varStr.length;j<fields[i].fieldSize;j++)
					buf[offset+j] = 0;
				break;
			case CharType:
				byte[] charStr = oneRecord[i].getBytes();
				for(int j=0;j<charStr.length;j++)
					buf[offset+j] = charStr[j];
				for(int j=charStr.length;j<fields[i].fieldSize;j++)
					buf[offset+j] = 0;
				break;
			}
			offset+=fields[i].fieldSize;
		}
		return buf;
	}
	
	/**
	 * ����oneRecord��String���飬�����غϷ�������
	 * @param oneRecord
	 * @param fields
	 * @return
	 */
	public static String[] checkRecordByFields(String[] oneRecord, Field[] fields){
		String[] legalRecord=null;
		int fieldCount = fields.length;
		if(fieldCount!=oneRecord.length){
			legalRecord = new String[fieldCount];
		}else{
			legalRecord = oneRecord;
		}
		for(int i=0;i<fieldCount;i++){
			switch (fields[i].fieldType) {
			case IntType:
				try{
					Integer.parseInt(legalRecord[i]);
				}catch(Exception e){
					legalRecord[i]="0";
				}
				break;
			case BigintType:
				try{
					Long.parseLong(legalRecord[i]);
				}catch(Exception e){
					legalRecord[i]="0";
				}
				break;
			case VarcharType:
				if(legalRecord[i]==null){
					legalRecord[i]="";
				}
				if(legalRecord[i].length()>fields[i].fieldSize){
					legalRecord[i] = legalRecord[i].substring(0, fields[i].fieldSize);
				}
				break;
			case CharType:
				if(legalRecord[i]==null){
					legalRecord[i]="";
				}
				if(legalRecord[i].length()>fields[i].fieldSize){
					legalRecord[i] = legalRecord[i].substring(0, fields[i].fieldSize);
				}
				break;
			default:
				legalRecord[i]="";
				break;
			}
		}
		
		return legalRecord;
	}
	
	/**
	 * ��ʾ����������ϸ��Ϣ
	 */
	public void showDetail(){
		System.out.print("Name:"+fieldName+"; Type:");
		switch (fieldType) {
		case IntType:
			System.out.print("Int");
			break;
		case BigintType:
			System.out.print("BigInt");
			break;
		case VarcharType:
			System.out.print("Varchar");
			break;
		case CharType:
			System.out.print("Char");
			break;
		}
		System.out.println("; Size:"+fieldSize+"B");
	}
	
	/**
	 * ����fieldName-fieldType-fieldSize��Field����
	 * @param filedString
	 * @return
	 */
	public static Field parseString(String fieldString){
		//String[] s = fieldString.split("-");
		//for(int i=0;i<s.length;i++)
		//	System.out.println(s[i]);
		StringTokenizer st = new StringTokenizer(fieldString,"-");
		String name = st.nextToken();
		int type = Integer.parseInt(st.nextToken());
		int size = Integer.parseInt(st.nextToken());
		return new Field(name,type,size);
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽Field����
	 * @param fieldDDLStr
	 * @return
	 */
	public static Field parseDDLString(String fieldDDLStr){
		String ddl = fieldDDLStr.trim();
		Field f = null;
		String name,type,size;
		int p1,p2,p3,sizeInt;

		//�õ��ֶ���
		p1 = ddl.indexOf(' ');
		if(p1==-1){//��ʽ����
			return null;
		}
		name = ddl.substring(0, p1);
		
		//�õ��ֶ�����
		p2 = ddl.indexOf('(', p1+1);
		if(p2 == -1){//û�����ţ�int��bigint,��Ĭ�ϴ�С��VARCHAR��CHAR
			type = ddl.substring(p1+1).trim();
			if(type.equalsIgnoreCase("INT"))
				f = new Field(name,IntType);
			else if(type.equalsIgnoreCase("BIGINT"))
				f = new Field(name,BigintType);
			else{//Ĭ�ϴ�С��VARCHAR��CHAR
				if(type.equalsIgnoreCase("VARCHAR"))
					f = new Field(name,VarcharType);
				else if(type.equalsIgnoreCase("CHAR"))
					f = new Field(name,CharType);
				else{//��֧�ֵ��ֶ�����
					return null;
				}
			}
		}else{//varchar��char
			type = ddl.substring(p1+1,p2).trim();
			p3 = ddl.indexOf(')', p2+1);
			if(p3==-1){//��ʽ����
				return null;
			}
			size = ddl.substring(p2+1, p3).trim();
			try{
				sizeInt = Integer.parseInt(size);
			}catch(Exception e){
				sizeInt = 0;
			}
			if(type.equalsIgnoreCase("VARCHAR"))
				f = new Field(name,VarcharType,sizeInt);
			else if(type.equalsIgnoreCase("CHAR"))
				f = new Field(name,CharType,sizeInt);
			else{//��֧�ֵ��ֶ�����,���ʽ����
				return null;
			}
		}
		
		return f;
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽һ��Field[]����
	 * @param fieldDDLStr
	 * @return
	 */
	public static Field[] getFieldsFromDDLString(String fieldDDLStr){
		String ddl = fieldDDLStr.trim();
		Field f;
		ArrayList<Field> fields = new ArrayList<Field>(5);
		int posStart,posOfComma;
		String subDDL;
		
		posStart = 0;
		posOfComma = ddl.indexOf(',',posStart);
		while(posOfComma!=-1){
			subDDL = ddl.substring(posStart,posOfComma);
			f = Field.parseDDLString(subDDL);
			fields.add(f);
			posStart = posOfComma+1;//������һ��
			posOfComma = ddl.indexOf(',',posStart);
		}
		//���һ��
		subDDL = ddl.substring(posStart);
		f = Field.parseDDLString(subDDL);
		fields.add(f);
		//ת��
		Field[] fs = new Field[fields.size()];
		fields.toArray(fs);
		return fs;
	}
	
	/**
	 * ���fields���ֶ����Ƿ�Ψһ
	 * @param fields
	 * @return
	 */
	public static boolean checkFieldsNamesUnique(Field[] fields){
		for(int i=0;i<fields.length;i++){
			String curName = fields[i].fieldName;
			for(int j=i+1;j<fields.length;j++){
				String chechName = fields[j].fieldName;
				if(curName.equals(chechName))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * ��fileds�õ��ֶ���colName���ֶ�����
	 * @param fileds
	 * @param colName
	 * @return
	 */
	public static int getTypeByNameFromFields(Field[] fields,String colName){
		String colNameUpper = colName.toUpperCase();
		for(int i=0;i<fields.length;i++){
			String curName = fields[i].fieldName;
			if(curName.equals(colNameUpper))
				return fields[i].fieldType;
		}
		return TypeError;
	}
	
	/**
	 * ��oneRecord�еõ��Զ��ֶ�����ֵ
	 * @return
	 */
	public static String getValueByFieldName(String[] oneRecord, Field[] fields,String colName){
		int fieldCount = fields.length;
		if(fieldCount!=oneRecord.length){
			return "";
		}
		String colNameUpper = colName.toUpperCase();
		int i;
		for(i=0;i<fieldCount;i++){
			String curFiledName = fields[i].fieldName;
			if(colNameUpper.equals(curFiledName))
				break;
		}
		if(i<fieldCount){
			return oneRecord[i];
		}else{
			return "";
		}
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		Field f1 = new Field("ID",Field.IntType);
		f1.showDetail();
		System.out.println(f1.toString());
		Field f2 = Field.parseString("sno-2-34");
		f2.showDetail();
		System.out.println(f2.toString());
		StringTokenizer o = new StringTokenizer(f2.toString(),"-");
		System.out.println(o.countTokens());
		while (o.hasMoreTokens()) {
	         System.out.println(o.nextToken());
	     }

		System.out.println(o.toString());
		*/
		
		/*
		int fieldCount = 3;
		Field[] fields=new Field[fieldCount];
		fields[0] = new Field("ID",Field.IntType);
		fields[1] = new Field("Name",Field.CharType);
		fields[2] = new Field("SNO",Field.BigintType);
		int recordSize = 0;
		for(int i=0;i<fieldCount;i++){
			recordSize+=fields[i].fieldSize;
			fields[i].showDetail();
			System.out.println(fields[i].toDDLString());
		}
		System.out.println("recordSize:"+recordSize);
		String[] oneRecord = {"-12332","����dfsd","-12345"};
		for(int i=0;i<fieldCount;i++){
			System.out.println(oneRecord[i]);
		}
		
		byte[] buf = Field.parseStringsToBytes(oneRecord, fields);
		for(int i=0;i<buf.length;i++){
			System.out.print(Integer.toHexString(0xff&buf[i])+" ");
		}
		System.out.println();
		System.out.println("buf.length:"+buf.length+","+new String(buf));
		
		String[] record2 = Field.parseBytesToStrings(buf, fields);
		for(int i=0;i<fieldCount;i++){
			System.out.println(record2[i]);
		}
		
		byte[] buf2 = Field.parseStringsToBytes(record2, fields);
		for(int i=0;i<buf2.length;i++){
			System.out.print(Integer.toHexString(0xff&buf[i])+" ");
		}
		System.out.println();
		System.out.println("buf2.length:"+buf2.length+","+new String(buf2));
		*/
		
		/*
		Field f1;
		f1 = parseDDLString("id  int");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("id  int()");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("id  bigint ");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("name  char()");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("name  varchar");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("sir  varchar(1)");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("sir  varchar(");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		
		f1 = parseDDLString("sir  varchar)");
		if(f1==null){
			System.out.println("������");
		}else{
			System.out.println(f1.toString()+",��:"+f1.toDDLString());
		}
		*/
		
		
		Field[] fields = Field.getFieldsFromDDLString("id int,name char,class varchar(10)");
		for(int i=0;i<fields.length;i++){
			if(fields[i]==null){
				System.out.println("������");
				continue;
			}
			System.out.println(fields[i].toString()+",��:"+fields[i].toDDLString());
		}
		
		System.out.println("checkFieldsNamesUnique:"+checkFieldsNamesUnique(fields));
		
		
		/*
		byte[] buf=new byte[4];
		int value = -123;
		System.out.println(String.valueOf(value));
		for(int j=0;j<4;j++)
			buf[j] = (byte)(value>>(j*8));//С�ˣ���λ��ǰ����λ�ں�
		System.out.println(Integer.toHexString(value));
		for(int i=0;i<buf.length;i++){
			System.out.print(Integer.toHexString(0xff&buf[i])+" ");
		}
		System.out.println();
		
		int value2 = 0;
		for(int j=0;j<4;j++)
			value2 |= (buf[j])<<(j*8);//С�ˣ���λ��ǰ����λ�ں�
		System.out.println(Integer.toHexString(value2));
		System.out.println(String.valueOf(value2));
		*/
	}
}
