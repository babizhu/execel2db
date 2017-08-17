package com.srxk.lmm.excel;

import com.srxk.lmm.pojo.ExcelData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 17-3-17.
 */
public class ExcelReader{
    final Sheet sheet;

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

                ExcelData data = readRow( cells );


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

    ExcelData readRow( Row cells ){
        ExcelData data = new ExcelData();
        if( cells.getCell( 0 ) != null ) {
            data.setId( (int) cells.getCell( 0 ).getNumericCellValue() );
        }
        if( cells.getCell( 1 ) != null ) {
            data.setBu( cells.getCell( 1 ).getStringCellValue() );
        }
        if( cells.getCell( 2 ) != null ) {
            data.setSalesman( cells.getCell( 2 ).getStringCellValue() );
        }

        if( cells.getCell( 3 ) != null ) {
            data.setOrderId( cells.getCell( 3 ).getStringCellValue() );
        }

        if( cells.getCell( 6 ) != null ) {
            data.setProductName( cells.getCell( 6 ).getStringCellValue() );
        }

        if( cells.getCell( 11 ) != null ) {
            data.setSettlementPrice( cells.getCell( 11 ).getNumericCellValue() );
        }

        if( cells.getCell( 13 ) != null ) {
            data.setCommission( cells.getCell( 13 ).getNumericCellValue() );
        }

        if( cells.getCell( 17 ) != null ) {
            data.setSettlementPrice1( cells.getCell( 17 ).getNumericCellValue() );
        }

        if( cells.getCell( 18 ) != null ) {
            data.setRebate( cells.getCell( 18 ).getNumericCellValue() );
        }

        if( cells.getCell( 21 ) != null ) {
            data.setPayables( cells.getCell( 21 ).getNumericCellValue() );
        }
        if( cells.getCell( 16 ) != null ) {
            data.setSupplier( cells.getCell( 16 ).getStringCellValue() );
        }

        if( cells.getCell( 25 ) != null ) {
            data.setPlayTime( cells.getCell( 25 ).getDateCellValue() );
        }
        if( cells.getCell( 26 ) != null ) {
            data.setCreateTime( cells.getCell( 26 ).getDateCellValue() );
        }

        if( cells.getCell( 27 ) != null ) {
            data.setCreater( cells.getCell( 27 ).getStringCellValue() );
        }

        if( cells.getCell( 28 ) != null ) {
            data.setClient( cells.getCell( 28 ).getStringCellValue() );
        }
        if( cells.getCell( 29 ) != null ) {
            data.setSupplier1( cells.getCell( 29 ).getStringCellValue() );
        }

        if( cells.getCell( 30 ) != null ) {
            data.setSettlementPrice2( cells.getCell( 30 ).getNumericCellValue() );
        }
        if( cells.getCell( 31 ) != null ) {
            data.setCommission2( cells.getCell( 31 ).getNumericCellValue() );

        }

        if( cells.getCell( 32 ) != null ) {
            data.setZzs( cells.getCell( 32 ).getNumericCellValue() );

        }

        if( cells.getCell( 33 ) != null ) {
            data.setZzsDq( cells.getCell( 33 ).getNumericCellValue() );

        }

        if( cells.getCell( 34 ) != null ) {
            data.setDepartmentId( cells.getCell( 34 ).getStringCellValue() );

        }
        return data;
    }

}
