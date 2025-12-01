#!/bin/bash

# Script pour compiler et exécuter un jour AOC 2025
# Usage: ./run.sh <day_number>
# Exemple: ./run.sh 1

if [ -z "$1" ]; then
    echo "Usage: $0 <day_number>"
    echo "Example: $0 1"
    exit 1
fi

DAY=$1
DAY_PADDED=$(printf "%02d" $DAY)

CLASS="aoc2025.days.Day${DAY_PADDED}"
SRC_FILE="src/aoc2025/days/Day${DAY_PADDED}.java"

if [ ! -f "$SRC_FILE" ]; then
    echo "Error: $SRC_FILE does not exist"
    echo "Run: ./newday.sh $DAY"
    exit 1
fi

echo "=== Compiling Day $DAY ==="
cd src

# Compiler les utilitaires si nécessaire
if [ ! -f "aoc2025/utils/Point2D.class" ] || \
   [ "aoc2025/utils/Point2D.java" -nt "aoc2025/utils/Point2D.class" ]; then
    echo "Compiling utilities..."
    javac aoc2025/utils/*.java
fi

# Compiler le jour
javac aoc2025/days/Day${DAY_PADDED}.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "=== Running Day $DAY ==="
cd ..
java -cp src $CLASS
