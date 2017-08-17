# blacKboxtst
This is a small proof-of-concept (PoC) to show to me and others that [Kotlin](https://kotlinlang.org/) can be used to build a small framework containing a DSL to specify and run black box tests towards a *system under test* (SUT).

This PoC only contains the most inner parts of such a framework. In order to be usable it would need to be expanded so that all tests are instantiated into [jUnit](http://junit.org/junit4/) tests and run as a suit. It would also need expansion of the TestToolbox suited for the SUT and an implementation och the interface that actually calls the system.

Even in this small size the PoC proves that the idea works and also demonstrates use of some great Kotlin features.

## Valuable Kotlin features
* Closures over mutable values - In Java any value enclosed in a lambda closure needs to be "effectively final". In Kotlin they can be *var*s and thereby modifiable. This forms the basis for sharing values between test steps.
* Explicit null handling - Makes the code clearer. Utilized in sharing of values between test steps.
* Lambda syntax - The lambda syntax in Kotlin seems to be targeted towards dsl-writing. Makes the test cases easy to read.
* Extension methods - This feature, especially function literals with receiver, is a great tool for dsl-crafting. Used in test steps.
* Scope control - The more flexible scope control for classes and functions can be utilized to limit the number of suggestions showing up in code completion when using a dsl. In this case when writing test cases.

## Benefits of this framework/dsl (demonstrated by the PoC)
* Test cases are defined in code, in this case Kotlin code, giving the test designer lots of tools and support through the IDE, preferably [InjelliJ IDEA](https://www.jetbrains.com/idea/).
* The test framework can be defined separately from the SUT and the test cases.
* Test cases can be reused by inclusion into other test cases.
* Each test case, even when included in another test case, defines the test data it needs. When included, needs of the different test cases are accumulated into one test data definition.
* Test cases can be unit tested by stubbing the TestToolbox. This minimises the risk of programming errors in designing the tests.

## Possible improvements and additions
* List of tests to be run in suites can be set up as List&lt;TestCase&gt;, hence being compilable and statically checked for existence.

Any comments, suggestions and improvement proposals are greatly welcome.