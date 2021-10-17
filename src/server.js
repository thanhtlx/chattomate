import express from "express";
import http from "http";
import routes from "./api/routes";
import bodyParser from "body-parser";
import mongoose from "mongoose";
import cors from "cors";
import dotenv from "dotenv";

class Server {
  constructor() {
    dotenv.config();
    this.application = express();
    this.port = process.env.PORT || 3000;
    this.server = http.createServer(this.application);
    this.mongodb();
    this.socket();
    this.plugin();
    this.routes();
  }

  plugin() {
    this.application.use(cors());
    this.application.use(
      bodyParser.urlencoded({
        extended: true,
      })
    );
    this.application.use(bodyParser.json());
    this.application.use(express.json());
    this.application.use(express.static("public"));
  }

  socket() {}

  mongodb() {
    mongoose
      .connect(process.env.MONGO_URL, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
      })
      .then(() => console.log("Mongo connected"))
      .catch((err) => {
        console.log(err);
      });
  }

  routes() {
    this.application.use("/api", routes);
    this.application.use("/api*", (req, res, next) => {
      res.status(400).send({ message: "Ooops! not found." });
    });
  }

  run() {
    this.server.listen(this.port, () => {
      console.log(`Example app listening at http://localhost:${this.port}`);
    });
  }
}

export default new Server();
