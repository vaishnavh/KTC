#cd $(dirname $(readlink -f $0))
cd ..

echo compiling java sources...
rm -rf class
mkdir class

javac -cp ./library/parallelcolt-0.10.0.jar:./library/dragontool.jar:./library/stanford-corenlp-3.5.2.jar -d class $(find ./src -name *.java)

echo make jar archive...
cd class
jar cf kptf.jar ./
rm ../kptf.jar
mv kptf.jar ../
cd ..
rm -rf class

echo done.
