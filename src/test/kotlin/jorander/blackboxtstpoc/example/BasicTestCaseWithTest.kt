package jorander.blackboxtstpoc.example

import jorander.blackboxtstpoc.core.*
import jorander.blackboxtstpoc.sut.*
import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalArgumentException

class BasicTestCaseWithTest {

    @Suppress("MemberVisibilityCanPrivate")
    class BasicTestCase(
            val customer: TestPerson = TestPerson("SEARCH_CRITERIA_2")
    ) : TestCase("A basic test case to demonstrate usage.") {
        override fun test(): TestScript {
            var documentId: DocumentId? = null
            var caseId: CaseId? = null

            return TestScript
                    .include<Pair<DocumentId, CaseId>>(ReceiveDocument(customer)) { (dId, cId) ->
                        println("documentId = $dId")
                        documentId = dId
                        println("caseId = $cId")
                        caseId = cId
                    }
                    .step("Clerk marks document as final and closes case") {
                        assertNotNull("DocumentId should not be null", documentId)
                        val document = loadDocument(documentId!!)
                        assertEquals("Document should be new", DocumentStatus.NEW, document.status)
                        val updatedDocument = document.copy(status = DocumentStatus.FINAL)
                        storeDocument(updatedDocument)
                        assertEquals("Case should be closed", CaseStatus.CLOSED, loadCase(caseId!!).status)
                    }
        }
    }


    @Test
    fun runHappyPath() {
        val CUSTOMERS_PNR = "123456789"
        val CASE_ID = CaseId("case1")
        var case = Case(CASE_ID, CaseStatus.OPEN)
        val DOC_ID = DocumentId("doc1")
        BasicTestCase().run(object : TestToolboxImpl() {
            override fun findTestPerson(searchCriteria: String): TestPersonValues {
                assertTrue("Search criteria should include criteria from included test", searchCriteria.contains("SEARCH_CRITERIA_1"))
                assertTrue("Search criteria should include criteria from this test", searchCriteria.contains("SEARCH_CRITERIA_2"))
                return TestPersonValues(CUSTOMERS_PNR)
            }

            override fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?> {
                assertTrue("Document should contain customer pnr", xmlDoc.contains(CUSTOMERS_PNR))
                return Pair(DOC_ID, CASE_ID)
            }

            override fun loadCase(caseId: CaseId): Case {
                assertEquals(CASE_ID, caseId)
                return case
            }

            override fun loadDocument(documentId: DocumentId): Document {
                assertEquals(DOC_ID, documentId)
                return Document(documentId, DocumentStatus.NEW)
            }

            override fun storeDocument(document: Document) {
                assertEquals(DocumentStatus.FINAL, document.status)
                case = case.copy(status = CaseStatus.CLOSED)
            }
        })
    }

    @Test(expected = IllegalArgumentException::class)
    fun runWithExceptionThrownFromSystemUnderTest() {
        BasicTestCase().run(object : TestToolboxImpl() {
            override fun findTestPerson(searchCriteria: String): TestPersonValues {
                return TestPersonValues("Some PNR")
            }

            override fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?> {
                throw IllegalArgumentException("Business Error: Document not valid....")
            }
        })
    }

    @Test(expected = UnsupportedOperationException::class)
    fun runWithTestCaseToolboxImplNotFullyImplemented() {
        BasicTestCase().run(object : TestToolboxImpl() {
            override fun findTestPerson(searchCriteria: String): TestPersonValues {
                return TestPersonValues("Some PNR")
            }
            /*
            Intentionally not implemented to test what happens when implementation of a used funktion is forgotten.
            override fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?> { }
            */
        })
    }
}