package com.everestit.ufobeacon.AsyncTasking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.everestit.ufobeacon.GlobalClass;
import com.everestit.ufobeacon.Home;
import com.everestit.ufobeacon.R;
import com.everestit.ufobeacon.Service.SoapService;
import com.everestit.ufobeacon.Service.XMLParser;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


/**
 * Created by Administrator on 05-Apr-17.
 */

public class GetOffers extends AsyncTask<String, String, String>
{

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    Context context;
    GlobalClass globalClass;

    public GetOffers(Context cxt)
    {
        context=cxt;
        globalClass=(GlobalClass)context.getApplicationContext();
    }
    @Override
    protected String doInBackground(String... params)
    {

        String BeaconId=globalClass.getBeaconId();
        Log.e("BeaconId Global class",BeaconId);
        SoapService soapService=new SoapService();
        String response=soapService.Offers(BeaconId);

        return response;
    }
    @Override
    protected void onPostExecute(String result)
    {
        if (result != null)
        {
            xmlparser(result);

        }
    }
    public void xmlparser(String xml)
    {
        Log.e("NotificatioService",xml.toString());

        GlobalClass globalVariable = (GlobalClass)context. getApplicationContext();

        try
        {
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName("OfferData");
            for (int i = 0; i < nl.getLength(); i++)
            {
                Element e = (Element) nl.item(i);

                //store all values in global class
                globalVariable.setOfferID(parser.getValue(e,"OfferID"));
                globalVariable.setOfferName(parser.getValue(e,"OfferName"));
                globalVariable.setOfferDetails(parser.getValue(e,"OfferDetails"));
                globalVariable.setProductID(parser.getValue(e,"ProductID"));
                globalVariable.setProductName(parser.getValue(e,"ProductName"));
                globalVariable.setStartDate(parser.getValue(e,"StartDate"));
                globalVariable.setEndDate(parser.getValue(e,"EndDate"));
                globalVariable.setImage(parser.getValue(e,"Image"));



                //show big notification using drawable image
               /* Bitmap bitback = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.notificationbanner);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                int icon = R.mipmap.logo;
                String title = (parser.getValue(e,"OfferName"));
                String message = (parser.getValue(e,"OfferDetails"));
                Intent intent = new Intent(context.getApplicationContext(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                final PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
                Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/notification");
                //call for show bit notification
                showBigNotification(bitback,mBuilder,icon,title,message,resultPendingIntent,alarmSound,context);
                playNotificationSound(context);*/


            /*    //click on notification then open ShowNotificati page
                Intent resultIntent = new Intent(context.getApplicationContext(), ShowNotifition.class);
                //call for show notification method
                String title = (parser.getValue(e,"OfferName"));
                String message = (parser.getValue(e,"OfferDetails"));
                showNotification(context.getApplicationContext(), title, message, resultIntent, null);*/





                //for multiple notification
                int offId=Integer.parseInt(parser.getValue(e,"OfferID"));
                String title = (parser.getValue(e,"OfferName"));
                String message = (parser.getValue(e,"OfferDetails"));
                String tempimg=(parser.getValue(e,"Image"));
                Bitmap img=decodeBase64(tempimg);
                sendNotification(message,title,context.getApplicationContext(),offId,img);



              /*  SharedPreferences prefs = context.getSharedPreferences("offId", MODE_PRIVATE);
                String offId = prefs.getString("offerId", null);

                String tempoffId=(parser.getValue(e,"OfferID"));

                if (offId!=null)
                {
                    Log.e("sharedpreferencedId",offId.toString());
                    Log.e("GlobalId",tempoffId.toString());

                    if(offId != tempoffId)
                    {
                        //click on notification then open ShowNotifiction page
                        Intent resultIntent = new Intent(context.getApplicationContext(), ShowNotificaton.class);
                        //call for show notification method

                        String title = globalVariable.getOfferName();
                        String message = globalVariable.getOfferDetails();
                        showNotification(context.getApplicationContext(), title, message, resultIntent, null);
                        Log.e("showNotification","call in if");
                    }
                }
                else
                {
                    //click on notification then open ShowNoification page
                    Intent resultIntent = new Intent(context.getApplicationContext(), ShowNotifition.class);
                    //call for show notification method

                    //stor the offer id for not send same notification again and again
                    String offerId=globalVariable.getOfferID();
                    SharedPreferences.Editor editor = context.getSharedPreferences("offId", MODE_PRIVATE).edit();
                    editor.putString("offerId", offerId);
                    editor.commit();

                    String title=globalVariable.getOfferName();
                    String message=globalVariable.getOfferDetails();
                    showNotification(context.getApplicationContext(), title, message,resultIntent,null);
                    Log.e("showNotification","call else");

                }*/


            }

        }
        catch (Exception ex)
        {
            Log.e("Exception",ex.toString());
        }
    }

    private void showNotification(Context context, String title, String message, Intent intent, String imageUrl)
    {
        Log.e("NotificatioService","showNotification");
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.mipmap.logo;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl))
        {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches())
            {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, resultPendingIntent, alarmSound,context);
                } else {
                    showSmallNotification(mBuilder, icon, title, message,resultPendingIntent, alarmSound,context);
                }
            }
        }
        else
        {
            showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound,context);
            playNotificationSound(context);
        }

    }
    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound,Context mContext) {
        Log.e("NotificatioService","showSmallNotification");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound,Context mContext) {
        Log.e("NotificatioService","showBigNotification");
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound(Context mContext) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //for multiple notification
    private void sendNotification(String message,String title,Context mContext,int Newid,Bitmap image) throws JSONException
    {
        Intent intent = new Intent(context.getApplicationContext(), Home.class);
        intent.putExtra("id", Newid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final int not_nu=generateRandom();
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, not_nu /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        /*Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notificationbanner);*/
        bigPictureStyle.bigPicture(image);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(bigPictureStyle)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(not_nu /* ID of notification */, notificationBuilder.build());
    }
    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
    //convert byte64 to bitmap image
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}