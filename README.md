<img src="https://github.com/pthomain/glitchy/blob/master/github/glitchy-header.png" style="height: 256px; width: auto;"/>

Ever had to handle an API exception?
------------------------------------

If so was it:

- a network availability / IO error?
- a parsing error with one of the models?
- an serialised error returned from the application layer of the back-end?
- an HTTP error?
- a coding error?
- an NPE?

If you've replied yes to more than one of the above then you probably have some error handling code that will check the type of the caught exception and whether you can gracefully recover from it.

And because this code is mostly redundant boilerplate, it's probably contained in a single class that generically handles the errors of all your calls. If that's the case then well done, you don't need this library 😄 

What does this library offer?
-----------------------------

This library primarily provides a way to register interceptors on your Retrofit RxJava calls (Single and Observable) and to compose on them.

This allows for decorating network calls for the purpose of caching or error handling.

Out of the box, a default error interceptor will catch Rx exceptions and reroute them via a factory that will convert them into a single custom type you can easily reason about (called Glitch, although you can create your own).  

But, why?
---------

It's easier to abstract and group your exceptions by type (i.e. network, API, parsing, config, etc) and let the caller handle them by their type. For instance, you might want to make a distinction between errors that can be recovered (network issues) and those that can't (parsing issue, 404, coding error) and notify the user accordingly.

Set up
------

Coming soon...

More
----

This library was originally part of __DéjàVu__ (https://github.com/pthomain/dejavu) but is now also available separately for developers interested only in the call composition part. Check it out if you're also interested in adding a cache layer to your API calls 😉


