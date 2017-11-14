#!/bin/bash -xe

# Get variables
NUM_WORKERS=4
KEYNAME="geowave-benchmark"
SUBNET_ID="subnet-0db4897b"
MASTER_SECURITY_GROUP="sg-edc8f490"
SLAVE_SECURITY_GROUP="sg-e9c8f494"
EMR_VERSION="emr-5.7.0"
TAGNAME=""
REGION="us-east-1"
VERSION="latest"
CLUSTER_NAME="temp-gw-test-cluster-pipeline"

AWS_CREDS=$(aws sts assume-role --role-arn 'arn:aws:iam::539674021708:role/geowave-jenkins-EMR-crossAccountSignIn' --role-session-name 'geowave-qa-launch-emr')

export AWS_ACCESS_KEY_ID=$(echo ${AWS_CREDS} | jq .Credentials.AccessKeyId | tr -d '"')
export AWS_SECRET_ACCESS_KEY=$(echo ${AWS_CREDS} | jq .Credentials.SecretAccessKey | tr -d '"')
export AWS_SESSION_TOKEN=$(echo ${AWS_CREDS} | jq .Credentials.SessionToken | tr -d '"')

# Create cluster
CLUSTER_ID=$(aws emr create-cluster \
--name ${CLUSTER_NAME} \
--ec2-attributes "KeyName=${KEYNAME},SubnetId=${SUBNET_ID},EmrManagedMasterSecurityGroup=${MASTER_SECURITY_GROUP},EmrManagedSlaveSecurityGroup=${SLAVE_SECURITY_GROUP}" \
--release-label ${EMR_VERSION} \
--applications Name=Hadoop Name=HBase Name=Pig Name=Hue Name=Hive \
--use-default-roles \
--no-auto-terminate \
--tags Project="Geowave" User="James-Auto" Duration="About 20 minutes; Delete if > 1 HR" \
--log-uri s3://temp-logs-james \
--instance-fleets InstanceFleetType=MASTER,TargetOnDemandCapacity=1,InstanceTypeConfigs=['{InstanceType=m4.xlarge}'] \
InstanceFleetType=CORE,TargetSpotCapacity=$NUM_WORKERS,InstanceTypeConfigs=['{InstanceType=m4.xlarge,BidPrice=0.5,WeightedCapacity=1}'],LaunchSpecifications={SpotSpecification='{TimeoutDurationMinutes=120,TimeoutAction=SWITCH_TO_ON_DEMAND}'} \
--bootstrap-action Path=s3://geowave/latest/scripts/emr/hbase/bootstrap-geowave.sh \
--region ${REGION} | jq .ClusterId | tr -d '"')

# Wait until cluster has been created
aws emr wait cluster-running --cluster-id ${CLUSTER_ID} --region ${REGION}

# Get cluster domain
DOMAIN=$(aws emr describe-cluster --cluster-id ${CLUSTER_ID} --region ${REGION} | jq .Cluster.MasterPublicDnsName | tr -d '"')

# Write variables to files
echo "$DOMAIN" > DOMAIN
sudo chmod 666 DOMAIN
echo "$KEYNAME" > KEYNAME
sudo chmod 666 KEYNAME
echo "$CLUSTER_ID" > CLUSTER_ID
sudo chmod 666 CLUSTER_ID
echo "$REGION" > REGION
sudo chmod 666 REGION