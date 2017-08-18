package com.srxk.lmm.db;

import com.srxk.lmm.pojo.ExcelData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by liulaoye on 17-3-20.
 * 收入成本
 */
public class SqlServer extends AbstractSqlServer{


    public void parseSql( List<ExcelData> datas ){
        /*
          一个excel行会产生一个List<String>，都保存到sqls中
          也就是说，所有的sql都要执行一个事物，要么整个excel数据全写入，要么全不写入
         */

//        final boolean fitemss00Duplicate = isFitemss00Duplicate( datas );
//        if( fitemss00Duplicate ) {
//            System.exit( 0 );
//        }
        for( ExcelData data : datas ) {
            List<String> rowExcelSql = new ArrayList<>();
            rowExcelSql.add( buildFitemss00Sql( data ) );
            rowExcelSql.addAll( buildAccvouchSql( data ) );
            sqls.add( rowExcelSql );
        }
    }


    /**
     * @param bu bu
     * @return ccode
     */
    private String[] getCcodeEqual( String bu ){
//        final String[] ccode = getCcode( bu );
        String[] ret = new String[2];
        switch( bu ) {
            case "国内BU":
                ret[0] = "60010301,60010302,2202,22210301";
                ret[1] = "1122,64010301,64010302,22210310";

                break;

            case "门票BU":
                ret[0] = "60010101,60010102,2202,22210301";
                ret[1] = "1122,64010101,64010102,22210310";
                break;
            case "目的地BU":
                ret[0] = "60010202,60010203,2202,22210301";
                ret[1] = "1122,64010202,64010203,22210310";
                break;
            case "出境BU":
                ret[0] = "60010402,2202,22210301";
                ret[1] = "1122,64010402,22210310";
                break;
            case "总经办":
                ret[0] = "6001060401,6001060402,2202,22210301";
                ret[1] = "1122,6401060401,6401060402,22210310";
                break;
        }
        return ret;
    }

    /**
     * 摘要
     *
     * @param data     data
     * @param ccode    ccode
     * @param is0006   006
     * @param is9Fenlu 是否第九条分录
     * @return 摘要
     */
    private String getSummary( ExcelData data, String ccode, boolean is0006, boolean is9Fenlu, boolean is10Fenlu ){
        final String bu = data.getBu();
        if( is9Fenlu ) {
            return "确认" + bu + "线上项目《" + data.getItemCode() + "》应交税金";
        }
        if( is10Fenlu ) {
            return "确认" + bu + "线上项目《" + data.getItemCode() + "》抵减的销项税额";
        }
//        String[] ret = new String[2];
        if( bu.equals( "国内BU" ) || bu.equals( "门票BU" ) || bu.equals( "目的地BU" ) ) {
            if( ccode.equals( "1122" ) || ccode.startsWith( "6001" ) ) {
                return "确认" + bu + "线上项目《" + data.getItemCode() + "》收入";
            } else {
                return "确认" + bu + "线上项目《" + data.getItemCode() + "》成本";
            }
        }
        if( bu.equals( "总经办" ) ) {
            if( ccode.equals( "1122" ) || ccode.startsWith( "6001" ) ) {
                return "确认" + bu + "线线下项目《" + data.getItemCode() + "》收入";
            } else {
                return "确认" + bu + "线下项目《" + data.getItemCode() + "》成本";
            }
        }
        if( bu.equals( "出境BU" ) ) {

            if( ccode.equals( "1122" ) ) {
                if( is0006 ) {
                    return "代供应商收上驴线上项目《" + data.getItemCode() + "》团款";
                } else {
                    return "确认" + bu + "线上项目《" + data.getItemCode() + "》反佣收入";
                }

            }
            if( ccode.equals( "60010402" ) ) {
                return "确认" + bu + "线上项目《" + data.getItemCode() + "》反佣收入";
            }
            if( ccode.equals( "64010402" ) ) {
                return "确认" + bu + "线上项目《" + data.getItemCode() + "》反佣成本";
            }
            if( ccode.equals( "2202" ) ) {
                return "代上驴付供应商" + bu + "线上项目《" + data.getItemCode() + "》团款";
            }
        }
        return null;

    }

