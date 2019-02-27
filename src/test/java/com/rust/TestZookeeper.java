 /*
  * Package com.rust
  * FileName: TestZookeeper
  * Author:   Takho
  * Date:     18/12/23 20:13
  */
 package com.rust;

 import org.apache.zookeeper.CreateMode;
 import org.apache.zookeeper.KeeperException;
 import org.apache.zookeeper.Watcher;
 import org.apache.zookeeper.Watcher.Event.EventType;
 import org.apache.zookeeper.Watcher.Event.KeeperState;
 import org.apache.zookeeper.ZooDefs.Ids;
 import org.apache.zookeeper.ZooKeeper;
 import org.apache.zookeeper.data.Stat;
 import org.junit.Before;
 import org.junit.Test;

 import java.io.IOException;
 import java.util.List;
 import java.util.concurrent.Phaser;
 import java.util.concurrent.atomic.AtomicInteger;

 /**
  * @author Takho
  */
 public class TestZookeeper {
	 private ZooKeeper zk;
	 private Watcher watcher;

	 @Before
	 public void initZkCli() throws IOException {
		 final Phaser phaser = new Phaser(1);
		 String conn = "s101:2181,s102:2181,s103:2181";
		 // watcher是观察者对象，用于回调
		 Watcher watcher = event -> {
			 System.out.println("有事件发生了" + event);
			 if (KeeperState.SyncConnected == event.getState()) {
				 phaser.arrive();
			 }
		 };
		 ZooKeeper zk = new ZooKeeper(conn, 5000, watcher);
		 phaser.awaitAdvance(phaser.getPhase());
		 this.zk = zk;
		 this.watcher = watcher;

	 }

	 @Test
	 public void connZK() throws Exception {
		 long sessionId = zk.getSessionId();
		 System.out.println(sessionId);
		 //	创建路径znode
		 zk.create("/root", "helloworld".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		 System.out.println("over");
	 }

	 /**
	  * 更新节点
	  *
	  * @throws Exception
	  */
	 @Test
	 public void updateZNode() throws Exception {

		 zk.setData("/root", "how are your?".getBytes(), 0);

		 System.out.println("over");

	 }


	 /**
	  * 连接到zk
	  *
	  * @throws Exception
	  */
	 @Test
	 public void getZNode() throws Exception {

		 //	状态对象，在getData时候，接收节点的状态信息的
		 Stat stat = new Stat();
		 byte[] data = zk.getData("/root", null, stat);

		 System.out.println(new String(data));

	 }


	 /**
	  * 创建临时节点
	  *
	  * @throws Exception
	  */
	 @Test
	 public void getEphemeralZNode() throws Exception {

		 zk.create("/root/myenode", "helloworld".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		 System.out.println("over");

	 }


	 /**
	  * 创建顺序节点
	  *
	  * @throws Exception
	  */
	 @Test
	 public void createPersistenceSeqZNode() throws Exception {

		 String s = zk.create("/root/myseq", "helloworld".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		 System.out.println(s);

		 System.out.println("over");

	 }

	 /**
	  * 获取孩子
	  *
	  * @throws Exception
	  */
	 @Test
	 public void getChildZNode() throws Exception {

		 List<String> children = zk.getChildren("/", null);
		 children.forEach(System.out::println);

	 }

	 /**
	  * 删除节点
	  *
	  * @throws Exception
	  */
	 @Test
	 public void deleteZNode() throws Exception {
		 zk.delete("/root/myenode0000000001", 0);
	 }

	 /**
	  * 测试观察者模式，回调
	  *
	  * @throws Exception
	  */
	 @Test
	 public void testWatcher() throws Exception {

		 byte[] bs = zk.getData("/root", watcher, new Stat());
		 System.out.println(new String(bs));

		 while (true) {

		 }


	 }


	 /**
	  * 重新注册观察者
	  *
	  * @throws Exception
	  */
	 @Test
	 public void registerWatcher() throws Exception {

		 Phaser p2 = new Phaser(1);

		 String conn = "s101:2181,s102:2181,s103:2181";
		 // watcher是观察者对象，用于回调
		 AtomicInteger ai = new AtomicInteger(1);
		 watcher = event -> {
			 System.out.println("有事件发生了" + event);
			 if (event.getState() == KeeperState.SyncConnected) {
			 }


			 if (event.getType().equals(EventType.NodeDataChanged)) {
				 try {
					 zk.getData("/root", watcher, null);
				 } catch (KeeperException | InterruptedException e) {
					 e.printStackTrace();
				 }
			 }
		 };

		 ZooKeeper zk = new ZooKeeper(conn, 5000, watcher);
		 this.zk = zk;
		 p2.arrive();
		 byte[] data = zk.getData("/root", this.watcher, new Stat());
		 System.out.println(new String(data));
		 while (true) {

		 }


	 }

	 @Test
	 public void listAllZnode() throws Exception {
		 printZnode("/");
	 }

	 private void printZnode(String path) throws Exception {
		 System.out.println(path);
		 List<String> children = zk.getChildren(path, false);
		 for (String k : children) {
			 try {

				 if (path.equals("/")) {
					 path = "";
				 }
				 printZnode(path + "/" + k);
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 }

	 }


 }
