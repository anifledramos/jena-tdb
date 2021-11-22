docker stop rdfdocker

docker rm rdfdocker

docker build -t jenahdt --rm=true --no-cache=true .

#docker run --name rdfdocker -it jenahdt

docker run --name rdfdocker --mount type=bind,source="$(pwd)/../data",target=/data -it jenahdt

docker exec -it rdfdocker /bin/bash