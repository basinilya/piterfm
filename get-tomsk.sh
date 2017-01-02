wget -O tomsk.json.dat "https://api.vse.fm/?citiesHash=no&stationsHash=no&rdsHash=no&genresHash=no"

{
printf 'tomsk_objs = '
gzip -dc tomsk.json.dat || cat tomsk.json.dat
printf ';'
} > tomsk.js

rm -f tomsk.json.dat


