package com.format.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PublicApi {

    @POST("/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponseDto>

    @POST("/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponseDto>

    @POST("/auth/refreshToken")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): RefreshTokenResponseDto

    @GET("/api/groups")
    suspend fun groups(): List<GroupDto>
}

@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken") val refreshToken: String,
)

@Serializable
data class LoginRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
)

@Serializable
data class LoginResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("accessTokenExpiresAt") val accessTokenExpiresAt: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("refreshTokenExpiresAt") val refreshTokenExpiresAt: String,
    @SerialName("user") val user: UserDto,
    @SerialName("downloadedFormulaGroups") val downloadedFormulaGroups: List<DownloadedGroupDto> = emptyList(),
    @SerialName("userReactions") val userReactions: List<ReactionDto> = emptyList(),
)

@Serializable
data class RegisterRequest(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
)

@Serializable
data class RegisterResponseDto(
    @SerialName("message") val message: String,
    @SerialName("user") val user: UserDto,
)

@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("passwordHash") val passwordHash: String,
)

@Serializable
data class RefreshTokenResponseDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("accessTokenExpiresAt") val accessTokenExpiresAt: String,
)

@Serializable
data class GroupDto(
    @SerialName("id") val id: Int,
    @SerialName("ownerId") val ownerId: Int,
    @SerialName("name") val name: String,
    @SerialName("formulas") val formulas: List<FormulaDto>,
)

@Serializable
data class FormulaDto(
    @SerialName("title") val title: String,
    @SerialName("mathFormula") val mathFormula: String,
    @SerialName("description") val description: String,
    @SerialName("id") val id: Int? = null,
)