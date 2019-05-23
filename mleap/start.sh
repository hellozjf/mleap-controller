source config.txt
java -jar mleap-controller-$version.jar --spring.profiles.active=prod --custom.harbor-ip=$harbor --server.port=$port &
