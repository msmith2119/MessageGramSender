package com.msmith.messagegramsender;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by morgan on 8/17/16.
 */
public class ToolBarActivityLauncher {

    public static void handleToolbarSelection(Activity activity, int id) {
        Intent intent = null;
        switch (id) {

            case R.id.main_activity:
                Toast.makeText(activity, "Home selected", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                break;
            // action with ID action_refresh was selected
            case R.id.contacts_activity:
                Toast.makeText(activity, "Contacts selected", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(activity, ContactListActivity.class);
                activity.startActivity(intent);
                break;
            // action with ID action_settings was selected
            case R.id.messages_activity:
                Toast.makeText(activity, "Messages selected", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(activity, MessageListActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.send_activity:
                Toast.makeText(activity, "Send selected", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(activity, SendMessageActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.quick_send_activity:
                Toast.makeText(activity, "Quick Send  selected", Toast.LENGTH_SHORT)
                        .show();
                intent = new Intent(activity, QuickSendActivity.class);
                activity.startActivity(intent);
                break;
            default:
                break;
        }

    }
}
