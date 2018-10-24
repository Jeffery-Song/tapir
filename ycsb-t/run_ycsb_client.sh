# example:
#       ./run_ycsb_client.sh load 0 a -s -threads 7 > loadlog
#       ./run_ycsb_client.sh run 0 a -s -threads 7 -p operationcount=50000 > runlog
# Load the records in Tapir
if [ "$1" == "load" ]; then
    shift
    SERVER=$1
    shift
    WL=$1
    shift
    java -cp tapir-interface/target/tapir-interface-0.1.4.jar:core/target/core-0.1.4.jar:tapir/target/tapir-binding-0.1.4.jar:javacpp/target/javacpp.jar \
        -Djava.library.path=libs/ com.yahoo.ycsb.Client -P workloads/workload$WL \
        -load -db com.yahoo.ycsb.db.TapirClient \
        $* -p tapir.configpath=../store/tools/shard -p tapir.nshards=3 -p tapir.closestreplica=$SERVER
elif [ "$1" == "run" ]; then
# Run the YCSB workload
    shift
    SERVER=$1
    shift
    WL=$1
    shift
    java -cp tapir-interface/target/tapir-interface-0.1.4.jar:core/target/core-0.1.4.jar:tapir/target/tapir-binding-0.1.4.jar:javacpp/target/javacpp.jar \
        -Djava.library.path=libs/ com.yahoo.ycsb.Client -P workloads/workload$WL \
        -t -db com.yahoo.ycsb.db.TapirClient \
        $* -p tapir.configpath=../store/tools/shard -p tapir.nshards=3 -p tapir.closestreplica=$SERVER
fi
