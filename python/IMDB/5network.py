def jaccard(genre1, genre2) :
    tokens1 = genre1.split('|')
    tokens2 = genre2.split('|')
    denom = len(set(tokens1 + tokens2))
    nom = (len(tokens1)+len(tokens2)-denom);
    return nom/denom

id_set = set()
id_genre = {}
f = open('id_genre.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    genre = tokens[1];
    id_set.add(id);
    id_genre[id] = genre;
f.close();

id_director = {}
f = open('id_director.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    director = tokens[1];
    id_set.add(id);
    id_director[id] = director;
f.close();

id_rating = {}
f = open('id_rating.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    rating = tokens[1];
    id_set.add(id);
    id_rating[id] = rating;
f.close();

id_vote = {}
f = open('id_vote.txt');
for line in f :
    tokens = line.strip().split(',');
    id = tokens[0];
    vote = int(tokens[1]);
    id_set.add(id);
    id_vote[id] = vote;
f.close();

f_out = open('movie_net.txt', 'w');
for id1 in id_set :
    candidates = [];
    for id2 in id_set :
        if id1 != id2 :
            score = 0;
            if id1 in id_genre and id2 in id_genre :
                score += jaccard(id_genre[id1], id_genre[id2])
            if id1 in id_director and id2 in id_director :
                if id_director[id1] == id_director[id2] :
                    score +=1
            if id1 in id_rating and id2 in id_rating :
                if id_rating[id1] == id_rating[id2] :
                    score +=1
            if score > 1 :
                vote = 0
                if id2 in id_vote :
                    vote = id_vote[id2]
                candidates.append((score, vote, id2));
    candidates = sorted(candidates)
    candidates.reverse()
    for (score, vote, id2) in candidates[:20] :
        f_out.write(id1+','+id2+','+str(score)+','+str(vote)+'\n')
f_out.close();
