All of the dependecies are included in the directory. The only external library used was Google's json-simple which is available from the following URL: https://code.google.com/archive/p/json-simple/downloads.

Building and using the command-line program is simple, After downloading it unzip the file using the method of your choice. Once unzipped use your command line to go into the src directory in the file. Type the following code to launch the program: $ java -cp ".:lib/json-simple-1.1.jar" src.FoodTruckFinder.
If necessary the files can be compiled using $ javac -cp "lib/json-simple-1.1.jar" src/FoodTruckFinder.java" &&  java -cp ".:lib/json-simple-1.1.jar" src.FoodTruckFinder.
