package hibernate.v2.database

import com.squareup.sqldelight.TransactionWithReturn

fun MyDatabase.runGettingLastId(
    body: TransactionWithReturn<Long>.() -> Unit
): Long {
    return transactionWithResult {
        body()
        utilQueries.lastInsertedRowId().executeAsOne()
    }
}