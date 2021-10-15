import express from "express";
import auth from "../middlewares/Auth.middleware";


const router = express.Router();

router.use(auth);
router.post("/login", (req, res) => {
  console.log("login");
});

export default router;
