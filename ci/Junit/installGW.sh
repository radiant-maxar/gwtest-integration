#!/bin/bash

sudo -u hdfs hdfs dfs -mkdir /apps/hbase/data/lib
sudo -u hdfs hdfs dfs -mv /user/hbase/lib/geowave-hbase-0.9.5-hdp2.jar /apps/hbase/data/lib/geowave-hbase-0.9.5-hdp2.jar
cd /etc/hbase/conf
zip -g /usr/local/geowave/tools/geowave-tools-0.9.5-hdp2.jar hbase-site.xml
cp /usr/local/geowave/tools/geowave-tools-0.9.5-hdp2.jar /usr/local/geowave/geoserver/webapps/geoserver/WEB-INF/lib/
rm /usr/local/geowave/geoserver/webapps/geoserver/WEB-INF/lib/geowave-geoserver-0.9.5-hdp2.jar

service geowave start