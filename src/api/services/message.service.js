import Message from "../models/Message";
import ConversationService from "./conversation.service";
import NotifyService from "./notify.service";
import * as Config from "../socket/config";
import UserService from "./user.service";

class MessageService {
  static async getAllMessage(conversationID) {
    return await Message.find({ conversation: conversationID });
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
    console.log("send by ");
    console.log(message.sendBy);
    let dataNotify = Object.assign({}, message)._doc;
    dataNotify.sendBy = UserService.getInfoUser(user);
    for (var member of conversation.members) {
      member = member.toString();
      if (member == message.sendBy.toString()) continue;
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
