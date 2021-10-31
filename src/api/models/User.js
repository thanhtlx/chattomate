import mongoose from "mongoose";

const schema = mongoose.Schema;

const userSchema = new schema(
  {
    idApi: {
      type: String,
      default: "",
    },
    name: {
      type: String,
      required: true,
    },
    avatarUrl: {
      type: String,
      required: false,
      default: "",
    },
    phone: {
      type: String,
      required: true,
    },
    email: {
      type: String,
      required: true,
    },
    password: {
      type: String,
      required: true,
    },
    friends: [
      {
        type: schema.Types.ObjectId,
        ref: "friends",
      },
    ],
    conversations: [
      {
        type: schema.Types.ObjectId,
        ref: "user_conversations",
      },
    ],
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const User = mongoose.model("users", userSchema);

export default User;
