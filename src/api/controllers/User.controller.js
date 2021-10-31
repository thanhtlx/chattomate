// import
import UserService from "../services/user.service";
import FriendService from "../services/friend.service";
import ConversationService from "../services/conversation.service";

class UserController {
  static async updateUser(req, res) {
    const user = await UserService.findID(req.user._id);
    var keys = Object.keys(req.body);
    for (var i = 0; i < keys.length; i++) {
      user.keys[i] = req.body[keys[i]];
    }
    await UserService.saveUser(user);
    return res.send({ status: "success" });
  }
}

export default UserController;
