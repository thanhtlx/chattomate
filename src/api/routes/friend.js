import express from "express";
import auth from "../middlewares/Auth.middleware";
import FriendController from "../controllers/Friend.controller";

const router = express.Router();

router.use(auth);

// get alls friends
router.get("/", FriendController.getFriends);

router.get("/pending", FriendController.getFriendsPending);

// request so request minh da gui
router.get("/request", FriendController.getFriendsRequest);

//send friend
router.post("/", FriendController.sendFriendRequest);

// update
router.put("/:id/accept", FriendController.acceptFriendRequest);

router.put("/:id/change-nickname", FriendController.changeNickname);

//delete friend
router.delete("/:id", FriendController.removeFriend);

export default router;
