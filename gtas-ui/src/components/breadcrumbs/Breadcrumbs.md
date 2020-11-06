Not currently using this since our page hierarchy is relatively flat, but
below is a non-functional example of how it might look.

It takes a **`uri`** prop and will format and render the crumbs for that path. Here, we are passing a **`uri`** of **/gtas/admin/settings**, which in most cases would be passed via **`props.uri`** by the router.

Breadcrumbs example:

```js
<div>
  <Breadcrumbs uri="/gtas/admin/settings" />
</div>
```

---

---
