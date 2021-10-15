import mongoose, { Schema } from "mongoose";
const schema = mongoose.Schema;

const userSchema = new schema(
  {
    user: {
      type: Schema.Types.ObjectId,
      ref: "users",
    },
    conversation: {
      type: Schema.Types.ObjectId,
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
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const User = mongoose.model("user_conversations", userSchema);

export default User;
