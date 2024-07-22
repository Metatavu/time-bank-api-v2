package fi.metatavu.timebank.api.slack

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class MessagePayload (
    val text: String
)