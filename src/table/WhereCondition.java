package table;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhereCondition {
	
	String fieldName;		//�ֶ���
	int comparisonOp;			//��ϵ������
	String referencValue;	//����ֵ
	boolean negation;
	
	public static final int EqualOp = 1;
	public static final int LessThanOp = 2;
	public static final int GreatThanOp = 3;
	public static final int LessEqualOp = 4;
	public static final int GreatEqualOp = 5;
	public static final int NotEqualOp = 6;
	public static final int LikeOp = 7;
	
	public static final int RelationAndOp = 11;
	public static final int RelationOrOp = 12;
	public static final int RelationNotOp = 13;
	
	/**
	 * ���췽��
	 * @param fieldName
	 * @param relationOp
	 * @param referencValue
	 */
	public WhereCondition(String fieldName, int comparisonOp, String referencValue){
		this.fieldName = fieldName.toUpperCase();
		this.comparisonOp = comparisonOp;
		this.referencValue = referencValue;
		this.negation = false;
	}
	
	/**
	 * ���췽��2
	 * @param fieldName
	 * @param comparisonOp
	 * @param referencValue
	 * @param negation
	 */
	public WhereCondition(String fieldName, int comparisonOp, String referencValue, boolean negation){
		this.fieldName = fieldName.toUpperCase();
		this.comparisonOp = comparisonOp;
		this.referencValue = referencValue;
		this.negation = negation;
	}
	
	/**
	 * toString()
	 */
	public String toString(){
		String temp="";
		if(this.negation)
			temp += "Not";
		temp += "("+this.fieldName+" ";
		switch (this.comparisonOp) {
		case EqualOp:
			temp += "=";
			break;
		case LessThanOp:
			temp += "<";
			break;
		case GreatThanOp:
			temp += ">";
			break;
		case LessEqualOp:
			temp += "<=";
			break;
		case GreatEqualOp:
			temp += ">=";
			break;
		case NotEqualOp:
			temp += "!=";
			break;
		case LikeOp:
			temp += "LIKE";
			break;
		}
		temp += " "+this.referencValue+")";
		return temp;
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽WhereCondition����
	 * @param whereDDLStr
	 * @return
	 */
	public static WhereCondition parseDDLString(String whereDDLStr){
		String ddl = whereDDLStr.trim();
		//System.out.println(ddl);
		if(ddl.charAt(0)=='('){//��һ���ַ�Ϊ'('�������һ���ַ�Ϊ')',�������
			ddl = ddl.substring(1,ddl.length()-1).trim();//ȥ������
			//System.out.println(ddl);
		}
		//System.out.println("Str:"+ddl);
		WhereCondition wc = null;
		String name,operator,value;
		int opInt = 1;
		boolean notFlag = false;
		int p1,p2,i;
		
		//�����ֶ����Ľ���
		//p1 = ddl.indexOf(' ');
		i=0;
		while(i<ddl.length()){
			if(!Character.isLetter(ddl.charAt(i)))//�����ַ�(�ո�Ҳ�����ַ�)
				break;
			i++;
		}
		p1 = i;
		if(p1==ddl.length()){//��ʽ����
			return null;
		}
		p2 = ddl.indexOf('(');
		//System.out.println("first:"+p1+","+p2);
		if(p2!=-1 && p2<p1){//��NOT���Σ���NOT�����'('
			p1=p2;
		}
		//�õ��ֶ���
		name = ddl.substring(0, p1);
		if(name.equalsIgnoreCase("NOT")){
			//�ж��Ƿ���NOT����
			notFlag = true;
			p2 = ddl.indexOf('(', p1);
			if(p2!=-1){//NOT����'('
				p1 = p2;
				p2 = ddl.indexOf(')', p1+1);//�ҵ�������')'
				if(p2==-1){//��ʽ����
					return null;
				}
				//System.out.println("in not:"+p1+","+p2);
				ddl = ddl.substring(p1+1, p2).trim();
				//System.out.println(ddl);
				p1 = 0;//���¶�λp1
			}
			//�����ֶ����Ľ���
			while(ddl.charAt(p1)==' '){	p1++; }//�����ո�
			//p2 = ddl.indexOf(' ', p1+1);
			i=p1+1;
			while(i<ddl.length()){
				if(!Character.isLetter(ddl.charAt(i)))//�����ַ�
					break;
				i++;
			}
			p2 = i;
			//System.out.println("after not:"+p1+","+p2);
			if(p2==ddl.length()){//��ʽ����
				return null;
			}
			name = ddl.substring(p1, p2).trim();
			p1 = p2;
		}
		//System.out.println("name:"+name);
		
		//�õ��Ƚϲ�����,p1Ϊ�Ƚϲ������Ŀ�ʼ
		while(ddl.charAt(p1)==' '){	p1++; }//�����ո�
		//�����Ƚϲ������Ľ���
		//p2 = ddl.indexOf(' ', p1);
		String subStrUp = ddl.substring(p1).toUpperCase();
		if(subStrUp.startsWith("LIKE")){
			p2 = p1+4;
		}else{
			i=p1+1;
			while(i<ddl.length()){
				char ci = ddl.charAt(i);
				if(ci==' ' || (ci!='=' && ci!='<' && ci!='>' && ci!='!'))//���ַ�
					break;
				i++;
			}
			p2 = i;
		}
		//System.out.println("second:"+p1+","+p2);
		if(p2==ddl.length()){//��ʽ����
			return null;
		}
		operator = ddl.substring(p1, p2).trim();
		//System.out.println("operator:"+operator);
		switch (operator.length()) {
		case 1:// = < >
			switch (operator.charAt(0)) {
			case '=':
				opInt = EqualOp;
				break;
			case '<':
				opInt = LessThanOp;
				break;
			case '>':
				opInt = GreatThanOp;
				break;
			default://��ʽ����
				return null;
			}
			break;
		case 2:// <= >= !=
			if(operator.charAt(1)!='='){//��ʽ����
				return null;
			}
			switch (operator.charAt(0)) {
			case '<':
				opInt = LessEqualOp;
				break;
			case '>':
				opInt = GreatEqualOp;
				break;
			case '!':
				opInt = NotEqualOp;
				break;
			default://��ʽ����
				return null;
			}
			break;
		case 4://like
			if(operator.equalsIgnoreCase("LIKE")){
				opInt = LikeOp;
			}else{//��ʽ����
				return null;
			}
			break;
		default://��ʽ����
			return null;
		}
		//�õ��ο�ֵ
		value = ddl.substring(p2).trim();
		//System.out.println("value:"+value);
		wc = new WhereCondition(name,opInt,value,notFlag);
		
		return wc;
	}
	
	/**
	 * ����DDL��Data Definition Language����䵽һ��WhereCondition[]����
	 * @param wheresDDLString
	 * @param andOrsList
	 * @return
	 */
	public static WhereCondition[] getWheresFromDDLString(String wheresDDLString, ArrayList<Integer> andOrsList){
		String ddl = wheresDDLString.trim();
		String ddlUpper = ddl.toUpperCase();
		WhereCondition wc;
		ArrayList<WhereCondition> wheres = new ArrayList<WhereCondition>(5);
		int posAnd,posOr,posStart,posOneWC;
		String subDDL;
		
		//��һ����Ϊ where�������һ���ո�
		if(! ddl.substring(0, 5).equalsIgnoreCase("WHERE") ){
			System.out.println("�����ˣ�Ӧ�ԡ�where����ͷ");
			return null;
		}
		posStart = ddl.indexOf(' ')+1;
		if(posStart == -1){
			System.out.println("��ʽ����Ӧ�ԡ�where����ͷ,�����һ���ո�");
			return null;
		}
		//����"And" �� "Or"
		posAnd = ddlUpper.indexOf("AND",posStart);
		posOr = ddlUpper.indexOf("OR",posStart);
		if(posAnd==-1 && posOr==-1){		//û��"And" �� "Or"
			posOneWC = -1;
		}else if(posAnd==-1 && posOr!=-1){	//��"Or"
			posOneWC = posOr;
			andOrsList.add(new Integer(WhereCondition.RelationOrOp));
		}else if(posAnd!=-1 && posOr==-1){	//��"And"
			posOneWC = posAnd;
			andOrsList.add(new Integer(WhereCondition.RelationAndOp));
		}else{								//����С��Ϊ׼
			if(posAnd>posOr){	//or
				posOneWC = posOr;
				andOrsList.add(new Integer(WhereCondition.RelationOrOp));
			}else{				//and
				posOneWC = posAnd;
				andOrsList.add(new Integer(WhereCondition.RelationAndOp));
			}
		}
		while(posOneWC!=-1){
			subDDL = ddl.substring(posStart,posOneWC);
			//System.out.println(subDDL);
			wc = WhereCondition.parseDDLString(subDDL);
			wheres.add(wc);
			posStart = posOneWC+3;//������һ��
			//����"And" �� "Or"
			posAnd = ddlUpper.indexOf("AND",posStart);
			posOr = ddlUpper.indexOf("OR",posStart);
			if(posAnd==-1 && posOr==-1){		//û��"And" �� "Or"
				posOneWC = -1;
			}else if(posAnd==-1 && posOr!=-1){	//��"Or"
				posOneWC = posOr;
				andOrsList.add(new Integer(WhereCondition.RelationOrOp));
			}else if(posAnd!=-1 && posOr==-1){	//��"And"
				posOneWC = posAnd;
				andOrsList.add(new Integer(WhereCondition.RelationAndOp));
			}else{								//����С��Ϊ׼
				if(posAnd<posOr){	//or
					posOneWC = posOr;
					andOrsList.add(new Integer(WhereCondition.RelationOrOp));
				}else{				//and
					posOneWC = posAnd;
					andOrsList.add(new Integer(WhereCondition.RelationAndOp));
				}
			}
		}
		//���һ��
		subDDL = ddl.substring(posStart);
		//System.out.println(subDDL);
		wc = WhereCondition.parseDDLString(subDDL);
		wheres.add(wc);
		
		//ת��
		WhereCondition[] wcs = new WhereCondition[wheres.size()];
		wheres.toArray(wcs);
		return wcs;
	}
	
	/**
	 * ��getWheresFromDDLString֮�����andOrsList��һ��int[]
	 * @param andOrsList
	 * @return
	 */
	public static int[] getAndOrs(ArrayList<Integer> andOrsList){
		int andOrNum = andOrsList.size();
		//System.out.println("andOrNum:"+andOrNum);
		int[] andOrs = new int[andOrNum];
		for(int i=0;i<andOrNum;i++){
			andOrs[i] = andOrsList.get(i).intValue();
		}
		return andOrs;
	}
	
	/**
	 * ����fields��Ϣ���ж�oneRecord��¼�Ƿ����conditions
	 * @param oneRecord
	 * @param fields
	 * @param conditions
	 * @return
	 */
	@Deprecated
	public static boolean checkRecordByFields(String[] oneRecord,Field[] fields,WhereCondition[] conditions){
		if(conditions==null || conditions.length==0)
			return true;
		boolean compareFlag = true;
		boolean curFlag;
		//�����������
		for (int i = 0; i < conditions.length; i++) {
			if(conditions[i]==null)
				continue;
			curFlag = conditions[i].checkRecordByFields(oneRecord, fields);
			compareFlag = compareFlag && curFlag;
			//���һ���������ж�
		}
		return compareFlag;
	}
	
	/**
	 * ����fields��Ϣ���ж�oneRecord��¼�Ƿ����(conditions AndOrs��ɵ�����)
	 * @param oneRecord
	 * @param fields
	 * @param conditions
	 * @param AndOrs
	 * @return
	 */
	public static boolean checkRecordByFields(String[] oneRecord,Field[] fields,WhereCondition[] conditions, int[] AndOrs){
		if(conditions==null || conditions.length==0)
			return true;
		if(AndOrs == null)// ����ҪAnd Or����
			return checkRecordByFields(oneRecord, fields, conditions);
		if(conditions.length != (AndOrs.length+1)){
			//������Ӧ�ñȲ�������һ��
			return false;
		}
		
		boolean[] bools = new boolean[conditions.length];
		//�����������
		for (int i = 0; i < conditions.length; i++) {
			if(conditions[i]==null){
				bools[i] = true;
				continue;
			}
			bools[i] = conditions[i].checkRecordByFields(oneRecord, fields);
		}
		return testBoolAndOrs(bools, AndOrs);
	}
	
	/**
	 * �ж�һ��bools[]ֵ AndOrs[]������Ľ��
	 * @param bools
	 * @param AndOrs
	 * @return
	 */
	static boolean testBoolAndOrs(boolean[] bools, int[] AndOrs){
		if(bools.length != (AndOrs.length+1)){
			//������Ӧ�ñȲ�������һ��
			return false;
		}
		if(bools.length==1){
			return bools[0];
		}
		Stack<Boolean> booleans = new Stack<Boolean>();
		Stack<Integer> ops = new Stack<Integer>();
		
		booleans.push(new Boolean(bools[0]));
		ops.push(new Integer(AndOrs[0]));
		
		int oldOp,newOp;
		boolean flag1,flag2,flag1OpFlag2;
		int i;
		for(i=1;i<AndOrs.length;i++){
			oldOp = ops.peek().intValue();	//�鿴ջ��������
			newOp = AndOrs[i];
			flag1 = booleans.peek().booleanValue();	//�鿴ջ��������
			flag2 = bools[i];
			if(newOp>=oldOp){
				//flag1 && flag2 || flag3�����
				//flag1 && flag2 && flag3�����
				//flag1 || flag2 || flag3�����
				booleans.pop();	//�Ƴ���ջ�����Ķ���flag1
				ops.pop();		//�Ƴ���ջ�����Ķ���oldOp
				//flag1 oldOp flag2
				if(oldOp==RelationAndOp)
					flag1OpFlag2 = flag1 && flag2;
				else//(oldOp==Or)
					flag1OpFlag2 = flag1 || flag2;
				booleans.push(new Boolean( flag1OpFlag2 ));
				ops.push(new Integer(newOp));
			}else{
				//flag1 || flag2 && flag3�����
				booleans.push(new Boolean( flag2 ));
				ops.push(new Integer(newOp));
			}
		}
		//System.out.println("now i="+i);
		booleans.push(new Boolean(bools[i]));//�����һ��������ѹջ
		
		//System.out.println(booleans.size());
		//System.out.println(ops.size());
		while(!ops.isEmpty()){
			oldOp = ops.pop().intValue();
			flag2 = booleans.pop().booleanValue();
			flag1 = booleans.pop().booleanValue();
			//flag1 oldOp flag2
			if(oldOp==RelationAndOp)
				flag1OpFlag2 = flag1 && flag2;
			else//(oldOp==Or)
				flag1OpFlag2 = flag1 || flag2;
			booleans.push(new Boolean( flag1OpFlag2 ));
		}
		//System.out.println(booleans.size());
		//System.out.println(ops.size());
		
		return booleans.pop().booleanValue();
	}
	
	/**
	 * ����this�����ж�oneRecord�Ƿ���ϼ�¼
	 * @param oneRecord
	 * @param fields
	 * @return
	 */
	boolean checkRecordByFields(String[] oneRecord,Field[] fields){
		boolean flag = false;
		int curInt,referenceInt;
		long curLong,referenceLong;
		String curStr,referenceStr;
		
		referenceStr = this.referencValue;//�ο��Ƚ�ֵ
		// ��������ֶ�
		for (int i = 0; i < fields.length; i++) {
			//�Ƚ��ֶ���
			if(fieldName.equalsIgnoreCase(fields[i].fieldName)){
				curStr = oneRecord[i];
				// �����ֶ����ͣ�����Ӧת��
				switch (fields[i].fieldType) {
				case Field.IntType:
					try {
						curInt = Integer.parseInt(curStr);
					} catch (Exception e) {
						curInt = 0;
					}
					try {
						referenceInt = Integer.parseInt(referenceStr);
					} catch (Exception e) {
						referenceInt = 0;
					}
					flag = compareByOp(this.comparisonOp, curInt, referenceInt);
					break;
				case Field.BigintType:
					try {
						curLong = Long.parseLong(curStr);
					} catch (Exception e) {
						curLong = 0;
					}
					try {
						referenceLong = Long.parseLong(referenceStr);
					} catch (Exception e) {
						referenceLong = 0;
					}
					flag = compareByOp(this.comparisonOp, curLong, referenceLong);
					break;
				default:
					flag = compareByOp(this.comparisonOp, curStr, referenceStr);
					break;
				}
			}
		}
		if(this.negation)
			return !flag;
		return flag;
	}
	
	/**
	 * current op reference�Ƚ�����(����)
	 * @param op
	 * @param current
	 * @param reference
	 * @return
	 */
	public static boolean compareByOp(int op,long current,long reference){
		boolean flag = true;
		switch (op) {
		case EqualOp:
			flag = current==reference;
			break;
		case LessThanOp:
			flag = current<reference;
			break;
		case GreatThanOp:
			flag = current>reference;
			break;
		case LessEqualOp:
			flag = current<=reference;
			break;
		case GreatEqualOp:
			flag = current>=reference;
			break;
		case NotEqualOp:
			flag = current!=reference;
			break;
		default:
			flag = false;
			break;
		}
		return flag;
	}
	
	/**
	 * current op reference�Ƚ�����(String)
	 * @param op
	 * @param current
	 * @param reference
	 * @return
	 */
	public static boolean compareByOp(int op,String current,String reference){
		boolean flag = true;
		switch (op) {
		case EqualOp:
			flag = current.compareTo(reference)==0;
			break;
		case LessThanOp:
			flag = current.compareTo(reference)<0;
			break;
		case GreatThanOp:
			flag = current.compareTo(reference)>0;
			break;
		case LessEqualOp:
			flag = current.compareTo(reference)<=0;
			break;
		case GreatEqualOp:
			flag = current.compareTo(reference)>=0;
			break;
		case NotEqualOp:
			flag = current.compareTo(reference)!=0;
			break;
		case LikeOp:
			reference = reference.trim();
			reference = reference.replace("%", "(.)*");
			//System.out.println(reference);
			//System.out.println(current);
			Pattern p = Pattern.compile(reference);	//������ʽ    
			Matcher m = p.matcher(current);			//�������ַ���
			flag = m.matches();
			//System.out.println("ƥ����:"+flag);
			break;
		default:
			flag = false;
			break;
		}
		return flag;
	}

	/**
	 * ����
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		Field[] fields = {new Field("ID",Field.BigintType),new Field("Name",Field.VarcharType)};
		
		int n = 3;
		WhereCondition[] conditions = new WhereCondition[n];
		conditions[0] = new WhereCondition("id",WhereCondition.GreatThanOp,"3");
		conditions[1] = new WhereCondition("name",WhereCondition.LessEqualOp,"hao");
		conditions[2] = new WhereCondition("name",WhereCondition.LikeOp,"h%",true);
		int[] AndOrs = new int[n-1];
		String[] oneRecord = new String[2];
		
		for(int times=0; times<5; times++ ){
			System.out.println("��"+(times+1)+"��:");
			int i = 0;
			//��AndOrs�漴��ֵ
			for (i = 0; i < AndOrs.length; i++) {
				AndOrs[i] = ((int)(Math.random()*2))==0?RelationAndOp:RelationOrOp;
			}
			//������ʽ
			System.out.print("(");
			for (i = 0; i < n - 1; i++) {
				System.out.print(conditions[i].toString() + " "
						+ ((AndOrs[i] == RelationAndOp) ? "And" : "Or")
						+ " ");
			}
			System.out.print(conditions[i].toString() + ")");
			System.out.println();
			
			oneRecord[0] = "123";oneRecord[1] = "hao";
			System.out.println(oneRecord[0]+" "+oneRecord[1]);
			System.out.println(conditions[0].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[1].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[2].checkRecordByFields(oneRecord, fields));
			System.out.println("If Ands:"+checkRecordByFields(oneRecord, fields, conditions));
			System.out.println(checkRecordByFields(oneRecord, fields, conditions, AndOrs));
			System.out.println();
			
			oneRecord[0] = "12";oneRecord[1] = "asdf";
			System.out.println(oneRecord[0]+" "+oneRecord[1]);
			System.out.println(conditions[0].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[1].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[2].checkRecordByFields(oneRecord, fields));
			System.out.println("If Ands:"+checkRecordByFields(oneRecord, fields, conditions));
			System.out.println(checkRecordByFields(oneRecord, fields, conditions, AndOrs));
			System.out.println();
	
			oneRecord[0] = "1";oneRecord[1] = "liu";
			System.out.println(oneRecord[0]+" "+oneRecord[1]);
			System.out.println(conditions[0].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[1].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[2].checkRecordByFields(oneRecord, fields));
			System.out.println("If Ands:"+checkRecordByFields(oneRecord, fields, conditions));
			System.out.println(checkRecordByFields(oneRecord, fields, conditions, AndOrs));
			System.out.println();
	
			oneRecord[0] = "4";oneRecord[1] = "helloworld";
			System.out.println(oneRecord[0]+" "+oneRecord[1]);
			System.out.println(conditions[0].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[1].checkRecordByFields(oneRecord, fields));
			System.out.println(conditions[2].checkRecordByFields(oneRecord, fields));
			System.out.println("If Ands:"+checkRecordByFields(oneRecord, fields, conditions));
			System.out.println(checkRecordByFields(oneRecord, fields, conditions, AndOrs));
			System.out.println();
		}
		*/
		
		/*
		WhereCondition wc = WhereCondition.parseDDLString(" not ( name < = hao )   ");
		if(wc==null){
			System.out.println("��ʽ����");
		}else{
			System.out.println(wc.toString());
		}
		*/
		
		ArrayList<Integer> AndOrsList = new ArrayList<Integer>(5);
		//WhereCondition[] wcs = WhereCondition.getWheresFromDDLString("where not ( name < = hao )  and id > 4 or sdf = 9 ", AndOrsList);
		WhereCondition[] wcs = WhereCondition.getWheresFromDDLString("where not id!=4 ", AndOrsList);
		if(wcs.length==1){
			System.out.println(wcs[0].toString());
		}else{
			int[] andOrs = getAndOrs(AndOrsList);
			
			int i;
			//������ʽ
			for (i = 0; i < andOrs.length; i++) {
				System.out.print(wcs[i].toString() + " "
						+ ((andOrs[i] == RelationAndOp) ? "And" : "Or")
						+ " ");
			}
			System.out.println(wcs[i].toString());
		}
	}
}
