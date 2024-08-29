package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
data class SeveraAccessToken (
    @JsonProperty("access_token")
    var accessToken: String,
    @JsonProperty("access_token_expires_in")
    var accessTokenExpiresIn: Int,
    @JsonProperty("refresh_token")
    var refreshToken: String,
    @JsonProperty("refresh_token_expires_in")
    var refreshTokenExpiresIn: Int,
    var scope: List<String>
)