import random

user_dict = {};
item_dict = {}

f = open('Ratings.timed.txt', 'r');
f.readline(); # ignore the first line
f_out = open('rating.merge', 'w');

min_year = 2005
min_month = 9

max_time = 0;
for line in f :
    tokens = line.strip().split('\t');
    if len(tokens) < 4 :
        continue
    if tokens[3].startswith('1') or tokens[3].startswith('2001') :
        continue;
    user = int(tokens[0]);
    item = int(tokens[1]);
    if user not in user_dict :
        user_dict[user] = len(user_dict)
    if item not in item_dict :
        item_dict[item] = len(item_dict)

    score = float(tokens[2]);
    year = int(tokens[3].split('-')[0])
    month = int(tokens[3].split('-')[1])
    time = (year - min_year) * 12 + (month - min_month)
    max_time = max(max_time, time)
    f_out.write(str(user_dict[user])+","+str(item_dict[item])+","+str(time)+","+str(score)+"\n");

f_out.close();
f.close();

print len(user_dict);
print len(item_dict);
print max_time

f = open('movie-names.txt', 'r');
f_out = open('movie_id_title.txt', 'w');
for line in f :
    tokens = line.strip().split('\t');
    title = tokens[0];
    item = int(tokens[1]);
    if item in item_dict :
	    f_out.write(str(item_dict[item])+","+str(title).replace(',','')+"\n");

f_out.close();
f.close();


f = open('links.txt', 'r');
f_out = open('network.txt', 'w');

for line in f :
    tokens = line.strip().split('\t');
    src = int(tokens[0]);
    trg = int(tokens[1]);
    if src not in user_dict or trg not in user_dict :
        continue;
    f_out.write(str(user_dict[src])+","+str(user_dict[trg])+"\n");

f_out.close();
f.close();

f = open('rating.merge', 'r');
f_train = open('rating.train', 'w');
f_test = open('rating.test', 'w');

for line in f :
    tokens = line.strip().split(',');
    time = int(tokens[2]);
    if time > 40 :
        f_test.write(line);
    else :
        f_train.write(line);

f_train.close();
f_test.close();
f.close();  
