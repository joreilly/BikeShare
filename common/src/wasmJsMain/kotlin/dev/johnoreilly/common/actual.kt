package dev.johnoreilly.common

actual fun getCountryName(countryCode: String): String = getCountryDisplayName(countryCode)

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun(
    """ (countryCode) => {
        const regionNames = new Intl.DisplayNames(['en'], {type: 'region'});
        return regionNames.of(countryCode)
    }
"""
)
private external fun getCountryDisplayName(countryCode: String): String
