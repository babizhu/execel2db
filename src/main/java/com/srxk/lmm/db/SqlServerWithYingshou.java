package com.srxk.lmm.db;

import com.srxk.lmm.pojo.ExcelDataWithYingshou;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by liulaoye on 17-3-20.
 *
 */
public class SqlServerWithYingshou extends AbstractSqlServer{


    private int inid = 1;
    private int inoId = 0;

    public void parseSql( List<ExcelDataWithYingshou> datas, int runType ){
       /*
          一个excel行会产生一条总的收款分录
          产生若干条实际的应收
         */
        List<String> accvouchSqls = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime( datas.get( 0 ).getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;

        //  处理第一条分录
        inoId = getMaxInoId( month );//自增，对应凭证的 记 。一个项目下所有的分录公用一个ino_id
        System.out.println( "inoId is " + inoId );
        if( runType == 2 ) {//收入
            for( ExcelDataWithYingshou data : datas ) {
                accvouchSqls.add( buildAccvouchSqlWithShouru( data ) );
            }

            accvouchSqls.add( buildCashTableWithShouru( datas.get( 0 ) ) );

            sqls.add( accvouchSqls );
        } else {//支出 (3)
            for( ExcelDataWithYingshou data : datas ) {
                accvouchSqls.add( buildAccvouchSqlWithZhichu( data, datas.get( datas.size() - 1 ).getId() ) );
            }

            accvouchSqls.add( buildCashTableWithZhichu( datas.get( datas.size() - 1 ), datas.size() ) );

            sqls.add( accvouchSqls );
        }

    }

    /**
     * 写入支出的现金流量表
     *
     * @param data
     * @return
     */
    private String buildCashTableWithZhichu( ExcelDataWithYingshou data, int maxInid ){
        Calendar cal = Calendar.getInstance();
        cal.setTime( data.getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;
        String sql = "insert into GL_CashTable(iperiod,isignseq,ino_id,inid,cCashItem,md,mc) " +
                "VALUES (" + month + ",1," + inoId + "," + maxInid + ",'04'," + "0," + data.getPayables() + ")";

        return sql;
    }

    private String buildAccvouchSqlWithZhichu( ExcelDataWithYingshou data, int lastId ){
        final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

        String sqlFormat = "INSERT INTO GL_accvouch" +
                "      (iperiod, csign, isignseq, ino_id, inid, dbill_date, idoc, cbill, ibook, " +
                "      cdigest," +
                "      ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, " +//8
                "      dt_date, ccus_id, csup_id,citem_id, " +//3
                "      citem_class, cname, ccode_equal,  bdelete,  " +//4
                "      doutbilldate, coutsign, " +//2
                "      bvouchedit, bvouchAddordele, bvouchmoneyhold, " +//3
                "      bvalueedit, bcodeedit, bPCSedit, bDeptedit, bItemedit, bCusSupInput, " +//6
                "      cDefine10) " +
                "VALUES (%s,'记',1,%d,%d,'%s',-1,'%s',0," +
                "'" + data.getSummary() + "'," +
                "'%s',%f,%f,0,0,0,0,0," +//8    ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                "'%s',%s,%s,%s," +//3              dt_date, ccus_id, csup_id,citem_id, "
                "%s,%s,'%s',0," +//4"           citem_class, cname, ccode_equal,  bdelete,  "
                "'%s',''," +//2                 doutbilldate, coutsign
                "1,0,0," +//3                   bvouchedit, bvouchAddordele, bvouchmoneyhold
                "1,1,1,1,1,0," +//6
                "'=lly')";


        String sql = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime( data.getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;
        /**********************************  总的收款分录  *************************************/
        if( data.getId() == lastId ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    //第2行跳过
                    "100201", 0f, data.getPayables(),//ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                    formatter.format( data.getCreateTime() ), "NULL", "NULL", "NULL",//dt_date, ccus_id, csup_id,citem_id, "
                    "NULL", "NULL", "2202",//citem_class, cname, ccode_equal,  bdelete,  "
                    formatter.format( data.getCreateTime() )//doutbilldate, coutsign
                    //第7行跳过
                    //第8行跳过
                    //第9行跳过
            );

        } else {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    //第2行跳过
                    "2202", data.getPayables(), 0f, //ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                    formatter.format( data.getCreateTime() ), "NULL", "'" + getSupplierIdFromName( data.getSupplier() ) + "'", "'" + data.getItemCode() + "'",//dt_date, ccus_id, csup_id,citem_id, "
                    "'00'", "NULL", "100201",//citem_class, cname, ccode_equal,  bdelete,  "
                    formatter.format( data.getCreateTime() )//doutbilldate, coutsign
                    //第7行跳过
                    //第8行跳过
                    //第9行跳过
            );
        }

//        accvouchSqls.add( sql );

        //出境部 专线返点是2条

//        System.out.println(sql);
        return sql;
    }


    /**
     * 写入收入的现金流量表
     *
     * @param data
     * @return
     */
    private String buildCashTableWithShouru( ExcelDataWithYingshou data ){
        Calendar cal = Calendar.getInstance();
        cal.setTime( data.getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;
        String sql = "insert into GL_CashTable(iperiod,isignseq,ino_id,inid,cCashItem,md,mc) " +
                "VALUES (" + month + ",1," + inoId + ",1,'01'," + data.getSettlementPrice() + ",0)";

        return sql;
    }


    /**
     * 生产营收分录
     *
     * @param data excel数据
     * @return \
     */
    private String buildAccvouchSqlWithShouru( ExcelDataWithYingshou data ){
        final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

        String sqlFormat = "INSERT INTO GL_accvouch" +
                "      (iperiod, csign, isignseq, ino_id, inid, dbill_date, idoc, cbill, ibook, " +
                "      cdigest," +
                "      ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, " +//8
                "      dt_date, ccus_id, citem_id, " +//3
                "      citem_class, cname, ccode_equal,  bdelete,  " +//4
                "      doutbilldate, coutsign, " +//2
                "      bvouchedit, bvouchAddordele, bvouchmoneyhold, " +//3
                "      bvalueedit, bcodeedit, bPCSedit, bDeptedit, bItemedit, bCusSupInput, " +//6
                "      cDefine10) " +
                "VALUES (%s,'记',1,%d,%d,'%s',-1,'%s',0," +
                "'" + data.getSummary() + "'," +
                "'%s',%f,%f,0,0,0,0,0," +//8    ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                "'%s',%s,%s," +//3              dt_date, ccus_id, citem_id, "
                "%s,%s,'%s',0," +//4"           citem_class, cname, ccode_equal,  bdelete,  "
                "'%s',''," +//2                 doutbilldate, coutsign
                "1,0,0," +//3                   bvouchedit, bvouchAddordele, bvouchmoneyhold
                "1,1,1,1,1,0," +//6
                "'=lly')";


        String sql;
        Calendar cal = Calendar.getInstance();
        cal.setTime( data.getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;
        /**********************************  总的收款分录  *************************************/
        if( data.getId() == 1 ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    //第2行跳过
                    "100201", data.getSettlementPrice(), 0f, //ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                    formatter.format( data.getCreateTime() ), "NULL", "NULL",//dt_date, ccus_id, citem_id, "
                    "NULL", "NULL", "1122",//citem_class, cname, ccode_equal,  bdelete,  "
                    formatter.format( data.getCreateTime() )//doutbilldate, coutsign
                    //第7行跳过
                    //第8行跳过
                    //第9行跳过
            );

        } else {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    //第2行跳过
                    "1122", 0f, data.getSettlementPrice(), //ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, "
                    formatter.format( data.getCreateTime() ), "'" + data.getClient() + "'", "'" + data.getItemCode() + "'",//dt_date, ccus_id, citem_id, "
                    "'00'", "NULL", "100201",//citem_class, cname, ccode_equal,  bdelete,  "
                    formatter.format( data.getCreateTime() )//doutbilldate, coutsign
                    //第7行跳过
                    //第8行跳过
                    //第9行跳过
            );
        }

//        accvouchSqls.add( sql );

        //出境部 专线返点是2条

//        System.out.println(sql);
        return sql;
    }


}
