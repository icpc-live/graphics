all: gen-java.jar

clean:
	rm -rf gen-java gen-javabean gen-py gen-java.jar gen-javabean.jar

thrift: Makefile ../src/se/kth/livetech/communication/communication.thrift
	thrift --gen java:beans --gen java --gen py ../src/se/kth/livetech/communication/communication.thrift

gen-java.jar: thrift
	javac -Xlint:unchecked -cp gen-java:../lib/libthrift.jar:../lib/slf4j-api-1.5.8.jar gen-java/se/kth/livetech/communication/thrift/*.java
	jar cf gen-java.jar -C gen-java/ .

gen-javabean.jar: thrift
	javac -Xlint:unchecked -cp gen-javabean:../lib/libthrift.jar gen-javabean/se/kth/livetech/communication/thrift/*.java
	jar cf gen-javabean.jar -C gen-javabean/ .

.PHONY: all, clean, thrift
