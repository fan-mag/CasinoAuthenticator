package helpers

import java.sql.Connection
import java.sql.DriverManager

open class Database {
    companion object {
        var conn: Connection
            get() {
                if (field.isClosed) {
                    println("Connection was closed, setting new connection")
                    field = DriverManager.getConnection(Environment.databaseUrl)
                    Thread.sleep(3000)
                }
                return field
            }

        init {
            Environment()
            Class.forName(Environment.databaseDriver)
            conn = DriverManager.getConnection(Environment.databaseUrl)
        }
    }
}