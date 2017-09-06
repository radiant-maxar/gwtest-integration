# Install maven.

wget http://mirror.stjschools.org/public/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
tar xvf apache-maven-3.5.0-bin.tar.gz
export M2_HOME=/home/hadoop/apache-maven-3.5.0
export M2=$M2_HOME/bin
export PATH=$M2:$PATH
mvn -version

# Install git
sudo yum -y install git
