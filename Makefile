
all:
	javac -classpath src src/TileGame.java

run:
	java -classpath src TileGame

clean:
	find . -name "*.class" -delete
