# LazyInit
LazyInit is a helper class that provides lazy initlization of singleton object feature.<br>
Example:<br>
Template is the object I need to create. <br>

The LazyInit accepts Supplier as an argument.<br>
<code>LazyInit&lt;Template&gt; lazy = LazyInit.of(Template::new);</code>

The lazy object is thread safe and the Template object returned by the lazy instance is always same.<br>
<code>Template template = lazy.getOrCreate();</code><br>
<code>template.print();</code>
