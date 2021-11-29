// import
import UserService from "../services/user.service";
import userValidation from "../validations/user.validation";

import bcrypt from "bcryptjs";

class UserController {
  static async getAllUser(req, res) {
    const users = await UserService.getAllUsers();
    return res.send({ status: "success", data: users });
  }

  static async updateUser(req, res) {
    const { error } = userValidation(req.body);
    if (error) {
      return res
        .status(404)
        .send({ status: "error", message: error.details[0].message });
    }
    var salt = bcrypt.genSaltSync(10);
    const user = await UserService.findID(req.user._id);
    var keys = Object.keys(req.body);
    for (var key of keys) {
      if (key == "password") {
        user[key] = bcrypt.hashSync(req.body[key], salt);
      } else user[key] = req.body[key];
    }
    await UserService.saveUser(user);
    return res.send({ status: "success", data: user });
  }

  static async registerFCM(req, res) {
    var user = await UserService.findID(req.user._id);
    const fcm_token = req.body.fcm_token;
    user.fcm.push(fcm_token);
    user = await UserService.saveUser(user);
    return res.send(user);
  }

  static async unRegisterFCM(req, res) {
    var user = await UserService.findID(req.user._id);
    const fcm_token = req.body.fcm_token;
    user.fcm = user.fcm.filter((string) => string != fcm_token);
    user = await UserService.saveUser(user);
    return res.send(user);
  }
}

export default UserController;
