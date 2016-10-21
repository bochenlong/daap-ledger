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
			<version>1.0.6</version>
		</dependency>

#### 配置Default Ethereum BlockChain Driver实现  依赖
		<!-- Default Ethereum BlockChain Driver -->
		<dependency>
			<groupId>biz.pdxtech.daap</groupId>
			<artifactId>daap-ethbcdriver</artifactId>
			<version>1.0.6</version>
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
			check
			apply
	参见DaapLedgerCaller 例子
			通过设置Transaction 中属性指定contract 地址和自定义逻辑等
			dst  -->合约地址
			meta -->元数据，可查询
			body -->自定义数据结构
