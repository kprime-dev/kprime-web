package it.unibz.krdb.kprime.generic.jsonld

import org.junit.Ignore
import org.junit.Test
import org.openprovenance.prov.interop.Formats
import org.openprovenance.prov.core.jsonld11.serialization.ProvSerialiser
import org.openprovenance.prov.interop.InteropFramework
import org.openprovenance.prov.model.*
import java.io.ByteArrayOutputStream
import org.openprovenance.prov.vanilla.ProvFactory

/**
 *  Provenance is information about entities, activities, and people involved in producing a piece of data or thing,
 *  which can be used to form assessments about its quality, reliability or trustworthiness.
 *  PROV-DM is the conceptual data model that forms a basis for the W3C provenance (PROV)
 *  family of specifications.
 *
 *  PROV-DM distinguishes core structures, forming the essence of provenance information,
 *  from extended structures catering for more specific uses of provenance.
 *
 *  PROV-DM is organized in six components, respectively dealing with:
 *  (1) entities and activities, and the time at which they were created, used, or ended;
 *  (2) derivations of entities from entities;
 *  (3) agents bearing responsibility for entities that were generated and activities that happened;
 *  (4) a notion of bundle, a mechanism to support provenance of provenance; and,
 *  (5) properties to link entities that refer to the same thing;
 *  (6) collections forming a logical structure for its members.

 */
class ProvJsonLdTest  {

    fun qn (pf: ProvFactory, ns:Namespace, name: String):QualifiedName {
            return pf.newQualifiedName("http://www.ipaw.info/pc1/",name, "pc1")
        //return ns.qualifiedName("http://www.ipaw.info/pc1/",name, pf)
    }

    @Test
    fun test_doc_build() {
        val ns = Namespace()
        ns.addKnownNamespaces()
        val pFactory = ProvFactory()
        val quote = pFactory.newEntity(qn(pFactory,ns, "a-little-provenance-goes-a-long-way"))
        quote.setValue(pFactory.newValue("A little provenance goes a long way", pFactory.getName().XSD_STRING));

        val doc = pFactory.newDocument()

        val provSerializer = ProvSerialiser()
        val outStream = ByteArrayOutputStream()
        provSerializer.serialiseDocument(outStream,doc,true)
//        iFramework.writeDocument(outStream, Formats.ProvFormat.JSONLD,doc)
//        println(outStream.toString(Charsets.UTF_8.name()))
    }



    /**
     * https://lucmoreau.wordpress.com/2014/08/01/provtoolbox-tutorial-1-creating-and-saving-a-prov-document/
     * https://github.com/lucmoreau/ProvToolbox/blob/master/modules-tutorial/tutorial1/src/main/java/org/openprovenance/prov/tutorial/tutorial1/Little.java
     */
    @Test
    @Ignore
    fun test_doc_creation() {

        val PROVBOOK_NS = "http://www.provbook.org"
        val PROVBOOK_PREFIX = "provbook"

        val JIM_NS = "http://www.cs.rpi.edu/~hendler/"
        val JIM_PREFIX = "jim"

        val ns = Namespace()
        ns.addKnownNamespaces()
        ns.register(PROVBOOK_PREFIX, PROVBOOK_NS)
        ns.register(JIM_PREFIX, JIM_NS)

        fun  qn(ns:Namespace, n:String, pFactory : ProvFactory):QualifiedName {
            return ns.qualifiedName(PROVBOOK_PREFIX, n, pFactory)
        }

        val pFactory = ProvFactory()

        val quote: Entity = pFactory.newEntity(qn(ns,"a-little-provenance-goes-a-long-way",pFactory))
        quote.value = pFactory.newValue("A little provenance goes a long way",
                pFactory.name.XSD_STRING)

        val original: Entity = pFactory.newEntity(ns.qualifiedName(JIM_PREFIX,"LittleSemanticsWeb.html",pFactory))

        val paul: Agent = pFactory.newAgent(qn(ns,"Paul", pFactory), "Paul Groth")
        val luc: Agent = pFactory.newAgent(qn(ns,"Luc", pFactory),
                mutableListOf(
                        pFactory.newAttribute(
                            Attribute.AttributeKind.PROV_LOCATION,
                            "http://www.mio/",
                            ns.qualifiedName(JIM_PREFIX,"Person",pFactory))
                ))

        val attr1: WasAttributedTo = pFactory.newWasAttributedTo(null, quote.id, paul.id);
        val attr2: WasAttributedTo = pFactory.newWasAttributedTo(null, quote.id, luc.id)
        val wdf: WasDerivedFrom = pFactory.newWasDerivedFrom(quote.id, original.id);

        val  document: Document = pFactory.newDocument()
        document.statementOrBundle
                .addAll(listOf(
                        quote,
                        paul,
                        luc,
                        attr1,
                        attr2,
                        original,
                        wdf ));
        document.setNamespace(ns);

        print_prov_notation(document)
        print_json_notation(document)

    }

    private fun print_json_notation(document: Document) {
        val provSerializer = ProvSerialiser()
        val outStream = ByteArrayOutputStream()
        provSerializer.serialiseDocument(outStream, document, true)
        val result = String(outStream.toByteArray())
        println(result)
    }


    /**
     * To provide examples of the PROV data model, the PROV notation (PROV-N) is introduced:
     * aimed at human consumption, PROV-N allows serializations of PROV instances to be
     * created in a compact manner. PROV-N facilitates the mapping of the PROV data model to concrete syntax,
     * and is used as the basis for a formal semantics of PROV.
     */
    fun print_prov_notation(doc: Document) {
        val interop = InteropFramework()
        var text:String
        //interop.writeDocument(text,doc)
        interop.writeDocument(System.out, Formats.ProvFormat.PROVN,doc)

    }
}