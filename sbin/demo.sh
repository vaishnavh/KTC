cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
rm -rf output_basic_sgd
mkdir output_basic_sgd
./run_basic_sgd.sh ../data/example.train output_basic_sgd 10 4 10 0.02 1 20000 20000 100 24 ../data/example.test ../data/example.query
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_basic_sgd\""
