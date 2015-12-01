#cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
rm -rf epinions_output_basic_sgd
mkdir epinions_output_basic_sgd
./run_basic_sgd.sh ../data/epinions/rating_centered_time.train epinions_output_basic_sgd 10 3 10 0.001 1 22164 296277 143 ../data/epinions/rating_centered_time.test
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_basic_sgd\""

echo "[DEMO] Start PTF SGD..."
rm -rf epinions_output_ptf_sgd
mkdir epinions_output_ptf_sgd
./run_kptf_sgd.sh ../data/epinions/rating_centered_time.train epinions_output_ptf_sgd 10 3 10 0.001 1 22164 296277 143 None:1 None:1 None:1 ../data/epinions/rating_centered_time.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"epinions_output_ptf_sgd\""

echo "[DEMO] Start KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_kptf_sgd.sh ../data/epinions/rating_centered_time.train epinions_output_kptf_sgd 10 3 10 0.001 1 22164 296277 143 RL:../data/epinions/preprocessed_network.txt:0.1 None:1 RBF:1 ../data/epinions/rating_centered_time.test
echo "[DEMO] Finish KPTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""

echo "[DEMO] Start Grid Search KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_grid_search_kptf.sh ../data/epinions/rating_centered_time.train epinions_output_kptf_sgd [10] 3 10 [0.01,0.015,0.02,0.05,0.1,0.5,1] [1,1.05,1.1,1.2,1.5,2,5,10,20,100] 22164 296277 143 RL:../data/epinions/preprocessed_network.txt:0.1 None:1 RBF:1 ../data/epinions/rating_centered_time.test
echo "[DEMO] Finish Grid Search KPTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""