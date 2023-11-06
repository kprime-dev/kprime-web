package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.dql.*
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase

object TraceCmdQueryFact: it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    override fun getCmdName(): String {
        return "query-fact"
    }

    override fun getCmdDescription(): String {
        return "Query a fact."
    }

    override fun getCmdUsage(): String {
        return " Example: ${getCmdName()} online-customer-sales from onlinecustomers inner orders and orders left sales"
    }

    override fun getCmdTopics(): String {
        return "read,fact"
    }

    /**
     * fact online-customer-sales from onlinecustomers inner orders and orders left sales
     */
    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        val database = context.env.database
        val tokens = command.split(" ").drop(1)

        if (tokens.size != 5 && tokens.size != 9) {
            return (TraceCmdResult() failure "wrong number of arguments")
        }

        // online-customer-sales  =
        // SELECT *
        // FROM onlinecustomers
        // INNER JOIN orders
        // ON onlinecustomers.customerid = orders.custimerid
        // LEFT JOIN sales
        // ON orders.orderid = sales.orderid


        // add-fact online-customer-sales
        // from onlinecustomers(customerid) inner orders
        // and orders(orderid) left sales

        val subj = tokens[0] // online-customer-sales
        //val word_from = tokens[1] // from
        val joinLeft = tokens[2] // onlinecustomers(customerid)
        val joinType = tokens[3] // INNER
        val joinRight = tokens[4] // orders


        // onlinecustomers(customerid)
        val joinLeftTokens = joinLeft.replace("(", " ").replace(")", " ").split(" ")
        val joinLeftTable = joinLeftTokens[0]
        val joinLeftOn = joinLeftTokens[1]

        val joinRightTokens = joinRight.replace("(", " ").replace(")", " ").split(" ")
        val joinRightTable = joinRightTokens[0]
        val joinRightOn = if (joinRightTokens.size == 2) joinRightTokens[1] else joinLeftOn

        val mapping = Mapping()
        mapping.name = subj
        val select = Select()

        val att = Attribute()
        att.name = "*"
        select.attributes.add(att)

        val from = From()
        from.tableName = joinLeftTable
        select.from = from

        val join = Join()
        join.joinLeftTableAlias = joinLeftTable
        join.joinOnLeft = joinLeftOn
        join.joinRightTable = joinRightTable
        join.joinOnRight = joinRightOn
        join.joinType = joinType
        from.addJoin(join)

        if (tokens.size == 9) {

            //val word_and = tokens[5] // and
            val joinLeft2 = tokens[6] // orders(orderid)
            val joinType2 = tokens[7] // LEFT
            val joinRight2 = tokens[8] // sales

            val joinLeftTokens2 = joinLeft2.replace("(", " ").replace(")", " ").split(" ")
            val joinLeftTable2 = joinLeftTokens2[0]
            val joinLeftOn2 = joinLeftTokens2[1]

            val joinRightTokens2 = joinRight2.replace("(", " ").replace(")", " ").split(" ")
            val joinRightTable2 = joinRightTokens2[0]
            val joinRightOn2 = if (joinRightTokens2.size == 2) joinRightTokens2[1] else joinLeftOn2

            val join2 = Join()
            join2.joinLeftTableAlias = joinLeftTable2
            join2.joinOnLeft = joinLeftOn2
            join2.joinRightTable = joinRightTable2
            join2.joinOnRight = joinRightOn2
            join2.joinType = joinType2
            from.addJoin(join2)
        }
        mapping.select = select
        database.mappings?.add(mapping)

        val selectSql = SQLizeSelectUseCase().sqlize(mapping)

        return (TraceCmdResult() message "command ok")
    }

}