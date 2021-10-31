import express from "express";
import auth from "../middlewares/Auth.middleware";
import UserController from "../controllers/User.controller";

const router = express.Router();

router.use(auth);

router.post("/", UserController.updateUser);

// edit password, name, avt , ....
router.put("/");

export default router;
