package cc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dmp.service.ImportData;
import com.dmp.vo.TranCompInfo;
import com.mysql.jdbc.log.LogUtils;

/**
 * Excel生成工具类
 * @author chenzmb
 *
 */

public class ExcelUtil {
	private final static String sheetName ="Data";
	private  String outPutFile ="./import/";
	
	private FileOutputStream fo = null;
	private Workbook wb = null;
	private CellStyle styleSame;//数据相同
	private CellStyle styleDiff;//数据不相同
	private CellStyle styleNil;//无数据
//	private Sheet sheet = null;
	public void createFile(String fileName){
		outPutFile = ImportData.path+"/output/";//ConfigInfo.getInstance().getFilepath();
		File fileD = new File(outPutFile) ;
		if(!fileD.exists()){
			fileD.mkdir();
		}
		try {
			File file = new File(outPutFile+fileName) ;
			if(!file.exists()){  //相同文件就追加
				file.createNewFile();
			}
			if (fo == null&&file.getPath().toLowerCase().endsWith("xlsx")) {
				fo = new FileOutputStream(file);
				// 处理Excel2007 级以上版本
				wb = new SXSSFWorkbook(100);
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void writeData(Map<String, TranCompInfo> data) throws Exception {
//		try {
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet == null) {
				sheet = wb.createSheet(sheetName);
				writeTitle(sheet);
			}
			// 创建Excel的sheet
			
			Iterator iter = data.keySet().iterator();
			int rowIdx=1;
			Cell cell = null;
			while (iter.hasNext()) {
				String key = iter.next().toString();
				TranCompInfo val = data.get(key);
				CellStyle style;
				String flag="";
				if (val.getComp().getCode().equals("-1")){
					style = this.getStyle(2);
					flag="增加";
				}else if(val.getComp().getNameCN().trim().equals(val.getName().trim())){
					style = this.getStyle(0);
					flag="不操作";
				}else{
					style = this.getStyle(1);
					flag="更新";
				}
//				System.out.println("code = "+key+", name = "+val.getName()+" --> "+val.getComp().getNameCN());
				Row row = sheet.createRow(rowIdx); //+1 表头列
				cell = makeCell(row,0,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(key);
				cell = makeCell(row,1,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(val.getName());
				cell = makeCell(row,2,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(val.getComp().getNameCN());
				cell = makeCell(row,3,style);
				cell.setCellValue(flag);
				
				rowIdx++;
			}
			
			
//		} catch (Exception e) {
//			LogUtil.getInstance().log(e);
//		}
		
	} 
	
	private CellStyle getStyle(int flag){
		// 设置单元格格式为文本类型
		if (flag==1){
			if (styleDiff==null){
				styleDiff = (XSSFCellStyle) wb.createCellStyle();
				styleDiff.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
				styleDiff.setFillPattern(CellStyle.SOLID_FOREGROUND) ;
				Font font = wb.createFont();
				font.setColor(IndexedColors.WHITE.getIndex());
				font.setFontHeightInPoints((short)10); //字体大小
				styleDiff.setFont(font);
				XSSFDataFormat format = (XSSFDataFormat) wb.createDataFormat();
				styleDiff.setDataFormat(format.getFormat("@"));
			}
			return styleDiff;
		}else if (flag==2){
			if (styleNil==null){
				styleNil = (XSSFCellStyle) wb.createCellStyle();
				styleNil.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
				styleNil.setFillPattern(CellStyle.SOLID_FOREGROUND) ;
				Font font = wb.createFont();
				font.setFontHeightInPoints((short)10); //字体大小
				styleNil.setFont(font);
				XSSFDataFormat format = (XSSFDataFormat) wb.createDataFormat();
				styleNil.setDataFormat(format.getFormat("@"));
			}
			return styleNil;
		}else{
			if (styleSame==null){
				styleSame = (XSSFCellStyle) wb.createCellStyle();
				Font font = wb.createFont();
				font.setFontHeightInPoints((short)10); //字体大小
				styleSame.setFont(font);
				XSSFDataFormat format = (XSSFDataFormat) wb.createDataFormat();
				styleSame.setDataFormat(format.getFormat("@"));
			}
			return styleSame;
		}
	}
	private void writeTitle(Sheet sheet){
		CellStyle style =  wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND) ;
		Font font = wb.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short)10); //字体大小
		font.setBold(true);
		style.setFont(font);
		String titles[] = TranCompInfo.titles ;
		Row row = sheet.createRow(0);
		Cell cell = null;
		
		int ec = 0;
		for (int ri = 0; ri < titles.length; ri++) {
			cell = makeCell(row,ri-ec,style);
			cell.setCellValue(titles[ri]);
		}
	}
	public void endWrite(){
		try{
			if(wb!=null){
				wb.write(fo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb instanceof XSSFWorkbook) {
					((XSSFWorkbook) wb).getPackage().close();
				}
				if (fo != null) {
					fo.close();
				}
			} catch (Exception e) {
			}
		}
	}
	private static Cell makeCell(Row row,int col,CellStyle style){
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellStyle(style);
		return cell;
	}
	
	public void writeDataForCompName(Map<String, TranCompInfo> data) throws Exception {
//		try {
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet == null) {
				sheet = wb.createSheet(sheetName);
//				writeTitle(sheet);
				CellStyle style =  wb.createCellStyle();
				style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND) ;
				Font font = wb.createFont();
				font.setColor(IndexedColors.WHITE.getIndex());
				font.setFontHeightInPoints((short)10); //字体大小
				font.setBold(true);
				style.setFont(font);
				String titles[] = new String[]{"企业编码","企业名称(CN)","企业名称(EN)"};
				Row row = sheet.createRow(0);
				Cell cell = null;
				
				int ec = 0;
				for (int ri = 0; ri < titles.length; ri++) {
					cell = makeCell(row,ri-ec,style);
					cell.setCellValue(titles[ri]);
				}
			}
			// 创建Excel的sheet
			
			Iterator iter = data.keySet().iterator();
			int rowIdx=1;
			Cell cell = null;
			while (iter.hasNext()) {
				String key = iter.next().toString();
				TranCompInfo val = data.get(key);
				CellStyle style = this.getStyle(0);
				Row row = sheet.createRow(rowIdx); //+1 表头列
				cell = makeCell(row,0,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(key);
				cell = makeCell(row,1,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(val.getName());
				cell = makeCell(row,2,style);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(val.getComp().getNameEN());
				rowIdx++;
			}
			
			
//		} catch (Exception e) {
//			LogUtil.getInstance().log(e);
//		}
		
	} 
}
