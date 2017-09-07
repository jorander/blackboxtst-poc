package jorander.blackboxtstpoc.core

import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

@Suppress("LeakingThis")
abstract class TestCase(val description: String = "No description") {

    open fun testSuite() = listOf(this)

    init {
        if (this::class.constructors.size != 1)
            throw IllegalStateException("Test case \"${this::class.simpleName}\" is only allowed to have one constructor.")

        this::class.constructors.first().parameters
                .onEach { if (it.name == null) throw IllegalStateException("All constructor parameters in test case must be named.") }
                .filter { it.type.isSubtypeOf(TestPerson::class.starProjectedType) }
                .forEach { if (!it.isOptional)  throw IllegalStateException("All parameters of TestPerson must have default values.")}
    }

    abstract fun test(): TestScript

    fun run(testToolboxImpl: TestToolboxImpl) {
        println("Executing test: ${this::class.qualifiedName}")
        println("Description: $description")

        val internalTestScript = test() as InternalTestScriptImpl

        declaredTestPersons(this)
                .onEach { (_, testPerson) -> testPerson.values = testToolboxImpl.findTestPerson(testPerson.searchCriteria()) }
                .forEach { (name, testPerson) -> println("Using: $name - ${testPerson.values.pnr}") }

        try {
            internalTestScript.execute(testToolboxImpl)
            println("-----------------------------")
            println()
            println("Test complete: Success!")
        } catch (t: Throwable) {
            println("Test throw exception: ${t.message}")
            t.printStackTrace(System.out)
            throw t
        } finally {
            println("--------TEST END-------------")
            println()
        }
    }
}

private fun declaredTestPersons(testCase: TestCase) =
        testCase::class.memberProperties.filter { it.returnType.isSubtypeOf(TestPerson::class.starProjectedType) }
                .map { Pair(it.name, it.getter.call(testCase) as TestPerson) }.toMap()

interface TestScript {
    companion object {
        fun step(description: String = "Executable step", code: TestToolbox.() -> Unit): TestScriptBuilder {
            return InternalTestScriptImpl().step(description, code)
        }

        fun <R> include(testCase: TestCase, callback: (R) -> Unit): TestScriptBuilder {
            return InternalTestScriptImpl().include(testCase, callback)
        }
    }
}

interface TestScriptBuilder : TestScript {

    fun step(description: String = "Executable step", code: TestToolbox.() -> Unit): TestScriptBuilder
    fun <R> include(testCase: TestCase, callback: (R) -> Unit): TestScriptBuilder
    fun returns(description: String = "", code: () -> Any): TestScript
}

private open class TestScriptImpl : TestScriptBuilder {
    private val steps: MutableList<TestScriptStep> = ArrayList()

    override fun step(description: String, code: TestToolbox.() -> Unit): TestScriptBuilder {
        steps.add(ExecutableStep(description, code))
        return this
    }

    override fun returns(description: String, code: () -> Any): TestScript {
        steps.add(Returns(description, code))
        return this
    }

    override fun <R> include(testCase: TestCase, callback: (R) -> Unit): TestScriptBuilder {
        val testCaseConstructor = testCase::class.constructors.first() // Can only have one

        val allConstructorParameters = testCaseConstructor.parameters
                .map { Pair(it.name!!, it) }.toMap() // All parameters are named

        val memberProperties = testCase::class.memberProperties
                .map { Pair(it.name, it.getter.call(testCase)) }.toMap()

        declaredTestPersons(testCase).forEach { (name, testPerson) ->
            testPerson
                    .addSearchCriteria(
                            declaredTestPersons(
                                    testCaseConstructor.callBy(allConstructorParameters
                                            .filterKeys { it != name }
                                            .map { (parameterName, parameter) -> Pair(parameter, memberProperties[parameterName]) }.toMap()
                                    )
                            )[name]!!.searchCriteria)
        }

        steps.add(Include(testCase, callback))
        return this
    }

    protected fun executeTestScriptSteps(testToolboxImpl: TestToolboxImpl): Any {
        return steps.map { it.execute(testToolboxImpl) }.last()
    }
}

private class InternalTestScriptImpl : TestScriptImpl() {

    fun execute(testToolboxImpl: TestToolboxImpl): Any {
        return executeTestScriptSteps(testToolboxImpl)
    }
}

private abstract class TestScriptStep(val description: String) {
    fun execute(testToolboxImpl: TestToolboxImpl): Any {
        if (description != "") {
            println("-----------------------------")
            println()
            println(description)
            println("-----------------------------")
        }
        return executeAction(testToolboxImpl)
    }

    abstract fun executeAction(testToolboxImpl: TestToolboxImpl): Any
}

private class ExecutableStep(description: String, val code: TestToolbox.() -> Unit) : TestScriptStep(description) {
    override fun executeAction(testToolboxImpl: TestToolboxImpl): Any {
        code(testToolboxImpl)
        return Any()
    }
}

private class Returns(description: String, val code: () -> Any) : TestScriptStep(description) {
    override fun executeAction(testToolboxImpl: TestToolboxImpl): Any {
        return code()
    }
}

private class Include<in R>(val testCase: TestCase, val callback: (R) -> Unit) : TestScriptStep("Including ${testCase::class.qualifiedName}") {
    @Suppress("UNCHECKED_CAST")
    override fun executeAction(testToolboxImpl: TestToolboxImpl): Any {
        val result = (testCase.test() as InternalTestScriptImpl).execute(testToolboxImpl)
        println()
        println("-----------------------------")
        println("Returning from ${testCase::class.qualifiedName}")
        callback(result as R)
        return Any()
    }
}