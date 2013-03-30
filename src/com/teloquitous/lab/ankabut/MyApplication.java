package com.teloquitous.lab.ankabut;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "dEFmOC14Ry14WFJyMkdqT1V4U18wNlE6MQ",
mode = ReportingInteractionMode.DIALOG,
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info,
resDialogTitle = R.string.crash_dialog_title, 
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
resDialogOkToast = R.string.crash_dialog_ok_toast
)
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}

}
