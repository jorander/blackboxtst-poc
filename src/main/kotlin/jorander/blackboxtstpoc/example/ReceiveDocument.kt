package jorander.blackboxtstpoc.example

import jorander.blackboxtstpoc.core.TestCase
import jorander.blackboxtstpoc.core.TestPerson
import jorander.blackboxtstpoc.core.TestScript
import jorander.blackboxtstpoc.sut.CaseId
import jorander.blackboxtstpoc.sut.CaseStatus
import jorander.blackboxtstpoc.sut.DocumentId
import org.junit.Assert.*

@Suppress("MemberVisibilityCanBePrivate")
class ReceiveDocument(
        val customer: TestPerson = TestPerson { "SEARCH_CRITERIA_1" },
        val relatedCustomer: TestPerson = TestPerson { "SEARCH_CRITERIA_RELATED: ${customer.values.pnr}" },
        private val year: Int = 2017
) : TestCase("A simple test case that can be run separately or included into a larger test case.") {

    private lateinit var documentId: DocumentId
    private var caseId: CaseId? = null

    override fun test() = TestScript
            .step("Customer sends in document") {
                val (docId, cId) = receiveDocument("fake document from customer: ${customer.values.pnr} for year: $year. Related customer: ${relatedCustomer.values.pnr}")
                documentId = docId
                caseId = cId
            }
            .step("Validate document results in open case") {
                assertNotNull("CaseId should not be null", caseId)
                val case = loadCase(caseId!!)
                assertEquals("Case should be open", CaseStatus.OPEN, case.status)
            }
            .returns { Pair(documentId, caseId!!) }


    override fun testSuite(): List<TestCase> {
        return (1999..2018).map { ReceiveDocument(year = it) }
    }
}