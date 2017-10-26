curl 'https://api.vse.fm/' \
    -o tomsk.json.dat \
    -H 'Origin: https://vse.fm' \
    -H 'Accept-Encoding: gzip, deflate, br' \
    -H 'Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,de;q=0.2' \
    -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36' \
    -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' \
    -H 'Accept: application/json, text/javascript, */*; q=0.01' \
    -H 'Referer: https://vse.fm/' \
    -H 'Connection: keep-alive' \
    --data 'configHash=no&citiesHash=no&stationsHash=no&logosHash=no&logosType=image_medium&genresHash=no&chartHash=no' \
    --compressed

{
gzip -dc tomsk.json.dat || cat tomsk.json.dat
} > tomsk.json

{
printf 'tomsk_objs = '
cat tomsk.json
printf ';'
} > tomsk.js

rm -f tomsk.json.dat


