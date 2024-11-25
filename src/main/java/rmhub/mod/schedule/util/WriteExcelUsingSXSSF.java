package rmhub.mod.schedule.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import rmhub.mod.schedule.domain.WorkbookRequest;

@Slf4j
public class WriteExcelUsingSXSSF {
	public static final int ROW_INDEX_HEARDER = 0;
    public static void writeExcel(WorkbookRequest workbookRequest) throws IOException {
        // Create Workbook
    	SXSSFSheet sheet = workbookRequest.getSheet();
        // Write header
    	if(workbookRequest.isWriteHeader())
    		writeHeader(sheet, ROW_INDEX_HEARDER, workbookRequest.getHeader());
        // Write data
        int rowIndexStart= workbookRequest.getRowIndexStart();
        log.info("rowIndexStart:"+rowIndexStart);
        for (String[] book : workbookRequest.getBooks()) {
            // Create row
            SXSSFRow row = sheet.createRow(rowIndexStart);
            // Write data on row
            writeBook(book, row);
            rowIndexStart++;
        }
        log.info("export file done!!!:"+rowIndexStart);
    }
 
    // Write header with format
    private static void writeHeader(SXSSFSheet sheet, int rowIndex,String[] header) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);
        // Create row
        SXSSFRow row = sheet.createRow(rowIndex);
 
        for (int i = 0; i < header.length; i++) {
        	SXSSFCell cell = row.createCell(i);
        	cell.setCellStyle(cellStyle);
            cell.setCellValue(header[i]);
		}
    }
 
    // Write data
    private static void writeBook(String[] book, SXSSFRow row) {
        for (int i = 0; i < book.length; i++) {
        	SXSSFCell cell = row.createCell(i);
            cell.setCellValue(book[i]==null?"":book[i]);
		}
    }
 
    // Create CellStyle for header
    private static CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
 
        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        return cellStyle;
    }
    
 
    // Create output file
    public static void createOutputFile(SXSSFWorkbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
        log.info("export file done!!!:");
    }
}
