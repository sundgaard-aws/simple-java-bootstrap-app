 #!/bin/bash
echo Ensuring that Java 11 corretto is installed...
yum update -y
rpm --import https://yum.corretto.aws/corretto.key
curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
yum update -y
yum install -y java-11-amazon-corretto-devel
echo Java 11 corretto installed.
