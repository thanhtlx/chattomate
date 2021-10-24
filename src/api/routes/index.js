import express from "express";
import message from "./message";
import user from "./user";
import auth from "./auth";
import test from "./test";
import friend from "./friend";
import conversation from "./conversation";

const app = express.Router();

app.use("/test", test);
app.use("/messages", message);
app.use("/users", user);
app.use("/friends", friend);
app.use("/auth", auth);
app.use("/conversations", conversation);

export default app;
