package jorander.blackboxtstpoc.core

import jorander.blackboxtstpoc.sut.Case
import jorander.blackboxtstpoc.sut.CaseId
import jorander.blackboxtstpoc.sut.Document
import jorander.blackboxtstpoc.sut.DocumentId

interface TestToolbox {
    fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?>
    fun loadCase(caseId: CaseId): Case
    fun loadDocument(documentId: DocumentId): Document
    fun storeDocument(document: Document)
}

open class TestToolboxImpl : TestToolbox {
    open fun findTestPerson(searchCriteria: String): TestPersonValues {
        throw UnsupportedOperationException("This method is used but not implemented.")
    }

    override fun receiveDocument(xmlDoc: String): Pair<DocumentId, CaseId?> {
        throw UnsupportedOperationException("This method is used but not implemented.")
    }

    override fun loadCase(caseId: CaseId): Case {
        throw UnsupportedOperationException("This method is used but not implemented.")
    }

    override fun loadDocument(documentId: DocumentId): Document {
        throw UnsupportedOperationException("This method is used but not implemented.")
    }

    override fun storeDocument(document: Document) {
        throw UnsupportedOperationException("This method is used but not implemented.")
    }
}