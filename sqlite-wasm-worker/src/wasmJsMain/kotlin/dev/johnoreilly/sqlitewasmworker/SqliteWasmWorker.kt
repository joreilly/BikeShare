package dev.johnoreilly.sqlitewasmworker

import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

fun createSQLiteWasmWorker(): WebWorkerSQLiteDriver = WebWorkerSQLiteDriver(jsWorker())

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsWorker(): Worker =
    js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url))""")
