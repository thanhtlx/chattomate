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
      type: Boolean,
      required: false,
      default: false,
    },
    notify: {
      type: Boolean,
      required: false,
      default: true,
    },
    delete: {
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
