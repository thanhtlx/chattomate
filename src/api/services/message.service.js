import User from "../models/User";
import bcrypt from "bcryptjs";

class UserService {
  static async checkUserExist(email) {
    return await User.findOne({ email: email });
  }

  static async saveUser(data) {
    var salt = bcrypt.genSaltSync(10);
    const user = new User({
      name: data.name,
      phone: data.phone,
      email: data.email,
      password: bcrypt.hashSync(data.password, salt),
    });
    try {
      const saveUser = await user.save();
      return saveUser;
    } catch (error) {
      return error;
    }
  }

  static async findID(id) {
    return await User.findById(id);
  }
}

export default UserService;
