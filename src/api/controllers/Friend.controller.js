// import
import FriendService from "../services/friend.service";
import UserService from "../services/user.service";
import ConversationService from "../services/conversation.service";
import {
  friendRequestValidation,
  changeNicknameValidation,
  acceptFriendValidation,
} from "../validations/friend.validation";

class FriendController {
  static async getFriends(req, res) {
    const id = req.user._id;
    const data = await FriendService.getAllFriendAccepted(id);
    if (!data) {
      return res.status(400).send({ status: "error", message: "error" });
    }
    return res.send({ status: "success", data: data });
  }

  static async getFriendsPending(req, res) {
    const id = req.user._id;
    const data = await FriendService.getAllFriendPendding(id);
    if (!data) {
      return res.status(400).send({ status: "error", message: "error" });
    }
    return res.send({ status: "success", data: data });
  }

  static async getFriendsRequest(req, res) {
    const id = req.user._id;
    const data = await FriendService.getAllFriendRequest(id);
    if (!data) {
      return res.status(400).send({ status: "error", message: "error" });
    }
    return res.send({ status: "success", data: data });
  }

  static async sendFriendRequest(req, res) {
    //validate
    const { error } = friendRequestValidation(req.body);
    if (error) {
      return res
        .status(400)
        .send({ status: "error", message: error.details[0].message });
    }
    const sender = req.user._id;
    const receiver = req.body.userId;
    //
    if (sender == receiver) {
      return res
        .status(400)
        .send({ status: "error", message: "Can't send request to itself" });
    }

    const result = await FriendService.createFriend(receiver, sender);
    if (result) {
      return res.send({ status: "success", data: result });
    }
    return res
      .status(400)
      .send({ status: "error", message: "friends requested" });
  }

  static async acceptFriendRequest(req, res) {
    const { error } = acceptFriendValidation(req.params);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }

    const acceptedID = req.user._id;
    const friendRequest = await FriendService.acceptFriend(
      acceptedID,
      req.params.id
    );
    if (!friendRequest) {
      res.status(400).send({ status: "error", message: "don't exist request" });
    }
    const userAccepted = await UserService.findID(acceptedID);
    const userFriend = await UserService.findID(friendRequest.friend);

    // create conversation
    if (
      !(await ConversationService.checkPrivateConversationExisted([
        userAccepted._id,
        userFriend._id,
      ]))
    ) {
      await ConversationService.createConversation(
        [userAccepted, userFriend],
        [acceptedID, friendRequest.friend.toString()]
      );
    }

    res.send({ status: "success", data: friendRequest });
  }

  static async changeNickname(req, res) {
    //validate
    const { error } = changeNicknameValidation({
      nickName: req.body.nickName,
      id: req.params.id,
    });
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }

    const nickName = req.body.nickName;
    const friendID = req.params.id;
    // check permistion
    const user = await UserService.findIDFriends(req.user._id);
    const friends = user.friends.filter((obj) => {
      return obj._id == friendID;
    });
    if (friends.length <= 0) {
      return res
        .status(400)
        .send({ status: "error", message: "Permission denied!" });
    }
    friends[0].nickName = nickName;
    await FriendService.save(friends[0]);
    return res.send({ status: "success", data: friends[0] });
  }

  static async removeFriend(req, res) {
    const { error } = acceptFriendValidation(req.params);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    const friendID = req.params.id;
    // check permistion
    const result = await FriendService.removeFriend(req.user._id, friendID);
    if (result) {
      return res.send({ status: "success", data: result });
    }
    return res
      .status(404)
      .send({ status: "error", data: "can't delete friend" });
  }
}

export default FriendController;
