import random

f = open('rating.merge', 'r');
max_time = 0;
for line in f :
    tokens = line.strip().split(',');
    time = int(tokens[2]);
    max_time = max(max_time, time);
f.close()
print max_time


f = open('rating.merge', 'r');
f_train = open('rating.train', 'w');
f_test = open('rating.test', 'w');

for line in f :
    tokens = line.strip().split(',');
    time = int(tokens[2]);
    if time > 100 :
        f_test.write(line);
    else :
        f_train.write(line);

f_train.close();
f_test.close();
f.close();  
