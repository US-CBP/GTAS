File input examples:

```js
import "bulma/css/bulma.css";
import File from "./File.js";

const cb = () => {};

<section className="section card message is-primary">
  <div>
    <File
      name="cb1"
      alt="File input control"
      inputType="file"
      onChange={cb}
      options={["jpg", "gif", "txt"]}
    />
  </div>
</section>;
```

---

---
