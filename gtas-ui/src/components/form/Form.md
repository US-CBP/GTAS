Requires:

- an **`entity`** prop with a post method for Add, or with get and put methods for Edit. Entities are defined in serviceWrapper.js.
- an **`id`** prop for Edit mode.
- child components containing data to have a **`datafield`** prop. Children can be standard html elements or components.

EXAMPLE

```js

import LabelledInput from "../labelledInput/LabelledInput.js";

const fetch = {
  put: async function(id) {
    return {};
  },
  get: async function(id) {
    return {};
  }
};

function cb(e) {}

<div className="column is-4 is-offset-4">
  <Form
    title="Important Form"
    submitText="GO"
    service={fetch}
    action="edit"
    id="loginform"
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
  </Form>
</div>;
```

DATAFIELD PROP -
tells Form.js to track and submit the component's data, and to use the **`datafield`** or **`name`** prop as the field name. The **`datafield`** prop can be assigned the name of the entity field it represents if the **`name`** prop requires a different name. For example, if you are saving data to a Widget entity with a "widgetName" field, but the child component holding the widgetName data has its **`name`** prop set to "component1", you can:

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

ERROR -
If **`datafield`** is present but neither **`name`** nor **`datafield`** resolves to a string, the Form will toss an error.

ADD/EDIT MODES -
the Form's default mode is Add, but you can change it to Edit by setting the **`action`** prop to "edit" and providing an **`id`** prop.

```jsx static
<mychildcomponent datafield="widgetName" action="edit" ... />
```

---

---
