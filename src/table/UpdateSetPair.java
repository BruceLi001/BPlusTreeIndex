package table;

import java.util.ArrayList;

public class UpdateSetPair {
	
	String fieldName;
	String newValue;
	
	/**
	 * ���췽��
	 * @param fieldName
	 * @param newValue
	 */
	public UpdateSetPair(String fieldName, String newValue){
		this.fieldName = fieldName.toUpperCase();
		this.newValue = newValue;
	}
	
	/**
	 * ����fields��Ϣ������oneRecord��setPairsָ��������
	 * @param oneRecord
	 * @param fields
	 * @param setPairs
	 */
	public static void updateRecordByFields(String[] oneRecord, Field[] fields, UpdateSetPair[] setPairs){
		if(setPairs==null)
			return;
		for(int i=0;i<setPairs.length;i++){
			if(setPairs[i]==null)
				continue;
			setPairs[i].updateRecordByFields(oneRecord, fields);
		}
	}
	
	/**
	 * ����fields��Ϣ������oneRecord��this_UpdateSetPair_objectָ��������
	 * @param oneRecord
	 * @param fields
	 */
	public void updateRecordByFields(String[] oneRecord, Field[] fields){
		// ��������ֶ�
		for (int i = 0; i < fields.length; i++) {
			//�Ƚ��ֶ���
			if(this.fieldName.equalsIgnoreCase(fields[i].fieldName)){
				String newSetValue = this.newValue;
				switch (fields[i].fieldType) {
				case Field.IntType:
					try {
						Integer.parseInt(newSetValue);
					} catch (Exception e) {
						newSetValue = "0";
					}
					break;
				case Field.BigintType:
					try {
						Long.parseLong(newSetValue);
					} catch (Exception e) {
						newSetValue = "0";
					}
					break;
				default:
					if(newSetValue.length()>fields[i].fieldSize){
						newSetValue = newSetValue.substring(0, fields[i].fieldSize);
					}
					break;
				}
				oneRecord[i] = newSetValue;
			}
		}
	}
	
	/**
	 * ת��Ϊ�ַ���
	 */
	public String toString(){
		return "set "+this.fieldName+"="+this.newValue;
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽UpdateSetPair����
	 * @param fieldDDLStr
	 * @return
	 */
	public static UpdateSetPair parseDDLString(String fieldDDLStr){
		String ddl = fieldDDLStr.trim();
		UpdateSetPair setPair = null;
		String name,value;
		int p;
		
		p = ddl.indexOf('=');
		if(p==-1){//������
			return null;
		}
		name = ddl.substring(0,p).trim();
		value = ddl.substring(p+1).trim();
		setPair = new UpdateSetPair(name,value);
		
		return setPair;
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽һ��UpdateSetPair[]����
	 * @param setPairDDLStr
	 * @return
	 */
	public static UpdateSetPair[] getPairsFromDDLString(String setPairDDLStr){
		String ddl = setPairDDLStr.trim();
		UpdateSetPair pair;
		ArrayList<UpdateSetPair> pairs = new ArrayList<UpdateSetPair>(5);
		int posStart,posOfComma;
		String subDDL;
		
		//��һ����Ϊ set�������һ���ո�
		if(! ddl.substring(0, 3).equalsIgnoreCase("SET") ){
			System.out.println("�����ˣ�Ӧ�ԡ�set����ͷ");
			return null;
		}
		posStart = ddl.indexOf(' ')+1;
		posOfComma = ddl.indexOf(',',posStart);
		while(posOfComma!=-1){
			subDDL = ddl.substring(posStart,posOfComma);
			pair = UpdateSetPair.parseDDLString(subDDL);
			pairs.add(pair);
			posStart = posOfComma+1;//������һ��
			posOfComma = ddl.indexOf(',',posStart);
		}
		//���һ��
		subDDL = ddl.substring(posStart);
		pair = UpdateSetPair.parseDDLString(subDDL);
		pairs.add(pair);
		//ת��
		UpdateSetPair[] ps = new UpdateSetPair[pairs.size()];
		pairs.toArray(ps);
		return ps;
	}
	
	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		UpdateSetPair setPairs[];
		setPairs = UpdateSetPair.getPairsFromDDLString("set id=5,sdf=435,34= 3");
		for(int i=0;i<setPairs.length;i++){
			System.out.println(setPairs[i].toString());
		}
		
		setPairs = UpdateSetPair.getPairsFromDDLString("sEt id=,sdf=435,34=0");
		for(int i=0;i<setPairs.length;i++){
			if(setPairs[i]==null){
				System.out.println("������");
				continue;
			}
			System.out.println(setPairs[i].toString());
		}
		
		/*
		UpdateSetPair setPairs[] = new UpdateSetPair[2];
		
		setPairs[0] = new UpdateSetPair("id","234");
		setPairs[1] = new UpdateSetPair("name","wahaha12345678901234567890");
		for(int i=0;i<setPairs.length;i++){
			System.out.println(setPairs[i].toString());
		}
		
		Field[] fields = {new Field("ID",Field.BigintType),new Field("Name",Field.VarcharType)};
		
		String[] oneRecord = new String[2];
		oneRecord[0] = "123";oneRecord[1] = "hao";
		for(int i=0;i<oneRecord.length;i++){
			System.out.print(oneRecord[i]+" ");
		}
		System.out.println();
		
		setPairs[0].updateRecordByFields(oneRecord, fields);
		for(int i=0;i<oneRecord.length;i++){
			System.out.print(oneRecord[i]+" ");
		}
		System.out.println();
		
		setPairs[1].updateRecordByFields(oneRecord, fields);
		for(int i=0;i<oneRecord.length;i++){
			System.out.print(oneRecord[i]+" ");
		}
		System.out.println();
		
		oneRecord[0] = "123";oneRecord[1] = "hao";
		for(int i=0;i<oneRecord.length;i++){
			System.out.print(oneRecord[i]+" ");
		}
		System.out.println();
		System.out.println("updateRecordByFields(oneRecord, fields, setPairs)");
		updateRecordByFields(oneRecord, fields, setPairs);
		for(int i=0;i<oneRecord.length;i++){
			System.out.print(oneRecord[i]+" ");
		}
		System.out.println();
		*/
	}
	
}
