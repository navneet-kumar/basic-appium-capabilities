package response

enum class Status private constructor(val status: String) {
    SUCCESS("Success"),
    ERROR("Error")
}
