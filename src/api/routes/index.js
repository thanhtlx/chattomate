import express from "express";
import message from "./message";
import user from "./user";
import auth from "./auth";
import test from "./test";
import conversations from "./conversations";

const app = express.Router();

app.use("/test", test);
app.use("/message", message);
app.use("/user", user);
app.use("/auth", auth);
app.use("/conversations", conversations);

export default app;
