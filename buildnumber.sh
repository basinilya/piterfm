#!/bin/sh
PATH=/bin:$PATH
SED="sed -b"
GIT="git"

VER=`$GIT describe --dirty --always --tags`

$SED -i \
    -e 's/\(android:versionName="\)[^"]*/\1'"$VER"'/' \
    -e 's/\(application.* android:label="\)[^"]*/\1'"PITER FM ($VER)"'/' \
    AndroidManifest.xml