    private String[] getCcode( String bu ){
        switch( bu ) {
            case "国内BU":
                return "1122,60010301,1122,60010302,64010301,64010302,2202,0,22210301,22210310".split( "," );
            case "门票BU":
                return "1122,60010101,1122,60010102,64010101,64010102,2202,0,22210301,22210310".split( "," );
            case "目的地BU":
                return "1122,60010202,1122,60010203,64010202,64010203,2202,0,22210301,22210310".split( "," );
            case "出境BU":
                return "1122,0,1122,60010402,0,64010402,2202,0,22210301,22210310".split( "," );
            case "总经办":
                return "1122,6001060401,1122,6001060402,6401060401,6401060402,2202,2202,22210301,22210310".split( "," );
        }
        return null;
    }

    /**
     * 生成一个凭证的7条分录的sql语句
     *
     * @param data excel数据
     * @return 7条分录的sql语句
     */
    private List<String> buildAccvouchSql( ExcelData data ){
        final SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
        List<String> accvouchSqls = new ArrayList<>();
        String sqlFormat = "INSERT INTO GL_accvouch" +
                "      (iperiod, csign, isignseq, ino_id, inid, dbill_date, idoc, cbill, ibook, " +
                "      cdigest," +
                "      ccode, md, mc, md_f, mc_f, nfrat, nd_s, nc_s, " +
                "      cn_id, dt_date, ccus_id, csup_id, citem_id, " +
                "      citem_class, cname, ccode_equal,  bdelete,  " +
                "      doutbilldate, coutsign, " +
                "      bvouchedit, bvouchAddordele, bvouchmoneyhold, " +
                "      bvalueedit, bcodeedit, ccodecontrol, bPCSedit, bDeptedit, bItemedit, bCusSupInput, " +
                "      cdept_id," +
                "      cDefine10) " +
                "VALUES (%s,'记',1,%d,%d,'%s',-1,'%s',0," +
                "'%s'," +
                "'%s',%f,%f,0,0,0,0,0," +
                "%s,%s,%s,%s,'%s'," +
                "'00',%s,'%s',0," +
                "'%s',''," +
                "1,0,0," +
                "1,1,%s,1,1,1,0," +
                "%s," +
                "'=lly')";

        Calendar cal = Calendar.getInstance();
        cal.setTime( data.getCreateTime() );
        int month = cal.get( Calendar.MONTH ) + 1;

        //  处理第一条分录
        int inoId = getMaxInoId( month );//自增，对应凭证的 记 。一个项目下所有的分录公用一个ino_id
        System.out.println( "inoId is " + inoId );

        final String[] ccode = getCcode( data.getBu() );
        if( ccode == null ) {
            System.out.println( data.getBu() + "未找到对应的ccode" );
            System.exit( 0 );
        }
        System.out.println( data.getBu() + " ccode is " + Arrays.toString( ccode ) );

        final String[] ccodeEqual = getCcodeEqual( data.getBu() );
        System.out.println( "ccodeEqual is " + Arrays.toString( ccodeEqual ) );

        int inid = 1;
        //查询供应商id
//        String supplyId = "110011";
        String supplyId = this.getSupplierIdFromName( data.getSupplier() );
        System.out.println( "供应商ID is " + supplyId );

        String sql;

        /**********************************  分录1  *************************************/
        if( data.getSettlementPrice() != 0 && !ccode[0].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[0], true, false, false ),
                    ccode[0], data.getSettlementPrice(), 0f,
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'" + data.getClient() + "'", "NULL", data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[0],
                    formatter.format( data.getCreateTime() ),
                    "NULL",
                    getDepartmenId( ccode[0], data )
            );

