const express = require("express");
const app = express();
const port = 3000;

// config env
require("dotenv").config();

app.get("/", (req, res) => {
  res.send(process.env.APP_NAME);
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});
