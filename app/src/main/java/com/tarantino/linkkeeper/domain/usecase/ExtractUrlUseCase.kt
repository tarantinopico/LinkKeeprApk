package com.tarantino.linkkeeper

import javax.inject.Inject

class ExtractUrlUseCase @Inject constructor() {
    operator fun invoke(text: String): String? {
        val regex = "(https?://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(/[^\\s]*)?)".toRegex()
        val matchResult = regex.find(text)
        return matchResult?.value
    }
}
