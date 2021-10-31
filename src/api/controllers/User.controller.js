// import
import UserService from "../services/user.service";
import userValidation from "../validations/user.validation";

import bcrypt from "bcryptjs";

class UserController {
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
}

export default UserController;
