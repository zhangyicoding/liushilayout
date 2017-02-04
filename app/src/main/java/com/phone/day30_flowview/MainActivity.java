package com.phone.day30_flowview;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.phone.day30_flowview.customview.FlowLayout;

/**
 * 流式布局
 * 
 * @author apple
 * 
 */
public class MainActivity extends Activity {

	FlowLayout flow_View;

	String strs[] = { "aksjd", "asdhj","asd","asd","asdas","asdasd" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		flow_View = (FlowLayout) findViewById(R.id.flow_View);

		init();
	}

	private void init() {
		for (int i = 0; i < strs.length; i++) {
			TextView view = (TextView) LayoutInflater.from(this).inflate(
					R.layout.tv_item, flow_View, false);
			view.setText(strs[i]);
			flow_View.addView(view);
		}
	}

}
