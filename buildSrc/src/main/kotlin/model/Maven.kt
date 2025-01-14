package model

import java.net.URI

data class Maven(
    val releaseUrl: URI,
    val snapshotUrl: URI,
    val credentials: Credential,
)
