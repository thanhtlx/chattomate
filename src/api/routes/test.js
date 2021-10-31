import express from "express";
import guest from "../middlewares/Guest.middleware";
import auth from "../middlewares/Auth.middleware";
import UserService from "../services/user.service";

const router = express.Router();

router.get("/auth", auth, async (req, res) => {
  return res.send({
    message: "authenticated",
    data: await UserService.findID(req.user._id),
  });
});
router.get("/guest", guest, (req, res) => {
  return res.send("u are guest");
});

export default router;
