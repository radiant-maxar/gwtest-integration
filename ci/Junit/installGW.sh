#!/bin/bash

set -e -x
if yum list installed "$@" >/dev/null 2>&1; then
	echo "geowave RPM already installed."
else
	sudo yum install -y http://s3.amazonaws.com/geowave-rpms/release/noarch/geowave-repo-1.0-3.noarch.rpm
fi

cat << EOF >> /tmp/geowave.pp
class { 'geowave::repo': repo_base_url => 'http://s3.amazonaws.com/geowave-rpms/release/noarch/', repo_enabled => 1, } ->
class { 'geowave':
geowave_version       => '0.9.5',
hadoop_vendor_version => 'hdp2',
install_hbase      => true,
install_app           => true,
install_app_server    => true,
http_port             => '8993',
EOF
sudo yum -y --enablerepo=geowave install geowave-0.9.5-puppet.noarch >> /dev/null
sh -c "puppet apply /tmp/geowave.pp"

sudo -u hdfs hdfs dfs -mkdir /apps/hbase/data/lib
sudo -u hdfs hdfs dfs -mv /user/hbase/lib/geowave-hbase-0.9.5-hdp2.jar /apps/hbase/data/lib/geowave-hbase-0.9.5-hdp2.jar
cd /etc/hbase/conf
zip -g /usr/local/geowave/tools/geowave-tools-0.9.5-hdp2.jar hbase-site.xml
cp /usr/local/geowave/tools/geowave-tools-0.9.5-hdp2.jar /usr/local/geowave/geoserver/webapps/geoserver/WEB-INF/lib/
rm /usr/local/geowave/geoserver/webapps/geoserver/WEB-INF/lib/geowave-geoserver-0.9.5-hdp2.jar

service geowave start