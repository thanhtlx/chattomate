import express from "express";
import auth from "../middlewares/Auth.middleware";

const router = express.Router();

router.use(auth);
router.get("/", (req, res) => {
  console.log("login");
});

export default router;
