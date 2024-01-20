package it.unibz.krdb.kprime.support

import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop

class CappedQueue<E>(private val maxSize: Int) {
    private val queue : MutableList<E> = ArrayList()

    fun add(element: E) {
        if (queue.size == maxSize) {
            queue.removeFirst()
        }
        queue.add(element)
    }

    fun remove(): E {
        return queue.pop()
    }

    fun peek(): E? {
        return queue.peek()
    }

    fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    fun toList(): List<E> {
        return queue.toList()
    }
}
