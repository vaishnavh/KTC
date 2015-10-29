#cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
rm -rf epinions_output_basic_sgd
mkdir epinions_output_basic_sgd
./run_basic_sgd.sh ../data/epinions/ratings.train epinions_output_basic_sgd 30 3 10 0.02 1 22164 296277 143 ../data/epinions/ratings.test
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_basic_sgd\""

echo "[DEMO] Start PTF SGD..."
rm -rf epinions_output_ptf_sgd
mkdir epinions_output_ptf_sgd
./run_kptf_sgd.sh ../data/epinions/ratings.train epinions_output_ptf_sgd 30 3 10 0.02 1 22164 296277 143 None:1 None:1 None:1 ../data/epinions/ratings.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_ptf_sgd\""

echo "[DEMO] Start KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_kptf_sgd.sh ../data/epinions/ratings.train epinions_output_kptf_sgd 30 3 10 0.02 1 22164 296277 143 RL:../data/epinions/preprocessed_network.txt:0.1 None:1 RBF:1 ../data/epinions/ratings.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""
