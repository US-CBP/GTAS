<!--

## Input basic types are text, select, checkbox, and file input


# text
<LabelledInput lblText='text' inputType='text' inputVal={this.state.txt2} name='text' callback={this.onTextChange} alt='nothing' readOnly='readOnly' />

# file upload
<LabelledInput lblText='File Upload' inputType='file' name='fileupload' callback={() => {}} alt='nothing' options={['image/*', 'application/msword', '.pdf']} />

# select
<LabelledInput lblText='New Input' inputType='select' name='newinput' id='newinput' callback={this.onChange}
selected={[this.state.txt2]} alt='nothing' options={['one', 'two', 'three']} />

#checkbox
<LabelledInput lblText='New Input' inputType='select' name='newinput' id='newinput' callback={this.onChange}
selected={[this.state.txt2]} alt='nothing' options={['one', 'two', 'three']} />
 -->

LabelledInputs example:

```js
import LabelledInput from "./LabelledInput.js";

const cb = () => {};
<section>
  <div>
    <LabelledInput
      name="chkboxfield"
      className="checkbox"
      callback={cb}
      labelText="Do you have access to a checkbox field?"
      inputType="checkbox"
      inputVal="Yes I do"
      alt="Checkbox is available"
      selected="true"
    />
    <LabelledInput
      name="selectfield"
      labelText="Three select options"
      inputType="select"
      inputVal="possibly"
      alt="Dropdown with 3 options"
      options={[
        { value: "true", label: "True" },
        { value: "false", label: "False" },
        { value: "maybe", label: "Maybe, maybe not" }
      ]}
      callback={cb}
      alt="Three selections"
    />
    <LabelledInput
      name="txtfield"
      className="checkbox"
      callback={cb}
      alt="text field input"
      labelText="Text field"
      inputType="text"
      inputVal="text field text"
      selected="true"
    />
    <LabelledInput
      name="textareafield"
      className="textarea"
      callback={cb}
      alt="Textarea input"
      labelText="Textarea field"
      inputType="textarea"
      inputVal="textarea field text"
    />
  </div>
</section>;
```

---

---
