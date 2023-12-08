#/bin/sh

REALM=timebank
CONTAINER_ID=$(docker ps | grep keycloak | awk '{print $1}')

docker exec -e JDBC_PARAMS='?useSSL=false'  -ti $CONTAINER_ID  /opt/keycloak/bin/kc.sh export --realm $REALM --file /tmp/my_realm.json -Djboss.socket.binding.port-offset=102 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=$REALM -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/my_realm.json
docker cp $CONTAINER_ID:/tmp/my_realm.json /tmp/my_realm.json
cp /tmp/my_realm.json src/test/resources/kc.json