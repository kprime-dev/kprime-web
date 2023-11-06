package it.unibz.krdb.kprime.adapter.jackson

import org.junit.Test
import kotlin.test.assertEquals

class JsonApiServiceAdapterTest {

    @Test
    fun test_json_api_to_string() {
        // given
        val jsonRoot = JsonApiServiceAdapter.JsonAPIRoot("3.0.2",
            JsonApiServiceAdapter.JsonAPIInfo(
                "Swagger Petstore - OpenAPI 3.0",
                "This is a sample Pet Store Server based on the OpenAPI 3.0 specification.",
                "http://swagger.io/terms/",
                JsonApiServiceAdapter.JsonAPIContact("apiteam@swagger.io"),
                JsonApiServiceAdapter.JsonAPILicense("Apache 2.0","http://www.apache.org/licenses/LICENSE-2.0.html"),
            "1.0.17"),
            JsonApiServiceAdapter.JsonAPIExternalDocs("Find out more about Swagger","http://swagger.io"),
            listOf(JsonApiServiceAdapter.JsonAPIServer("/api/v3")),
            listOf(
                JsonApiServiceAdapter.JsonAPITag("pet","Everything about your Pets",
                    JsonApiServiceAdapter.JsonAPIExternalDocs("For more...","http://swagger.io")),
                JsonApiServiceAdapter.JsonAPITag("store","Access to Petstore orders",
                    JsonApiServiceAdapter.JsonAPIExternalDocs("For info...","http://swagger.io")),
                JsonApiServiceAdapter.JsonAPITag("user","Operations about user")
            ),
            emptyMap(),
            JsonApiServiceAdapter.JsonAPIComponents(
                mapOf(
                    "Order" to JsonApiServiceAdapter.JsonAPISchema(
                        "object", mapOf(
                            "id" to JsonApiServiceAdapter.JsonAPIProps("integer", "int64", 10)
                        )
                    )
                )
                , emptyMap(), emptyMap()))
        // when
        val toJsonAPIString = JsonApiServiceAdapter().toJsonAPIString(jsonRoot)
        // then
        assertEquals("""
                {
                  "openapi" : "3.0.2",
                  "info" : {
                    "title" : "Swagger Petstore - OpenAPI 3.0",
                    "description" : "This is a sample Pet Store Server based on the OpenAPI 3.0 specification.",
                    "termsOfService" : "http://swagger.io/terms/",
                    "contact" : {
                      "email" : "apiteam@swagger.io"
                    },
                    "license" : {
                      "name" : "Apache 2.0",
                      "url" : "http://www.apache.org/licenses/LICENSE-2.0.html"
                    },
                    "version" : "1.0.17"
                  },
                  "externalDocs" : {
                    "description" : "Find out more about Swagger",
                    "url" : "http://swagger.io"
                  },
                  "servers" : [ {
                    "url" : "/api/v3"
                  } ],
                  "tags" : [ {
                    "name" : "pet",
                    "description" : "Everything about your Pets",
                    "externalDocs" : {
                      "description" : "For more...",
                      "url" : "http://swagger.io"
                    }
                  }, {
                    "name" : "store",
                    "description" : "Access to Petstore orders",
                    "externalDocs" : {
                      "description" : "For info...",
                      "url" : "http://swagger.io"
                    }
                  }, {
                    "name" : "user",
                    "description" : "Operations about user"
                  } ],
                  "paths" : { },
                  "components" : {
                    "schemas" : {
                      "Order" : {
                        "type" : "object",
                        "properties" : {
                          "id" : {
                            "type" : "integer",
                            "format" : "int64",
                            "example" : 10
                          }
                        }
                      }
                    },
                    "requestBodies" : { },
                    "securitySchemes" : { }
                  }
                }
        """.trimIndent(),toJsonAPIString)
    }
}