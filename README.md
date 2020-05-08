<img src="https://github.com/pthomain/glitchy/blob/master/github/glitchy-header.png" style="height: 256px; width: auto;"/>

TL;DR
-----

This library provides interceptors to automatically categorise your Retrofit / RxJava exceptions in 2 types: 

- Handled exceptions that can be handled gracefully in the UI (e.g. IO exceptions)
- Unhandled exceptions that should be reported and from which the app might not be able to recover (e.g. NPEs).

How does it work?
-----------------

The library allows you to define a custom exception to act as a wrapper to the original thrown exception.
You can then add any type of metadata fields to your custom exception and provide a factory for the conversion of the original exception to your custom type.

Handled exceptions are wrapped in an `Outcome` object and delivered via `onNext()` while unhandled exceptions are delivered via `onError()` and can be dealt with using an RxJava uncaught error handler.

Alternatively, the default `onError()` mechanism can be maintained by not wrapping your response in an `Outcome`. In this case, you can handle your exceptions the usual way by checking if their type is the one you've defined in your factory.

Additionally, this library provides a way to register different types of interceptors on your Retrofit RxJava calls (Single and Observable) and to compose on them. 

But, why?
---------

It's easier to abstract and group your exceptions by type (i.e. network, API, parsing, config, etc) and let the caller handle them by their type. For instance, you might want to make a distinction between errors that can be recovered (network issues) and those that can't (parsing issue, 404, coding error) and notify the user accordingly.

Set up
------

Coming soon... (see app for implementation)

More
----

This library was originally part of __DejaVu__ (https://github.com/pthomain/dejavu) but is now also available separately for developers interested only in the call composition part. Check it out if you're also interested in adding a cache layer to your API calls 😉


