docker stop gccdocker

docker rm gccdocker

docker build -t jena --rm=true .

docker run --name jenatdb -it jena

# docker run --name jenatdb --mount type=bind,source="$(pwd)/data", target=/data -it jena

#docker exec -it jenatdb /bin/bash
