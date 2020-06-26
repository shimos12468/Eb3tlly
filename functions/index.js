'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('Pickly/notificationRequests/{user_id}/{notification_id}').onCreate((snap, context) => {

  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;
  
  let noti = snap.val();
  let from = noti.from;
  let to = noti.to;
  let statue = noti.statue;
  let orderid = noti.orderid;

    const fromUser = admin.database().ref(`Pickly/notificationRequests/${user_id}/${notification_id}`).once('value');
	const sendName = admin.database().ref(`Pickly/users/${from}/name`).once('value');
    const deviceToken = admin.database().ref(`Pickly/users/${to}/device_token`).once('value');
    const orderTo = admin.database().ref(`Pickly/orders/${orderid}/DName`).once('value');


      //console.log('notifying ' + to + ' about ' + statue + ' from ' + userName + '  ' + from + ' order id ' + orderid );

	const payload = {
    			notification: {
    			  title : 'Eb3tly',
    			  body: 'لديك اشعار جديد',
    			  icon: "default",
    			  click_action : "com.armjld.eb3tly.Notifications",
    			},
    data : {
         'title': sendName + '',
          'body' : sendName + 'has ' + statue + ' your order',
          'statue' : statue,
          'orderid' : orderid,
          'sendby' : from,
          'sendto' : to
          }
       };

    return Promise.all([orderTo, deviceToken]).then(result => {
    //const userName = result[0].val();
    const token_id = result[1].val();
    return admin.messaging().sendToDevice(token_id, payload).then(response => {
    	console.log('This was the notification Feature');
    	return null;
        });
	});
});