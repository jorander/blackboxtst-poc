package jorander.blackboxtstpoc.example

import jorander.blackboxtstpoc.core.TestPersonValues
import jorander.blackboxtstpoc.core.TestToolboxImpl
import jorander.blackboxtstpoc.sut.*
import org.junit.Assert.*
import org.junit.Test

class ReceiveDocumentTest{

    @Test
    fun runHappyPath() {
        val CUSTOMERS_PNR = "987654321"
        val CASE_ID = CaseId("case2")
        val case = Case(CASE_ID, CaseStatus.OPEN)
        val DOC_ID = DocumentId("doc2")

        ReceiveDocument().run(object : TestToolboxImpl() {
            override fun findTestPerson(searchCriteria: String): TestPersonValues {
                return if (searchCriteria == "SEARCH_CRITERIA_1") {
                    assertEquals("SEARCH_CRITERIA_1", searchCriteria)
                    TestPersonValues(CUSTOMERS_PNR)
                } else {
                    assertTrue("Search criteria should include criteria", searchCriteria.contains("SEARCH_CRITERIA_RELATED"))
                    assertTrue("Search criteria should include other customers pnr", searchCriteria.contains(CUSTOMERS_PNR))
                    TestPersonValues("8801012863")
                }
            }

            override fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?> {
                assertTrue("Document should contain customer pnr", xmlDoc.contains(CUSTOMERS_PNR))
                return Pair(DOC_ID, CASE_ID)
            }

            override fun loadCase(caseId: CaseId): Case {
                assertEquals(CASE_ID, caseId)
                return case
            }
        })
    }
}