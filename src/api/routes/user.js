import express from "express";
import auth from "../middlewares/Auth.middleware";
import UserController from "../controllers/User.controller";

const router = express.Router();

router.use(auth);

// edit password, name, avt , ....
router.put("/", UserController.updateUser);

export default router;
