import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.util.Objects

object UriUtil {
    fun parseQueryMap(uri: URI): Map<String, String> {
        val queryPairs: MutableMap<String, String> = LinkedHashMap()
        try {
            val rawQuery = uri.rawQuery
            // 過濾沒有 query string
            // 還有過濾無法成對 keyValue 的 query, e.g. http://host/path?123
            if (Objects.isNull(rawQuery) || !rawQuery.contains("=")) {
                return queryPairs
            }
            val pairs = rawQuery.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val utf8 = "UTF-8"
            for (pair in pairs) {

                // 統一 decode
                val deCodePair = URLDecoder.decode(pair, utf8)

                // check deCodePair 空字串,
                if (!deCodePair.isEmpty()) {
                    val keyValue = pair.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    // check is key value
                    if (2 == keyValue.size) {
                        val key = keyValue[0]
                        val value = keyValue[1]
                        queryPairs[key] = value
                    }
                }
            }
            return queryPairs
        } catch (uriBuilderUrlException: URISyntaxException) {
            uriBuilderUrlException.printStackTrace()
        } catch (urlDecodeException: UnsupportedEncodingException) {
            urlDecodeException.printStackTrace()
        }
        return queryPairs
    }
}