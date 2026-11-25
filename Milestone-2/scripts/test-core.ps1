$ErrorActionPreference='Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
New-Item -ItemType Directory -Force target/core-checks | Out-Null
$files = @(Get-ChildItem src/main/java/pearlmart/core/*.java,src/test/java/pearlmart/core/*.java | ForEach-Object FullName)
& javac -encoding UTF-8 -d target/core-checks $files
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
& java -cp target/core-checks pearlmart.core.WorkflowChecks | Tee-Object -FilePath evidence/windows-core-test-results.txt
if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
