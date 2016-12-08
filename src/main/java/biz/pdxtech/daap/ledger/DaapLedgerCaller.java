package biz.pdxtech.daap.ledger;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverException;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DaapLedgerCaller {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("privateKey", "137f9a8fa4fac8ad5b3752cc056eb0f733e5090271d61941a22f790833af4be9");

        BlockChainDriver driver = BlockChainDriverFactory.get(props);
        query(driver);
//        apply(driver);
//        applyForQueryData(driver);
    }


    /**
     * 查询交易分为两种方式：
     * 1 设置Transaction.meta属性进行查询 注意：meta中的各个属性条件之间是与的关系，即需都满足
     * 根据交易ID查询 设置meta - ("DaaP-Query-TXID","txidtext".getBytes())
     * 根据交易内容查询 设置meta - ("DaaP-Query-BODY","bodytext".getBytes())
     * 如果需要模糊查询 则使用 "DaaP-Query-BODYLIKE"
     * 根据自定义信息meta查询 设置meta - ("name","nametext") 可设置多个，但认为它们是与的关系
     * 辅助查询条件页码 设置meta - ("DaaP-Query-PAGENO", "pageNotext") 不写默认为1页，每页固定100记录
     * <p>
     * <p>
     * 2 设置Transaction.body属性进行查询，这时候你需要传入一个exp表达式，表达式形如："${body[bodytext]}".getBytes())
     * 根据交易ID查询 设置body表达式 "${txid[txidtext]}".getBytes()
     * 根据交易内容查询 设置body表达式 "${body[bodytext]}".getBytes() // 模糊body.like 注意：bodytext是将原byte[] Hex序列化过的文本
     * 根据自定义信息meta查询 设置body表达式 "${meta[metak,metav]||meta[metak,metav]}".getBytes() 注意：metav是将原byte[] Hex序列化过的文本
     * 辅助查询条件页码 设置body表达式 "${body.like[bodytext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定100记录
     * <p>
     * 通过exp可以设置较为复杂的查询请求见下面例子
     * <p>
     * 查询请注意：
     * 1 如果查询条件中含有txid的条件，则默认只根据txid查询
     * 2 exp表达式中必须以${}的形式
     *
     * @param driver
     */
    private static void query(BlockChainDriver driver) {
        Transaction tx = new Transaction();
        /**1 设置Transaction.meta属性进行查询*/
        Map<String, byte[]> map = new LinkedHashMap<>();
        /** 根据交易ID查询*/
        map.put("DaaP-Query-TXID", "34ae1a39747e2a72660e871530b51f207100c5350b413335cf75999a51d274d5".getBytes());
        /** 根据交易内容匹配*/
        //map.put("DaaP-Query-BODY", "a writer".getBytes());
        //map.put("DaaP-Query-BODYLIKE", "a writer".getBytes());
        /** 根据自定义信息查询*/
        //map.put("name", "kangxinghao".getBytes());
        //map.put("age", "22".getBytes());
        tx.setMeta(map);
        /**2 设置Transaction.body属性进行查询*/
        /** 根据交易ID查询*/
        //tx.setBody("${txid[d3d1b34e2b0280d27a870ff986d7cc74796743a57d12ebe48637874f4efd3bf0]}".getBytes());
        /** 根据交易内容查询*/
        //tx.setBody(("${body.like[" + HexUtil.encode("a good man".getBytes()) +  "]}").getBytes());
        /** 根据自定义信息查询*/
        //String metaExp = ("${meta[name," + HexUtil.encode("kangxinghao".getBytes()) + "]||meta[age," + HexUtil.encode("45".getBytes()) + "]}");
        //tx.setBody(metaExp.getBytes());
        /** 复杂查询*/
        // 查询 body 含singer 或者  job 为singer的信息
        //String exp = ("${body.like[" + HexUtil.encode("singer".getBytes()) + "]||meta[job," + HexUtil.encode("singer".getBytes()) + "]}");
        //tx.setBody(exp.getBytes());
        //查询 (body 含singer 或者  job 为singer) 且name 为liudehua的信息
        //String exp = ("${(body.like[" + HexUtil.encode("singer".getBytes()) + "]||meta[job," + HexUtil.encode("singer".getBytes()) + "])&&meta[name," + HexUtil.encode("liudehua".getBytes()) + "]}");
        //tx.setBody(exp.getBytes());
        try {
            tx.setDst(new URI("daap://pdxdev/pdx.dapp/ledger/query"));
            List<Transaction> res = driver.query(tx);
            if (res != null && res.size() > 0) {
                res.stream().forEach(a -> System.out.println(new String(a.getBody())));
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void apply(BlockChainDriver driver) {
        Transaction tx = new Transaction();
        Map<String, byte[]> meta = new HashMap<>();
        meta.put("name", "linchong".getBytes());
        meta.put("age", "33".getBytes());
        meta.put("sex", "man".getBytes());
        tx.setMeta(meta);
        tx.setBody("this is a kf man".getBytes());
        try {
            tx.setDst(new URI("daap://pdxdev/pdx.dapp/ledger/apply"));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String res;
        try {
            res = driver.apply(tx);
            System.out.println(res);
        } catch (BlockChainDriverException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void applyForQueryData(BlockChainDriver driver) {
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setBody("this is a good man".getBytes());
        HashMap map = new LinkedHashMap();
        map.put("name", "liudehua".getBytes());
        map.put("age", "45".getBytes());
        map.put("sex", "man".getBytes());
        map.put("job", "singer".getBytes());
        tx1.setMeta(map);
        transactions.add(tx1);

        Transaction tx2 = new Transaction();
        tx2.setBody("this is a famous singer,she sings very well".getBytes());
        HashMap map2 = new LinkedHashMap();
        map2.put("name", "wangfei".getBytes());
        map2.put("age", "48".getBytes());
        map2.put("sex", "woman".getBytes());
        map2.put("job", "singer".getBytes());
        tx2.setMeta(map2);
        transactions.add(tx2);

        Transaction tx3 = new Transaction();
        tx3.setBody("this is a bad man".getBytes());
        HashMap map3 = new LinkedHashMap();
        map3.put("name", "wangdachui".getBytes());
        map3.put("age", "21".getBytes());
        map3.put("sex", "man".getBytes());
        map3.put("isFat", "yes".getBytes());
        tx3.setMeta(map3);
        transactions.add(tx3);

        Transaction tx4 = new Transaction();
        tx4.setBody("this is a programmer".getBytes());
        HashMap map4 = new LinkedHashMap();
        map4.put("name", "kangxinghao".getBytes());
        map4.put("age", "21".getBytes());
        map4.put("sex", "man".getBytes());
        map4.put("isTall", "yes".getBytes());
        map4.put("job", "engineer".getBytes());
        tx4.setMeta(map4);
        transactions.add(tx4);

        Transaction tx5 = new Transaction();
        tx5.setBody("this is a writer,he has written many books".getBytes());
        HashMap map5 = new LinkedHashMap();
        map5.put("name", "bochenlong".getBytes());
        map5.put("age", "25".getBytes());
        map5.put("sex", "man".getBytes());
        tx5.setMeta(map5);
        transactions.add(tx5);

        transactions.stream().map(a -> {
            try {
                a.setDst(new URI("daap://pdxdev/pdx.dapp/ledger/apply"));
                return a;
            } catch (Exception e) {
                e.printStackTrace();
                return a;
            }
        }).map(b -> {
            try {
                String res = driver.apply(b);
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }).forEach(System.out::println);

    }
}
