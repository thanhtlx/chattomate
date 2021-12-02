import Message from "../models/Message";
import ConversationService from "./conversation.service";
import NotifyService from "./notify.service";
import * as Config from "../socket/config";
import UserService from "./user.service";

class MessageService {
  static async getAllMessage(conversationID) {
    let result = await Message.find({
      conversation: conversationID,
    }).populate("sendBy");
    if (result == null) return result;
    result = result.map(
        function (message) {
          message.sendBy = UserService.getInfoUser(message.sendBy);
          return message;
        }
    );
    return result;
  }

  static async findID(messageID) {
    return await Message.findById(messageID);
  }

  static async createMessage(data) {
    const message = await Message.create(data);
    const user = await UserService.findID(message.sendBy);
    const conversationID = message.conversation;
    const conversation = await ConversationService.findID(conversationID);
    if (!conversation) return null;
    let dataNotify = Object.assign({}, message)._doc;
    dataNotify.sendBy = UserService.getInfoUser(user);
    for (var member of conversation.members) {
      member = member.toString();
      if (member == message.sendBy._id.toString()) continue;
      const isNotify = await ConversationService.checkNotify(
        member,
        conversationID
      );
      if (isNotify)
        NotifyService.notify(Config.CHANNEL_NEW_MESSAGE, member, message);
    }
    return message;
  }
}

export default MessageService;
