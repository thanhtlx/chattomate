// import
import sendMessageValidation from "../validations/message.validation";
import MessageService from "../services/message.service";
import NotifyService from "../services/notify.service";
import path from "path";
import fs from "fs";

const __dirname = path.resolve();

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

    if (req.body.file) {
      const name = "/public/uploads/";
      const file = Date.now(); 
      const data = req.body.file
      let pathfile = name + file + ".3gp";
      if(req.body.type == "4")
       pathfile =  name + file + ".png";
      
      await fs.writeFileSync(pathfile, data);
      req.body.contentUrl = name + file + ".png";
      delete req.body["file"]
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

  static async updateLocation(req, res) {
    const messageID = req.params.messageID;
    let message = await MessageService.findID(messageID);

    if (!message) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    if (message.type !== 7) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    console.log(message.sendBy);
    console.log(req.user._id);
    if (message.sendBy.toString() !== req.user._id) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    const lat = req.body.lat;
    const long = req.body.long;
    if (!lat || !long) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    message.content = lat + "," + long;
    await message.save();
    // notification
    NotifyService.notifyMapChange(message);
    return res.send({ status: "success", data: message.content });
  }
  static async getLocation(req, res) {
    const messageID = req.params.messageID;
    const message = await MessageService.findID(messageID);
    if (!message) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    if (message.type !== 7) {
      return res.status(400).send({ status: "error", message: "Not found!" });
    }
    return res.send({ status: "success", data: message.content });
  }
}

export default MessageController;
