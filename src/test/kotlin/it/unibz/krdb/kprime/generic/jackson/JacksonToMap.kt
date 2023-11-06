package it.unibz.krdb.kprime.generic.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.kotlin.utils.join
import org.jetbrains.kotlin.utils.keysToMap
import org.junit.Test
import kotlin.test.assertEquals

class JacksonToMap {

    @Test
    fun test_from_json_to_tree() {
        //given
        val json = "{\"name\":\"mkyong\", \"age\":\"37\"}"
        // when
        val tree = ObjectMapper().readTree(json)
        // then
        assertEquals("",tree.fields().asSequence().joinToString())
    }

    @Test
    fun test_from_alpinebits_to_tree() {
        // given
        val json = """
{
  "jsonapi": {
    "version": "1.0"
  },
  "meta": {
    "pages": 456,
    "count": 4560
  },
  "links": {
    "swagger": "https://swagger.opendatahub.bz.it/?url=https://destinationdata.alpinebits.opendatahub.testingmachine.eu/specification.json",
    "self": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events?page[number]=1",
    "first": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events?page[number]=1",
    "next": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events?page[number]=2",
    "prev": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events?page[number]=1",
    "last": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events?page[number]=456"
  },
  "data": [
    {
      "type": "events",
      "id": "33573FD9206943F1802373CF61BEF189_REDUCED",
      "meta": {
        "dataProvider": "http://tourism.opendatahub.bz.it/",
        "lastUpdate": "2022-08-26T00:05:17.048Z"
      },
      "links": {
        "self": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events/33573FD9206943F1802373CF61BEF189_REDUCED"
      },
      "attributes": {
        "abstract": null,
        "description": null,
        "name": {
          "deu": "Jubiläumsfeier 150 Jahre Südtiroler Haflinger",
          "eng": "Jubilee celebrations 150 years Haflinger horse",
          "ita": "Festeggiamenti Giubileo 150 anni cavallo Haflinger"
        },
        "shortName": null,
        "url": null,
        "endDate": "2024-04-28T00:00:00.000Z",
        "inPersonCapacity": null,
        "onlineCapacity": null,
        "participationUrl": null,
        "recorded": null,
        "registrationUrl": null,
        "startDate": "2024-04-24T00:00:00.000Z",
        "status": "published"
      },
      "relationships": {
        "categories": {
          "data": [
            {
              "id": "alpinebits:inPersonEvent",
              "type": "categories"
            }
          ],
          "link": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events/33573FD9206943F1802373CF61BEF189_REDUCED/categories"
        },
        "multimediaDescriptions": null,
        "contributors": null,
        "organizers": null,
        "publisher": {
          "data": {
            "id": "lts",
            "type": "agents"
          },
          "link": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events/33573FD9206943F1802373CF61BEF189_REDUCED/publisher"
        },
        "series": null,
        "sponsors": null,
        "subEvents": null,
        "venues": {
          "data": [
            {
              "id": "33573FD9206943F1802373CF61BEF189_REDUCED_venue",
              "type": "venues"
            }
          ],
          "link": "https://destinationdata.alpinebits.opendatahub.testingmachine.eu/2022-04/events/33573FD9206943F1802373CF61BEF189_REDUCED/venues"
        }
      }
    }
  ]
}
        """.trimIndent()
        // when
        val tree = ObjectMapper().readTree(json)
        // then
        assertEquals("jsonapi, meta, links, data",
            tree.fields().asSequence().joinToString{ it.key })
        assertEquals("{\"pages\":456,\"count\":4560}, {\"dataProvider\":\"http://tourism.opendatahub.bz.it/\",\"lastUpdate\":\"2022-08-26T00:05:17.048Z\"}",
            tree.findValues("meta").joinToString())
        assertEquals("pages, count, dataProvider, lastUpdate",
            tree.findValues("meta").joinToString { it.fields().asSequence().joinToString { it.key } })
        assertEquals("true",
            tree.findValues("data").joinToString { it.isArray.toString() } )
        assertEquals("type, id, meta, links, attributes, relationships",
            tree.findValues("data").joinToString { it[0].fields().asSequence().joinToString { it.key } })

    }
}