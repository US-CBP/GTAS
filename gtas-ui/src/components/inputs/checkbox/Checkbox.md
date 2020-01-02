Checkbox and Radio examples:

```js
import Checkbox from "./Checkbox.js";

const cb = () => {};

<section className="section card message is-info">
  <div>
    <Checkbox
      name="cb1"
      inputType="checkbox"
      alt="cb1"
      inputVal="Mangoes"
      callback={cb}
    />
    <Checkbox
      name="cb2"
      inputType="checkbox"
      alt="cb2"
      inputVal="Carrots"
      callback={cb}
    />
    <Checkbox
      name="cb3"
      inputType="checkbox"
      alt="cb3"
      inputVal="Onions"
      callback={cb}
      selected="true"
    />
  </div>
  <br />
  <div>
    <Checkbox name="r1" inputType="radio" alt="r1" inputVal="Orange" callback={cb} />
    <Checkbox name="r2" inputType="radio" alt="r2" inputVal="Yellow" callback={cb} />
    <Checkbox
      name="r3"
      inputType="radio"
      alt="r3"
      inputVal="Blue"
      callback={cb}
      selected="true"
    />
  </div>
</section>;
```

---

---
