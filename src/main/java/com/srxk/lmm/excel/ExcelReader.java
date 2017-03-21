package com.srxk.lmm.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import com.srxk.lmm.pojo.ExcelData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 17-3-17.
 */
public class ExcelReader{
    private final Sheet sheet;

    public ExcelReader( String excelFilePath ) throws IOException, InvalidFormatException{
        File file = new File( excelFilePath );
        InputStream is = new FileInputStream( file );
        Workbook wb = WorkbookFactory.create( is );
//        System.out.println( wb.getActiveSheetIndex() );
        sheet = wb.getSheetAt( 0 );
    }


    public List<ExcelData> read(){
        List<ExcelData> rs = new ArrayList<>();
        int rowNumber = 0;
        for( Row cells : sheet ) {
//            System.out.println( cells.getCell( 0 ));
            if( rowNumber++ > 1 && cells.getCell( 0 ) != null ) {

                ExcelData data = new ExcelData();
                data.setId( (int) cells.getCell( 0 ).getNumericCellValue() );
                data.setBu( cells.getCell( 1 ).getStringCellValue() );
                data.setSalesman( cells.getCell( 2 ).getStringCellValue() );
                data.setOrderId( (int) cells.getCell( 3 ).getNumericCellValue() );
                data.setProductName( cells.getCell( 6).getStringCellValue() );

                data.setSettlementPrice( cells.getCell( 11 ).getNumericCellValue() );
                data.setCommission( cells.getCell( 13 ).getNumericCellValue() );
                data.setSettlementPrice1( cells.getCell( 17 ).getNumericCellValue() );
                data.setRebate( cells.getCell( 18 ).getNumericCellValue() );
                data.setPayables( cells.getCell( 21 ).getNumericCellValue() );

                data.setSupplier( cells.getCell( 16 ).getStringCellValue() );
                data.setPlayTime( cells.getCell( 25 ).getDateCellValue() );
                data.setCreateTime( cells.getCell( 26 ).getDateCellValue() );
                data.setCreater( cells.getCell( 27 ).getStringCellValue() );

                rs.add( data );
            }
        }
        return rs;

//            for( Cell cell : cells ) {
//                System.out.print( cell + "\t");
//                if( rowNumber++ >1 ){
//                    ExcelData data = new ExcelData();
//
//
//                }
//            }
//            System.out.println();

    }
}
