FROM java:8
ENV WORKDIR /tocc-config-center3344
ENV JAR tocc-config-cneter3344.jar
ENV TZ=Asia/Shanghai
RUN /bin/cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ >/etc/timezone
WORKDIR $WORKDIR
COPY target/$JAR .
CMD java -jar $JAR
EXPOSE 3344


