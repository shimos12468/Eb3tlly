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

    const deviceToken = admin.database().ref(`Pickly/users/${to}/device_token`).once('value');

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

exports.fawrypayment = functions.database.ref('Pickly/fawrypayments/{payment_id}').onCreate((snap, context)=>{
	
		console.log('Money added');		
		const fawrycode = context.params.payment_id;
		const fawrymony = snap.child("money").val();
		
		console.log(fawrycode);		
		var ref = admin.database().ref("Pickly/users");
		
		ref.orderByChild('fawrycode').equalTo(fawrycode).on("value", function(snapshot) {
			snapshot.forEach(function(childSnapshot) {
			  
				let uid = childSnapshot.key;
				var money = childSnapshot.child("walletmoney").val();
				var lastmoney = fawrymony - money;
				
				console.log(money);
				console.log(lastmoney);
				console.log(uid);
			
				var lref = admin.database().ref('Pickly/users');
				lref.child(uid).update({
					'walletmoney': lastmoney
				});
			});
		});	
	});

exports.fawrycode = functions.database.ref('Pickly/fawrycode/{fawry_id}').onCreate((snap, context)=>{
	    console.log('Code added');
		const fawryid = context.params.fawry_id;
		var userid = snap.child("uId").val();
		var db = admin.database();
		var buf = db.ref("Pickly/users");
		buf.child(userid).update({
			'fawrycode':Fawryid
		});
	});