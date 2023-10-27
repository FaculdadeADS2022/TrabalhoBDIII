FROM tomcat:latest

RUN mkdir -p /usr/local/tomcat/webapps/sem3bank

COPY sem3bank.war /usr/local/tomcat/webapps/sem3bank/
