#!/usr/bin/env bash
bash build.sh
docker run -t scala-prefix-tree sbt test