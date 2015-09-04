package com.board.game.sasha.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.board.game.sasha.gui.FrontPage;
import com.board.game.sasha.gui.MainActivity;
import com.board.game.sasha.R;
import com.board.game.sasha.commonutils.Utils;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by sachin.c1 on 12-Aug-15.
 */
public class AlertDialogFactory {
    private Context context;
    private String type;
    private String move,time;
    private SharedPreferences pref;
    private String mode;
    public AlertDialogFactory(Context context, String type) {
        this.context = context;
        this.type = type;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        mode = pref.getString("mode","number");
    }



    public AlertDialogFactory(Context context, String type,String move,String time) {
        this.context = context;
        this.type = type;
        this.move = move;
        this.time = time;
    }


    public AlertDialog getDialog() {
        if (type.equalsIgnoreCase("FINISH")) {
            return new FinishDialog(context,
                    "Congratulations!! You Won the Game",
                    "Hooray!!",
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
        } else if (type.equalsIgnoreCase("SCORE")) {
            return new ScoreDialog(context,
                    "Your Best statistics:"
                    ).getInstance();
        }
        return null;
    }



    private class ScoreDialog extends BaseDialog {

        public ScoreDialog(final Context context,String Title) {
            super(context, Title);

        }

        public AlertDialog getInstance() {
             View view = getLayoutInflater().inflate(R.layout.best_score_layout,null,false);
            if(!Utils.isNullorWhiteSpace(move) && !Utils.isNullorWhiteSpace(time)) {
                ((TextView)view.findViewById(R.id.text0)).setVisibility(View.GONE);
                ((TextView)view.findViewById(R.id.text1)).setVisibility(View.VISIBLE);
                ((TextView)view.findViewById(R.id.text1)).setText(String.format(context.getResources().getString(R.string.your_move), move));
                ((TextView)view.findViewById(R.id.text2)).setVisibility(View.VISIBLE);
                ((TextView)view.findViewById(R.id.text2)).setText(String.format(context.getResources().getString(R.string.your_time), time));
            }
            builder.setView(view);

            AlertDialog dialog = builder.create();
            return dialog;
        }
    }



    private class FinishDialog extends BaseDialog {

        public FinishDialog(final Context context, String Message, String Title, String positive, String negative) {
            super(context, Message, Title);
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).finish();
                        ((MainActivity) context).saveGameBestStats();
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
                            ((MainActivity) context).saveGameBestStats();
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
            if(!mode.equalsIgnoreCase("picture"))
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

        public BaseDialog(Context context, String Title){
            super(context);
            c = context;
            title = Title;
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(true);
        }

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
