source config.txt
kill -9 $(netstat -nlp | grep :$port | awk '{print $7}' | awk -F"/" '{ print $1 }')
docker-compose down
