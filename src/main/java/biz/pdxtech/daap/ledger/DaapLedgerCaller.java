package biz.pdxtech.daap.ledger;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;
import biz.pdxtech.daap.ledger.util.ContractState;
import biz.pdxtech.daap.ledger.util.HexUtil;
import biz.pdxtech.daap.ledger.util.MessageCodec;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DaapLedgerCaller {
    public static void main(String[] args) {
        /*1 setting props*/
        Properties props = new Properties();
        props.setProperty("privateKey", "623aacd6be3174d096711814e27487f26ce0126279ab3c85823cd3b2e86da28d");
        
        /*2 setting testKey*/
        setTestKey("2017-01-14T15:18:50.528");
        /*3 setting contract dst*/
        setContractDst("daap://default_pdx_chain/pdxdev/pdx.dapp/example");
    
        /*4 query from pdx*/
        BlockChainDriver driver = BlockChainDriverFactory.get(props);
        queryTx(driver, testKey);
        queryState(driver, testKey);
    }
    
    private static String testKey;
    private static String contractDst;
    
    /**
     * Ledger查询分为两类；两类查询都可以通过设置Transaction.meta属性进行查询或者Transaction.body属性进行查询
     * <p>
     * ### 设置Transaction.meta属性查询
     * 1 查询交易
     * ① 根据交易ID查询；设置meta - ("DaaP-Query-TXID","txidtext".getBytes())
     * ② 根据自定义信息meta查询 设置meta - ("name","nametext") 可设置多个，但认为它们是与的关系
     * <p>
     * 2 查询合约状态
     * ① 根据交易ID查询；设置meta - ("DaaP-Query-STATE-TXID","txidtext".getBytes())；此处查询出来的结果为此次交易执行完的合约状态
     * ② 根据合约查询 设置meta - ("DaaP-Query-STATE-DST","dsttext")
     * <p>
     * 2 设置Transaction.body属性进行查询，这时候你需要传入一个exp表达式，表达式形如："${tx:body[bodytext]}".getBytes())
     * <p>
     * ### 设置Transaction.body属性查询
     * 1 查询交易
     * ① 根据交易ID查询 设置body表达式 "${tx:txid[txidtext]}".getBytes()
     * ② 根据自定义信息meta查询 设置body表达式 "${tx:meta[metak,metav]||meta[metak,metav]}".getBytes() 注意：metav是将原byte[] Hex序列化过的文本
     * 辅助查询条件页码 设置body表达式 "${tx:txid[txidtext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录
     * <p>
     * 2 查询合约状态
     * ① 根据交易Id查询 设置body表达式 "${state:txid[txidtext]}".getBytes()
     * ② 根据合约查询 设置body表达式 "${state:dst[dsttext]}".getBytes()
     * 辅助查询条件页码 设置body表达式 "${state:dst[dsttext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录
     * <p>
     * 查询请注意：
     * 1 如果查询条件中含有txid的条件，则默认只根据txid查询
     * 2 exp表达式中必须以${tx:}/${state:}的形式
     *
     * @param driver
     */
    private static void queryTx(BlockChainDriver driver, String testKey) {
        assert StringUtils.isNotEmpty(testKey);
        
        
        /**1 设置Transaction.meta属性查询交易*/
        /** 根据交易ID查询*/
        Transaction tx = new Transaction();
        Map<String, byte[]> map = new LinkedHashMap<>();
        map.put("DaaP-Query-TX-TXID", "e4ebcbd2f017ab188d0b828cdf1568d17d1ab4ce5a0dcf63418656d8740b813e".getBytes());
        tx.setMeta(map);
        queryTx(tx, driver);
        
        /** 根据自定义信息查询*/
        tx = new Transaction();
        map = new LinkedHashMap<>();
        map.put("name", DaapCaller.addTestKey("kangxinghao", testKey).getBytes());
        map.put("age", DaapCaller.addTestKey("22", testKey).getBytes());
        tx.setMeta(map);
        queryTx(tx, driver);
        
        /**2 设置Transaction.body属性进行查询*/
        /** 根据交易ID查询*/
        tx = new Transaction();
        tx.setBody("${tx:txid[e4ebcbd2f017ab188d0b828cdf1568d17d1ab4ce5a0dcf63418656d8740b813e]}".getBytes());
        queryTx(tx, driver);
        
        /** 根据自定义信息查询*/
        String exp = ("${tx:meta[name," + HexUtil.toHex(DaapCaller.addTestKey("kangxinghao", testKey).getBytes())
                + "]||meta[age," + HexUtil.toHex(DaapCaller.addTestKey("22", testKey).getBytes()) + "]}");
        tx.setBody(exp.getBytes());
        queryTx(tx, driver);
    }
    
    private static void queryState(BlockChainDriver driver, String testKey) {
        assert StringUtils.isNotEmpty(testKey);
        
        /**1 设置Transaction.meta属性查询合约状态*/
        Transaction tx = new Transaction();
        Map<String, byte[]> map = new LinkedHashMap<>();
        map.put("DaaP-Query-STATE-TXID", "e4ebcbd2f017ab188d0b828cdf1568d17d1ab4ce5a0dcf63418656d8740b813e".getBytes());
        tx.setMeta(map);
        queryState(tx, driver);
        /**2 设置Transaction.body属性进行查询*/
        /** 根据交易ID查询*/
        tx = new Transaction();
        tx.setBody("${state:txid[e4ebcbd2f017ab188d0b828cdf1568d17d1ab4ce5a0dcf63418656d8740b813e]}".getBytes());
        queryState(tx, driver);
        /** 根据合约地址查询*/
        tx = new Transaction();
        String exp = ("${state:dst[" + contractDst + "]");
        tx.setBody(exp.getBytes());
        queryState(tx, driver);
    }
    
    private static void queryTx(Transaction tx, BlockChainDriver driver) {
        try {
            tx.setDst(new URI("daap://tender-pdx/pdx.dapp/ledger"));
            List<Transaction> res = driver.query(tx);
            if (res != null && res.size() > 0) {
                res.stream().forEach(a -> System.out.println(new String(a.getBody())));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    private static void queryState(Transaction tx, BlockChainDriver driver) {
        try {
            tx.setDst(new URI("daap://tender-pdx/pdx.dapp/ledger"));
            List<Transaction> res = driver.query(tx);
            if (res != null && res.size() > 0) {
                res.stream().forEach(a -> {
                    ContractState state = MessageCodec.toObject(a.getBody(), ContractState.class);
                    state.getMap().entrySet().stream().forEach(b -> {
                        System.out.println("key : " + b.getKey());
                        System.out.println("value : " + new String(b.getValue()));
                    });
                });
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    public static String getTestKey() {
        return testKey;
    }
    
    public static void setTestKey(String testKey) {
        DaapLedgerCaller.testKey = testKey;
    }
    
    private static void setContractDst(String dst) {
        contractDst = dst;
    }
    
    public static String getContractDst() {
        return contractDst;
    }
}
