title_to_id = {}
f = open('movie_id_title-flixster.txt', 'r')
for line in f:
    tokens = line.strip().split(',')
    id = tokens[0]
    title = tokens[1]
    title_to_id[title] = id;
f.close()

id_to_vect = {}
f = open('title_vector.txt', 'r')
f_out = open('id_vector.txt', 'w')
for line in f:
    tokens = line.strip().split('\t');
    title = tokens[0];
    if title in title_to_id :
        f_out.write(title_to_id[title]+"\t");
        for val in tokens[1:] :
            f_out.write(val+"\t");
        f_out.write('\n');
f.close()
f_out.close()

    





