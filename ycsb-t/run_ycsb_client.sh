
# Load the records in Tapir
java -cp tapir-interface/target/tapir-interface-0.1.4.jar:core/target/core-0.1.4.jar:tapir/target/tapir-binding-0.1.4.jar:javacpp/target/javacpp.jar \
-Djava.library.path=libs/ com.yahoo.ycsb.Client -P workloads/workloada \
-load -db com.yahoo.ycsb.db.TapirClient \
-s -p tapir.configpath=../store/tools/shard -p tapir.nshards=3 -p tapir.closestreplica=0 > load.log

# Run the YCSB workload
java -cp tapir-interface/target/tapir-interface-0.1.4.jar:core/target/core-0.1.4.jar:tapir/target/tapir-binding-0.1.4.jar:javacpp/target/javacpp.jar \
-Djava.library.path=libs/ com.yahoo.ycsb.Client -P workloads/workloada \
-t -db com.yahoo.ycsb.db.TapirClient \
-s -threads 7 -p operationcount=1000000 -p tapir.configpath=../store/tools/shard -p tapir.nshards=3 -p tapir.closestreplica=0 > run.log

killall -s9 server

rm -f ../127*
