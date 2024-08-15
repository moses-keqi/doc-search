FROM alpine:3.19.1
#FROM alpine:3.6
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.nju.edu.cn/g' /etc/apk/repositories && apk update && apk upgrade
#RUN apk add --no-cache bash openjdk17 libreoffice nginx
RUN apk add --no-cache bash tzdata curl font-droid-nonlatin  openjdk17 libreoffice nginx
#/usr/bin/libreoffice


RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone


RUN apk del tzdata



# Set the lang, you can also specify it as as environment variable through docker-compose.yml
ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US.UTF-8

RUN mkdir -p /root/.fonts
ADD doc-search/target/doc-search.jar /app.jar
COPY doc-search-web/dist /usr/share/nginx/html/
ADD docker/nginx/default.conf /etc/nginx/http.d/default.conf
ADD docker/nginx/simsun.ttc /root/.fonts/simsun.ttc
COPY docker/entry-point.sh /entry-point.sh
RUN chmod +x /entry-point.sh
EXPOSE 80
CMD ["/entry-point.sh"]
#ENTRYPOINT ["java", "-jar", "app.jar"]
#CMD ["nginx", "-g", "daemon off;"]


# docker build -t moese/doc-search:20240815001 .

