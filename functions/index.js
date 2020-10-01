'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
/*exports.sendNotification = functions.database.ref('Pickly/notificationRequests/{user_id}/{notification_id}').onCreate((snap, context) => {
  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;
  
  let noti = snap.val();
  let from = noti.from;
  let to = noti.to;
  let statue = noti.statue;
  let orderid = noti.orderid;
  let action = noti.action;
  let uName = noti.uName;
  
  const deviceToken = admin.database().ref(Pickly/users/${to}/device_token).once('value');
  return deviceToken.then(result => {
  const token_id = result.val();
	
  if (!result.exists() || result.val() === "") return false;
  
  const payload = {
          notification: {
            title : uName,
            body: statue,
            icon: "default",
            click_action : "com.armjld.eb3tly.Notifications_Notifications",
          },
    
  data : {
          'title': uName,
          'body' : statue,
          'orderid' : orderid,
		  'action' : action,
          'sendby' : from,
          'sendto' : to,
		  'type' : 'normal'
          }
       };

  return admin.messaging().sendToDevice(token_id, payload).then(response => {
      console.log('This was the notification Feature');
      return null;
    }); 
  });
});*/

exports.fawrypayment = functions.database.ref('Pickly/fawrypayments/{payment_id}').onCreate((snap, context)=>{
    console.log('Money added');   
    const payment_id = context.params.payment_id;
	
	const paymentphone = snap.child("phone").val();
    const fawrymony = snap.child("money").val();
	
    var ref = admin.database().ref("Pickly/users");
    ref.orderByChild('phone').equalTo(paymentphone).once("value", function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
		
        let uid = childSnapshot.key;
        var money = childSnapshot.child("walletmoney").val();
		
        var lastmoney = Number(fawrymony) + Number(money);
		
		var lref = admin.database().ref('Pickly/users');
		if(Number(lastmoney) >= 0){
			lref.child(uid).update({
			  'walletmoney': lastmoney,
			  'currentDate':'none'
			});
        } else {
			lref.child(uid).update({
			  'walletmoney': lastmoney
			});
        }
      });
    }); 
  });
  
exports.fawrycode = functions.database.ref('Pickly/fawrycode/{fawry_id}').onCreate((snap, context)=>{
    const fawryid = context.params.fawry_id;
    var userid = snap.child("uId").val();
	
    var db = admin.database();
    var buf = db.ref("Pickly/users");
    buf.child(userid).update({
      'fawrycode':Fawryid
    });
  });
  
exports.chatNoti = functions.database.ref('Pickly/chatRooms/{room_id}/{message_id}').onCreate(async(snap, context) => {
  
  const _reciverid = snap.child('reciverid').val();
  const senderid = snap.child('senderid').val();
  const __msg = snap.child('msg').val();
  
  console.log(senderid + ' : ' + _reciverid + ' : ' + __msg);
  
  const deviceToken = admin.database().ref(`Pickly/users/${_reciverid}/device_token`).once('value');
  const fromName = admin.database().ref(`Pickly/users/${senderid}/name`).once('value');
  

  return Promise.all([deviceToken, fromName]).then(result => {
  const token_id = result[0].val();
  const userName = result[1].val();

  console.log('This was the Chat notification From : ' + userName + ' the message : ' + token_id);
  
  if(token_id === null || token_id === "") return false;
  
  const payload = {
          notification: {
            title : userName,
            body: __msg,
            icon: "default",
            click_action : "com.armjld.eb3tly.Home_HomeActivity",
          },
	data : {
          'title': userName,
          'body' : __msg,
		  'type' : 'chat'
          }
       };
	   
  return admin.messaging().sendToDevice(token_id, payload).then(response => {
	    console.log('this was a chat notification featured');
		return null;
	});
  });
	
});