import { mongoose } from "mongoose";

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
        type: Schema.Types.ObjectId,
        ref: "users",
      },
    ],
    seenBy: [
      {
        type: Schema.Types.ObjectId,
        ref: "users",
      },
    ],
    sendBy: {
      type: Schema.Types.ObjectId,
      ref: "users",
    },
    conversations: {},
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const Message = mongoose.model("messages", userSchema);

export default Message;
