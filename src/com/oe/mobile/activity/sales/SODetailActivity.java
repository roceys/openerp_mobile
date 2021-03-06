/*
 * Copyright (C) 2013  stevendreamer (in github)
 * Project Location: https://github.com/stevendreamer/openerp_mobile

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * Addition: any copy of this program should keep the author name info.
 * any copy without the author info will be a pirate

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.oe.mobile.activity.sales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.debortoliwines.openerp.api.Row;
import com.debortoliwines.openerp.api.RowCollection;
import com.oe.mobile.R;
import com.oe.mobile.R.id;
import com.oe.mobile.R.layout;
import com.oe.mobile.R.menu;
import com.oe.mobile.retired.Attribute;
import com.oe.mobile.retired.ItemDetailThread;
import com.oe.mobile.retired.Model;
import com.oe.mobile.service.Stock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SODetailActivity extends Activity {

	ListView detaillist;
	
	ProgressDialog dialog;
	Handler handler;
	int soId;

	MyTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_so_detail);
		soId = (Integer) getIntent().getExtras().getInt("so_id");

		detaillist = (ListView) findViewById(R.id.soLinesDetailList);

		// detaillist = (ListView)findViewById(R.id.itemDetaillist);
		dialog = ProgressDialog.show(this, "", "�������ݣ����Ե� ��", true, true);

		task = new MyTask();
		task.execute(soId);
	}

	private class MyTask extends AsyncTask<Integer, Integer, HashMap> {

		@Override
		protected void onPreExecute() {
			Log.i("SODetailPage", "onPreExecute() called");
			// dialog.show();
		}

		@Override
		protected HashMap doInBackground(Integer... params) {
			HashMap<String, Object> result = new HashMap<String, Object>();
			Row soHeader = null;
			RowCollection soLines = null;
			try {
				soHeader = Stock.getSOById(params[0]);
				soLines = Stock.getSOLines(params[0]);
				result.put("soHeader", soHeader);
				result.put("soLines", soLines);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {

		}

		@Override
		protected void onPostExecute(HashMap result) {

			setPageView(result);
			dialog.dismiss();

		}

	}

	public void setPageView(HashMap result) {

		Row soHeader = (Row) result.get("soHeader");
		RowCollection soLines = (RowCollection) result.get("soLines");

		TextView soDetailNumber = (TextView) findViewById(R.id.soDetailNumber);
		TextView soDetailCustomer = (TextView) findViewById(R.id.soDetailCustomer);
		TextView soDetailTotal = (TextView) findViewById(R.id.soDetailTotal);
		TextView soDetailStatus = (TextView) findViewById(R.id.soDetailStatus);

		Log.i("SODETAIL", soHeader.get("name").toString());
		if (soHeader.get("name") != null)
			soDetailNumber.setText(soHeader.get("name").toString());
		if (soHeader.get("state") != null)
			soDetailStatus.setText(soHeader.get("state").toString());
		if (soHeader.get("amount_total") != null)
			soDetailTotal.setText(soHeader.get("amount_total").toString());
		if (soHeader.get("amount_total") != null)
			soDetailTotal.setText(soHeader.get("amount_total").toString());
		if (soHeader.get("partner_id") != null)
			soDetailCustomer.setText(((Object[]) soHeader.get("partner_id"))[1]
					.toString());

		ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (Row r : soLines) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			// "name", "state", "origin"
			listItem.put("name", r.get("name"));
			listItem.put("product_uom_qty", r.get("product_uom_qty"));
			listItem.put("price_unit", r.get("price_unit"));
			listItem.put("id", r.get("id"));
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.so_line_list, new String[] { "name",
						"product_uom_qty", "price_unit", "id" }, new int[] {
						R.id.so_line_name, R.id.so_line_product_qty,
						R.id.so_line_unit_price, R.id.so_line_id });
		detaillist.setAdapter(simpleAdapter);

	}

}
