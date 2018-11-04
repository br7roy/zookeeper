 /*
  * Package com.rust
  * FileName: ZkClient
  * Author:   Rust
  * Date:     2018/11/4 19:57
  */
 package com.rust;

 import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

 import java.util.concurrent.TimeUnit;

 /**
  * @author Rust
  */
 public class ZkClient {
  public static void main(String[] args) throws Exception {
   String conn = "192.168.231.101:2181,192.168.231.102:2181,192.168.231.103:2181";
   ZooKeeper zk = new ZooKeeper(conn, 5000, null);

   while (!zk.getState().isConnected()) {

   }

   zk.create("/tank", "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

   TimeUnit.SECONDS.sleep(3);

   zk.close();


  }
 }
