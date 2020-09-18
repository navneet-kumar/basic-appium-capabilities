package services.implementations

import java.util.*


enum class TranslationExtension(private val jp: String, private val eng: String) {

    Settings("設定", "Settings"),
    General("一般", "General"),
    LanguageRegion("言語と地域", "Language & Region"),
    CurrentLanguage("iPhoneの使用言語", "iPhone Language"),
    TargetLanguage("日本語", "English");

    fun value(locale: Locale): String {
        return if (locale == Locale.JAPAN) jp else eng
    }
}