#!/bin/sh
PATH=/bin:$PATH
SED="/bin/sed -b"
GIT="/usr/bin/git"

VER=`$GIT describe --dirty --tags`
echo "$VER"

$SED -i 's/\(android:versionName="\)[^"]*/\1'"$VER"'/' AndroidManifest.xml
