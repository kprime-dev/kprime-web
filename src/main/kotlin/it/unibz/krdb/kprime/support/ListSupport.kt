package it.unibz.krdb.kprime.support

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> List<T>.append(newValue: T): List<T> {
    var newList = this
    newList += newValue
    return newList
}

fun <T> MutableList<T>.addIf(element: T, condition: (T) -> Boolean) {
    if (this.findLast(condition)!=null)
        this.add(element)
}



