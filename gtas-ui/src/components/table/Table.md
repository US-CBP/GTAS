Table example:

The table component can accept either a data array, a uri, or a function that fetches data. It will attempt to auto-format the headers, or you can optionally pass in an array of strings representing the header text for each column.

```js

function cb(e) {
  console.log(e);
}

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

<div>
  <div className="box">
    <Table
      callback={cb}
      title="Table with a data array"
      data={fruits}
      id="table1"
    />
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
</div>;
```
