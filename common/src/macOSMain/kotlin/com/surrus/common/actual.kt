package com.surrus.common

actual fun ktorScope(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking { block() }
