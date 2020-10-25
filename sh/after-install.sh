echo Updating trading app linux service definition...
cp /var/app/trading-app.service /etc/systemd/system/
echo Reloading linux daemon service...
systemctl daemon-reload
echo Fixing compressed JAR...
cd /var/app
rm -Rf jar
mkdir jar
cd jar
jar xf ../*.jar
rm -f ../*.jar
echo Creating new uncompressed JAR...
jar cmvf0 META-INF/MANIFEST.MF ../trading-app-0.0.1-SNAPSHOT.jar *
echo Done.