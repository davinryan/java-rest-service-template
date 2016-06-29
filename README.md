# Setup documentation for this Service Template

## How to setup tomcat ee for efficient local development
If you want to run this application in Tomcat EE then do the following:

1) add the following items to $TOMCAT_EE_HOME/conf/context.xml. E.g. for connections to localhost
```
<Context>
...
    <!-- Whether to run application in debug mode or not -->
    <Environment
            name="providerInvoicingService.debug"
            value="true"
            type="java.lang.String"
            override="false"/>

    <!-- Whether to stop redacted messages getting logged so you can all data from the user. -->
    <Environment
            name="providerInvoicingService.disableLogRedactedMessages"
            value="true"
            type="java.lang.String"
            override="false"/>
         
    <Resource
            name="jdbc/egateway"
            auth="Container"
            type="javax.sql.DataSource"
            driverClassName="oracle.jdbc.OracleDriver"
            url="jdbc:oracle:thin:@127.0.0.1:1521:xe"
            username="EGATEWAY_OWNER"
            password="password"
            maxActive="20"
            maxIdle="10"
            maxWait="-1"/>

    <Resource
            auth="Container"
            name="jms/MFPQueueConnectionFactory"
            type="com.ibm.mq.jms.MQQueueConnectionFactory"
            description="MFP MQ Connection Factory"
            factory="com.ibm.mq.jms.MQQueueConnectionFactoryFactory"
            HOST="imana"
            PORT="12111"
            CHAN="MFPT.21.01"
            QMGR="MFPT.21"
            TRAN="1"/>

    <Resource
            auth="Container"
            name="jms/EFM_MFP.eSchedule.Submit"
            type="com.ibm.mq.jms.MQQueue"
            factory="com.ibm.mq.jms.MQQueueFactory"
            description="MFP MQ JMS queue"
            QU="EFM_MFP.eSchedule.Errors"
            QMGR="MFPT.21"/>
... 
</Context>
```
2) Install ./setup/ojdbc6-11.2.0.1.0.jar into $TOMCAT_EE_HOME/lib

3) Extract ./setup/ibmMQ7Jars.zip to $TOMCAT_EE_HOME/lib

4) To set the environment variable on Windows, create a setenv.bat or setenv.sh for linux/mac manually, and put it into the ${tomcat-folder}\bin folder.
Set the contents of this file to
#### Windows
```
set "JAVA_OPTS=%JAVA_OPTS% -Xms256m -Xmx1536m -XX:PermSize=64m -XX:MaxPermSize=128m"
```
#### Linux
```
export JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx1536m -XX:PermSize=128m -XX:MaxPermSize=512m"
```

5) Tomcat ee won't start correctly using intellij or eclipse directly unless you Disable the EJB blacklist enabled in versions >1.7.4 by default.
You'll basically get this error payload
WARN - "null OEJP/4.7" FAIL "Security error - foo.Bar is not whitelisted as deserializable, prevented before loading it." - Debug for StackTrace

Therefore edit $TOMCAT_EE_HOME/conf/system.properties and change the line

tomee.serialization.class.blacklist = *

to
 
tomee.serialization.class.blacklist = *

## Links to useful documentation
 Spring JPA: http://projects.spring.io/spring-data-jpa/

