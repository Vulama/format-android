package com.format.domain.model

import com.format.common.model.Epoch
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    val type: Boolean,
    val createdAt: Epoch,
    val formulaId: Int,
)