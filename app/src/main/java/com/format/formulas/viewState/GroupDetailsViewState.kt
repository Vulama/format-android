package com.format.formulas.viewState

import com.format.domain.model.Reaction

data class GroupDetailsViewState(
    val isGroupPublished: Boolean = false,
    val isPublishInProgress: Boolean = false,
    val groupReactions: List<Reaction>? = null
)