package com.surrus.common

import kotlinx.coroutines.CoroutineDispatcher

// TEMP until following is resolved https://github.com/ktorio/ktor/issues/1622
expect fun ktorScope(block: suspend () -> Unit)

