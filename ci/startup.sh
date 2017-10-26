#!/bin/bash -xe

# Install maven.

wget http://mirror.stjschools.org/public/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar xvf apache-maven-3.5.0-bin.tar.gz
export M2_HOME=/home/hadoop/apache-maven-3.5.0
export M2=$M2_HOME/bin
export PATH=$M2:$PATH
mvn -version

# Install git
sudo yum -y install git

# Get GW scripts
cd /mnt
sudo wget s3.amazonaws.com/geowave/0.9.5/scripts/sandbox/quickstart/geowave-env.sh
sudo wget s3.amazonaws.com/geowave/0.9.5/scripts/emr/quickstart/KDEColorMap.sld
sudo wget s3.amazonaws.com/geowave/0.9.5/scripts/emr/quickstart/SubsamplePoints.sld
source /mnt/geowave-env.sh

# Get gdelt data
sudo mkdir $STAGING_DIR/gdelt;cd $STAGING_DIR/gdelt
sudo wget http://data.gdeltproject.org/events/md5sums
for file in `cat md5sums | cut -d' ' -f3 | grep "^${TIME_REGEX}"` ; \
do sudo wget http://data.gdeltproject.org/events/$file ; done
md5sum -c md5sums 2>&1 | grep "^${TIME_REGEX}"

# Install Gdal
cd /mnt
sudo wget http://demo.geo-solutions.it/share/github/imageio-ext/releases/1.1.X/1.1.7/native/gdal/linux/gdal192-CentOS5.8-gcc4.1.2-x86_64.tar.gz
tar -xvf gdal192-CentOS5.8-gcc4.1.2-x86_64.tar.gz
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/mnt
cd ~
