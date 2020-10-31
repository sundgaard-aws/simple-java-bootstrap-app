#!bin/bash
echo $PWD
ls -al
# Notice that /var/app is configured in appspec.yml and could be any path you want
#java -jar /var/app/trading-app-0.0.1-SNAPSHOT.jar --server.port=80 &

# should really run as a service, check below link
# https://www.baeldung.com/spring-boot-app-as-a-service

systemctl enable trading-app.service
systemctl start trading-app.service