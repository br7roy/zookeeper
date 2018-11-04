 /*
  * Package com.rust
  * FileName: TankMaster
  * Author:   Rust
  * Date:     2018/11/4 20:17
  */
 package com.rust;

 import org.apache.zookeeper.CreateMode;
 import org.apache.zookeeper.WatchedEvent;
 import org.apache.zookeeper.Watcher;
 import org.apache.zookeeper.Watcher.Event.EventType;
 import org.apache.zookeeper.ZooDefs.Ids;
 import org.apache.zookeeper.ZooKeeper;

 import java.util.Random;

 /**
  * 主备热切换
  * @author Rust
  */
 public class TankMaster {
	 static String status = "ACTIVE";

	 public static void main(String[] args) throws Exception {
		 String conn = "192.168.231.101:2181,192.168.231.102:2181,192.168.231.103:2181";
		 ZooKeeper zk = new ZooKeeper(conn, 5000, null);
		 String ip = "202.106.29.138";
		 String port = new Random().nextInt(500) + "";
		 System.out.println(port);

		 if (zk.exists("/tank/master", new MasterWatcher()) != null) { // 节点已经被创建
			 status = "STANDBY";
		 } else {
			 zk.create("/tank/master", (ip + ":" + port).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			 status = "ACTIVE";
			 // 启动Server

			 System.out.println("启动就是ACTIVE");
		 }
	 }

	 static class MasterWatcher implements Watcher {
		 @Override
		 public void process(WatchedEvent event) {
			 if (event.getType() == EventType.NodeDeleted) {
				 status = "ACTIVE";
				 System.out.println("切换为ACTIVE");
			 }


		 }
	 }
 }
