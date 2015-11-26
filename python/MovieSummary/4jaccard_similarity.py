frquent_words = [];
f = open('frequent_words_manual.txt', 'r');
for line in f:
    word = line.strip().split('\t')[0];
    frquent_words.append(word);
f.close();


def jaccard(vec1, vec2) :
    denom = 0
    nom = 0;
    for i in range(0, len(vec1)) :
        if vec1[i] == 1 or vec2[i] ==1:
            denom +=1;
        if vec1[i] == 1 and vec2[i] ==1:
            nom += 1;
    return (nom+0.0)/denom if denom > 0 else 0;

id_to_vect = {}
f = open('id_vector.txt', 'r')
for line in f:
    tokens = line.strip().split('\t');
    id = int(tokens[0]);
    vect = [int(v) for v in tokens[1:]];
    id_to_vect[id] = vect;
f.close()

f_out = open('movie_net.txt', 'w');
for src in id_to_vect :
    similar_ids = []
    vec1 = id_to_vect[src]
    for trg in id_to_vect :
        if src==trg :
            continue
        vec2 = id_to_vect[trg]
        similar_ids.append((jaccard(vec1, vec2), trg));
    similar_ids =sorted(similar_ids)
    similar_ids.reverse();
    for (score, trg) in similar_ids[:20]:
        if score > 0.2 :
            f_out.write(str(src)+','+str(trg)+','+str(score)+'\n');
f_out.close();

    





