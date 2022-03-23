package com.conwole.main.Fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.conwole.main.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {

    public TrainingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_training, container, false);

        Button telegram = view.findViewById(R.id.telegram);
        Button telegramX = view.findViewById(R.id.telegram_x);

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager pm = getContext().getPackageManager();
                boolean isInstalled = isPackageInstalled("org.telegram.messenger", pm);

                if(isInstalled){
                    Toast.makeText(getContext(), "Installed", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(view, "Confirm Open Url?", Snackbar.LENGTH_LONG)
                            .setAction("YES", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Uri uri = Uri.parse("https://t.me/joinchat/AAAAAFDH1H6E2ArfHDNOPA");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.setPackage("org.telegram.messenger");
                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("https://t.me/joinchat/AAAAAFDH1H6E2ArfHDNOPA")));
                                    }
                                }
                            });

                    snackbar.show();

                }else {
                    Toast.makeText(getContext(), "Not Installed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        telegramX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager pm = getContext().getPackageManager();
                boolean isInstalled = isPackageInstalled("org.thunderdog.challegram", pm);

                if(isInstalled){
                    Toast.makeText(getContext(), "Installed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Not Installed", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}