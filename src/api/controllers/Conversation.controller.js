// import
import ConversationService from "../services/conversation.service";
import UserService from "../services/user.service";
import {
  createValidation,
  updateValidation,
  addMembersValidation,
  removeMemberValidation,
} from "../validations/conversation.validation";

class ConversationController {
  static async getConversations(req, res) {
    const conversations = await ConversationService.getConversations(
      req.user._id
    );
    if (conversations) {
      return res.send({ status: "success", data: conversations });
    }
    return res.status(400).send({ status: "error", message: "Unknown" });
  }

  static async createConveration(req, res) {
    // validation
    const { error } = createValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }

    const userID = req.user._id;
    if (
      await ConversationService.checkPrivateConversationExisted(
        req.body.members
      )
    ) {
      return res.status(400).send({
        status: "error",
        message: "Can't create conversation, because conversation existed!",
      });
    }

    const members = [];
    const errors = [];

    await Promise.all(
      await req.body.members.map(async (member) => {
        const user = await UserService.findID(member);
        if (user) {
          members.push(user);
        } else {
          errors.push(member);
        }
      })
    );
    if (errors.length > 0) {
      return res
        .status(400)
        .send({ status: "error", message: "User don't exist", data: errors });
    }
    const userCreated = await UserService.findID(userID);
    const conversation = await ConversationService.createConversation(
      members,
      [userID],
      "Cuộc hội thoại nhóm",
      userCreated.name + " đã tạo cuộc hội thoại"
    );

    // socket.io
    // new conversation
    return res.send({ status: "success", data: conversation });
  }

  static async updateConversation(req, res) {
    const { error } = updateValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }

    const userID = req.user._id;
    const conversationID = req.body.id;
    // check user is admin
    const conversation = await ConversationService.findID(conversationID);
    if (!(await ConversationController.checkPermission(conversation, userID))) {
      return res
        .status(400)
        .send({ status: "error", message: "you don't have admin" });
    }
    // socket.io notification

    const result = await ConversationService.updateConversation(
      conversation,
      req.body
    );
    return res.send({ status: "success", data: result });
  }

  static async deleteConversation(req, res) {
    const userID = req.user._id;
    const conversationID = req.params.id;
    const conversation = await ConversationService.findID(conversationID);
    if (await ConversationController.checkPermission(conversation, userID)) {
      const result = await ConversationService.deleteConversation(conversation);
      if (result) {
        return res.send({ status: "success" });
      }
    }
    return res.status(404).send({ status: "error" });
  }

  static async addMembers(req, res) {
    const { error } = addMembersValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    const userID = req.user._id;
    const conversationID = req.body.id;
    const members = req.body.members;
    const conversation = await ConversationService.findID(conversationID);
    const permission = await ConversationController.checkPermission(
      conversation,
      userID
    );
    if (!permission) {
      return res
        .status(404)
        .send({ status: "error", message: "you don't have admin" });
    }
    const userAdd = await UserService.findID(userID);
    const result = await ConversationService.addMembers(
      conversation,
      members,
      userAdd.name + " đã thêm bạn vào một cuộc trò chuyện"
    );
    return res.send({ status: "success", data: result });
  }

  static async removeMembers(req, res) {
    const { error } = removeMemberValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    const userID = req.user._id;
    const conversationID = req.body.id;
    const member = req.body.member;
    const conversation = await ConversationService.findID(conversationID);
    if (!(await ConversationController.checkPermission(conversation, userID))) {
      return res
        .status(404)
        .send({ status: "error", message: "you don't have admin" });
    }
    const result = await ConversationService.removeMember(conversation, member);
    return res.send({ status: "success", data: result });
  }

  static async checkPermission(conversation, userID) {
    for (let admin of conversation.admins) {
      if (userID == admin.toString()) return true;
    }
    return false;
  }
}

export default ConversationController;
