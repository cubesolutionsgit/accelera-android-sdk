package ai.accelera.library.models

import androidx.annotation.StringDef

internal const val DIRECT = "direct"
internal const val LINK = "link"
internal const val PUSH = "push"

@StringDef(DIRECT, LINK, PUSH)
internal annotation class TrackVisitSource
