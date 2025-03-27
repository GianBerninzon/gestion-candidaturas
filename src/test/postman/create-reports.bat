@echo off
setlocal enabledelayedexpansion

echo Ejecutando pruebas de API con Newman...
mkdir reports\html 2>nul

echo Ejecutando tests Auth...
call newman run collections\Auth_API_Tests.postman_collection.json -e environments\Desarrollo.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\auth-report.html
echo Resultado Auth: %ERRORLEVEL%

echo Ejecutando tests Empresas...
call newman run collections\Empresas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\empresas-report.html
echo Resultado Empresas: %ERRORLEVEL%

echo Ejecutando tests Candidaturas...
call newman run collections\Candidaturas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\candidaturas-report.html
echo Resultado Candidaturas: %ERRORLEVEL%

echo Ejecutando tests Preguntas...
call newman run collections\Preguntas_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\preguntas-report.html
echo Resultado Preguntas: %ERRORLEVEL%

echo Ejecutando tests Reclutadores...
call newman run collections\Reclutadores_API_Tests.postman_collection.json -e environments\Current.postman_environment.json --export-environment environments\Current.postman_environment.json --reporters cli,htmlextra --reporter-htmlextra-export reports\html\reclutadores-report.html
echo Resultado Reclutadores: %ERRORLEVEL%

echo Todas las pruebas fueron ejecutadas!
echo Revisa los reportes HTML para ver los resultados detallados.
echo.
echo Presiona Ctrl+C para cerrar esta ventana.
cmd /k