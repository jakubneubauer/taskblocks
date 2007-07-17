define(ITEM,
   <li>
     `ifelse(CURRENT_FILE,$1,<b>,<a href="$1">)'
     $2
     `ifelse(CURRENT_FILE,$1,</b>,</a>)'
   </li>
)


<div style="text-align:center;">
  <h1>Task Blocks</h1>
</div>

<div class="header">
  <ul>
  ITEM(index.html, Main Page)
  ITEM(screenshots.html, Screenshots)
  ITEM(download.html, Download)
  </ul>
</div>
