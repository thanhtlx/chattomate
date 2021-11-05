import express from "express";
import auth from "../middlewares/Auth.middleware";
import MessageController from "../controllers/Message.controller";


const router = express.Router();

router.use(auth);

// get alls messages
router.get("/:conversation_id",MessageController.getMessages);

//send message
router.post("/", MessageController.sendMessage);

//  how to delete 
router.put("/:messageID",MessageController.deleteMessage);

//remove message 
router.delete("/", MessageController.destroyMessage);



export default router;
