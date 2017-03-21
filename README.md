# slf4j-lazy-params

Provides lazy evaluation for SLF4J replacement parameters, for example when one or more values might be
relatively expensive to compute such as serializing an object to JSON for DEBUG logging but not at higher levels.

Just statically import methods from `Slf4jParameterSupplier` and use them to wrap one or more replacement
parameters in the same logging statement. Note specifically you do _not_ need to make all the parameters
lazy.

Examples:

```java
// Explicitly create the parameter supplier and pass it to the lazy method
Supplier<String> jsonSupplier = () -> jsonHelper.toJson(thing);
LOG.debug("Thing {} took {} millis to fetch. JSON value: {}",
        thing.getId(), fetchMillis, lazy(jsonSupplier));

// Pass a Supplier as a lambda to the lazy method for one or more parameters
LOG.debug("Thing {} took {} millis to fetch. JSON value: {}",
        thing.getId(), fetchMillis, lazy(() -> jsonHelper.toJson(thing)));

// The delayed method is just an alias for lazy if you prefer that name over lazy
LOG.debug("Thing {} took {} millis to fetch. JSON value: {}",
        thing.getId(), fetchMillis, delayed(() -> jsonHelper.toJson(thing)));


// If thingToJson converts the thing to JSON it can be a simple lambda... 
LOG.debug("Thing {} took {} millis to fetch. JSON value: {}",
        thing.getId(), fetchMillis, lazy(() -> thingToJson()));

// ...or a method reference
LOG.debug("Thing {} took {} millis to fetch. JSON value: {}",
        thing.getId(), fetchMillis, lazy(this::thingToJson));
```
