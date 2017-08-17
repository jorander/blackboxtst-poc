package jorander.blackboxtstpoc.core

class TestPerson(searchCriteria: String) {
    var searchCriteria = searchCriteria
        private set

    fun addSearchCriteria(other: String){
        if (!searchCriteria.contains(other)){
            searchCriteria += (" " + other)
        }
    }

    lateinit var values: TestPersonValues
}

data class TestPersonValues(val pnr: String)