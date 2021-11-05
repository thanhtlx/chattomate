// import
import sendMessageValidation from "../validations/message.validation";
import MessageService from "../services/message.service";

class MessageController {
  static async getMessages(req, res) {
    const userID = req.user._id;
    const conversationID = req.params.conversation_id;
    const messages = await MessageService.getAllMessage(conversationID);
    return messages.filter((message) => {
      return !(userID in message.deleteBy);
    });
  }

  static async sendMessage(req, res) {
    const { error } = sendMessageValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    if (req.body.file) {
      // save file to publice gan vao contentURL
    }
    const data = req.body;
    data.push({
      sendBy: userID,
    });
    const messaage = await MessageService.createMessage(data);
    if (messaage) {
      return res.send({ status: "success", data: messaage });
    }
    return res
      .status(404)
      .send({ status: "error", messaage: "can't send message" });
  }

  static async deleteMessage(req, res) {
    const messageID = req.params.messageID;
    const message = await MessageService.findID(messageID);
    message.deleteBy.push(req.user._id);
    await message.save();
    return res.send({ status: "success", data: message });
  }

  static async destroyMessage(req, res) {
    const messageID = req.params.messageID;
    const message = await MessageService.findID(messageID);
    if (message.sendBy == req.user._id) {
      await message.delete();
      return res.send({ status: "success", data: message });
    }
    return res
      .status(400)
      .send({ status: "error", message: "permistion deneil" });
  }
}

export default MessageController;
