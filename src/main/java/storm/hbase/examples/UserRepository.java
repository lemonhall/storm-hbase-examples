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

import java.util.List;
import java.io.IOException;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;
import org.springframework.data.hadoop.hbase.HbaseUtils;
import org.springframework.data.hadoop.hbase.HbaseSynchronizationManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Repository
public class UserRepository {

	@Autowired
	private HbaseTemplate hbaseTemplate;

	private HTableInterface utable;

	private String tableName = "users";

	public static byte[] CF_INFO = Bytes.toBytes("cfInfo");

	private byte[] qUser = Bytes.toBytes("user");
	private byte[] qEmail = Bytes.toBytes("email");
	private byte[] qPassword = Bytes.toBytes("password");

	private static final Log log = LogFactory.getLog(UserRepository.class);

	public List<User> findAll() {
		return hbaseTemplate.find(tableName, "cfInfo", new RowMapper<User>() {
			@Override
			public User mapRow(Result result, int rowNum) throws Exception {
				return new User(Bytes.toString(result.getValue(CF_INFO, qUser)), 
							    Bytes.toString(result.getValue(CF_INFO, qEmail)),
							    Bytes.toString(result.getValue(CF_INFO, qPassword)));
			}
		});

	}

	public User save(final String userName, final String email,final String password) {
		return hbaseTemplate.execute(tableName, new TableCallback<User>() {
			@Override
			public User doInTable(HTableInterface table) throws Throwable {
				table.setAutoFlushTo(false);
				User user = new User(userName, email, password);
					Put p = new Put(Bytes.toBytes(user.getName()));
					p.add(CF_INFO, qUser, Bytes.toBytes(user.getName()));
					table.put(p);
				return user;
			}
		});
	}

	public User inc(final String userName) {
		return hbaseTemplate.execute(tableName, new TableCallback<User>() {
			@Override
			public User doInTable(HTableInterface table) throws Throwable {
				table.setAutoFlushTo(false);
				if(table.isAutoFlush()){
					log.info("isAutoFlush=True");
				}else{
					log.info("isAutoFlush=False");
				}
				User user = new User(userName, "", "");
					Put p = new Put(Bytes.toBytes(user.getName()));
					p.add(CF_INFO, qUser, Bytes.toBytes(user.getName()));
					table.put(p);
				return user;
			}
		});
	}

	public void init(){
		utable = HbaseUtils.getHTable("users", hbaseTemplate.getConfiguration(), hbaseTemplate.getCharset(), hbaseTemplate.getTableFactory());
		HbaseSynchronizationManager.bindResource("users", utable);
	}

}