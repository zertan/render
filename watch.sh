#!/usr/bin/env bash
set -euxo pipefail
trap 'kill 0' EXIT

files=$(find src/main -type f -iname "*.clj[cs]")
parallel npx squint compile --output-dir out/js {} ::: ${files[@]}
npx vite dev &

while read j
do
  j=$(echo ${j} | awk '{print $1}')
  echo "file changed: ${j} recompiling"
  
  npx squint compile "${j}" --output-dir out/js
  
done <  <(inotifywait -m -e modify ${files[@]} )
