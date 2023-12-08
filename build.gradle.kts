import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.allopen") version "1.9.10"
    id("io.quarkus")
    id("org.openapi.generator") version "7.1.0"
    id("org.jetbrains.kotlin.kapt") version "1.9.20"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val jaxrsFunctionalTestBuilderVersion: String by project
val wiremockVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-kotlin")

    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-rest-client-reactive")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")
    implementation("io.quarkus:quarkus-oidc-client-reactive-filter")
    implementation("io.quarkus:quarkus-reactive-mysql-client")
    implementation("io.quarkus:quarkus-keycloak-admin-client-reactive")

//    implementation("io.quarkus:quarkus-resteasy-jackson")
//    implementation("io.quarkus:quarkus-hibernate-orm-panache")
//    implementation("io.quarkus:quarkus-rest-client")
//    implementation("io.quarkus:quarkus-rest-client-jackson")
//    implementation("io.quarkus:quarkus-keycloak-admin-client")

    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-liquibase")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-keycloak-authorization")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.smallrye.reactive:mutiny-kotlin")
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.quarkus:quarkus-test-hibernate-reactive-panache")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.wiremock:wiremock:$wiremockVersion")
    testImplementation("io.quarkus:quarkus-test-keycloak-server")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:mysql")
    testImplementation("fi.metatavu.jaxrs.testbuilder:jaxrs-functional-test-builder:$jaxrsFunctionalTestBuilderVersion") {
        exclude(group="com.fasterxml.jackson.core", module="jackson-core")
        exclude(group="com.fasterxml.jackson.core", module="jackson-databind")
        exclude(group="com.fasterxml.jackson.datatype", module="jackson-datatype-jsr310")
    }

    kapt("org.hibernate:hibernate-jpamodelgen:6.2.13.Final")
}

group = "fi.metatavu.timebank"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets["main"].java {
    srcDir("build/generated/api-spec/src/main/kotlin")
}
sourceSets["test"].java {
    srcDir("build/generated/api-client/src/main/kotlin")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
    dependsOn("generateApiSpec", "generateApiClient")
}

val generateApiSpec = tasks.register("generateApiSpec",GenerateTask::class){
    setProperty("generatorName", "kotlin-server")
    setProperty("inputSpec",  "$rootDir/time-bank-api-spec/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-spec")
    setProperty("apiPackage", "${project.group}.spec")
    setProperty("invokerPackage", "${project.group}.invoker")
    setProperty("modelPackage", "${project.group}.model")
    setProperty("templateDir", "$rootDir/openapi/api-spec")

    this.configOptions.put("library", "jaxrs-spec")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("useCoroutines", "true")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("additionalModelTypeAnnotations", "@io.quarkus.runtime.annotations.RegisterForReflection")
    this.configOptions.put("useJakartaEe", "true")
}

val generateApiClient = tasks.register("generateApiClient",GenerateTask::class){
    setProperty("generatorName", "kotlin")
    setProperty("library", "jvm-okhttp3")
    setProperty("inputSpec",  "$rootDir/time-bank-api-spec/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-client")
    setProperty("packageName", "${project.group}.test.client")
    this.configOptions.put("dateLibrary", "string")
    this.configOptions.put("collectionType", "array")
    this.configOptions.put("serializationLibrary", "jackson")
    this.configOptions.put("enumPropertyNaming", "UPPERCASE")
}

tasks.named("compileKotlin") {
    dependsOn(generateApiSpec)
}

tasks.named("compileTestKotlin") {
    dependsOn(generateApiClient)
}

//project.afterEvaluate {
//
//    project.tasks.named("kaptGenerateStubsKotlin") {
//        mustRunAfter(generateApiSpec)
//    }
//
//    project.tasks.named("kaptGenerateStubsTestKotlin") {
//        mustRunAfter(generateApiClient)
//    }
//
//    project.tasks.named("compileQuarkusGeneratedSourcesJava") {
//        mustRunAfter(project.tasks.named("compileJava"))
//    }
//}


