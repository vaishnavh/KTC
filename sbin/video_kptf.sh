
chmod u+x ./* 
echo "[DEMO] Start KPTF SGD..."
output=$1+"_kptf_sgd"
mkdir $output
./run_kptf_sgd.sh ../data/video/corrupt_10.csv $output $2 4 $3 $4 $5 240 320 3 141 RBF:$6 RBF:$6 None:1 RBF:$7 ../data/video/test.csv
