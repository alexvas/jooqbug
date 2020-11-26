import org.liquibase.gradle.LiquibaseTask

buildscript {
    val jooqVersion = "3.14.3"

    configurations["classpath"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jooq") {
            useVersion(jooqVersion)
        }
    }
}

plugins {
    java
    id("nu.studer.jooq") version "5.2"
    id("org.liquibase.gradle") version "2.0.4"
}

val jooqVersion = "3.14.3"
val postgresqlVersion = "42.2.18"

dependencies {
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")

    jooqGenerator("org.jooq:jooq-codegen:$jooqVersion")
    jooqGenerator("org.postgresql:postgresql:$postgresqlVersion")

    liquibaseRuntime("org.liquibase:liquibase-core:3.10.3")
    liquibaseRuntime("org.postgresql:postgresql:$postgresqlVersion")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")

}

repositories {
    jcenter()
}

val dbUrl = "jdbc:postgresql://127.0.0.1:5432/authz2"
val dbUsername = "authz2"
val dbPassword = ""

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUsername
                    password = dbPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "Pg.*|databasechangelog.*"
                    }
                    generate.apply {
                        isGeneratedAnnotation = true
                        isRelations = false
                        isDeprecated = false
                        isDeprecationOnUnknownTypes = false
                        isIndexes = false
                        isRoutines = false
                        isSequences = false
                        isKeys = false
                        isUdts = false
                        isGlobalCatalogReferences = false
                        isGlobalSchemaReferences = false
                        isGlobalTableReferences = true
                        isRecords = true
                        isJavaTimeTypes = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        directory = "src/main/generated"
                        packageName = "testpackage.jooq"
                    }
                }
            }
        }
    }
}

val update = tasks.named("update", LiquibaseTask::class) {
    inputs.dir("src/main/liquibase-changes")
}

liquibase {
    activities.register("authz") {
        this.arguments = mapOf(
                "logLevel" to "info",
                "changeLogFile" to "src/main/liquibase-changes/changelog.xml",
                "url" to dbUrl,
                "username" to dbUsername,
                "password" to dbPassword,
                "defaultSchemaName" to "public"
        )
    }
}
