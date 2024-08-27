package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.severa.models.SeveraAccessToken

class TestTokenData {
    companion object {
        fun getAccessToken(): SeveraAccessToken{
            return SeveraAccessToken(
                accessToken = "exampleAccessToken123",
                accessTokenExpiresIn = 3600,
                refreshToken = "exampleRefreshToken123",
                refreshTokenExpiresIn = 80000,
                scope = listOf("users:read")
            )
        }
    }
}