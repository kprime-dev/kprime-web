package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.todo.Todo
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class TodoOrderTest {

    private val todoList = listOf<Todo>(
            Todo(
                    1,
                    "My title 1",
                    false,
                    false,
                    "my-key",
                    Date(),
                    Date(),
                    Date(),
                    Date(),
                    4,
                    1,
                    "",
                    "npedot",
                    false,
                    false,
                    ""),
            Todo(
                    2,
                    "My title 2",
                    false,
                    false,
                    "my-key",
                    Date(),
                    Date(),
                    Date(),
                    Date(),
                    2,
                    1,
                    "My title 1",
                    "npedot",
                    false,
                    false,
                    ""),
            Todo(
                    3,
                    "My title 3",
                    false,
                    false,
                    "my-key",
                    Date(),
                    Date(),
                    Date(),
                    Date(),
                    3,
                    1,
                    "",
                    "npedot",
                    false,
                    false),
            Todo(
                    4,
                    "My title 4",
                    false,
                    false,
                    "my-key",
                    Date(),
                    Date(),
                    Date(),
                    Date(),
                    7,
                    1,
                    "My title 1",
                    "npedot",
                    false,
                    false),
            Todo(
                        5,
                    "My title 5",
                    false,
                    false,
                    "my-key",
                    Date(),
                    Date(),
                    Date(),
                    Date(),
                    7,
                    1,
                    "My title 4",
                    "npedot",
                    false,
                    false)

    )


    @Test
    fun test_tree_order() {
        val tree = TreeSet<Todo>(todoList)
        var result = ""
        // when
        tree.sortedBy { it.partof }.forEach { result += it.title + System.lineSeparator() }
        // then
        assertEquals("""
            My title 1
            My title 3
            My title 4
            My title 2
        """.trimIndent(), result.trim())
    }


    class Node<T>(
            var value: T,
            var level: Int = 0,
            var children: MutableList<Node<T>> = mutableListOf()
    ) {

        companion object {

            fun <T> traverseDepthFirst(
                    rootNode: Node<T>,
                    action: (child: Node<T>) -> Unit
            ) {
                val stack = ArrayDeque<Node<T>>()
                stack.addFirst(rootNode)

                while (stack.isNotEmpty()) {
                    val currentNode = stack.removeFirst()
                    action.invoke(currentNode)

                    for (index in currentNode.children.size - 1 downTo 0) {
                        stack.addFirst(currentNode.children[index])
                    }
                }
            }

            fun <T> traverseBreadthFirst(
                    rootNode: Node<T>,
                    action: (value: T) -> Unit
            ) {
                val queue = ArrayDeque<Node<T>>()
                queue.addFirst(rootNode)

                while(queue.isNotEmpty()) {
                    val currentNode = queue.removeLast()

                    action.invoke(currentNode.value)

                    for(childNode in currentNode.children) {
                        queue.addFirst(childNode)
                    }
                }
            }


        }
    }

    // given:
    // list<todos>,
    // map<node<name,todos>> with only root node

    // then:
    // map<node<todos>> with all elements chained

    fun rootOfTodo(todoList: List<Todo>): Node<Todo>? {
        val tree = mutableMapOf<String, Node<Todo>>()
        val rootTodo = Todo( title = "root")
        val todoListParentOrdered = todoList.sortedBy { it.partof }
        val rootNode = Node(rootTodo, 0)
        tree["root"] = rootNode
        for (todo in todoListParentOrdered) {
            val parent = tree[todo.partof] ?: rootNode
            val element = Node(todo, parent.level+1)
            parent.children.add(element)
            tree[todo.title] = element
        }
        return tree["root"]
    }

    fun rootOfNAryTestTree(): Node<Int> {

        var level1Node1 = Node(2)
        level1Node1.children = mutableListOf(Node(4), Node(5), Node(6))

        var level1Node2 = Node(3)
        level1Node2.children = mutableListOf(Node(7), Node(8))

        var rootNode = Node(1)
        rootNode.children = mutableListOf(level1Node1, level1Node2)

        return rootNode
    }

    @Test
    fun test_traverse_depth_int() {
        // given
        val rootOfNAryTestTree = rootOfNAryTestTree()
        var result = ""
        // when
        Node.traverseDepthFirst(rootOfNAryTestTree) {
            result += "" + it.value + System.lineSeparator()
        }
        // then
        assertEquals("""
                    1
                    2
                    4
                    5
                    6
                    3
                    7
                    8
        """.trimIndent(),result.trim())
    }

    @Test
    fun test_traverse_depth_todo() {
        // given
        val rootTodo = rootOfTodo(todoList)
        if (rootTodo==null) return
        var result = ""
        // when
        Node.traverseDepthFirst(rootTodo) {
            result += "${it.value.title} ${it.level}" + System.lineSeparator()
        }
        // then
        assertEquals("""
                root 0
                My title 1 1
                My title 2 2
                My title 4 2
                My title 5 3
                My title 3 1
        """.trimIndent(), result.trim())
    }

}