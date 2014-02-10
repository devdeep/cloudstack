#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

function usage() {
 echo ""
 echo "usage: ./package.sh [-p|--pack] [-h|--help] [-b|--build] [ARGS]"
 echo ""
 echo "The commonly used Arguments are:"
 echo "oss|OSS             To package with only redistributable libraries (default)"
 echo "noredist|NOREDIST   To package with non-redistributable libraries"
 echo "build   RPM version to build; default is 1"
 echo ""
 echo "Examples: ./package.sh -p|--pack oss|OSS -b|build 1"
 echo "          ./package.sh -p|--pack noredist|NOREDIST"
 echo "          ./package.sh (Default pack OSS) (Default build 1)"
 exit 1
}

function defaultPackaging() {
CWD=`pwd`
RPMDIR=$CWD/../../dist/rpmbuild
PACK_PROJECT=cloudstack
BUILD_VER=$1

VERSION=`(cd ../../; mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version) | grep '^[0-9]\.'`
if echo $VERSION | grep SNAPSHOT ; then
  REALVER=`echo $VERSION | cut -d '-' -f 1`
  DEFVER="-D_ver $REALVER"
  DEFPRE="-D_prerelease 1"
  DEFREL="-D_rel SNAPSHOT"
else
  REALVER=$VERSION
  DEFVER="-D_ver $REALVER"
  DEFREL="-D_rel $BUILD_VER"
fi

mkdir -p $RPMDIR/SPECS
mkdir -p $RPMDIR/BUILD
mkdir -p $RPMDIR/SRPMS
mkdir -p $RPMDIR/RPMS
mkdir -p $RPMDIR/SOURCES/$PACK_PROJECT-$VERSION

(cd ../../; tar -c --exclude .git --exclude dist  .  | tar -C $RPMDIR/SOURCES/$PACK_PROJECT-$VERSION -x )
(cd $RPMDIR/SOURCES/; tar -czf $PACK_PROJECT-$VERSION.tgz $PACK_PROJECT-$VERSION)

cp cloud.spec $RPMDIR/SPECS

(cd $RPMDIR; rpmbuild --define "_topdir $RPMDIR" "${DEFVER}" "${DEFREL}" ${DEFPRE+"${DEFPRE}"} -ba SPECS/cloud.spec)

exit
}

function packaging() {
	 
CWD=`pwd`
RPMDIR=$CWD/../../dist/rpmbuild
PACK_PROJECT=cloudstack
DEFOSSNOSS="-D_ossnoss $packageval"
BUILD_VER=$1

VERSION=`(cd ../../; mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version) | grep '^[0-9]\.'`
if echo $VERSION | grep SNAPSHOT ; then
  REALVER=`echo $VERSION | cut -d '-' -f 1`
  DEFVER="-D_ver $REALVER"
  DEFPRE="-D_prerelease 1"
  DEFREL="-D_rel SNAPSHOT"
else
  REALVER=`echo $VERSION`
  DEFVER="-D_ver $REALVER"
  DEFREL="-D_rel $BUILD_VER"
fi

mkdir -p $RPMDIR/SPECS
mkdir -p $RPMDIR/BUILD
mkdir -p $RPMDIR/RPMS
mkdir -p $RPMDIR/SRPMS
mkdir -p $RPMDIR/SOURCES/$PACK_PROJECT-$VERSION


(cd ../../; tar -c --exclude .git --exclude dist  .  | tar -C $RPMDIR/SOURCES/$PACK_PROJECT-$VERSION -x )
(cd $RPMDIR/SOURCES/; tar -czf $PACK_PROJECT-$VERSION.tgz $PACK_PROJECT-$VERSION)

cp cloud.spec $RPMDIR/SPECS

(cd $RPMDIR; rpmbuild --define "_topdir $RPMDIR" "${DEFVER}" "${DEFREL}" ${DEFPRE+"${DEFPRE}"} ${DEFOSSNOSS+"$DEFOSSNOSS"} -bb SPECS/cloud.spec)

exit
}


if [ $# -lt 1 ] ; then

	defaultPackaging 1

elif [ $# -gt 0 ] ; then

	SHORTOPTS="hp:"
	LONGOPTS="help,pack:"

	ARGS=$(getopt -s bash -u -a --options $SHORTOPTS  --longoptions $LONGOPTS --name $0 -- "$@" )
	eval set -- "$ARGS"

	while [ $# -gt 0 ] ; do
	case "$1" in
	-h | --help)
		usage
		exit 0
		;;
	-p | --pack)
		echo "Doing CloudStack Packaging ....."
		packageval=$2
              buildver=$4
              echo $buildver
		if [ "$packageval" == "oss" -o "$packageval" == "OSS" ] ; then
                     if [ $buildver ]; then
                          defaultPackaging $buildver
                     else
                          defaultPackaging 1
                     fi
			
		elif [ "$packageval" == "noredist" -o "$packageval" == "NOREDIST" ] ; then
                     if [ $buildver ]; then
                          packaging $buildver
                     else
                          packaging 1
                     fi
		else
			echo "Error: Incorrect value provided in package.sh script, Please see help ./package.sh --help|-h for more details."
			exit 1
		fi
		;;
	-)
		echo "Unrecognized option..."
		usage
		exit 1
		;;
	--)
		echo "Unrecognized option..."
		usage
		exit 1
		;;
	-*)
		echo "Unrecognized option..."
		usage
		exit 1
		;;
	*)
		shift
		break
		;;
	esac
	done

else
	echo "Incorrect choice.  Nothing to do." >&2
	echo "Please, execute ./package.sh --help for more help"
fi
