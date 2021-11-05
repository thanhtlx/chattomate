import mongoose from "mongoose";

const schema = mongoose.Schema;

const userSchema = new schema(
  {
    content: {
      type: String,
      required: false,
      default: "",
    },
    contentUrl: {
      type: String,
      required: false,
      default: "",
    },
    duration: {
      type: Number,
      required: false,
      default: 0,
    },
    type: {
      type: Number,
      required: true,
    },
    deleteBy: [
      {
        type: schema.Types.ObjectId,
        ref: "users",
      },
    ],
    seenBy: [
      {
        type: schema.Types.ObjectId,
        ref: "users",
      },
    ],
    sendBy: {
      type: schema.Types.ObjectId,
      ref: "users",
    },
    conversation: {
      type: schema.Types.ObjectId,
      ref: "conversations",
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const Message = mongoose.model("messages", userSchema);

export default Message;
