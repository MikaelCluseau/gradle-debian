#!/bin/sh 

set -e

# called by uscan with '--upstream-version' <version> <file>
echo "version $2"
package=`dpkg-parsechangelog | sed -n 's/^Source: //p'`
version=$2
upstream_version="$(echo $version | sed 's/~m/-milestone-/')"
tarball=$3
TAR=${package}_${version}.orig.tar.xz
DIR=${package}-${upstream_version}

unzip $tarball

XZ_OPT=--best tar --numeric --group 0 --owner 0 -c -v -J -f $TAR \
    --anchored -X debian/orig-tar.excludes $DIR

rm -rf $tarball $DIR
