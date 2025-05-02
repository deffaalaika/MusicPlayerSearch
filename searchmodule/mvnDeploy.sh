mvn deploy:deploy-file \
  -Durl=https://maven.pkg.github.com/deffaalaika/MusicPlayerSearch \
  -DrepositoryId=github \
  -Dfile=build/outputs/aar/searchmodule-release.aar \
  -DgroupId=com.deffa.searchmodule \
  -DartifactId=searchmodule \
  -Dversion=1.0.8 \
  -Dpackaging=aar