package app.spidy.express.data

import java.util.*
import kotlin.collections.HashMap

class Headers {
    private val heads = hashMapOf<String, String>()

    fun add(key: String, value: String) {
        heads[key.toLowerCase(Locale.ROOT)] = value
    }

    fun get(key: String): String? {
        return heads[key.toLowerCase(Locale.ROOT)]
    }

    fun toHashMap(): HashMap<String, String> {
        return heads
    }

    fun bulkAdd(heads: HashMap<String, String>) {
        for ((k, v) in heads) {
            add(k, v)
        }
    }

    override fun toString(): String {
        return "$heads"
    }
}