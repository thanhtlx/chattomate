import express from "express";
import auth from "../middlewares/Auth.middleware";
import FriendController from "../controllers/Friend.controller";


const router = express.Router();

router.use(auth);

// get alls messages
router.get("/:conversation_id");

//send message
router.post("/", (req, res) => {
  console.log("login");
});

// 
router.put("/");

//delete message 
router.delete("/");

export default router;
