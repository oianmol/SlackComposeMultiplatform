package dev.baseio.slackclone.chatmessaging.chatthread

object MentionsPatterns {

    const val HASH_TAG = "HASH"
    const val INVITE_TAG = "INVITE_TAG"
    const val URL_TAG = "URL"
    const val AT_THE_RATE = "AT_RATE"
    val urlPattern = Regex(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
            "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
            "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        hashSetOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    val hashTagPattern = Regex("\\B(\\#[a-zA-Z0-9._]+\\b)(?!;)")
    val inviteTagPattern = Regex("\\B(\\/[invite]+\\b)(?!;)")
    val mentionTagPattern = Regex("\\B(\\@[a-zA-Z0-9._]+\\b)(?!;)")
}
