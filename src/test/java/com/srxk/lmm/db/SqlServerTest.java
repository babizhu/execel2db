package com.srxk.lmm.db;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liulaoye on 17-3-21.
 */
public class SqlServerTest{
    @Test
    public void run() throws Exception{

        List<Integer> t = new ArrayList<>(  );
        for( int i = 0; i < 8; i++ ) {
            t.add( i );
        }

        List<Integer> excelRowSql = new ArrayList<>(  );
//        for( int i = 0; i < sqls.size(); i++ ) {
//            excelRowSql.add( sqls );
//        }
        int index = 1;
        for( Integer sql : t ) {
            excelRowSql.add( sql );
            if( index++ % 8 == 0){
                run( excelRowSql );
                excelRowSql.clear();
            }
        }
    }

    @Test
    public void abc(){
        float a= 0.0000f;
        System.out.println( a == 0 );

    }
    private void run( List<Integer> excelRowSql ){
        System.out.println(excelRowSql);
    }

}