package com.srxk.lmm;

import com.srxk.lmm.db.SqlServer;
import com.srxk.lmm.excel.ExcelReader;
import com.srxk.lmm.pojo.ExcelData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

/**
 * Created by liulaoye on 17-3-17.
 */
public class Launcher{
    public static void main( String[] args ) throws IOException, InvalidFormatException{
        String excelFilePath = "excel.xlsx";
        if( args.length > 0 ) {
            excelFilePath = args[0];
        }

        System.out.println( "读取的excel文件为：" + excelFilePath );
        List<ExcelData> excelDatas = new ExcelReader( excelFilePath ).read();
//        System.out.println( excelDatas );
////
        final SqlServer db = new SqlServer();
////
        db.parseSql( excelDatas );
        db.printSql();
        db.run();
//        db.run(sqls);

//
    }
}
