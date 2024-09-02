package dev.mtib.void.api.storage

import kotlin.io.path.Path

object EnvFileStorage: StorageInterface by FileStorage(
    Path(System.getenv("VOID_STORAGE_PATH") ?: "storage")
)