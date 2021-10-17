import express from "express";
import AuthController from "../controllers/Auth.controller";
import guest from "../middlewares/Guest.middleware";
import auth from "../middlewares/Auth.middleware";
const router = express.Router();

router.post("/register", guest, AuthController.register);

router.post("/login", guest, AuthController.login);

router.get("/logout", auth, AuthController.logout);

export default router;
