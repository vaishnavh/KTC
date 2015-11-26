title_id = {}
f = open('movie_id_title-flixster.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    title = tokens[1];
    title_id[title] = id;
f.close();

f = open('ratings.list', 'r');
id_vote = {}
title = '';
for line in f:
    line = line.strip();
    while line.find('   ') > 0 :
        line = line.replace('   ', '  ');
    tokens = line.split('  ');
    #print tokens
    title = tokens[3].split(' (')[0];
    vote = int(tokens[1])
    if title in title_id :
        id_vote[title_id[title]] = vote
f.close();

f_out = open('id_vote.txt', 'w');
for id in id_vote :
    f_out.write(id + ',' + str(id_vote[id]) + '\n');
f_out.close();
