#!/bin/bash -xe

export NUM_WORKERS=4
export KEYNAME="testkey"
export MAC=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/)
export SUBNET_ID=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/subnet-id)
export MASTER_SECURITY_GROUP=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/security-group-ids)
export SLAVE_SECURITY_GROUP=$(curl http://169.254.169.254/latest/meta-data/network/interfaces/macs/$MAC/security-group-ids)
export EMR_VERSION="emr-5.6.0"
export TAGNAME=""
export REGION="us-east-1"
export VERSION="latest"
export CLUSTER_NAME="temp-gw-test-cluster-pipeline"


export CLUSTER_ID=$(aws emr create-cluster \
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

aws emr wait cluster-running --cluster-id ${CLUSTER_ID} --region ${REGION}
export DOMAIN=$(aws emr describe-cluster --cluster-id ${CLUSTER_ID} --region ${REGION} | jq .Cluster.MasterPublicDnsName | tr -d '"')
