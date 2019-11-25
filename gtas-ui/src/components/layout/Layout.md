<!-- # These components build very simple, statelesss, style-only elements.

# SingleColumn and DoubleColumn are default patterns for pages with single or double column layouts

# Under SingleColumn, all children widgets will be stacked with each in its own row
<MyPage>
  <SingleColumn>
    <WidgetContainer/>
    <WidgetContainer/>
  </SingleColumn>
</MyPage>

# Under DoubleColumn, the widget containers will align next to each other
<MyPage>
  <DoubleColumn>
    <WidgetContainer/>
    <WidgetContainer/>
  </DoubleColumn>
</MyPage>

# The Column Component just sets the column width of an individual column using the boostrap col-x convention
# Setting width to 6 (below), will nest the widget in a div with the className "col-6 align-self-center"
  <Column width="6">
    <Widget/>
    <Widget/>
    <Widget/>
  </Column>

# You can combine them like this:
<MyPage>
  <DoubleColumn>
    <Column width="3">
      <WidgetSearch1/>
      <WidgetImage2/>
    </Column>
    <Column width="6"/>
      <WidgetForm1/>
    </Column>
  </DoubleColumn>
</MyPage> -->
