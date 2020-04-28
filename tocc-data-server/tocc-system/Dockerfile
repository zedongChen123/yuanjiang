FROM java:8
ENV WORKDIR /tocc-system
ENV JAR tocc-system.jar
ENV TZ=Asia/Shanghai
RUN /bin/cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ >/etc/timezone
WORKDIR $WORKDIR
COPY target/$JAR .
CMD java -jar $JAR
EXPOSE 8006


