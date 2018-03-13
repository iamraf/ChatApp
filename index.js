'use strict'

/**
 * This is a part of ChatApp Project (https://github.com/h01d/ChatApp)
 * Licensed under Apache License 2.0
 *
 * @author  Raf (https://github.com/h01d)
 * @version 1.1
 * @since   27/02/2018
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Notifications/{user_id}/{notification_id}').onWrite(event => {
    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;

    if(!event.data.val())
    {
        return console.log('A Notification has been deleted from the database : ', notification_id);
    }

    const fromUser = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');

    return fromUser.then(fromUserResult => 
    {
        const from_user_id = fromUserResult.val().from;
        const type = fromUserResult.val().type;
    
        const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = admin.database().ref(`/Users/${user_id}/token`).once('value');

        return Promise.all([userQuery, deviceToken]).then(result =>
        {
            const userName = result[0].val();
            const token_id = result[1].val();
            
            if(type == "request")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a Friend Request",
                        body: `${userName} wants to be your friend!`,
                        icon: "default",
                        click_action : "com.github.h01d.chatapp_PROFILE_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${from_user_id}) sent friend request to ${user_id}`);
                });
            }
            else if(type == "message")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a new Message",
                        body: `${userName} messaged you!`,
                        icon: "default",
                        click_action : "com.github.h01d.chatapp_CHAT_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${from_user_id}) send a message to ${user_id}`);
                });
            }
            else if(type == "accept")
            {
                const payload = 
                {
                    data: 
                    {
                        title : "You have a new friend",
                        body: `${userName} accepted your request!`,
                        icon: "default",
                        click_action : "com.github.h01d.chatapp_PROFILE_TARGET_NOTIFICATION",
                        from_user_id : from_user_id
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response => 
                {
                    console.log(`${userName} (${user_id}) accepted request by ${from_user_id}`);
                });
            }
        });
    });
});
