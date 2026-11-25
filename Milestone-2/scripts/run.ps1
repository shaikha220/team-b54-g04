$ErrorActionPreference='Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
& mvn clean javafx:run
if ($LASTEXITCODE -ne 0) { throw 'Run failed; verify JDK 21, Maven and Maven Central access.' }
