'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('Pickly/notificationRequests/{user_id}/{notification_id}').onWrite((change, context) => {

  let noti = event.data.current.val();
  let from = noti.from;
  let to = noti.to;
  let statue = noti.statue;
  let orderid = noti.orderid;

  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;
  console.log('We have a notification to : ', user_id);

  const fromUser = admin.database().ref(`Pickly/notificationRequests/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('You have new notification from  : ', from_user_id);


	const sendName = admin.database().ref(`Pickly/users/${from}/name`).once('value');
    const deviceToken = admin.database().ref(`Pickly/users/${to}/device_token`).once('value');
    const orderTo = admin.database().ref(`Pickly/orders/${orderid}/DName`).once('value');

    return Promise.all([sendName, deviceToken]).then(result => {
      const userName = result[0].val();
      const token_id = result[1].val();
      console.log('notifying ' + to + ' about ' + statue + ' from ' + userName + '  ' + from + ' order id ' + orderid );

    var badgeCount = 1;
      const payload = {
        notification: {
          title : sendName,
          body: sendName + 'has ' + statue + ' your order',
          icon: "default",
          click_action : "com.armjld.eb3tly_TARGET_NOTIFICATION",
          badge: badgeCount.toString()
        },
        data : {
          from : from,
          'title': sendName,
          'body' : sendName + 'has ' + statue + ' your order'
        }
      };
    badgeCount++;

      return admin.messaging().sendToDevice(token_id, payload).then(response => {
        console.log('This was the notification Feature');
		return null;
      });

    });

  });

});