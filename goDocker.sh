docker stop tdbdocker

docker rm tdbdocker

docker build -t jenatdb --rm=true --no-cache=true .

#docker run --name tdbdocker -it jenatdb

docker run --name tdbdocker --mount type=bind,source="$(pwd)/../data",target=/data -it jenatdb

docker exec -it tdbdocker /bin/bash