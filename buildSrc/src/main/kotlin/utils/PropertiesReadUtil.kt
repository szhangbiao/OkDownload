package utils

import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

object PropertiesReadUtil {

    fun readProperties(propertiesPath: String, key: String): String? {
        if (propertiesPath.isEmpty() || key.isEmpty()) {
            return null
        }
        return try {
            val properties = Properties()
            val inputStream: InputStream = FileInputStream(propertiesPath)
            // 读取local.properties文件
            properties.load(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            properties.getProperty(key)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}