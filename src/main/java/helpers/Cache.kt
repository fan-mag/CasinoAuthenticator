package helpers

import java.util.*

object Cache {
    private val APIKEY_PRIVILEGE: MutableMap<String, Int> = TreeMap()
    private val LOGIN_APIKEY: MutableMap<String, String> = TreeMap()

    fun get(type: CacheType, key: Any): Any? {
        return when (type) {
            CacheType.LOGIN_APIKEY -> {
                if (LOGIN_APIKEY.keys.contains(key.toString()))
                    LOGIN_APIKEY[key]
                else null
            }
            CacheType.APIKEY_PRIVILEGE -> {
                if (APIKEY_PRIVILEGE.keys.contains(key.toString()))
                    APIKEY_PRIVILEGE[key]
                else null
            }
        }
    }

    fun put(type: CacheType, key: Any, value: Any) {
        when (type) {
            CacheType.LOGIN_APIKEY -> LOGIN_APIKEY[key.toString()] = value.toString()
            CacheType.APIKEY_PRIVILEGE -> APIKEY_PRIVILEGE[key.toString()] = value.toString().toInt()
        }
    }

    fun delete(type: CacheType, key: Any) {
        when (type) {
            CacheType.LOGIN_APIKEY -> LOGIN_APIKEY.remove(key.toString())
            CacheType.APIKEY_PRIVILEGE -> APIKEY_PRIVILEGE.remove(key.toString())
        }
    }

    fun clear(type: CacheType) {
        when (type) {
            CacheType.LOGIN_APIKEY -> LOGIN_APIKEY.clear()
            CacheType.APIKEY_PRIVILEGE -> APIKEY_PRIVILEGE.clear()
        }

    }

    fun clear() {
        clear(CacheType.LOGIN_APIKEY)
        clear(CacheType.APIKEY_PRIVILEGE)
    }
}