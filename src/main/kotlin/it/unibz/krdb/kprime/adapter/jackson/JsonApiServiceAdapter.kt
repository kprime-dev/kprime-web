package it.unibz.krdb.kprime.adapter.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.term.JsonApiService
import it.unibz.krdb.kprime.domain.term.Term
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Database

class JsonApiServiceAdapter: JsonApiService {

    data class JsonAPILicense(
        val name:String,
        val url:String
    )
    data class JsonAPIContact(
        val email:String
    )
    data class JsonAPIInfo(
        val title:String,
        val description:String,
        val termsOfService:String,
        val contact:JsonAPIContact,
        val license:JsonAPILicense,
        val version:String
    )
    data class JsonAPIExternalDocs(
        val description:String,
        val url:String
    )
    data class JsonAPIServer(
        val url:String
    )
    data class JsonAPITag(
        val name:String,
        val description:String,
        val externalDocs:JsonAPIExternalDocs? = null
    )
    data class JsonAPIProps(
        val type:String,
        val format:String,
        val example:Any
    )
    data class JsonAPISchema(
        val type:String,
        val properties: Map<String,JsonAPIProps>
    )
    data class JsonAPIComponents(
        val schemas:Map<String,JsonAPISchema>,
        val requestBodies:Map<String,String>,
        val securitySchemes:Map<String,String>
    )
    data class JsonAPIRoot(
        val openapi:String,
        val info:JsonAPIInfo,
        val externalDocs:JsonAPIExternalDocs,
        val servers:List<JsonAPIServer>,
        val tags:List<JsonAPITag>,
        val paths:Map<String,String>,
        val components:JsonAPIComponents)

    internal fun toJsonAPIString(jsonApi:JsonAPIRoot): String {
        return ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writerWithDefaultPrettyPrinter().writeValueAsString(jsonApi)
    }



    private val mockedJsonApi  = fun(context:PrjContext, database:Database, allTerms: List<Term>):JsonAPIRoot {
        return JsonAPIRoot(
            "3.0.2",
            JsonAPIInfo(
                context.name,
                context.description,
                context.termsOfServiceUrl?:"",
                JsonAPIContact(context.steward?:"no steward"),
                JsonAPILicense(context.license?:"no license", context.licenseUrl?:""),
                "1.0.17"
            ),
            JsonAPIExternalDocs("Find out more about Swagger", "http://swagger.io"),
            listOf(JsonAPIServer("/api/v3")),
            allTerms.map { JsonAPITag( it.name, it.description, JsonAPIExternalDocs("For more...", it.url)) },
            emptyMap(),
            JsonAPIComponents(fromDbToMap(database)
                , emptyMap()
                , emptyMap())
        )
    }

    private fun fromDbToMap(db:Database):Map<String,JsonAPISchema> {
        return db.schema.tables().map { it.name to JsonAPISchema("object", fromAttributesToProps(it.columns)) }.toMap()
    }

    private fun fromAttributesToProps(columns: ArrayList<Column>):Map<String,JsonAPIProps> {
        return columns.map { it.name to JsonAPIProps(it.type?:"string","int64",10) }.toMap()
    }

    override fun openApi(project: PrjContext, database: Database, allTerms: List<Term>):String {
        //var fileContent = JsonApiServiceAdapter::class.java.getResource("/vocabulary/openapi.json").readText()
        val projectName = project.name
        println("openApi.projectName:[$projectName]")
        val fileContent = toJsonAPIString(mockedJsonApi(project,database,allTerms))
        return fileContent
    }
}