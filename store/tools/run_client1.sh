/home/xiaoniu.sxn/tapir/store/benchmark/benchClient -c /home/xiaoniu.sxn/tapir/store/tools/shard -N 1 -f /home/xiaoniu.sxn/tapir/store/tools/keys -d 10 -l 2 -w 0 -k 100000 -m txn-l -e 0 -s 0 -z -1;
killall -s9 server
rm -f 127*
