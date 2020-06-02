<img src="https://github.com/pthomain/glitchy/blob/master/github/glitchy-header.png" style="height: 256px; width: auto;"/>

TL;DR
-----

This library provides interceptors to generically handle and categorise your Retrofit / RxJava exceptions in 2 types: 

- Handled exceptions that can be handled gracefully in the UI (e.g. IO exceptions)
- Unhandled exceptions that should be reported and from which the app might not be able to recover (e.g. NPEs).

How does it work?
-----------------

This library provides a way to create a Retrofit call adapter factory on which call interceptors can be registered. This allows for a centralised way to intercept all your network calls (`Single` or `Observable`) and to apply composition logic on them. 
One of the usages of this is to provide a centralised way to handle all Retrofit exceptions and 2 interceptors are provided to do just that.

An `ErrorInterceptor` allows you to define a custom exception of type `E` to act as a wrapper to the originally caught exception.
You can add any type of metadata fields to your custom exception and implement a factory for the conversion of the original exception to your custom type. 
The library also provide a default `ErrorFactory` implementation (`GlitchFactory`) that handles common cases and wraps original exceptions in a `Glitch` wrapper exception.

Handled exceptions (as defined in your `ErrorFactory` implementation) are wrapped in an `Outcome.Error<E>` object and delivered via `onNext()` while unhandled exceptions are delivered via the normal `onError()` mechanism and can be dealt with using an RxJava uncaught error handler.

But, why?
---------

It's easier to abstract and group your exceptions by type (i.e. network, API, parsing, config, etc) and let the subscriber handle them accordingly. For instance, you might want to make a distinction between errors that can be recovered (network issues) and those that can't (parsing issue, 404, coding error).

The motive here is to define a different logic for unhandled exceptions, such as tracking them and displaying a specific UI to the user. On the other hand, handled exceptions (wrapped in `Outcome.Error`) can provide additional context to the UI to help choose the appropriate action to present to the user, such as prompting to check for connectivity and offering a retry option.

Set up
------

Coming soon... (see app for implementation)

More
----

This library was originally part of __DejaVu__ (https://github.com/pthomain/dejavu) but is now also available separately for developers interested only in the call composition part. Check it out if you're also interested in adding a cache layer to your API calls 😉


