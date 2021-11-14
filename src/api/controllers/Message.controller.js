// import
import sendMessageValidation from "../validations/message.validation";
import MessageService from "../services/message.service";

class MessageController {
  static async getMessages(req, res) {
    const userID = req.user._id;
    const conversationID = req.params.conversation_id;
    const messages = await MessageService.getAllMessage(conversationID);
    const result = messages.filter((message) => {
      const deleteBys = message.deleteBy.map((user) => user.toString());
      return !deleteBys.includes(userID);
    });
    return res.send({ status: "success", data: result });
  }

  static async sendMessage(req, res) {
    const userID = req.user._id;

    const { error } = sendMessageValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    // coi nhu doan nay minh upload xong roi nhe, roi check may cai khac

    if (req.body.files) {
      const name = "/public/uploads";
      const file = req.files.file;
      const pathfile = __dirname + name + file.name;
      console.log(pathfile);
      file.mv(pathfile);
      req.body.contentUrl = pathfile;
      delete req.body["files"];
    }
    const data = req.body;
    data.sendBy = userID;
    const messaage = await MessageService.createMessage(data);
    console.log("err");
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
      .send({ status: "error", message: "permission denied!" });
  }
}

export default MessageController;
