// POST https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send
//
// chattomate - a91de;
// var admin = require("firebase-admin");

// var serviceAccount = require("path/to/serviceAccountKey.json");

// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount)
// });

import https from "https";
import google from "googleapis";
import key from "../../../auth.json";

const PROJECT_ID = "chattomate-a91de";
const HOST = "fcm.googleapis.com";
const PATH = "/v1/projects/" + PROJECT_ID + "/messages:send";
const MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
const SCOPES = [MESSAGING_SCOPE];

async function getAccessToken() {
  return new Promise(function (resolve, reject) {
    console.log(key);
    const jwtClient = new google.Auth.JWT(
      key.client_email,
      null,
      key.private_key,
      SCOPES,
      null
    );
    jwtClient.authorize(function (err, tokens) {
      if (err) {
        reject(err);
        return;
      }
      resolve(tokens.access_token);
    });
  });
}
// [END retrieve_access_token]

/**
 * Send HTTP request to FCM with given message.
 *
 * @param {object} fcmMessage will make up the body of the request.
 * fcmMessage 
 * {
  "Message":{
    "token" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
    "data": {
      "score": "5x1",
      "time": "15:10"
    },
    "android": {
      "direct_boot_ok": true,
    },
}
 */
function sendFcmMessage(fcmMessage) {
  getAccessToken().then(function (accessToken) {
    const options = {
      hostname: HOST,
      path: PATH,
      method: "POST",
      headers: {
        Authorization: "Bearer " + accessToken,
        "Content-Type": "application/json",
      },
    };

    const request = https.request(options, function (resp) {
      resp.setEncoding("utf8");
      resp.on("data", function (data) {
        console.log("Message sent to Firebase for delivery, response:");
        console.log(data);
      });
    });

    request.on("error", function (err) {
      console.log("Unable to send message to Firebase");
      console.log(err);
    });
    request.write(JSON.stringify(fcmMessage));
    request.end();
  });
}

export default sendFcmMessage;
