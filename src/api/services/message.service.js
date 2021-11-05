import Message from "../models/Message";

class MessageService {
  static async getAllMessage(conversationID) {
    return await Message.find({ conversation: conversationID });
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
    return await Message.findById(id);
  }
}

export default MessageService;
