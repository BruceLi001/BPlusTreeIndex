package fileio;

import java.io.*;
import java.util.*;

public class BitMap implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int size;
	boolean[] map;

	/**
	 * ������С������map
	 * @param n
	 */
	public BitMap(int n) {
		this.size = n;
		map = new boolean[n];
	}

	/**
	 * ����map
	 * @param bits
	 */
	public BitMap(boolean[] bits) {
		size = bits.length;
		map = bits;
	}

	/**
	 * �õ�map����
	 * @return boolean[]
	 */
	public boolean[] getMap() {
		return map;
	}

	/**
	 * �趨�µ�map
	 * @param bits
	 */
	public void setMap(boolean[] bits) {
		size = bits.length;
		map = bits;
	}
	
	/**
	 * �õ�map�Ĵ�С
	 * @return
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * �趨map���´�С
	 * @param n
	 */
	public void setSize(int n){
		boolean[] bits = new boolean[n];
		int smaller = (size>n)?n:size;
		for(int i=0;i<smaller;i++){
			bits[i]=map[i];
		}
		size = n;
		map = bits;
	}

	/**
	 * ��������Ѿ���λ,�ָ�����ʼ״̬
	 */
	public void reset(){
		map = null;
		map = new boolean[size];
	}
	
	/**
	 * �õ�λ��n��ʹ��״̬
	 * @param n
	 * @return -1Խ��;	1��ʹ��;	0δʹ��
	 */
	public int get(int n) {
		if (n > this.size)
			return -1;
		return (map[n - 1])?1:0;
	}

	/**
	 * �趨λ��nΪ��ʹ��
	 * @param n
	 */
	public void set(int n) {
		map[n - 1] = true;
	}

	/**
	 * �趨λ��nΪδ��ʹ��
	 * @param n
	 */
	public void clear(int n) {
		map[n - 1] = false;
	}
	
	/**
	 * �õ���һ��δ�õ�bitλ�ã���ʼλ��Ϊ1
	 * @return ��һ��δ�õ�bitλ��
	 */
	public int getFirstAvailable(){
		for(int i=0;i<size;i++){
			if(map[i]==false)
				return (i+1);
		}
		return -1;
	}
	
	/**
	 * �õ�map�С�����λ���ĸ���
	 * @return
	 */
	public int getSetNum(){
		int num=0;
		for(int i=0;i<size;i++){
			if(map[i])
				num++;
		}
		return num;
	}
	
	/**
	 * �õ�map�С����λ���ĸ���
	 * @return
	 */
	public int getClearNum(){
		int num=0;
		for(int i=0;i<size;i++){
			if(!map[i])
				num++;
		}
		return num;
	}
	
	/**
	 * ��ʾbitmap
	 */
	public void showMap(){
		int i=0,j=0;
		while(i<size){
			if(map[i])
				System.out.print(1);
			else
				System.out.print(0);
			i++;
			j++;
			if(j==16){
				System.out.println();
				j=0;
			}
		}
		System.out.println();
	}
	
	/**
	 * toString
	 */
	public String toString(){
		String temp="";
		int i=0,j=0;
		while(i<size){
			if(map[i])
				temp+="1";
			else
				temp+="0";
			i++;
			j++;
			if(j==16){
				temp+="\n";
				j=0;
			}
		}
		temp+="\n";
		return temp;
	}
	
	/**
	 * д���ļ�
	 * @param mapFileName
	 * @return
	 */
	public boolean toFile(String mapFileName){
		return toFile(new File(mapFileName));
	}
	
	/**
	 * д���ļ�
	 * @param mapFile
	 * @return
	 */
	public boolean toFile(File mapFile){
		ObjectOutputStream stream = null;
		try{
			stream = new ObjectOutputStream(new FileOutputStream(mapFile));
			stream.writeObject(this);
			stream.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * ���ļ��ж�������
	 * @param mapFileName
	 * @return
	 */
	public static BitMap getFormFile(String mapFileName){
		return getFormFile(new File(mapFileName));
	}
	
	/**
	 * ���ļ��ж�������
	 * @param mapFile
	 * @return
	 */
	public static BitMap getFormFile(File mapFile){
		ObjectInputStream stream = null;
		BitMap mapFromFile = null;
		try{
			stream = new ObjectInputStream(new FileInputStream(mapFile));
			mapFromFile = (BitMap)(stream.readObject());
			stream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapFromFile;
	}
	
	/**
	 * test����
	 * @param args
	 */
	public static void main(String[] args) {
		String mapFileName = "bitmap.map";
		{
			BitMap map = new BitMap(45);
			map.showMap();
			System.out.println("First Available:"+map.getFirstAvailable());
			
			Random rand = new Random(System.currentTimeMillis());
			int num;
			num = (int)(Math.random()*(map.size-1));
			for(int i=1;i<num;i++){
				map.set(i);
			}
			map.showMap();
			
			num = (int)(Math.random()*num);
			for(int i=1;i<num;i++){
				map.set(Math.abs(rand.nextInt())%(map.size-1)+1);
			}
			map.showMap();
			System.out.println("getSetNum:"+map.getSetNum());
			System.out.println("getClearNum:"+map.getClearNum());
			System.out.println("First Available:"+map.getFirstAvailable());
			map.toFile(mapFileName);
			/*
			ObjectOutputStream stream = null;
			try{
				stream = new ObjectOutputStream(new FileOutputStream(mapFileName));
				stream.writeObject(map);
				stream.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			*/
		}
		
		System.out.println();
		System.out.println();
		BitMap map2 = null;
		/*
		ObjectInputStream stream = null;
		try{
			stream = new ObjectInputStream(new FileInputStream(mapFileName));
			map2 = (BitMap)(stream.readObject());
			stream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		map2 = BitMap.getFormFile(mapFileName);
		map2.showMap();
		System.out.println("getSetNum:"+map2.getSetNum());
		System.out.println("getClearNum:"+map2.getClearNum());
		map2.setSize(60);
		map2.showMap();
		System.out.println("getSetNum:"+map2.getSetNum());
		System.out.println("getClearNum:"+map2.getClearNum());
		System.out.println("First Available:"+map2.getFirstAvailable());
	}
}
