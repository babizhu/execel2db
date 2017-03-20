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
        String excelFilePath = "/home/liulaoye/文档/驴妈妈/201601模板.xlsx";
        List<ExcelData> excelDatas = new ExcelReader( excelFilePath ).read();
        System.out.println( excelDatas );
//
        final SqlServer db = new SqlServer();
//
        final List<String> sqls = db.parseSql( excelDatas );
        db.printSql( sqls );
        System.out.println();
//        db.run(sqls);
//        db.run(sqls);

//
    }
}
