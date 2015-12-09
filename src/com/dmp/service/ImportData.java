package com.dmp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dmp.vo.TranCompInfo;

import cc.util.ExcelUtil;
import cc.util.Jdbc;


public class ImportData {
	public static String path;
	public static void main(String[] args) {
		if (args.length==0){
			System.out.println("no action!!!!!!");
			return;
		}
		String arg = args[0].trim().toLowerCase();
		path = ".";
		if (args.length>1){
			path = args[1].trim().toLowerCase();
		}
		
		System.out.println(arg);
		if (arg.equalsIgnoreCase("check")){
			checkData();
		}else if(arg.equalsIgnoreCase("update")){
			updateData();
		}else if(arg.equalsIgnoreCase("rollback")){
			rollbackData();
		}else if(arg.equalsIgnoreCase("checkNull")){
			checkCompName();
		}
	}
	
	public static void checkData(){
		
		try{
			System.out.println("读取数据["+path+"/import/importData.xlsx]");
			Map <String,TranCompInfo> data = new HashMap();
			XSSFWorkbook wb = new XSSFWorkbook(path+"/import/importData.xlsx");  
			XSSFSheet  sheet = wb.getSheetAt(0);
			XSSFRow row;  
			String cell;
			// 循环输出表格中的内容  
			StringBuffer ids = new StringBuffer("('',  ") ;
			for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {  
			    row = sheet.getRow(i);  
			    for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {  
			        // 通过 row.getCell(j).toString() 获取单元格内容，  
			    	row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
			        cell = row.getCell(j).toString();  
			      //  System.out.print(cell + "\t");  
			    }  
			    TranCompInfo tranComp = new TranCompInfo();
			    String cell1 = row.getCell(row.getFirstCellNum()).toString();  
			    String cell2 = row.getCell(row.getFirstCellNum()+1).toString();  
			    ids.append("'").append(cell1).append("',");
			    tranComp.setCode(cell1);
			    tranComp.setName(cell2);
			    data.put(cell1 , tranComp) ;
			}  
			ids.deleteCharAt(ids.length()-1) ;
			ids.append(")");
			System.out.println("读取数据结束");
			queryRequest(ids.toString(),data);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void queryRequest(String con,Map <String,TranCompInfo>  data)  throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			System.out.println("检查数据库中相应企业信息...");
			conn = Jdbc.createConnection();
			String sql="select code,compName_cn from customsdict.dict_company  WHERE `code` in "+con+"";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery() ;
			
			
			Set<String> set = data.keySet() ; 
			while(rs.next()){
				TranCompInfo info = data.get(rs.getString(1));
				if (info!= null){
					info.getComp().setCode(rs.getString(1));
					info.getComp().setNameCN(rs.getString(2));
				}
			}
			Iterator iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next().toString();
				TranCompInfo val = data.get(key);
//				System.out.println("code = "+key+", name = "+val.getName()+" --> "+val.getComp().getNameCN());
			}
			ExcelUtil util = new ExcelUtil();
			util.createFile("checkData.xlsx");
			util.writeData(data);
			util.endWrite();
			System.out.println("生成差异文件【"+path+"\\output\\checkData.xlsx]");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			Jdbc.closeConnection(conn) ;
		}
	}
	private static boolean copy(String fileFrom, String fileTo) {  
        try {  
            FileInputStream in = new java.io.FileInputStream(fileFrom);  
            FileOutputStream out = new FileOutputStream(fileTo);  
            byte[] bt = new byte[1024];  
            int count;  
            while ((count = in.read(bt)) > 0) {  
                out.write(bt, 0, count);  
            }  
            in.close();  
            out.close();  
            return true;  
        } catch (IOException ex) {  
            return false;  
        }  
    }  
	public static void updateData(){
		try{
			Map <String,TranCompInfo> updateData = new HashMap();
			Map <String,TranCompInfo> insertData = new HashMap();
			copy("./output/checkData.xlsx","./output/checkDataBak.xlsx");
			XSSFWorkbook wb = new XSSFWorkbook("./output/checkData.xlsx");  
			XSSFSheet  sheet = wb.getSheetAt(0);
			XSSFRow row;  
			String cell;
			// 循环输出表格中的内容  
			for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {  
			    row = sheet.getRow(i);  
			    for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {   
			        cell = row.getCell(j).toString();
			    }
			    TranCompInfo tranComp = new TranCompInfo();
			    
			    if (row.getCell(row.getFirstCellNum()).getCellType()==Cell.CELL_TYPE_NUMERIC){
			    	row.getCell(row.getFirstCellNum()).setCellType(Cell.CELL_TYPE_STRING);
			    }
			    String cell1 = row.getCell(row.getFirstCellNum()).getStringCellValue();  
			    String cell2 = row.getCell(row.getFirstCellNum()+1).toString();  
			    XSSFCell optCell=row.getCell(row.getFirstCellNum()+3);
			    if (optCell!=null) {
			    	boolean updateflag=false;
			    	boolean insertflag=false;
			    	if (optCell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			    		if (optCell.getNumericCellValue()==1){
			    			updateflag=true;
			    		}
			    	}else if(optCell.getCellType()==Cell.CELL_TYPE_STRING){
			    		if(optCell.getStringCellValue().trim().equals("更新")){
			    			updateflag=true;
			    		}else if(optCell.getStringCellValue().trim().equals("增加")){
			    			insertflag=true;
			    		}
			    	}else if(optCell.getCellType()==Cell.CELL_TYPE_BLANK){
			    		System.out.println(optCell.getCellType()+" is blank");
			    	}else{
			    		System.out.println(optCell.getCellType());
			    	}
			    	if(updateflag){
				    	tranComp.setCode(cell1);
					    tranComp.setName(cell2);
					    updateData.put(cell1 , tranComp) ;
				    }else if(insertflag){
				    	tranComp.setCode(cell1);
					    tranComp.setName(cell2);
					    insertData.put(cell1 , tranComp) ;
				    }
			    }
			}
			updateRequest(updateData);
			insertRequest(insertData);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void updateRequest(Map <String,TranCompInfo>  data)  throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = Jdbc.createConnection();
			
			Set<Entry<String, TranCompInfo>> entrySet = data.entrySet() ; 
			Iterator<Entry<String, TranCompInfo>> it = entrySet.iterator() ; 
			while(it.hasNext()){
				Entry<String, TranCompInfo> entry = it.next() ; 
				
				String code = entry.getKey() ;
				TranCompInfo info = entry.getValue() ; 
				//INSERT INTO customsdict.dict_company (CODE,compName_cn) VALUES ('1','1');
				String sql="UPDATE customsdict.dict_company SET compName_cn='"+info.getName().trim()+"' WHERE `code`='"+code+"'";
				System.out.println(sql);
				pstmt = conn.prepareStatement(sql);
				pstmt.execute(); 
			}
		//	pstmt.execute(); 
			//XXX TODO
		}finally{
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			Jdbc.closeConnection(conn) ;
		}
	}
	public static void insertRequest(Map <String,TranCompInfo>  data)  throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = Jdbc.createConnection();
			
			Set<Entry<String, TranCompInfo>> entrySet = data.entrySet() ; 
			Iterator<Entry<String, TranCompInfo>> it = entrySet.iterator() ; 
			while(it.hasNext()){
				Entry<String, TranCompInfo> entry = it.next() ; 
				
				String code = entry.getKey() ;
				TranCompInfo info = entry.getValue() ; 
				try{
					//INSERT INTO customsdict.dict_company (CODE,compName_cn) VALUES ('1','1');
					String sql="INSERT INTO customsdict.dict_company (CODE,compName_cn) VALUES ('"+code+"','"+info.getName().trim()+"')";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.execute(); 
				}catch(Exception es){
					System.out.println(code+",error:"+es.getMessage());
				}
				
			}
		//	pstmt.execute(); 
			//XXX TODO
		}finally{
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			Jdbc.closeConnection(conn) ;
		}
	}
	public static void rollbackData(){
		try{
			Map <String,TranCompInfo> data = new HashMap();
			XSSFWorkbook wb = new XSSFWorkbook("./output/checkDataBak.xlsx");  
			XSSFSheet  sheet = wb.getSheetAt(0);
			XSSFRow row;  
			String cell;
			// 循环输出表格中的内容  
			for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {  
			    row = sheet.getRow(i);  
			    for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {  
			        // 通过 row.getCell(j).toString() 获取单元格内容，  
			        cell = row.getCell(j).toString();  
			      //  System.out.print(cell + "\t");  
			    }  
			    TranCompInfo tranComp = new TranCompInfo();
			    String cell1 = row.getCell(row.getFirstCellNum()).toString();  
			    String cell2 = row.getCell(row.getFirstCellNum()+2).toString();  
			    XSSFCell optCell=row.getCell(row.getFirstCellNum()+3);
			    if (optCell!=null) {
			    	boolean flag=false;
			    	if (optCell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			    		if (optCell.getNumericCellValue()==1){
			    			flag=true;
			    		}
			    	}else if(optCell.getCellType()==Cell.CELL_TYPE_STRING){
			    		if(optCell.getStringCellValue().trim().equals("更新")){
			    			flag=true;
			    		}
			    	}else if(optCell.getCellType()==Cell.CELL_TYPE_BLANK){
			    		System.out.println(optCell.getCellType()+" is blank");
			    	}else{
			    		System.out.println(optCell.getCellType());
			    	}
			    	if(flag){
				    	tranComp.setCode(cell1);
					    tranComp.setName(cell2);
					    data.put(cell1 , tranComp) ;
				    }
			    }
			    
			    
			}
			updateRequest(data);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void deleteRequest(Map <String,TranCompInfo>  data)  throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = Jdbc.createConnection();
			
			Set<Entry<String, TranCompInfo>> entrySet = data.entrySet() ; 
			Iterator<Entry<String, TranCompInfo>> it = entrySet.iterator() ; 
			while(it.hasNext()){
				Entry<String, TranCompInfo> entry = it.next() ; 
				
				String code = entry.getKey() ;
				TranCompInfo info = entry.getValue() ; 
				//INSERT INTO customsdict.dict_company (CODE,compName_cn) VALUES ('1','1');
				String sql="DELETE FROM  customsdict.dict_company where CODE='"+code+"'";
				System.out.println(sql);
				pstmt = conn.prepareStatement(sql);
				pstmt.execute(); 
			}
		//	pstmt.execute(); 
			//XXX TODO
		}finally{
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			Jdbc.closeConnection(conn) ;
		}
	}
	
	public static void checkCompName() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		Map <String,TranCompInfo>  data = new HashMap();
		try{
			System.out.println("检查数据库中相应企业为空的信息...");
			conn = Jdbc.createConnection();
			String sql="select concat(code,''),compName_cn,compName_en from customsdict.dict_company  WHERE compName_cn='' and code<>''";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery() ;
			
			System.out.println("读取数据...");
			while(rs.next()){
				TranCompInfo info = new TranCompInfo();
				info.setCode(rs.getString(1));
				info.setName(rs.getString(2));
				info.getComp().setCode(rs.getString(1));
				info.getComp().setNameCN(rs.getString(2));
				info.getComp().setNameEN(rs.getString(3));
				data.put(info.getCode(), info);
			}
			System.out.println("写入excel文件...");
			ExcelUtil util = new ExcelUtil();
			util.createFile("企业名称为空记录.xlsx");
			util.writeDataForCompName(data);
			util.endWrite();
			System.out.println("生成文件【"+path+"\\output\\企业名称为空记录.xlsx]");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			Jdbc.closeConnection(conn) ;
		}
	}
	

}
