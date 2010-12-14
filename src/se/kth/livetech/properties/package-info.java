package se.kth.livetech.properties;

/**
 * A linked hierarchical property system with update notifications.
 * 
 * Properties can be linked to provide default values and inheritance.
 * 
 * Property lookup looks recursively up the hierarchy for the most specific value.
 * 
 * PropertyListener:s provide update events. They are weakly linked.
 * 
 * Property updates are link-aware.
 * 
 * Example:
 * 
 * a.b.c.foo = "Hello"
 * a.b.c.d.bar = "World 1"
 * a.b.c.d.baz = "World 3"
 * e.f.bar = "World 2"
 * x.y -> a.b
 * x.y.c.d -> e.f
 * 
 * x.y.c.foo looks at x.y.c.foo, (x.y->a.b).c.foo = "Hello"
 * x.y.c.d.bar looks at (x.y.c.d->e.f).bar = "World 2"
 * x.y.c.d.baz looks at (x.y.c.d->e.f).baz, (x.y->a.b).c.d.baz = "World 3"
 * 
 * An update of a.b.c.d.baz also tells listeners of:
 * a.b.c.d
 * a.b.c
 * a.b
 * x.y.c.d.baz
 * x.y.c.d
 * x.y.c
 * x.y
 * x
 * a
 */
