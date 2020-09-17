package services

import response.Response

interface LanguageService {
    fun toEnglish(device: Map<String, String>): Response
}
