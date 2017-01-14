package biz.pdxtech.daap.ledger;

import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.bcdriver.BlockChainDriver;
import biz.pdxtech.daap.bcdriver.BlockChainDriverFactory;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

/**
 * refer to
 * https://github.com/PDXTechnologies/daap-caller/blob/master/src/main/java/biz/pdxtech/daap/caller/DaapCaller.java
 */
public class DaapCaller {
    public static void main(String[] args) {
        /*1 setting props*/
        Properties props = new Properties();
        props.setProperty("privateKey", "623aacd6be3174d096711814e27487f26ce0126279ab3c85823cd3b2e86da28d");
        
        /*2 setting contract dst*/
        setContractDst("daap://default_pdx_chain/pdxdev/pdx.dapp/example");
        
        /*3 generate testKey*/
        System.out.println("**************************************");
        String testKey = LocalDateTime.now().toString();
        System.out.println("remember testKey is : " + testKey);
        System.out.println("**************************************");
        
        /*4 write tx to pdx*/
        BlockChainDriver driver = BlockChainDriverFactory.get(props);
        applyForQueryData(driver, testKey);
    }
    
    private static String contractDst;
    
    private static void applyForQueryData(BlockChainDriver driver, String testKey) {
        assert StringUtils.isNotEmpty(testKey);
        
        List<Transaction> transactions = new ArrayList<>();
        Transaction tx1 = new Transaction();
        tx1.setBody(addTestKey("this is a good man", testKey).getBytes());
        HashMap map1 = new LinkedHashMap();
        map1.put("name", addTestKey("liudehua", testKey).getBytes());
        map1.put("age", addTestKey("45", testKey).getBytes());
        map1.put("sex", addTestKey("man", testKey).getBytes());
        map1.put("job", addTestKey("singer", testKey).getBytes());
        tx1.setMeta(map1);
        transactions.add(tx1);
        
        Transaction tx2 = new Transaction();
        tx2.setBody(addTestKey("this is a famous singer,she sings very well", testKey).getBytes());
        HashMap map2 = new LinkedHashMap();
        map2.put("name", addTestKey("wangfei", testKey).getBytes());
        map2.put("age", addTestKey("48", testKey).getBytes());
        map2.put("sex", addTestKey("woman", testKey).getBytes());
        map2.put("job", addTestKey("singer", testKey).getBytes());
        tx2.setMeta(map2);
        transactions.add(tx2);
        
        Transaction tx3 = new Transaction();
        tx3.setBody(addTestKey("this is a bad man", testKey).getBytes());
        HashMap map3 = new LinkedHashMap();
        map3.put("name", addTestKey("wangdachui", testKey).getBytes());
        map3.put("age", addTestKey("21", testKey).getBytes());
        map3.put("sex", addTestKey("man", testKey).getBytes());
        map3.put("isFat", addTestKey("yes", testKey).getBytes());
        tx3.setMeta(map3);
        transactions.add(tx3);
        
        Transaction tx4 = new Transaction();
        tx4.setBody(addTestKey("this is a programmer ", testKey).getBytes());
        HashMap map4 = new LinkedHashMap();
        map4.put("name", addTestKey("kangxinghao", testKey).getBytes());
        map4.put("age", addTestKey("21", testKey).getBytes());
        map4.put("sex", addTestKey("man", testKey).getBytes());
        map4.put("isTall", addTestKey("yes", testKey).getBytes());
        map4.put("job", addTestKey("engineer", testKey).getBytes());
        tx4.setMeta(map4);
        transactions.add(tx4);
        
        Transaction tx5 = new Transaction();
        tx5.setBody(addTestKey("this is a writer,he has written many books ", testKey).getBytes());
        HashMap map5 = new LinkedHashMap();
        map5.put("name", addTestKey("bochenlong", testKey).getBytes());
        map5.put("age", addTestKey("25", testKey).getBytes());
        map5.put("sex", addTestKey("man", testKey).getBytes());
        tx5.setMeta(map5);
        transactions.add(tx5);
        
        transactions.stream().map(a -> {
            try {
                a.setDst(new URI(contractDst));
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
    
    public static String addTestKey(String value, String testKey) {
        return new StringBuilder().append(value).append(" ").append(testKey).toString();
    }
    
    private static void setContractDst(String dst) {
        contractDst = dst;
    }
    
    public static String getContractDst() {
        return contractDst;
    }
}
