#!/bin/bash -xe

# Get variables
NUM_WORKERS=1 # Put back to 4 later
KEYNAME="testkey"
MAC=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/)
SUBNET_ID=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/subnet-id)
MASTER_SECURITY_GROUP=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/security-group-ids)
SLAVE_SECURITY_GROUP=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/security-group-ids)
EMR_VERSION="emr-5.6.0"
TAGNAME=""
REGION="us-east-1"
VERSION="latest"
CLUSTER_NAME="temp-gw-test-cluster-pipeline"

# Create cluster
CLUSTER_ID=$(aws emr create-cluster \
--name ${CLUSTER_NAME} \
--ec2-attributes "KeyName=${KEYNAME},SubnetId=${SUBNET_ID},EmrManagedMasterSecurityGroup=${MASTER_SECURITY_GROUP},EmrManagedSlaveSecurityGroup=${SLAVE_SECURITY_GROUP}" \
--release-label ${EMR_VERSION} \
--applications Name=Hadoop Name=HBase \
--use-default-roles \
--no-auto-terminate \
--instance-fleets InstanceFleetType=MASTER,TargetOnDemandCapacity=1,InstanceTypeConfigs=['{InstanceType=m4.xlarge}'] \
InstanceFleetType=CORE,TargetSpotCapacity=$NUM_WORKERS,InstanceTypeConfigs=['{InstanceType=m4.xlarge,BidPrice=0.5,WeightedCapacity=1}'],LaunchSpecifications={SpotSpecification='{TimeoutDurationMinutes=120,TimeoutAction=SWITCH_TO_ON_DEMAND}'} \
--bootstrap-action Path=s3://geowave/latest/scripts/emr/quickstart/hbase/bootstrap-geowave.sh \
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