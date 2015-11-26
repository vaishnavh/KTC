word_to_count = {}
f = open('plot_summaries.txt')
for line in f:
    line = line.strip().replace('.', '').replace(',', '').replace("'", " ").replace('"',"").replace('#','').replace('?','').replace('!','').replace('&nbsp;', '');
    tokens = line.lower().split();
    inside_dict = set(tokens);
    for token in inside_dict:
        if token not in word_to_count :
            word_to_count[token] = 1;
        else :
            word_to_count[token] += 1;
f.close()

frequent_list = sorted([(word_to_count[k], k) for k in word_to_count.keys()]);
frequent_list.reverse();
f_out = open('frequent_words.txt', 'w');
for (num, word) in frequent_list :
    f_out.write(word+'\t'+str(num)+'\n');
f_out.close()