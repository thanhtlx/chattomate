import express from "express";
import auth from "../middlewares/Auth.middleware";
import ConversationController from "../controllers/Conversation.controller";

const router = express.Router();

router.use(auth);

// get alls conversations
router.get("/", ConversationController.getConversations);

//create conversation
router.post("/", ConversationController.createConveration);

// edit info conversations
router.put("/", ConversationController.updateConversation);

//delete conversations
// delete cai gi ? nguoi dung xoa
// => xoá toàn bộ message (ẩn đối với người xoá nhưng mọi người vẫn nhìn thấy )
// tính năng này đợi đến đoạn message
router.delete("/:id", ConversationController.deleteConversation);

router.put("/remove-members", ConversationController.removeMembers);

router.put("/add-members", ConversationController.addMembers);

export default router;
