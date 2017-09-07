package jorander.blackboxtstpoc.core

class TestPerson(searchCriteria: () -> String) {
    var searchCriteria = searchCriteria
        private set

    fun addSearchCriteria(otherCriteria: () -> String) {
        if (!searchCriteria().contains(otherCriteria())) {
            searchCriteria =  searchCriteria.let { firstCriteria -> {firstCriteria() + " " + otherCriteria()} }
        }
    }

    var values = TestPersonValues("dummy")
}

data class TestPersonValues(val pnr: String)