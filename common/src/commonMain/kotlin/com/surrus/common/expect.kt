package com.surrus.common

// TEMP until following is resolved https://github.com/ktorio/ktor/issues/1622
expect fun ktorScope(block: suspend () -> Unit)
