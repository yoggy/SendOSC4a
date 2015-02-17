package net.sabamiso.android.sendosc4a;

import net.sabamiso.android.util.Config;
import net.sabamiso.android.util.Resolver;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SendOSCActivity extends Activity {

	SendOSCView view;
	Resolver resolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// disable StrictMode
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		super.onCreate(savedInstanceState);

		Config.init(this);
		Resolver.init(this);
		resolver = Resolver.getInstance();
		
		view = new SendOSCView(this);
		setContentView(view);
		
		view.setup();
		view.invalidate();
	}

	@Override
    protected void onResume() {
        super.onResume();
		resolver.onResume();
        view.onResume();
        
        hideSystemUI();
    }
    
	@Override
    protected void onPause() {
        view.onPause();
		resolver.onPause();
        super.onPause();
    }

	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        hideSystemUI();
    }
        
    private void hideSystemUI() {
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
