package helpers

object RequestProcess {
    fun validateContentType(contentType: String?): Boolean {
        if (contentType == null) return false
        if (!contentType.contains("application/json")) return false
        return true
    }

    fun isBodyNull(requestBody: String?) : Boolean {
        return (requestBody == null)
    }
}