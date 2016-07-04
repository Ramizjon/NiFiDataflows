# NiFiDataflows
**NiFiDataflows** is a project that automates dataflow processing and data transmission between systems using [Apache NiFi](https://nifi.apache.org/) product.

#### KafkaToHDFSDataflow
KafkaToHDFSDataflow performs data transmission between Apache Kafka and HDFS.

Processors that are being used in the pipeline:
- GetKafka - Fetches messages from Apache Kafka
- PutHDFS - Writes FlowFile data to Hadoop Distributed File System (HDFS)

Service asks Kafka for new messages in topic every 60 seconds, then messages are being stored in *NiFi FlowFile* that can contain maximum 6000 messages. Each FlowFile represents one pull from Kafka topic. After that, successed FlowFiles are being written to HDFS partitioned by date. All values are configurable with NiFi UI.

#### Installation
In order to install Apache NiFi and use template of this project you have to perform following actions:
- Download appropriate NiFi version from [official page](http://nifi.apache.org/download.html) 
- Unzip package to some folder in local FS
- Run NiFi with `bin/nifi.sh run` command
- Import XML-formatted NiFi template from this repository
- Configure Processors regarding own Kafka configuration and HDFS directory location

#### Usability

Apache NiFi is pretty simple to dive in. Apache provides comprehensive documentation for it. 
In order to create simple dataflow you need to:
- Use ready NiFi FlowFile Processors
- Connect them with FlowFile Connections in directed graphs.

Popup hints are provided during Processors selection.
NiFi provides own Expression Language to reference FlowFile attributes and manipulate their values.

In this project, in order to partition FlowFiles by date *NiFi Expression Language* is used.
For instance, to create partitioning of format `year/month/day/hour` the following syntax is applied:

```
/path/to/nifi-output/${now():format('yyyy-MM')}/${now():format('dd')}/${now():format('HH')}
```

#### Maintainability
NiFi provides guaranteed devivery of data, even at high scale. NiFi’s content repository is designed to act as a rolling buffer of history. Data is removed only as it ages off the content repository or as space is needed. This combined with the data provenance capability makes for an incredibly useful basis to enable click-to-content, download of content, and replay, all at a specific point in an object’s lifecycle which can even span generations.


#### Scalability
NiFi executes within a JVM living within a host operating system. It is also able to operate within a cluster.
A NiFi cluster is comprised of one or more NiFi Nodes (Node) controlled by a single NiFi Cluster Manager (NCM). The design of clustering is a simple master/slave model where the NCM is the master and the Nodes are the slaves. The NCM’s reason for existence is to keep track of which Nodes are in the cluster, their status, and to replicate requests to modify or observe the flow. While the model is that of master and slave, if the master dies the Nodes are all instructed to continue operating as they were to ensure the data flow remains live. 
![Screenshot 1](https://s31.postimg.org/5xqm4vs4r/nifi4.png)


#### Screenshots
Project's dataflow NiFi UI schema representation:
![Screenshot 2] (https://s32.postimg.org/piy6f5zn9/nifi1.png)

Resulting files on HDFS under partitioned directory:
![Screenshot 3](https://s32.postimg.org/kxpjacb05/nifi2.png)
