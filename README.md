TL;DR
-----

Customisable RxJava / Coroutine Flow interceptors to register on Retrofit in order to handle and differentiate network call exceptions in 2 categories:

- Handled exceptions that can be handled gracefully in the UI (e.g. IO exceptions)
- Unhandled exceptions that should be reported and from which the app might not be able to recover (e.g. NPEs).

You can also wrap your success and failure responses in an `Outcome<T>` object (either `Outcome.Success<S>` or `Outcome.Error<E>`), which is similar to Arrow's `Either` or Kotlin's `Result`.

But, why?
---------

It's easier to abstract and group your exceptions by type (i.e. network, API, parsing, config, etc) and let the subscriber handle them accordingly. For instance, you might want to make a distinction between errors that can be recovered (network issues) and those that can't (parsing issue, 404, coding error).

The motive here is to define a different logic for unhandled exceptions, e.g. tracking them or displaying a specific UI to the user. 

On the other hand, handled exceptions can provide additional context to the UI to help choose the appropriate action to present to the user, such as prompting to check for connectivity and offering a retry option.

How does it work?
-----------------

This library can be used with or without Retrofit. It provides a set a of reactive interactors (RxJava / Coroutine Flow) that can be registered to operate in a predefined chained order on reactive streams. 

In practice, an interceptor assumes the role of a composer (e.g. `ObservableTransformer` in RxJava) but provides a common interface for both RxJava and Coroutine Flow implementations. 

Retrofit
--------

When used with Retrofit, the library provides a builder on which the interceptors are registered and returns a call adapter factory to be used to build Retrofit, instead of the default one. This provides a centralised way to intercept and compose all Retrofit calls (`Single<T>`/`Observable<T>` using the RxJava adapter or `Flow<T>` with the Coroutines one).

Retrofit interceptors receive metadata related to the return type of the call. Return type parsers can be used to define custom types (such as wrappers around the original type, like `Outcome<T>`).

Error Handling
--------------

An `ErrorInterceptor` allows you to define a custom exception to use as a wrapper to the original exception. You can define your custom exception to contain extra metadata fields and implement a factory for the conversion of the original exception to your custom type. 

This is helpful to handle error parsing logic for all your calls in the same class. In return, it simplifies exception handling in the receiver.

The library provides a default `ErrorFactory` implementation (`GlitchFactory`) that handles common cases and wraps original exceptions in a `Glitch` wrapper exception.

These exceptions are emitted using the default RxJava / Flow error mechanism. However you can choose to wrap your return type in an `Outcome<T>` in your Retrofit interface. Doing so will either emit a `Outcome.Success<S>` or an `Outcome.Error<E>` to the default RxJava / Flow success handlers.

Unhandled exceptions are always delivered via the normal `onError()` mechanism and can be dealt with using an RxJava uncaught error handler or using `catch` with Flow.

Set up
------

| Interceptors | | |
|--- |--- |--- |
| Core | Contains the interceptors only | ```implementation 'dev.pthomain.glitchy:core:3.2'``` |
| RxJava | Contains the Rx interceptors | ```implementation 'dev.pthomain.glitchy:rxjava:3.2'``` |
| Coroutines Flow | Contains the Flow interceptors | ```implementation 'dev.pthomain.glitchy:flow:3.2'``` |

| Retrofit | | |
|--- |--- |--- |
| Retrofit | Contains the Retrofit call adapter factory | ```implementation 'dev.pthomain.glitchy:retrofit:3.2'``` |
| Retrofit RxJava (1) | Contains the Rx Retrofit interceptors | ```implementation 'dev.pthomain.glitchy:retrofit-rxjava:3.2'``` |
| Retrofit Flow (2)| Contains the Flow Retrofit interceptors | ```implementation 'dev.pthomain.glitchy:retrofit-flow:3.2'``` |

The most common use case is to use Retrofit with RxJava or Coroutines Flow in which case you only need to add the dependency (1) or (2) to your Gradle setup.

(Documentation coming soon, see app for implementation)

More
----

This library was originally part of __DejaVu__ (https://github.com/pthomain/dejavu). Check it out if you're also interested in adding a cache layer to your API calls.

