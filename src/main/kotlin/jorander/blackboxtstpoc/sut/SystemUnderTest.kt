package jorander.blackboxtstpoc.sut

/*
    This file contains a dummy domain of the System Under Test (SUT).
    The domain objects are used in type signatures of TestToolbox and
    in the sample test cases implemented in the tests of this project.
*/

data class DocumentId(val id: String)
enum class DocumentStatus{NEW, FINAL}
data class Document(val id: DocumentId, val status: DocumentStatus)

data class CaseId(val id: String)
enum class CaseStatus{OPEN, CLOSED}
data class Case(val id: CaseId, val status: CaseStatus)