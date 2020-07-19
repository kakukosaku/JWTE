package com.github.kakukosaku.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Description
 *
 * @author kaku
 * Date    2020/7/19
 */
public class Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(System.getProperty("file.encoding"));  // UTF-8
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zoo = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("connected to zk!");
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        try {
            var resp = zoo.getData("/zk_test", false, null);
            System.out.println(new String(resp));
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        System.out.println(zoo.getSessionId());
        System.out.println(Arrays.toString(zoo.getSessionPasswd()));
        try {
            zoo.getChildren("/zk_test", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("got children znode event" + event);
                    try {
                        System.out.println(zoo.getChildren("/zk_test", false));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        try {
            zoo.create("/zk_test/lock-1", "hello from zk java client".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException.NodeExistsException e) {
            e.printStackTrace();
            System.out.println("exception(node has exists) when create znode!");
        } catch (KeeperException e) {
            e.printStackTrace();
            System.out.println("unk-exception when create znode!");
        }

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            zoo.close();
            System.out.println("closing session");
        }
    }

}
