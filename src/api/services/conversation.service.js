import Conversation from "../models/Conversation";
import UserConversation from "../models/UserConversation";
import UserService from "./user.service";
import NotifyService from "./notify.service";
import * as Config from "../socket/config";

class ConversationService {
  static async getConversations(id) {
    const userConversations = await UserConversation.find({
      user: id,
    }).populate("conversation");

    const data = [];
    userConversations.map((conversation) => {
      data.push(FriendService.getInfoConversation(conversation));
    });

    return data;
  }

  static async checkNotify(userID, conversationID) {
    const userConversation = await UserConversation.findOne({
      user: userID,
      conversation: conversationID,
    });
    if (userConversation) {
      console.log(userID);
      console.log(userConversation);
      console.log(userConversation.notify);
      return userConversation.notify;
    }
    return false;
  }

  static getInfoConversation(userConversation) {
    const data = (({ spam, notify }) => ({
      spam,
      notify,
    }))(userConversation);
    const conversation = userConversation.conversation;
    const data2 = (({
      _id,
      name,
      backgroundURI,
      emoji,
      ghim,
      admins,
      members,
    }) => ({
      _id,
      name,
      backgroundURI,
      emoji,
      ghim,
      admins,
      members,
    }))(conversation);
    return { ...data, ...data2 };
  }

  static async createConversation(
    members,
    admins,
    name = "",
    message = "new conversation"
  ) {
    // check ton tai roi thi thoi
    const membersID = members.map((member) => member._id);
    const conversation = await Conversation.create({
      name: name,
      admins: admins,
      members: membersID,
      isPrivate: membersID.length == 2,
    });
    members.forEach(async (member) => {
      const userConversation = await UserConversation.create({
        user: member._id,
        conversation: conversation._id,
      });
      member.conversations.push(userConversation);
      await member.save();
      await NotifyService.notify(Config.CHANNEL_NEW_CONVERSATION, member._id, {
        message: message,
        data: conversation,
      });
    });
    return conversation;
  }

  static async updateConversation(conversation, data) {
    if (data.name) {
      conversation.name = data.name;
    }
    if (data.backgroundURI) {
      conversation.backgroundURI = data.backgroundURI;
    }
    if (data.emoji) {
      conversation.emoji = data.emoji;
    }
    if (data.ghim) {
      conversation.ghim = data.ghim;
    }
    await ConversationService.saveConversation(conversation);
    const members = conversation.members;
    for (var member of members) {
      NotifyService.notify(
        Config.CHANNEL_CONVERSATION_CHANGE,
        member.toString(),
        {
          message: "conversation change",
          data: conversation,
        }
      );
    }
    return conversation;
  }

  static async findID(id) {
    return await Conversation.findById(id);
  }

  static async saveConversation(conversation) {
    return await conversation.save();
  }

  static async deleteConversation(conversation) {
    // await UserConversation.deleteMany({ conversation: conversation._id });
    // await conversation.delete();
    return true;
  }

  static async addMembers(conversation, members, message) {
    await Promise.all(
      await members.map(async (member) => {
        if (member in conversation.members) return;

        let userConversation = await UserConversation.findOne({
          user: member,
          conversation: conversation._id,
        });
        if (userConversation) {
          return;
        } else {
          userConversation = await UserConversation.create({
            user: member,
            conversation: conversation._id,
          });
        }

        const user = await UserService.findID(member);
        user.conversations.push(userConversation._id);
        await UserService.saveUser(user);
        conversation.members.push(member);
        await ConversationService.saveConversation(conversation);
        await NotifyService.notify(Config.CHANNEL_NEW_CONVERSATION, user._id, {
          message: message,
          data: conversation,
        });
      })
    );
    return conversation;
  }

  static async removeMember(conversation, userID) {
    // remove from conversation member
    for (let member of conversation.members) {
      if (member.toString() == userID) {
        console.log(conversation.members.indexOf(member));
        conversation.members.splice(conversation.members.indexOf(member));
        await ConversationService.saveConversation(conversation);
      }
    }
    return conversation;
  }
  // fix 
  static async checkPrivateConversationExisted(members) {
    members.sort();
    const conversation = await Conversation.findOne({
      members: members,
      isPrivate: true,
    });
    console.log(conversation);
    return conversation;
  }
}

export default ConversationService;
