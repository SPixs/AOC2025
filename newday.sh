#!/bin/bash

# Script pour créer les fichiers d'un nouveau jour AOC 2025
# Usage: ./newday.sh <day_number>
# Exemple: ./newday.sh 1

if [ -z "$1" ]; then
    echo "Usage: $0 <day_number>"
    echo "Example: $0 1"
    exit 1
fi

DAY=$1
DAY_PADDED=$(printf "%02d" $DAY)

SRC_FILE="src/aoc2025/days/Day${DAY_PADDED}.java"
INPUT_FILE="input/input_day${DAY_PADDED}.txt"

# Vérifier si le fichier existe déjà
if [ -f "$SRC_FILE" ]; then
    echo "Warning: $SRC_FILE already exists!"
    read -p "Overwrite? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Créer le fichier source depuis le template
sed -e "s/DayXX/Day${DAY_PADDED}/g" \
    -e "s/Day XX/Day ${DAY}/g" \
    -e "s/day\/XX/day\/${DAY}/g" \
    -e "s/readDay(XX)/readDay(${DAY})/g" \
    -e "s/input_dayXX/input_day${DAY_PADDED}/g" \
    templates/DayTemplate.java > "$SRC_FILE"

echo "Created $SRC_FILE"

# Créer le fichier input vide s'il n'existe pas
if [ ! -f "$INPUT_FILE" ]; then
    touch "$INPUT_FILE"
    echo "Created $INPUT_FILE (empty - paste your input here)"
else
    echo "$INPUT_FILE already exists"
fi

echo ""
echo "Next steps:"
echo "  1. Copy your puzzle input to $INPUT_FILE"
echo "  2. Open $SRC_FILE and implement solvePart1/solvePart2"
echo "  3. Run: cd src && javac aoc2025/days/Day${DAY_PADDED}.java && java aoc2025.days.Day${DAY_PADDED}"
echo ""
echo "Or with the run script:"
echo "  ./run.sh ${DAY}"
