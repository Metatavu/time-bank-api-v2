package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonAlias
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class SeveraAccessToken (
    @JsonAlias("access_token")
    var accessToken: String,
    @JsonAlias("access_token_expires_in")
    var accessTokenExpiresIn: Int,
    @JsonAlias("refresh_token")
    var refreshToken: String,
    @JsonAlias("refresh_token_expires_in")
    var refreshTokenExpiresIn: Int,
    var scope: List<String>
)