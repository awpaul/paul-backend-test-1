FROM centos/systemd

# Java & MySQL
ENV JAVA_VERSION 1.8.0
ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}
RUN yum install java-${JAVA_VERSION}-openjdk-devel mariadb-server mariadb mysql-connector-java rpm-build unzip initscripts -y && \
    yum clean all

# Gradle
ENV GRADLE_VERSION 6.9
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin
RUN cd /usr/lib \
 && curl -fl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-bin.zip \
 && unzip "gradle-bin.zip" \
 && ln -s "/usr/lib/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle \
 && rm "gradle-bin.zip"

# Caches
VOLUME ["/sys/fs/cgroup", "/root/.gradle/caches", "/usr/bin/app"]

# Setup MySQL
RUN mysql_install_db --user=mysql --basedir=/usr --ldata=/var/lib/mysql
ENV NODE_VERSION=12.18.0
RUN curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"
RUN node --version
RUN npm --version
RUN mkdir /home/app
RUN mkdir /home/app/paul-backend-test-1
COPY . /home/app/paul-backend-test-1
EXPOSE 8443
#Execute app build and run
WORKDIR /home/app/paul-backend-test-1
CMD ["gradle", "clean", "buildRunTestApp"]