f = open('rating.train', 'r')
sums = 0;
count = 0;
for line in f:
   tokens = line.strip().split(',');
   value = float(tokens[3]);
   sums += value;
   count += 1;

average = sums/count;
print average;

f.close();

f = open('rating.train', 'r')
f_out = open('rating_centered.train', 'w');

for line in f:
    tokens = line.strip().split(',');
    value = float(tokens[3]) - average;
    f_out.write(tokens[0] +","+ tokens[1] +","+ tokens[2] + ","+ str(value)+"\n");

f.close()
f_out.close();

f = open('rating.test', 'r')
f_out = open('rating_centered.test', 'w');

for line in f:
    tokens = line.strip().split(',');
    value = float(tokens[3]) - average;
    f_out.write(tokens[0] +","+ tokens[1] +","+ tokens[2] + ","+ str(value)+"\n");

f.close()
f_out.close();    
