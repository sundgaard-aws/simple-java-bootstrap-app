# simple-java-bootstrap-app
Simple Java Bootstrap App v1.0

# Building it
mvn compile

# Running it
mvn spring-boot:run

# Verifying it
curl localhost:8008

# Install Linux Service
``` sh
# Copy the .service file to /etc/systemd/system/
sudo cp trading-app.service /etc/systemd/system/
# Start the service.
sudo systemctl start trading-app.service
# Check the status of the service.
sudo systemctl status  trading-app.service
```

# Windows kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F