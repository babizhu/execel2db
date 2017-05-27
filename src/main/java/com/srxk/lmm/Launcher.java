package com.srxk.lmm;

import com.srxk.lmm.db.SqlServer;
import com.srxk.lmm.db.SqlServerWithYingshouYingfu;
import com.srxk.lmm.excel.ExcelReader;
import com.srxk.lmm.excel.ExcelReaderWithYingshou;
import com.srxk.lmm.pojo.ExcelData;
import com.srxk.lmm.pojo.ExcelDataWithYingshou;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liulaoye on 17-3-17.
 * 由于sqljdbc.jar没有通过maven引入，导致maven打包生成的jar包中的/META-INF/MANIFEST.MF文件的
 * Class-Path: 不包含此jar包的引用，需要maven打包之后手动增加：lib/sqljdbc4-1.0.0.jar
 */
public class Launcher{
    public static void main( String[] args ) throws IOException, InvalidFormatException{
        String excelFilePath = "excel.xlsx";

        if( args.length <2){
            System.out.println( "输入参数小于2：" + Arrays.toString(args) );
            System.exit( 0 );
        }
        if( args.length > 0 ) {
            excelFilePath = args[0];
        }

        int runType = Integer.parseInt( args[1] );


        System.out.println( "读取的excel文件为：" + excelFilePath );

        if( runType == 1 ) {
            List<ExcelData> excelDatas = new ExcelReader( excelFilePath ).read();
//        System.out.println( excelDatas );
////
            final SqlServer db = new SqlServer();
////
            db.parseSql( excelDatas );
            db.printSql();
            db.run();
        }else if( runType == 2 || runType == 3){
            List<ExcelDataWithYingshou> excelDatas = new ExcelReaderWithYingshou( excelFilePath ).read();
//            System.out.println( excelDatas );

            final SqlServerWithYingshouYingfu db = new SqlServerWithYingshouYingfu();
            db.parseSql( excelDatas, runType );
            db.printSql();
            db.run();
        }
//        db.run(sqls);

//
    }
}
