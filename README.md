[Ref]
1、http://docs.spring.io/spring-hadoop/docs/2.0.2.RELEASE/reference/html/hadoop.html

2、https://github.com/spring-projects/spring-data-book/blob/master/hadoop/hbase/src/main/resources/META-INF/spring/application-context.xml

3、NEED to mention spring auto inject：	<context:component-scan base-package="storm.hbase.examples"/>


[Env]
export MAVEN_OPTS=-Dfile.encoding=UTF-8
export LC_ALL=UTF-8

[Build]
mvn clean package appassembler:assemble

[Create Table]



[Run]
mvn compile exec:java -Dstorm.topology=storm.hbase.examples.kafkaTopology