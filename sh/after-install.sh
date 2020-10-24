cp /var/app/trading-app.service /etc/systemd/system/
systemctl daemon-reload
cd /var/app
rm -Rf jar
mkdir jar
cd jar
jar xf ../*.jar
rm -f ../*.jar
jar cvf0 ../new.jar *