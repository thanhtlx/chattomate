import express from "express";
import auth from "../middlewares/Auth.middleware";
import UserController from "../controllers/User.controller";

const router = express.Router();

router.use(auth);

router.get("/", UserController.getAllUser);
// edit password, name, avt , ....
router.put("/", UserController.updateUser);

router.post("/register-fcm", UserController.registerFCM);
router.post("/un-register-fcm", UserController.unRegisterFCM);


export default router;
