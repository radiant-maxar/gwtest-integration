#!/bin/bash

#GEOWAVE

# Use GeoWave repo RPM to configure a host and search for GeoWave RPMs to install
# Several of the rpms (accumulo, jetty and tools) are both GeoWave version and vendor version specific
# In the examples below the rpm name geowave-$VERSION-VENDOR_VERSION would be adjusted as needed
rpm -Uvh http://s3.amazonaws.com/geowave-rpms/release/noarch/geowave-repo-1.0-3.noarch.rpm
yum --enablerepo=geowave search geowave-0.9.3-apache

# Install GeoWave Accumulo iterator on a host (probably a namenode)
yum --enablerepo=geowave install geowave-0.9.3-apache-accumulo

# Update
yum --enablerepo=geowave install geowave-0.9.3-apache-*

# GEOSERVER

export BUILD_ARGS="-Daccumulo.version=1.7.2 -Daccumulo.api=1.7 -Dhbase.version=1.3.0 -Dhadoop.version=2.7.3 -Dgeotools.version=16.0 -Dgeoserver.version=2.10.0"
cp deploy/target/*-geoserver-singlejar.jar /opt/tomcat/webapps/geoserver/WEB-INF/lib/
mvn package -P geotools-container-singlejar $BUILD_ARGS
