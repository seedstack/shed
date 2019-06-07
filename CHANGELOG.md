# Version 1.1.6 (2019-??-??)

* [new] Add an executor implementing a simple retrying strategy after exception. 
* [new] Adds the posiility to priorize a list of Objects by its class at `PriorityUtils`
* [chg] Updated caffeine to 2.7.0 from 2.6.0
* [chg] Updated parent-internal to 3.4.6 from 3.4.4

# Version 1.1.5 (2018-12-03)

* [fix] Avoid duplicates when searching for annotations under certain conditions.  
* [chg] Avoid returning synthetic methods and constructors in reflection operations.  

# Version 1.1.4 (2018-12-03)

* [chg] Built and tested with OpenJDK 11 (minimum Java version still being 8).

# Version 1.1.3 (2018-05-03)

* [fix] Fix infinite recursion when scanning Kotlin annotations.

# Version 1.1.2 (2018-02-14)

* [chg] Java 9 compatibility.
* [fix] Also catch `NoClassDefFoundException` on `Classes.optional()`.

# Version 1.1.1 (2017-11-28)

* [new] Add simple caching API.
* [new] Helpers for throwing consumers, suppliers, functions and bi-function.
* [chg] Better error info on instantiateDefault.  
* [fix] Fix bad throw in some throwing classes.

# Version 1.1.0 (2017-07-31)

* [new] Add no-dependency LRU cache.
* [new] Add `classImplements` predicate.
* [new] Add basic utilities for field/method reflection.
* [new] Add the ability to create a default instance of a class.
* [new] Add utilities to work with types.
* [new] Add utilities to work with priorities.
* [chg] Reflection performance improvements.

# Version 1.0.2 (2017-04-29)

* [chg] Extracted interface for StandardAnnotationResolver.
 
# Version 1.0.1 (2017-01-16)

* [fix] Fix reference to snapshot parent pom. 

# Version 1.0.0 (2016-12-12)

* [new] Initial version.
