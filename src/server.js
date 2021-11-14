import express from "express";
import http from "http";
import routes from "./api/routes";
import mongoose from "mongoose";
import cors from "cors";
import dotenv from "dotenv";
import * as socketio from "socket.io";
import mSocket from "./api/socket/socket";
import jwt from "jsonwebtoken";

class Server {
  constructor() {
    dotenv.config();
    this.application = express();
    this.plugin();
    this.port = process.env.PORT || 8888;
    this.server = http.createServer(this.application);
    this.socket();
    this.mongodb();
    this.routes();
  }

  plugin() {
    this.application.use(cors());
    this.application.use(express.urlencoded());
    this.application.use(express.json());
    this.application.use(express.static("public"));
  }

  socket() {
    const io = new socketio.Server(this.server);
    io.use((socket, next) => {
      if (!socket.handshake.query) {
        return;
      }
      let token = socket.handshake.query.token;
      try {
        const verified = jwt.verify(token, process.env.TOKEN_SECRET);
        socket.handshake.query.id = verified._id;
        return next();
      } catch (error) {
        console.log("Socket error");
      }
    });
    global.io = io;
    mSocket.init(global.io);
  }

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
