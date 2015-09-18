# Sort-Big-Arrays
Multi-threaded binary file sort.
- input file (array) is sequence of 32-bit signed integer numbers in big-endian binary format
- input file size is always multiple of four

## How to build and run
1. build: ./gradlew
2. run java -jar build/lib/sort.jar <threads number> <input file> <output file> [chunk size in Kb]
