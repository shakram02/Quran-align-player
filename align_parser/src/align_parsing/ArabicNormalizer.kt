package align_parsing

/**
 * Normalizer for Arabic.
 * Replaces Alef, Waw, Yeh variants with the original letters.
 * Removes Arabic-related symbols and ligatures
 */
object ArabicNormalizer {
    private val ALEF = "\u0627"
    private val WAW = "\u0648"
    private val YEH = "\u064A"

    // src: https://www.fileformat.info/info/charset/UTF-8/list.htm?start=1024
    private val unicodeAlef = Regex("\\u0622|\\u0623|\\u0625|\\u0627|\\u0654|\\u0655|\\u065F|[\\u0670-\\u0675]")
    private val unicodeWaw = Regex("[\\u0624\\u0676\\u0677]")
    private val unicodeYeh = Regex("\\u0626|\\u0649|\\u064A|\\u0678")
    private val specialUnicodeLigatures = Regex("\\u0616|\\u0617|")
    private val specialUnicodeSmallHighLetters = Regex("\\u0617")
    private val symbols = Regex("[\\u061b-\\u061f]|[\\u0657-\\u065e]|[\\u06D6-\\u06ED]|\\u0640|\\u200D")
    private val tashkeel = Regex("[\\u0618-\\u061a]|[\\u064b-\\u0653]|[\\u0656-\\u065E]")

    /**
     * Normalize an input buffer of Arabic text
     *
     * @param input input buffer
     * @return normalized string
     */
    fun normalize(input: String): String {
        return input
            .replace(unicodeAlef, ALEF)
            .replace(unicodeWaw, WAW)
            .replace(unicodeYeh, YEH)
            .replace(symbols, "")
            .replace(tashkeel, "")
            .replace(specialUnicodeLigatures, "")
            .replace(specialUnicodeSmallHighLetters, "")
    }
}