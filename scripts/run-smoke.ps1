$ErrorActionPreference = "Stop"
.\mvnw.cmd test -Denv=local -Dservice=platform "-Dtest=ConfigSanityTest,HelloWorldTest" [ApplyLeavePage.java](src/test/java/com/kavish/services/hcm/pages/ApplyLeavePage.java) 
