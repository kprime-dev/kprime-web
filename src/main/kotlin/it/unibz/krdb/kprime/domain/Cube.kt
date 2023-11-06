package it.unibz.krdb.kprime.domain

data class Cube(
        val table:String,
        val dimension:String) {

    fun count(countCol:String):String {
        return "select count($countCol),$dimension from $table group by $dimension order by count($countCol)"
    }

    fun sum(sumCol:String="Products.Price") = """
SELECT SUM($sumCol), OrderDetails.OrderID
FROM OrderDetails
INNER JOIN Products ON OrderDetails.ProductID=Products.ProductID
GROUP BY OrderID
ORDER BY SUM($sumCol) DESC;
    """.trimIndent()
}