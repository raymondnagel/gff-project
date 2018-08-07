mkdir dist\extern
xcopy extern dist\extern /E /Y
xcopy extras\test.bat dist
xcopy extras\test-info_manual.txt dist
xcopy extras\GoodFight.exe dist
del dist\README.txt
rename dist GFF
pause