copy ..\app\* ..\target\*
"c:\Program Files\Java\jdk-19\bin\jpackage" --verbose --name Infomancer --app-version 1.0.0.2 --input ../target/ --main-jar infomancerforge-0.0.1-SNAPSHOT.jar --type exe --jlink-options --bind-services --vendor "Inkus Games" --icon if_icon.ico --win-shortcut --win-menu --win-menu-group "Inkus"

rmdir /s .\InfomancerForge
mkdir .\InfomancerForge
mkdir .\InfomancerForge\dependency
copy ..\app\* .\InfomancerForge\*
copy ..\target\*.jar .\InfomancerForge\*
copy ..\target\dependency\* .\InfomancerForge\dependency\*
