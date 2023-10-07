#!/usr/bin/env bash

files="$@"

echo ${files[@]}

while read j
do
  j=$(echo ${j} | awk '{print $1}')
  filename=$(basename -- "${j}")
  echo "file changed: " ${filename} " recompiling"
  
  npx squint compile ${filename} && mv "${filename%.*}.mjs" js/
  
done <  <(inotifywait -m -e modify ${files[@]} )
