Requires:

- an **`entity`** prop with a post or get method. Entities are defined in serviceWrapper.js.
- an **`id`** prop.
- an **`callback`** prop to return data to the parent.
- child components containing data to have a **`datafield`** prop. Children can be standard html elements or components.

EXAMPLE

```js
import LabelledInput from "../labelledInput/LabelledInput.js";

const fetch = {
  post: async function(param) {
    return {};
  },
  get: async function(param) {
    return {};
  }
};

function cb(e) {}

<div className="card column is-4 is-offset-4">
  <FilterForm
    title="Student Filter Form"
    service={fetch.get}
    id="loginform"
    callback={cb}
  >
    <LabelledInput
      inputType="text"
      datafield
      labelText="Name"
      name="name"
      callback={cb}
      alt="name"
    />
    <LabelledInput
      inputType="number"
      datafield
      labelText="Age"
      name="age"
      callback={cb}
      alt="age"
    />
    <LabelledInput
      inputType="text"
      datafield
      labelText="School"
      name="school"
      callback={cb}
      alt="school"
    />
    <LabelledInput
      inputType="number"
      datafield
      labelText="Grade"
      name="grade"
      callback={cb}
      alt="grade"
    />
  </FilterForm>
</div>;
```

DATAFIELD PROP -
tells FilterForm.js to track and submit the component's data, and to use the **`datafield`** or **`name`** prop as the field name. The **`datafield`** prop can be assigned the name of the entity field it represents if the **`name`** prop requires a different name. For example, if you are saving data to a Widget entity with a "widgetName" field, but the child component holding the widgetName data has its **`name`** prop set to "component1", you can:

leave the component **`name`** as is and set **`datafield`** to "widgetName"

```jsx static
  <mychildcomponent name="component1"   datafield="widgetName" ... />
```

add the **`datafield`** prop as a boolean and change the component name to "widgetName"

```jsx static
  <mychildcomponent name="widgetName" datafield ... />
```

omit the **`name`** prop and set **`datafield`** to the field name "widgetName"

```jsx static
<mychildcomponent  datafield="widgetName" ... />
```

CALLBACK PROP -
is used to return data fetched from the supplied service to the parent component. This data can then be fed to another component contained by the parent.

ERROR -
If **`datafield`** is present but neither **`name`** nor **`datafield`** resolves to a string, the FilterForm will toss an error.

---

---
