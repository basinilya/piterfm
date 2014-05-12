#!/bin/sh
PATH=/bin:$PATH
SED="sed -b"
GIT="git"

VER=`$GIT describe --dirty --always --tags`

$SED -i 's/\(android:versionName="\)[^"]*/\1'"$VER"'/' AndroidManifest.xml
