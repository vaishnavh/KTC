words_to_index = {};
f = open('frequent_words_manual.txt', 'r');
for line in f:
    word = line.strip().split('\t')[0];
    words_to_index[word] = len(words_to_index);
f.close();

id_to_title = {}
f = open('movie.metadata.tsv', 'r')
for line in f:
    tokens = line.strip().split('\t')
    id = tokens[0]
    title = tokens[2]
    id_to_title[id] = title;
f.close()

vect_len = len(words_to_index);
id_to_vector = {}
f = open('plot_summaries.txt', 'r')
f_out = open('title_vector.txt', 'w')
for line in f:
    vector = [0] * vect_len;
    id = line.split('\t')[0]
    if id not in id_to_title :
        continue;
    line = line.strip().replace('.', '').replace(',', '').replace("'", " ").replace('"',"").replace('#','').replace('?','').replace('!','').replace('&nbsp;', '');
    tokens = line.lower().split();
    for token in tokens:
        if token in words_to_index :
            vector[words_to_index[token]] = 1;
    f_out.write(id_to_title[id]+"\t");
    for val in vector :
        f_out.write(str(val)+"\t");
    f_out.write('\n');
f.close()
f_out.close()

    





