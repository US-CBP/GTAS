Table example:

The table component can accept either a data array, a uri, or a function that fetches data. It will attempt to auto-format the headers, or you can optionally pass in an array of strings representing the header text for each column.

```js
const cb = e => {};

const fruits = [
  {
    Fruit: "Cherry",
    Favorites: "122",
    Origin: "China",
    BestKnownFor: "Sundays and cobbler"
  },
  {
    Fruit: "Grapes",
    Favorites: "208",
    Origin: "Iran",
    BestKnownFor: "Soda, juice, and bubblegum"
  },
  {
    Fruit: "Lime",
    Favorites: "76",
    Origin: "Suriname",
    BestKnownFor: "Keylime pie and preventing scurvy"
  },
  {
    Fruit: "Pineapple",
    Favorites: "217",
    Origin: "Mozambique",
    BestKnownFor: "Upsidedown cake and Spongebob"
  },
  {
    Fruit: "Pumpkin",
    Favorites: "186",
    Origin: "USA",
    BestKnownFor: "Halloween, spice, and inedible pie"
  },
  {
    Fruit: "Apple",
    Favorites: "386",
    Origin: "India",
    BestKnownFor: "Pie, juice, and sauce"
  }
];

const casing = [
  {
    "Title Cased": "Title Cased",
    camelCased: "camelCased",
    "text-with-dashes": "text-with-dashes",
    text_with_underscores: "text_with_underscores",
    "lower cased": "lower cased",
    UPPERCASED: "UPPERCASED"
  }
];

<div className="section card message is-danger">
  <div className="box">
    <Table callback={cb} title="Table with a data array" data={fruits} id="table1" />
  </div>
  <br />
  <div className="box">
    <Table
      callback={cb}
      title="Autoformatted Casing on Headers"
      data={casing}
      id="table2"
    />
  </div>
  <br />
</div>;
```

You can also limit the visible columns by passing an array to the **`header`** prop:

```js
import Table from "./Table.js";

const cb = e => {};
const allData = [
  {
    first_field: "first field",
    second_field: "second field",
    third_field: "I will not be displayed",
    fourth_field: "4th field! Skipped the 3rd field",
    fifth_field: "I will not be displayed either",
    sixth_field: "Skipped the 5th field"
  },
  {
    first_field: "1st field",
    second_field: "2nd field",
    third_field: "I will not be displayed",
    fourth_field: "4th field! Skipped the 3rd field",
    fifth_field: "I will not be displayed either",
    sixth_field: "Skipped the 5th field"
  },
  {
    first_field: "first field again",
    second_field: "second field again",
    third_field: "I will not be displayed again",
    fourth_field: "4th field! Skipped the 3rd field again",
    fifth_field: "I will not be displayed either again",
    sixth_field: "Skipped the 5th field again"
  }
];

const someColumns = [
  { Accessor: "first_field" },
  { Accessor: "second_field" },
  { Accessor: "fourth_field" },
  { Accessor: "sixth_field" }
];

<div className="section card message is-danger">
  <div className="box">
    <Table
      callback={cb}
      title="My dataset has 6 Cols, but I am only showing 4"
      data={allData}
      header={someColumns}
      id="table2"
    />
  </div>
</div>;
```
