import { mongoose } from "mongoose";
const schema = mongoose.Schema;

const userSchema = new schema(
  {
    content: {
      type: String,
      required: false,
    },
    contentUrl: {
      type: String,
      required: false,
    },
    duration: {
      type: Number,
      required: false,
    },
    type: {
      type: Number,
      required: true,
    },
    delete: {
      type: Boolean,
      required: false,
      default: false,
    },
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
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const User = mongoose.model("messages", userSchema);

export default User;
