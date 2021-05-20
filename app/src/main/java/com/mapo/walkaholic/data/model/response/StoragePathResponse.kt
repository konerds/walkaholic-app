package com.mapo.walkaholic.data.model.response

import com.mapo.walkaholic.data.model.StoragePath

data class StoragePathResponse (
    val error: Boolean,
    val StoragePath: ArrayList<StoragePath>
)