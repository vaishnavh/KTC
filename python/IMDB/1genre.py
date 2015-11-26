title_id = {}
f = open('movie_id_title-flixster.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    title = tokens[1];
    title_id[title] = id;
f.close();

f = open('genres.list', 'r');
id_genre = {}
for line in f:
    line = line.strip();
    while line.find('\t\t') >= 0 :
        line = line.replace('\t\t', '\t');
    tokens = line.strip().split('\t');
    title = tokens[0].split(' (')[0]
    genre = tokens[1];
    if title in title_id :
        id = title_id[title]
        if id not in id_genre :
            id_genre[id] = []
        id_genre[id].append(genre);
f.close();

f_out = open('id_genre.txt', 'w');
for id in id_genre :
    genre = list(set(id_genre[id]));
    genre = sorted(genre);
    f_out.write(id + ',' + '|'.join(genre) + '\n');
f_out.close();
