title_id = {}
f = open('movie_id_title-flixster.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    title = tokens[1];
    title_id[title] = id;
f.close();

f = open('directors.list', 'r');
id_director = {}
director = 0;
for line in f:
    line = line.strip();
    while line.find('\t\t') >= 0 :
        line = line.replace('\t\t', '\t');
    tokens = line.strip().split('\t');
    if len(tokens) == 2:
        director += 1;
        title = tokens[1].split(' (')[0]
    else :
        title = tokens[0].split(' (')[0];
    if title in title_id :
        id = title_id[title]
        id_director[id] = director;
f.close();

print director

f_out = open('id_director.txt', 'w');
for id in id_director :
    f_out.write(id + ',' + str(id_director[id]) + '\n');
f_out.close();
