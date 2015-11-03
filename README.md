# Tomcat JDBC Connection Pool Plugin

[![Codacy Badge](https://api.codacy.com/project/badge/grade/b67192ff30fb48bdb4aab2abb486ad26)](https://www.codacy.com/app/Codacy/play-tomcatcp)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/b67192ff30fb48bdb4aab2abb486ad26)](https://www.codacy.com/app/Codacy/play-tomcatcp)
[![Circle CI](https://circleci.com/gh/codacy/play-tomcatcp.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/play-tomcatcp)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-tomcatcp_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/play-tomcatcp_2.11)

This plugin works with PlayFramework `2.3.x`. It uses version `8.0.11` of Tomcat JDBC CP.

## Usage

Add the following dependency to your `project/build.sbt` or `project/Build.scala`:

    "com.codacy" %% "play-tomcatcp" % "1.0.0"

### Step 2: Disable default `dbplugin`

Add the following line to your `conf/application.conf`:

    dbplugin=disabled

This will disable dbplugin and avoids that BoneCP creates useless connections.

### Step 3: Enable TomcatCP Plugin

Add the following line to your `conf/play.plugins`:

    200:com.codacy.play.tomcatcp.TomcatCPPlugin

### Step 4: Configure TomcatCP

For detailed descriptions on each option check the [Tomcat JDBC CP Configuration](https://tomcat.apache.org/tomcat-8.0-doc/jdbc-pool.html#Attributes) page.

##### Example Configuration:

```
dbplugin=disabled

db.default.driver=org.example.Driver
db.default.url="jdbc:postgresql://localhost:5432/exampledb"
db.default.user="example"
db.default.password="example"
```

## Thanks

The code here is highly inspired by the [HikariCP](http://edulify.github.io/play-hikaricp.edulify.com/) plugin.
