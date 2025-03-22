@echo off
echo Ejecutando pruebas de API con Newman...

mkdir reports\html 2>nul

echo Ejecutando tests Auth...
newman run collections\Auth_API_Tests.postman_collection.json -e environments\Desarrollo.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\auth-report.html

echo Ejecutando tests Empresas...
newman run collections\Empresas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\empresas-report.html

echo Ejecutando tests Candidaturas...
newman run collections\Candidaturas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\candidaturas-report.html

echo Ejecutando tests Preguntas...
newman run collections\Preguntas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\preguntas-report.html

echo Ejecutando tests Reclutadores...
newman run collections\Reclutadores_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\reclutadores-report.html

echo Todas las pruebas completadas!
pause