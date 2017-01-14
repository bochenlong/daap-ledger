# daap-ledger

## PDX DaaP ledger application


### 1. maven工程创建  pom.xml 配置

#### 配置 maven repo
		<repository>
			<id>pdx-release</id>
			<name>biz.pdxtech</name>
			<url>http://daap.pdx.life:8081/nexus/content/repositories/releases</url>
		</repository>

#### 配置PDX BlockChain Driver API  依赖
		<!-- bcdriver api -->
		<dependency>
			<groupId>biz.pdxtech.daap</groupId>
			<artifactId>daap-bcdriver</artifactId>
			<version>1.2.1</version>
		</dependency>

#### 配置Default Ethereum BlockChain Driver实现  依赖
		<!-- Default Ethereum BlockChain Driver -->
		<dependency>
			<groupId>biz.pdxtech.daap</groupId>
			<artifactId>daap-ethbcdriver</artifactId>
			<version>1.2.1</version>
		</dependency>

### 2. BcDriver实例

**实例化BcDriver**

调用BcDriver首先需要实例化Driver.主要是两个参数。一个是协议栈类型；另一个是用户私钥。缺省情况下，协议栈类型为ethereum, 私钥会由BcDriver自动生成。调用者也可以通
过如下两个方法之一自己进行参数设置。


#### 2.1通过环境变量实例化

	BlockChainDriver driver = BlockChainDriverFactory.get();
		PDX_DAAP_CHAIN_TYPE		// 协议栈类型，默认 ethereum (以太坊）
		PDX_DAAP_PRIVATEKEY		//【可选】ECC私钥，HEX格式


#### 2.2通过property配置实例化

	BlockChainDriver driver = BlockChainDriverFactory.get(property);
	#blockchain Type
	type=ethereum
	#privateKey
	privateKey=********************************************


###	3.BcDriver调用

	通过BcDriver以下方法调用链上或者远端合约：
			query
			apply
	参见DaapLedgerCaller 例子
			通过设置Transaction 中属性指定contract 地址和自定义逻辑等
			dst  -->合约地址
			meta -->元数据，可查询
			body -->自定义数据结构

#### 3.1 ledger-query方法详细说明
Ledger查询分为两类；两类查询都可以通过设置Transaction.meta属性进行查询或者Transaction.body属性进行查询

**设置Transaction.meta属性查询**
1 查询交易
 * 根据交易ID查询；设置meta - ("DaaP-Query-TXID","txidtext".getBytes())
 * 根据自定义信息meta查询 设置meta - ("name","nametext") 可设置多个，但认为它们是与的关系
   
2 查询合约状态
 * 根据交易ID查询；设置meta - ("DaaP-Query-STATE-TXID","txidtext".getBytes())；此处查询出来的结果为此次交易执行完的合约状态
 * 根据合约查询 设置meta - ("DaaP-Query-STATE-DST","dsttext")
   
**设置Transaction.body属性进行查询，这时候你需要传入一个exp表达式，表达式形如："${tx:body[bodytext]}".getBytes())**
1 查询交易
 * 根据交易ID查询 设置body表达式 `"${tx:txid[txidtext]}".getBytes() `
 * 根据自定义信息meta查询 设置body表达式 `"${tx:meta[metak,metav]||meta[metak,metav]}".getBytes()`
   
  *注意：metav是将原byte[] Hex序列化过的文本*
  *辅助查询条件页码 设置body表达式 "${tx:txid[txidtext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录*

2 查询合约状态
* 根据交易Id查询 设置body表达式 "${state:txid[txidtext]}".getBytes()
* 根据合约查询 设置body表达式 "${state:dst[dsttext]}".getBytes()
 
 *辅助查询条件页码 设置body表达式 "${state:dst[dsttext]&&pageno[2]}".getBytes() 不写默认为1页，每页固定1000记录*
 
##### 查询请注意：
 1 如果查询条件中含有txid的条件，则默认只根据txid查询
 2 exp表达式中必须以${tx:}/${state:}的形式
