import mongoose from "mongoose";

const schema = mongoose.Schema;

const userSchema = new schema(
  {
    name: {
      type: String,
      default: "",
    },
    backgroundURI: {
      type: String,
      default: "",
    },
    emoji: {
      type: String,
      default: "",
    },
    noted: {
      type: String,
      default: "",
    },
    ghim: {
      type: schema.Types.ObjectId,
      ref: "messages",
    },
    admins: [
      {
        type: schema.Types.ObjectId,
        ref: "users",
      },
    ],
    members: [
      {
        type: schema.Types.ObjectId,
        ref: "users",
      },
    ],
    isPrivate: {
      type: Boolean,
      default: true,
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const Conversation = mongoose.model("conversations", userSchema);

export default Conversation;
