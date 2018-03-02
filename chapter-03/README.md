## Chapter 3 notes

Netty's networking abstraction is represented by:

* `Channel` - simplifies complexity of working directly with sockets.
* `EventLoop` - defines core abstraction for handling events that occur during lifetime of a connection. It is bound to a single `Thread` for its lifetime.
* `ChannelFuture` - a `Future` that notifies attached listener when operation has completed.

### `ChannelHandler` and `ChannelPipeline` - flow and exection

#### `ChannelHandler`

* container for all application logic
* handles inbound and outbound data
* methods are triggered by network events
* application business logic often resides in one or more `ChannelInboundHandler`s

#### `ChannelPipeline`

* container for a chain of `ChannelHandler`s
* defines an API for propagating the flow of inbound and outbound events along the chain
* creating `Channel` assigns it to `ChannelPipeline`



