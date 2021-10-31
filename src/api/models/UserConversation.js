import mongoose from "mongoose";

const schema = mongoose.Schema;

const userSchema = new schema(
  {
    user: {
      type: schema.Types.ObjectId,
      ref: "users",
    },
    conversation: {
      type: schema.Types.ObjectId,
      ref: "conversations",
    },
    nickName: {
      type: String,
      required: false,
    },
    spam: {
      // bỏ qua tin nhắn
      type: Boolean,
      required: false,
      default: false,
    },
    notify: {
      // tắt thông báo
      type: Boolean,
      required: false,
      default: true,
    },
    delete: {
      // quên mất để là gì rồi :(
      type: Boolean,
      default: false,
    },
    isBlock: {
      //kiểu bị đá ra khỏi cuộc trò chuyện
      //
      type: Boolean,
      default: false,
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const UserConversation = mongoose.model("user_conversations", userSchema);

export default UserConversation;
