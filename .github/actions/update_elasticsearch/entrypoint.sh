#!/bin/sh

cd "${GITHUB_WORKSPACE}"

maven_repo_url='https://repo.maven.apache.org/maven2/org/elasticsearch/elasticsearch/'
release_notes_url='https://www.elastic.co/guide/en/elasticsearch/reference/current/'

current_elasticsearch_version=$(sed -nr 's/.*<elasticsearch\.version>([0-9]+\.[0-9]+\.[0-9]+)<\/elasticsearch\.version>.*/\1/p' ./pom.xml)

major=$(echo $current_elasticsearch_version | awk -F. '{print $1}')
minor=$(echo $current_elasticsearch_version | awk -F. '{print $2}')
patch=$(echo $current_elasticsearch_version | awk -F. '{print $3}')

let next_major=$major+1
let next_minor=$minor+1
let next_patch=$patch+1

next_major_version=${next_major}.0.0
next_minor_version=${major}.${next_minor}.0
next_patch_version=${major}.${minor}.${next_patch}

for version_to_try in $next_patch_version $next_minor_version $next_major_version
do
    version_url_in_repo="${maven_repo_url}${version_to_try}/"

    echo "Checking ${version_url_in_repo} with HEAD request..."
    http_code=$(curl -I -o /dev/null -w "%{http_code}" ${version_url_in_repo})

    if [ $http_code -eq 200 ]
    then
        elasticsearch_version=$version_to_try
        break
    fi
done

if [ $elasticsearch_version ]
then
    echo "New elasticsearch version: ${elasticsearch_version}"

    release_notes_url="${release_notes_url}release-notes-${elasticsearch_version}.html"

    echo "Getting release notes from ${release_notes_url}"
    release_notes_http=$(curl ${release_notes_url})

    lucene_version=$(echo $release_notes_http | sed -nr "s/.*upgrade[a-z ]*lucene[a-z ]*([0-9]+\.[0-9]+\.[0-9]+).*/\1/pI")
    java_version=$(echo $release_notes_http | sed -nr "s/.*upgrade[a-z ]*JDK[a-z ]*([0-9]+).*/\1/pI")

    echo "New Lucene version is ${lucene_version}"
    echo "New Java version is ${java_version}"

    # Updrage Elasticsearch version
    sed -r -i "s/<elasticsearch\.version>[0-9]+\.[0-9]+\.[0-9]+<\/elasticsearch\.version>/<elasticsearch.version>${elasticsearch_version}<\/elasticsearch.version>/" ./pom.xml

    if [ $lucene_version ]
    then
        sed -r -i "s/<lucene\.version>[0-9]+\.[0-9]+\.[0-9]+<\/lucene\.version>/<lucene.version>${lucene_version}<\/lucene.version>/" ./pom.xml
    fi

    if [ $java_version ]
    then
        echo $java_version > ./es-java-version
    fi
else
    echo "No new Elasticseach version was found"
fi

echo "elasticsearch-version=${elasticsearch_version}" >> ${GITHUB_OUTPUT}
echo "lucene-version=${lucene_version}" >> ${GITHUB_OUTPUT}
echo "java-version=${java_version}" >> ${GITHUB_OUTPUT}
