package it.unibz.krdb.kprime.generic.graphql

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import org.junit.Test
import kotlin.test.assertEquals

class GraphQlTest {

    @Test
    fun test_hello_graphql_world() {
        val schema = "type Query{hello: String}"

        val schemaParser = SchemaParser()
        val typeDefinitionRegistry: TypeDefinitionRegistry = schemaParser.parse(schema)

        val runtimeWiring: RuntimeWiring = newRuntimeWiring()
            .type("Query") { builder -> builder.dataFetcher("hello", StaticDataFetcher("world")) }
            .build()

        val schemaGenerator = SchemaGenerator()
        val graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

        val build: GraphQL = GraphQL.newGraphQL(graphQLSchema).build()
        val executionResult: ExecutionResult = build.execute("{hello}")

        val data = executionResult.getData<Any>()
        assertEquals("{hello=world}",data.toString())
    }

}