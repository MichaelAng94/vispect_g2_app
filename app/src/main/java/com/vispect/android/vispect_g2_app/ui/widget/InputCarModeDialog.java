package com.vispect.android.vispect_g2_app.ui.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;


/**
 * δ�����豸���ѿ�
 * Created by xu on 2016/03/16.
 */
public class InputCarModeDialog extends Dialog {

	public InputCarModeDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public InputCarModeDialog(Context context, int theme){
        super(context, theme);
    }


	public static class Builder {  
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private EditText brand;
        private EditText mode;
  
        public Builder(Context context) {
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param message
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);
            return this;  
        }  
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {
            this.contentView = v;  
            return this;  
        }  
  
        /** 
         * Set the positive button resource and it's listener 
         *  
         * @param positiveButtonText 
         * @return 
         */  
        public Builder setPositiveButton(int positiveButtonText,  
                OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setPositiveButton(String positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;  
            this.positiveButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(int negativeButtonText,  
                OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public Builder setNegativeButton(String negativeButtonText,
                OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  

        public String getBrand(){
            return brand.getText().toString();
        }

        public String getMode(){
            return mode.getText().toString();
        }

        public InputCarModeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme  
            final InputCarModeDialog dialog = new InputCarModeDialog(context, R.style.MyDialog);
            View layout = inflater.inflate(R.layout.dialog_input_carinfo, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // set the confirm button
            brand = (EditText)layout.findViewById(R.id.edit_car_brand);
            mode = (EditText)layout.findViewById(R.id.edit_car_mode);
            if (positiveButtonText != null) {  
                ((Button) layout.findViewById(R.id.btn_doalog_connect_positive))
                        .setText(positiveButtonText);  
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.btn_doalog_connect_positive))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btn_doalog_connect_positive).setVisibility(
                        View.GONE);
            }  
            // set the cancel button  
            if (negativeButtonText != null) {  
                ((Button) layout.findViewById(R.id.btn_doalog_connect_cancel))
                        .setText(negativeButtonText);  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.btn_doalog_connect_cancel))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btn_doalog_connect_cancel).setVisibility(
                        View.GONE);
            }  
            // set the content message  
            if (message != null) {  
                ((TextView) layout.findViewById(R.id.btn_doalog_connect_message)).setText(message);
            } else if (contentView != null) {  
                // if no message set  
                // add the contentView to the dialog body  
                ((LinearLayout) layout.findViewById(R.id.ll_main))
                        .removeAllViews();  
                ((LinearLayout) layout.findViewById(R.id.ll_main))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }  
            dialog.setContentView(layout);  
            return dialog;  
        }  
    }

}
