FROM tomcat:latest

COPY target/*.war /usr/local/tomcat/webapps/
COPY web.xml /usr/local/tomcat/conf/

CMD ["catalina.sh", "run"]