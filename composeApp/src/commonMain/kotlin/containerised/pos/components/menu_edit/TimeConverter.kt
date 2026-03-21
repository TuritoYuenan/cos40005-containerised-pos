package containerised.pos.components.menu_edit

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun timestampConverter(date: String?): Long? {
    if (date.isNullOrBlank()) return null

    val localDateTime = LocalDateTime.parse(date)

    val instant = localDateTime.toInstant(TimeZone.UTC)

    return instant.toEpochMilliseconds()
}

fun millisConverter(millis: Long?): String? {
    if (millis == null) return null

    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

    return localDateTime.toString()
}