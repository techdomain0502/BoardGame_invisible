package com.board.game.sasha.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.board.game.sasha.FrontPage;
import com.board.game.sasha.MainActivity;

/**
 * Created by sachin.c1 on 12-Aug-15.
 */
public class AlertDialogFactory {
    private Context context;
    private String type;

    public AlertDialogFactory(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public AlertDialog getDialog() {
        if (type.equalsIgnoreCase("FINISH")) {
            return new FinishDialog(context,
                    "Congratulations!! You Won the Game",
                    "Winner, Horray!!",
                    "Restart",
                    "Exit Game"
            ).getInstance();
        } else if (type.equalsIgnoreCase("EXIT")) {
            return new ExitDialog(context,
                    "Do you want to leave the Game?",
                    "Alert!!",
                    "Resume",
                    "Restart",
                    "Exit & Save").getInstance();
        }
        return null;
    }

    private class FinishDialog extends BaseDialog {

        public FinishDialog(final Context context, String Message, String Title, String positive, String negative) {
            super(context, Message, Title);
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).finish();
                        ((MainActivity) context).clearSavedGameState();
                    }
                    Intent intent = new Intent(getContext(), FrontPage.class);
                    context.startActivity(intent);

                }
            });
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).finish();
                        ((MainActivity) context).clearSavedGameState();
                    }
                }
            });

        }

        public AlertDialog getInstance() {
            AlertDialog dialog = builder.create();
            return dialog;
        }
    }

    private class ExitDialog extends BaseDialog {

        public ExitDialog(final Context context, String Message, String Title, String positive, String neutral, String negative) {
            super(context, Message, Title);

            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                    if (context instanceof MainActivity)
                        ((MainActivity) context).resumeTimer();
                }
            });

            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).finish();
                        ((MainActivity) context).clearSavedGameState();
                    }
                    Intent intent = new Intent(getContext(), FrontPage.class);
                    context.startActivity(intent);
                }
            });

            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) context).notifyBoardToSave();
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).finish();
                        ((MainActivity) context).notifyBoardToSave();
                    }
                }
            });
        }

        public AlertDialog getInstance() {
            AlertDialog dialog = builder.create();
            return dialog;
        }
    }

    private class BaseDialog extends AlertDialog {
        private Context c;
        private String msg;
        private String title;
        protected AlertDialog.Builder builder;

        public BaseDialog(Context context, String Message, String Title) {
            super(context);
            c = context;
            msg = Message;
            title = Title;
            builder = new AlertDialog.Builder(context);
            builder.setMessage(msg);
            builder.setTitle(title);
            builder.setCancelable(false);
        }


    }

}
