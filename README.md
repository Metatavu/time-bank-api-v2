# Timebank API

#### API project for [Timebank](https://github.com/metatavu/time-bank-ui)

### Running the application locally

1. Clone the repository with `git clone --recurse-submodules git@github.com:Metatavu/time-bank-api-v2.git`
2. Ensure that tests are passing in JVM mode with `quarkus dev` in case of wrong submodule branch/HEAD
3. Build the application with `./gradlew build -Dquarkus.package.type=native -Dquarkus.native.native-image-xmx=12G`
   - If you don't have GraalVM and Native-Image installed Quarkus will automatically run build in Docker container
4. Run `docker build -f src/main/docker/Dockerfile.native -t metatavu/time-bank-api .`
5. Go to [https://github.com/Metatavu/dockerfile-keycloak](https://github.com/Metatavu/dockerfile-keycloak)
    and proceed as instructed.
6. Ask for Docker Compose file from someone who has worked with the project
   - *Contains environment variables and therefore will not be shared here*
7. Run `docker compose up`
8. **Enjoy!**

### Developing the application

1. Clone the repository with `git clone --resuce-submodules git@github.com:Metatavu/time-bank-api-v2.git`
2. Start a MySQL database with Docker or whatever
3. Ask for environment variables from someone who has worked with the project
4. Run `quarkus dev`
5. Look up [Quarkus guides](https://quarkus.io/guides/)
6. **Enjoy!** 