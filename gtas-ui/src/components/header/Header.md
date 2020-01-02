**Global header **

Takes no params so far, and the nav items are hard-coded.

Could update this later to read from a json file to dynamically determine which items to display or, if we require a new layout for a right-to-left language, to order the items appropriately before rendering.

Header example:

```js
import "bootstrap/dist/css/bootstrap.min.css";

<section className="section card message is-success">
  <div>
    <Header origin="IAD" />
  </div>
</section>;
```

---

---
