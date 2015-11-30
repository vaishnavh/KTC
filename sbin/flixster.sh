#cd $(dirname $(readlink -f $0))
chmod u+x ./* 

echo "[DEMO] Start Basic SGD..."
rm -rf output_basic_sgd
mkdir output_basic_sgd
./run_basic_sgd.sh ../data/flixster/rating_centered_time.train output_basic_sgd 10 3 10 0.02 1 147612 48794 51 ../data/flixster/rating_centered_time.test
echo "[DEMO] Finish Basic SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_basic_sgd\""

echo "[DEMO] Start PTF SGD..."
rm -rf output_ptf_sgd
mkdir output_ptf_sgd
./run_kptf_sgd.sh ../data/flixster/rating_centered_time.train output_kptf_sgd 10 3 10 0.02 1 147612 48794 51 None:1 None:1 None:1 ../data/flixster/rating_centered_time.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_ptf_sgd\""

echo "[DEMO] Start KPTF SGD..."
rm -rf output_kptf_sgd
mkdir output_kptf_sgd
./run_kptf_sgd.sh ../data/flixster/rating_centered_time.train output_kptf_sgd 10 3 10 0.02 1 147612 48794 51 RL:../data/flixster/network.txt:0.1 None:1 RBF:1 ../data/flixster/rating_centered_time.test
echo "[DEMO] Finish PTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""

echo "[DEMO] Start Grid Search KPTF SGD..."
rm -rf epinions_output_kptf_sgd
mkdir epinions_output_kptf_sgd
./run_grid_search_kptf.sh ../data/flixster/rating_centered_time.train output_kptf_sgd [10] 3 10 [0.001,0.01,0.1,1] [1] 147612 48794 51 RL:../data/flixster/network.txt:0.1 None:1 RBF:1 ../data/flixster/rating_centered_time.test
echo "[DEMO] Finish Grid Search KPTF SGD..."
echo "[DEMO] Outputs are saved in a local directory named \"output_kptf_sgd\""
