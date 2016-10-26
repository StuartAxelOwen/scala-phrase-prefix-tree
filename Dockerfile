FROM java:8

ENV SCALA_VERSION 2.11.8
ENV SBT_VERSION 0.13.12

# Install Scala
## Piping curl directly in tar
RUN \
  curl -fsL http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /app

# Things that don't change much:
ADD build.sbt /app/build.sbt
ADD project /app/project

RUN sbt compile

# Things that change often: (so we don't have to re-resolve deps each time)
ADD src /app/src

RUN sbt compile

CMD sbt test