$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($env:APP_USERNAME)) {
    Write-Error "APP_USERNAME is required."
}

if ([string]::IsNullOrWhiteSpace($env:APP_PASSWORD)) {
    Write-Error "APP_PASSWORD is required."
}

.\mvnw.cmd test -Denv=local -Dservice=platform "-Dapp.username=$env:APP_USERNAME" "-Dapp.password=$env:APP_PASSWORD" @args
