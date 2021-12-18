import express from "express";
import auth from "../middlewares/Auth.middleware";
import MessageController from "../controllers/Message.controller";


const router = express.Router();

router.use(auth);

// get alls messages
router.get("/:conversation_id",MessageController.getMessages);

//send message
router.post("/", MessageController.sendMessage);

// update location 
router.put("location/:messageID", MessageController.updateLocation);
// get location 
router.get("/location/:messageID", MessageController.getLocation);

//  how to delete 
router.put("/:messageID",MessageController.deleteMessage);

//remove message 
router.delete("/:messageID", MessageController.destroyMessage);



export default router;
