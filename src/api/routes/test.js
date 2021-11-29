import express from "express";
import guest from "../middlewares/Guest.middleware";
import auth from "../middlewares/Auth.middleware";
import UserService from "../services/user.service";
import sendFcmMessage from "../services/fcm";

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

router.post("/fcm", guest, (req, res) => {
  sendFcmMessage({
    message: {
      token: req.body.token,
      data: {},
      android: {
        direct_boot_ok: true,
      },
    },
  });
  return res.send("ok");
});

router.post("/a-fcm", auth, async (req, res) => {
  const user = await UserService.findID(req.user._id);
  user.fcm.map((token) => {
    sendFcmMessage({
      message: {
        token: token,
        data: {},
        android: {
          direct_boot_ok: true,
        },
      },
    });
  });

  return res.send("ok");
});

export default router;
