#!/usr/bin/zsh

docker run -d \
	--name user-manager-db \
	-e POSTGRES_USER=user-manager \
	-e POSTGRES_PASSWORD=v63R_M4na9eR \
	-e POSTGRES_DB=user-manager \
	-p 5460:5432 \
	postgres:16-alpine