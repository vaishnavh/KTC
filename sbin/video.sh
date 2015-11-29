#cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
output=$1+"_basic_sgd"
mkdir $output
./run_basic_sgd.sh ../data/video/corrupt_short.csv $output $2 4 $3 $4 $5 240 320 3 141 ../data/video/true_short.csv
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory"

echo "[DEMO] Start PTF SGD..."
output=$1+"_ptf_sgd"
mkdir $output
./run_kptf_sgd.sh ../data/video/corrupt_short.csv $output $2 4 $3 $4 $5 240 320 3 141 None:1 None:1 None:1 None:1 ../data/video/true_short.csv
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory"
 
echo "[DEMO] Start KPTF SGD..."
output=$1+"_kptf_sgd"
mkdir $output
./run_kptf_sgd.sh ../data/video/corrupt_short.csv $output $2 4 $3 $4 $5 240 320 3 141 RBF:$6 RBF:$6 None:1 RBF:$7 ../data/video/true_short.csv
echo "[DEMO] Finish PTF SGD...e"
echo "[DEMO] Outputs are saved in a local directory"
