mkdir -p libs
cp ../libtapir/libtapir.so ./libs/

# Make the tapir binding using maven
mvn clean package