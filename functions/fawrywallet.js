const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.fawrypayment = functions.database.ref('Pickly/fawrypayments').onCreate((snap, context)=>{
		const fawrymony = snap.child("money").val();
		const fawrycode = snap.val();
		var ref;
		return firebase.database().ref('Pickly/users').orderByChild('fawrycode').equalTo(fawrycode).then(function(snapshot){ 

			snapshot.forEach(function(ds){
				console.log(ds);
			let money  = ds.child('walletmoney').val();
			let lastmoney = fawrymony-money;
			let ref = firebase.database().ref('Pickly/users');
			let id = ds.child('id').val();
			console.log(money + "  " +lastmoney + "  "+id + "   "+ref);
			 ref.child(id).update({
					'walletmoney': lastmoney
			});
			});
			return snapshot;
		});


});
