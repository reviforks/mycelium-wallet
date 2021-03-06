/*
 * Copyright 2013, 2014 Megion Research and Development GmbH
 *
 * Licensed under the Microsoft Reference Source License (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license.
 * If you do not accept the license, do not use the software.
 *
 * 1. Definitions
 * The terms "reproduce," "reproduction," and "distribution" have the same meaning here as under U.S. copyright law.
 * "You" means the licensee of the software.
 * "Your company" means the company you worked for when you downloaded the software.
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes
 * of debugging your products, maintaining your products, or enhancing the interoperability of your products with the
 * software, and specifically excludes the right to distribute the software outside of your company.
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor
 * under this license.
 *
 * 2. Grant of Rights
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free copyright license to reproduce the software for reference use.
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free patent license under licensed patents for reference use.
 *
 * 3. Limitations
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor’s name, logo, or trademarks.
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software
 * (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties,
 * guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot
 * change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability,
 * fitness for a particular purpose and non-infringement.
 */

package com.mycelium.wallet.activity.main;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.mycelium.net.ServerEndpointType;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.event.TorStateChanged;
import com.squareup.otto.Subscribe;

public class BalanceMasterFragment extends Fragment {
   private  TextView tvTor;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      setHasOptionsMenu(true);
      return Preconditions.checkNotNull(inflater.inflate(R.layout.balance_master_fragment, container, false));
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public void onResume() {
      Activity activity = getActivity();
      // Set beta build
      PackageInfo pInfo;
      try {
         pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
         ((TextView) activity.findViewById(R.id.tvBuildText)).setText(getResources().getString(R.string.build_text,
               pInfo.versionName));
      } catch (NameNotFoundException e) {
         // Ignore
         //todo insert uncaught error handler
      }

      MbwManager mbwManager = MbwManager.getInstance(activity);
      tvTor = (TextView) activity.findViewById(R.id.tvTorState);
      if (mbwManager.getTorMode() == ServerEndpointType.Types.ONLY_TOR && mbwManager.getTorManager() != null) {
         tvTor.setVisibility(View.VISIBLE);
         showTorState(mbwManager.getTorManager().getInitState());
      }else{
         tvTor.setVisibility(View.GONE);
      }

      MbwManager.getInstance(this.getActivity()).getEventBus().register(this);
      super.onResume();
   }

   @Override
   public void onPause() {
      MbwManager.getInstance(this.getActivity()).getEventBus().unregister(this);
      super.onPause();
   }

   @Subscribe
   public void onTorState(TorStateChanged torState){
      showTorState(torState.percentage);
   }

   private void showTorState(int percentage) {
      if (percentage==0) {
         tvTor.setText("");
      } else if (percentage==100){
         tvTor.setText("");
      } else {
         tvTor.setText(getString(R.string.tor_state_init));
      }
   }


}