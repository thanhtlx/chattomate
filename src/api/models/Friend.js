import mongoose from "mongoose";
const schema = mongoose.Schema;

const userSchema = new schema(
  {
    nickName: {
      type: String,
      required: false,
      default: "",
    },
    status: {
      type: Number, // -1 pending request, 0 friend, 1 send request
      required: true,
    },
    friend: {
      type: schema.Types.ObjectId,
      ref: "users",
    },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
  },
  { strict: false }
);

const Friend = mongoose.model("friends", userSchema);

export default Friend;
