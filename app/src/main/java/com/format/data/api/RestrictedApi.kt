package com.format.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RestrictedApi {
    @POST("/api/publishGroup")
    suspend fun publishGroup(
        @Body request: PublishGroupDto
    ): PublishGroupResponseDto

    @POST("/api/formula/react")
    suspend fun formulaReact(
        @Body request: FormulaReactDto
    ): FormulaReactResponseDto

    @POST("/api/reactions")
    suspend fun reactions(
        @Body request: ReactionsRequestDto
    ): ReactionsResponseDto

    @POST("/api/user/groupDownload")
    suspend fun groupDownloaded(
        @Body remoteFormulaRequest: RemoteFormulaRequest
    ): DownloadedGroupDto

    @POST("/api/user/groupDelete")
    suspend fun groupDeleted(
        @Body remoteFormulaRequest: RemoteFormulaRequest
    ): DownloadedGroupDto

    @GET("/api/loadUserData")
    suspend fun loadUserData(): LoadUserDataDto
}

@Serializable
data class LoadUserDataDto(
    @SerialName("downloadedFormulaGroups") val downloadedFormulaGroups: List<DownloadedGroupDto> = emptyList(),
    @SerialName("userReactions") val userReactions: List<ReactionDto> = emptyList(),
)

@Serializable
data class DownloadedGroupDto(
    @SerialName("userId") val id: Int,
    @SerialName("formulaGroupId") val ownerId: Int,
    @SerialName("formulaGroup") val formulaGroup: GroupDto? = null,
)

@Serializable
data class RemoteFormulaRequest(
    @SerialName("formulaGroupId") val formulaGroupId: Int,
)

@Serializable
data class ReactionsRequestDto(
    @SerialName("groupId") val groupId: Int,
)

@Serializable
data class ReactionsResponseDto(
    @SerialName("reactions") val reactions: List<ReactionDto>,
)

@Serializable
data class FormulaReactDto(
    @SerialName("formulaId") val formulaId: Int,
    @SerialName("responseType") val responseType: String,
)

@Serializable
data class FormulaReactResponseDto(
    @SerialName("message") val message: String,
    @SerialName("reaction") val reaction: ReactionDto,
)

@Serializable
data class ReactionDto(
    @SerialName("responseType") val responseType: String,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("formulaId") val formulaId: Int,
)

@Serializable
data class PublishGroupDto(
    @SerialName("name") val name: String,
    @SerialName("formulas") val formulas: List<FormulaDto>
)

@Serializable
data class PublishGroupResponseDto(
    @SerialName("message") val message: String,
    @SerialName("group") val group: GroupDto,
)
