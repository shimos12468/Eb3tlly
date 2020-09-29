const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.fawrycode = functions.database.ref('Pickly/users').onCreate((snap, context)=>{
		var Fawryid = snap.key;
		var userid = snap.child("uId").val();
		var db = admin.database();
		var buf = db.ref("Pickly/users");
		buf.child(userid).update({
			'fawrycode':Fawryid
		});
	});


	
