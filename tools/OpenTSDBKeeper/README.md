## What is it?
This is a component used to manage a group of OpenTSDB. It has two feature now.

- First, it provides you a simple view of all you OpenTSDB state. When you have a group of OpenTSDB, it will help you get to know which works well and which comes slow.
- Second, it can be used to assign OpenTSDB to your consumer or client. You can let your consumers apply from this keeper rather than let them keep the OpenTSDB (url) directly.

## About State Monitor
Here, we pull OpenTSDB State Information by its [stat api](http://opentsdb.net/docs/build/html/api_http/stats/index.html). We do this for all your OpenTSDB and make a real-time monitor for you. Try `/opentsdb/state`. 

You can also grab all raw stat information for your OpenTSDB with `/opentsdb/state/raw`. We grade your OpenTSDB and make a rank basing on its threads,region clients and jvm. 

Also, we support mail notification here. When we detect anyone dead, we will send an email.

## About Resource Distribution
If you have a distributed compute layer and it writes to OpenTSDB continuously. This Keeper can help to make a better write performance.

During out practise, writing data to OpenTSDB continuously and heavily will easily make it crashed. Or it will become less efficient. This Keeper help you analyze all your OpenTSDB state and make a balance. Try `/opentsdb/apply` .



## Config

You should rename application.properties.example to application.properties and fill all the items you need.

## Build
```
# This project is a maven-based project
mvn install
```

## Run
```
# change the version following to the latest
java -jar OpenTSDBKeeper-1.0.0-RELEASE.jar
```

## Roadmap
- restart OpenTSDB when detecting it dead
- make the analysis for OpenTSDB State better
- regist OpenTSDB process to Zookeeper and use Zookeeper as cordinator
- persist the loss data to disk or database where connection is not stable

## Any Question?
Please mail to chenhao.ni@sky-data.cn or chenhaonee@outlook.com.
