const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyNewBloodRequest = functions.firestore
  .document('bloodRequests/{requestId}')
  .onCreate(async (snap, context) => {
    const data = snap.data();
    const message = {
      notification: {
        title: 'New Blood Request',
        body: `Blood Group ${data.bloodGroup} requested at ${data.location}`
      },
      topic: 'blood_requests'
    };

    try {
      await admin.messaging().send(message);
    } catch (e) {
      console.error('Error sending FCM', e);
    }
  });
