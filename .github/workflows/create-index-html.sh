#!/bin/bash

b=""
for h in $(ls *.html); do
  t=$(xmllint --html --xpath '/html/head/title/text()' $h 2>/dev/null)

  read -d '' card << EOT
<div class="card m-3" style="width: 18rem;">
  <div class="card-body">
    <h5 class="card-title">$t</h5>
    <a href="$h" class="btn btn-primary">View</a>
  </div>
</div>
EOT
  b="$b$card"
done

cat <<EOF > index.html
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
  <title>JUG-IN Talks</title>
</head>
<body>
  <h1 class="m-5 text-center">Talks of the Java User Group Ingolstadt e.V.</h1>
  <div class="d-flex flex-wrap">
    $b
  </div>
</body>
</html>
EOF