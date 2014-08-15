/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package storm.hbase.examples;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;


import storm.kafka.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class kafkaTopology {

  public static void main(String[] args) throws Exception {

    BrokerHosts brokerHosts = new ZkHosts("127.0.0.1:2181");

    String topic = "test";  
    String zkRoot = "/kafkastorm";  
    String spoutId = "myKafka";

    SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, topic, zkRoot, spoutId);

    spoutConfig.zkServers = new ArrayList<String>(){{  
                add("127.0.0.1");  
            }};  
    spoutConfig.zkPort = 2181; 

    spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("word", new KafkaSpout(spoutConfig), 1);

    builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");

    Config conf = new Config();
    conf.setDebug(true);


    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("kafka-storm", conf, builder.createTopology());

      Thread.sleep(1000000000);

      cluster.shutdown();
    }
  }
}
