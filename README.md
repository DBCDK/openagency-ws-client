# Howto 

The Project build is based on JenkinsFile. 

To Make a new version do .

## 1. Make branch of old version
```bash
NEWVERSION=`grep -A2 'artifactId.openagency-ws-client./artifactId' pom.xml  | grep version | sed -e 's/.*<version>\([0-9.]*\)-.*/\1/'`
svn copy . https://svn.dbc.dk/repos/openagency-ws-client/branches/$NEWVERSION
```

## 2. update Trunk version 

Change trunk version in your favorite editor 


# TOTO inform downstream projects 
  
 * hive
 * holdings-items
 * openorder-head
 * rawrepo
 * rawrepo-mq
 * rawrepo-oai
 * rawrapo-old-1.0

