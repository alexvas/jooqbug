This is a toy project for a jOOQ bug. How to reproduce:

1. Edit `dbUrl`, `dbUsername` and `dbPassword` in build.gradle.kts to point to your DB
1. run `./gradlew update generateJooq`
1. run `./gradlew compileJava` and see an error. A class for `Mode` enum was not generated. 
