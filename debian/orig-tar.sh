#!/bin/sh 

set -e

# called by uscan with '--upstream-version' <version> <file>
echo "version $2"
package=`dpkg-parsechangelog | sed -n 's/^Source: //p'`
version=$2
tarball=$3
TAR=${package}_${version}.orig.tar.gz
DIR=${package}-${version}.orig

unzip $tarball -d $DIR

GZIP=--best tar --numeric --group 0 --owner 0 -c -v -z -f $TAR \
    --anchored -X debian/orig-tar.excludes $DIR

rm -rf $tarball $DIR
