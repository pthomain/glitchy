# glitchy
Make sense of your API errors

Ever had an API call exception?
-------------------------------

If so was it:

- a network availability / IO error?
- a parsing error with one of the models?
- an serialised error returned from the application layer of the back-end?
- an HTTP error?
- a NPE?

If you've replied yes to more than one of the above then you probably have some error handling code that will check what type of exception was caught and whether you can gracefully recover from it.

And because this code is mostly redundant boilerplate, it's probably contained in a single class that generically handles the errors of all your calls. If that's the case then well done, you don't need this library 😄 

You're still here?
------------------

Then you might be interested in what this library has to offer. It provides a way to intercept any exception emitted by an RxJava call on a Retrofit client and to route them via a factory that will convert them into a single type you can easily reason about. 

But, why?
---------

It's easier to abstract and group your exceptions by type (i.e. network, api, parsing, etc) and let the caller handle them by their type. For instance, you might want to make a distinction between errors that can be recovered (network issues) and those that can't (parsing issue, 404) and notify the user accordingly.

More
----

This library was originally part of __DéjàVu__ (https://github.com/pthomain/dejavu) but is now also available separately for developers interested only in the error handling part. Check it out if you're also interested in add a cache implementation to your network layer 😉