            accvouchSqls.add( sql );
            System.out.println( "第一条sql" );
        }

        /************************************  分录2  *************************************/
        if( data.getSettlementPrice2() != 0 && !ccode[1].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[1], true, false, false ),
                    ccode[1], 0f, data.getSettlementPrice2(),
                    "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                    "NULL", ccodeEqual[1],
                    formatter.format( data.getCreateTime() ),
                    "NULL",
                    getDepartmenId( ccode[1], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录3  *************************************/
        if( data.getCommission() != 0 && !ccode[2].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[2], false, false, false ),
                    ccode[2], data.getCommission(), 0f,
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'0003'", "NULL", data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[0],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[2], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录4  *************************************/
        if( data.getCommission2() != 0 && !ccode[3].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[3], true, false, false ),
                    ccode[3], 0f, data.getCommission2(),
                    "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                    "NULL", ccodeEqual[1],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[3], data )
            );

            accvouchSqls.add( sql );
        }

        /************************************  分录5  *************************************/
        if( data.getSettlementPrice1() != 0 && !ccode[4].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[4], true, false, false ),
                    ccode[4], data.getSettlementPrice1(), 0f,
                    "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                    "NULL", ccodeEqual[0],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[4], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录6  *************************************/
        if( data.getRebate() != 0 && !ccode[5].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[5], true, false, false ),
                    ccode[5], -data.getRebate(), 0f,
                    "NULL", "NULL", "NULL", "NULL", data.getItemCode(),
                    "NULL", ccodeEqual[0],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[5], data )
            );

            accvouchSqls.add( sql );

            //出境部 专线返点是2条

        }


        /*************************************  分录7  *************************************/
        if( data.getPayables() != 0 && !ccode[6].equals( "0" ) ) {
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[6], true, false, false ),
                    ccode[6], 0f, data.getPayables(),
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "NULL", "'" + supplyId + "'", data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[1],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[6], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录8(总经办)  *************************************/

        if( data.getRebate() != 0 && !ccode[7].equals( "0" ) ) {
            String supplyId1 = this.getSupplierIdFromName( data.getSupplier1() );
            supplyId1 = supplyId1.equals( "" ) ? "NULL" : "'" + supplyId1 + "'";
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[1], true, false, false ),
                    ccode[7], 0f, -data.getRebate(),
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'" + data.getClient() + "'", supplyId1, data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[1],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[7], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录9(新增)  *************************************/

        if( data.getZzs() != 0 && !ccode[8].equals( "0" ) ) {
            String supplyId1 = this.getSupplierIdFromName( data.getSupplier1() );
            supplyId1 = supplyId1.equals( "" ) ? "NULL" : "'" + supplyId1 + "'";
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[1], false, true, false ),
                    ccode[8], 0f, data.getZzs(),
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'" + data.getClient() + "'", supplyId1, data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[1],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[8], data )
            );

            accvouchSqls.add( sql );
        }

        /*************************************  分录10(新增)  *************************************/

        if( data.getZzsDq() != 0 && !ccode[9].equals( "0" ) ) {
            String supplyId1 = this.getSupplierIdFromName( data.getSupplier1() );
            supplyId1 = supplyId1.equals( "" ) ? "NULL" : "'" + supplyId1 + "'";
            sql = String.format( sqlFormat, month,
                    inoId, inid++, formatter.format( data.getCreateTime() ), data.getCreater(),
                    getSummary( data, ccode[1], false, false, true ),
                    ccode[9], data.getZzsDq(), 0f,
                    "'" + data.getOrderId() + "'", "'" + formatter.format( data.getPlayTime() ) + "'", "'" + data.getClient() + "'", supplyId1, data.getItemCode(),
                    "'" + data.getSalesman() + "'", ccodeEqual[0],
                    formatter.format( data.getCreateTime() ),
                    "'***'",
                    getDepartmenId( ccode[9], data )
            );

            accvouchSqls.add( sql );
        }
        return accvouchSqls;
    }

    private String getDepartmenId( String ccode, ExcelData data ){
        if( ccode.equals( "22210301" ) || ccode.equals( "22210310" ) ) {
            return data.getDepartmentId();
        }
        return "NULL";
    }

//    /**
//     * 检查项目是否重复
//     *
//     * @param datas
//     * @return true:重复了
//     */
//
//    private boolean isFitemss00Duplicate( List<ExcelData> datas ){
//        for( ExcelData data : datas ) {
//            PreparedStatement pst = null;
//            ResultSet rs = null;
//            Connection con = DatabaseUtil.INSTANCE.getConnection();
//            String sql = "SELECT * FROM fitemss00 where citemname='" + data.getOrderId() + "'";
//            try {
//                pst = con.prepareStatement( sql );
//
//                rs = pst.executeQuery();
//                while( rs.next() ) {
//                    System.out.println( "发现重复的订单号：" + data );
//                    return true;
//                }
//
//            } catch( SQLException e ) {
//                e.printStackTrace();
//            } finally {
//                DatabaseUtil.INSTANCE.close( rs, pst, con );
//            }
//
//        }
//        return false;
//    }

    private String buildFitemss00Sql( ExcelData data ){
        final SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );

        return "INSERT INTO fitemss00 (citemcode,citemname,bclose,citemccode,日期,供应商) " +
                "VALUES ('" + data.getItemCode() + "'," +
                "'" + data.getOrderId() + "', " +
                0 + ", " +
                "'01', " +
                "'" + format.format( data.getPlayTime() ) + "', " +
                "'" + data.getSupplier() + "' " +
                ")";
    }


}
