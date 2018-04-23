#!/bin/bash -xe

# Get variables
NUM_WORKERS=2
KEYNAME="geowave-benchmark"
SUBNET_ID="subnet-0db4897b"
MASTER_SECURITY_GROUP="sg-edc8f490"
SLAVE_SECURITY_GROUP="sg-e9c8f494"
EMR_VERSION="emr-5.13.0"
TAGNAME=""
REGION="us-east-1"
VERSION="latest"
CLUSTER_NAME="temp-gw-test-cluster-pipeline"

AWS_CREDS=$(aws sts assume-role --role-arn 'arn:aws:iam::539674021708:role/geowave-jenkins-EMR-crossAccountSignIn' --role-session-name 'geowave-qa-launch-emr')

export AWS_ACCESS_KEY_ID=$(echo ${AWS_CREDS} | jq .Credentials.AccessKeyId | tr -d '"')
export AWS_SECRET_ACCESS_KEY=$(echo ${AWS_CREDS} | jq .Credentials.SecretAccessKey | tr -d '"')
export AWS_SESSION_TOKEN=$(echo ${AWS_CREDS} | jq .Credentials.SessionToken | tr -d '"')

# Switch between configurations, based on what is being tested.
if [ $option = "hbase" ]; then
	additionalApps="Name=HBase"
	bootstraps="Path=s3://geowave/latest/scripts/emr/hbase/bootstrap-geowave.sh"
	size="32"
elif [ $option = "jupyter" ]; then
	additionalApps="Name=HBase Name=Pig Name=Spark Name=Hive"
	bootstraps="Path=s3://geowave/latest/scripts/emr/hbase/bootstrap-geowave.sh Path=s3://geowave-jupyter/bootstrap-jupyter.sh"
	size="48"
elif [ $option = "accumulo" ]; then
	additionalApps=""
	bootstraps="Path=s3://geowave/latest/scripts/emr/accumulo/bootstrap-geowave.sh"
	size="32"
else
	echo "WARNING: The option $option is unrecognized.  Please check test scripts!!!"
	additionalApps=""
	bootstraps=""
	size="32"
fi



# Create cluster
CLUSTER_ID=$(aws emr create-cluster \
--name ${CLUSTER_NAME} \
--ec2-attributes "KeyName=${KEYNAME},SubnetId=${SUBNET_ID},EmrManagedMasterSecurityGroup=${MASTER_SECURITY_GROUP},EmrManagedSlaveSecurityGroup=${SLAVE_SECURITY_GROUP}" \
--release-label ${EMR_VERSION} \
--applications Name=Hadoop Name=ZooKeeper ${additionalApps} \
--use-default-roles \
--tags Project="Geowave" User="James-Auto" DeleteWhen="Running for more than 1 HR" \
--log-uri s3://james-emr-test-logs \
--instance-groups InstanceGroupType=MASTER,InstanceType=m4.xlarge,BidPrice=0.5,InstanceCount=1 \
InstanceGroupType=CORE,InstanceType=m4.xlarge,BidPrice=0.5,InstanceCount=2 \
--ebs-root-volume-size ${size} \
--bootstrap-action ${bootstraps} \
--region ${REGION} | jq .ClusterId | tr -d '"')

# Wait until cluster has been created
aws emr wait cluster-running --cluster-id ${CLUSTER_ID} --region ${REGION}

# Get cluster domain
DOMAIN=$(aws emr describe-cluster --cluster-id ${CLUSTER_ID} --region ${REGION} | jq .Cluster.MasterPublicDnsName | tr -d '"')

# Write variables to files
echo "$DOMAIN" > DOMAIN_${option}
sudo chmod 666 DOMAIN_${option}
echo "$KEYNAME" > KEYNAME
sudo chmod 666 KEYNAME
echo "$CLUSTER_ID" > CLUSTER_ID_${option}
sudo chmod 666 CLUSTER_ID_${option}
echo "$REGION" > REGION
sudo chmod 666 REGION