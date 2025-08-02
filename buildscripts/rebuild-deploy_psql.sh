#!/bin/bash

if [[ $(pwd | awk -F/ '{print $(NF), $(NF-1)}' | sed -e 's@ @/@g') != 'buildscripts/AbeChat' ]]; then
	echo 'Your not running this from the right duh-dir. Look in buildscripts whereever your abecChat repo is saved'
	exit 11
fi


docker compose down
docker image rm abechat-abechat-server-r:latest db redis-session
cd ../AbeChatServer
./gradlew clean build --parallel --refresh-dependencies
docker compose up -d abechat-server-r db redis-session
cd ../buildscripts
