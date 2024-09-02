@file:UseSerializers(OptionSerializer::class)

package dev.mtib.void.api.models

import arrow.core.serialization.OptionSerializer
import kotlinx.serialization.UseSerializers
import arrow.core.Option
import arrow.core.none
import kotlinx.serialization.Serializable

@Serializable
data class VoidMetadata(
    val data: Option<Map<String, String>> = none()
)
