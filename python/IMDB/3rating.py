title_id = {}
f = open('movie_id_title-flixster.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    title = tokens[1];
    title_id[title] = id;
f.close();

f = open('mpaa-ratings-reasons.list', 'r');
id_rating = {}
title = '';
for line in f:
    line = line.strip();
    if line.startswith('MV: ') :
        title = line.replace('MV: ', '').split(' (')[0];
    elif line.startswith('RE: Rated ') :
        rating = line.replace('RE: Rated ', '').split(' ')[0];
        if title in title_id :
            id_rating[title_id[title]] = rating
f.close();

f_out = open('id_rating.txt', 'w');
for id in id_rating :
    f_out.write(id + ',' + str(id_rating[id]) + '\n');
f_out.close();
