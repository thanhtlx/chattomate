import mongoose, { Schema } from "mongoose";
const schema = mongoose.Schema;

const userSchema = new schema(
  {
    name: {
      type: String,
      required: true,
    },
    backgroundURI: {
      type: String,
      required: false,
    },
    emoji: {
      type: String,
      required: false,
    },
    ghim: {
      type: Schema.Types.ObjectId,
      ref: "messages",  
    },
    members: [
      {
        type: Schema.Types.ObjectId,
        ref: "users",
      },
    ],
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const User = mongoose.model("conversations", userSchema);

export default User;
