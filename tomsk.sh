#!/bin/bash
{
cat <<'EOF'
package ru.piter.fm.radio;

public class Tomsk {
    public static void init() {
EOF


<tomsk.html sed -e 's,^[ 	]*,,' -e '/<div class="mixer_name"/,/<\/td>/!d' \
    -e 's,\(<[^>]*\)station_id="\([^"]*\)",<div class="station_id">\2</div>\1,g' \
    -e 's,\(<[^>]*\)title="\([^"]*\)",<div class="title">\2</div>\1,g' \
    -e 's,\(.\)\(<div\),\1\n\2,g' | sed \
    -e '/<div class="title"/d' \
    -e 's,.*mixer_name[^>]*>\([^<]*\).*,new TomskStation( "\1",' \
    -e 's,</td>,);,' \
    -e 's,[^>]*>\([^<]*\).*,\,"\1",' \

cat <<'EOF'
    }
}
EOF
} >src/ru/piter/fm/radio/Tomsk.java

#<div title="Lost Frequencies &amp; Janieck Devy - Reality" class="mixer_rds" station_id="41">Lost Frequencies &amp; Janieck Devy - Reality</div>
