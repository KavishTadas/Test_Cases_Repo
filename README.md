# Test Creation

## Prerequisites

- Java 21

## First Run

Use the Maven wrapper so everyone runs with the same Maven setup:

```sh
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Local Browser Run

Set the application credentials, then run the local helper. The helper uses the default `local` environment and `platform` service.

macOS/Linux:

```sh
export APP_USERNAME="your-username"
export APP_PASSWORD="your-password"
./scripts/run-local.sh
```

Windows PowerShell:

```powershell
$env:APP_USERNAME = "your-username"
$env:APP_PASSWORD = "your-password"
.\scripts\run-local.ps1
```

Extra Maven arguments are forwarded to the wrapper:

```sh
./scripts/run-local.sh -Dtest=LoginTest
```

```powershell
.\scripts\run-local.ps1 -Dtest=LoginTest
```

## Docker Selenium Run

Start the optional Selenium Chrome container:

```sh
docker compose -f docker-compose.selenium.yml up -d
```

Run tests against the Selenium Grid endpoint:

```sh
APP_USERNAME="your-username" APP_PASSWORD="your-password" ./scripts/run-local.sh -Dgrid.url=http://localhost:4444
```

Windows PowerShell:

```powershell
$env:APP_USERNAME = "your-username"
$env:APP_PASSWORD = "your-password"
.\scripts\run-local.ps1 -Dgrid.url=http://localhost:4444
```

Stop the container when finished:

```sh
docker compose -f docker-compose.selenium.yml down
```

## Allure Reports

Generate the Allure report after a test run:

```sh
./mvnw allure:report
```

Open the report with the local Allure server:

```sh
./mvnw allure:serve
```

On Windows, use `.\mvnw.cmd` for the same goals.

## Profiles

Default `local` profile runs pass `-Denv=local` and exclude HCM tests.

Use the `include-hcm` Maven profile to include HCM tests:

```sh
./mvnw test -Pinclude-hcm
```

On Windows:

```powershell
.\mvnw.cmd test -Pinclude-hcm
```
