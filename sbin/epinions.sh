#cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
rm -rf epinions_output_basic_sgd
mkdir epinions_output_basic_sgd
./run_basic_sgd.sh ../data/epinions/ratings.train epinions_output_basic_sgd 10 3 10 0.001 1 22164 296277 143 ../data/epinions/ratings.test
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_basic_sgd\""

echo "[DEMO] Start PTF SGD..."
rm -rf epinions_output_ptf_sgd
mkdir epinions_output_ptf_sgd
./run_kptf_sgd.sh ../data/epinions/ratings.train epinions_output_ptf_sgd 10 3 10 0.001 1 22164 296277 143 None:1 None:1 None:1 ../data/epinions/ratings.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_ptf_sgd\""

echo "[DEMO] Start KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_kptf_sgd.sh ../data/epinions/ratings.train epinions_output_kptf_sgd 10 3 10 0.001 1 22164 296277 143 RL:../data/epinions/preprocessed_network.txt:0.1 None:1 RBF:1 ../data/epinions/ratings.test
echo "[DEMO] Finish KPTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""

echo "[DEMO] Start Grid Search KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_grid_search_kptf.sh ../data/epinions/ratings.train epinions_output_kptf_sgd [10] 3 10 [0.001,0.01,0.1,1] [1,0.1,0.01] 22164 296277 143 RL:../data/epinions/preprocessed_network.txt:0.1 None:1 RBF:1 ../data/epinions/ratings.test
echo "[DEMO] Finish Grid Search KPTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""
 