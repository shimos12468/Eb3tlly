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
  let action = noti.action;
  let uName = noti.uName;

    const fromUser = admin.database().ref(`Pickly/notificationRequests/${user_id}/${notification_id}`).once('value');
	const sendName = admin.database().ref(`Pickly/users/${from}/name`).once('value');
    const deviceToken = admin.database().ref(`Pickly/users/${to}/device_token`).once('value');
    const orderTo = admin.database().ref(`Pickly/orders/${orderid}/DName`).once('value');

    return deviceToken.then(result => {
    if (!result.exists() || result.val() === "") return false;
	
	const token_id = result.val();
	
	const payload = {
    			notification: {
    			  title : uName,
    			  body: statue,
    			  icon: "default",
    			  click_action : "com.armjld.eb3tly.Notifications_Notifications",
    			},
    data : {
          'uName': uName,
          'statue' : statue,
          'orderid' : orderid,
		  'action' : action,
          'sendby' : from,
          'sendto' : to
          }
       };
	   

	return admin.messaging().sendToDevice(token_id, payload).then(response => {
    	console.log('This was the notification Feature');
    	return null;
    });	
	});

	
});