package com.shipbob.vendors.bitmaphandling;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.facebook.ShipBob.activities.MainActivity;
import com.shipbob.databasehandler.ShippingInformationDatabaseHandler;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.widget.Toast;

public class PhotoHandler  implements PictureCallback {

  private final Context context;
    long id;

  public PhotoHandler(Context context) {
    this.context = context;
      this.id = -1;
  }

    public PhotoHandler(Context context, long id) {
        this.context = context;
        this.id = id;
    }

    @Override
  public void onPictureTaken(byte[] data, Camera camera) {

    File pictureFileDir = getDir();

    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

      Toast.makeText(context, "Can't create directory to save image.",
          Toast.LENGTH_LONG).show();
      return;

      
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
    String date = dateFormat.format(new Date());
    String photoFile = "Picture_" + date + ".jpg";

    String filename = pictureFileDir.getPath() + File.separator + photoFile;

    File pictureFile = new File(filename);

    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      fos.write(data);
      fos.close();
    /*  Toast.makeText(context, "New Image saved:" + photoFile,
          Toast.LENGTH_LONG).show();*/
      
      movetoShippingActivity(filename);
    } catch (Exception error) {
    
      Toast.makeText(context, "Image could not be saved.",
          Toast.LENGTH_LONG).show();
    }
  }

  private File getDir() {
    File sdDir = Environment
      .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return new File(sdDir, "CameraAPIDemo");
  }


  private void movetoShippingActivity(String fileName){

      Intent i = new Intent(context, MainActivity.class);
      createShippingRecord(id, fileName);
//      i.putExtra("shipInformationTableId", shipInformationTableId);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      this.context.startActivity(i);
  }
  
  private boolean createShippingRecord(long id, String fileName){
//		 ShippingInformationTable shipInformation= new ShippingInformationTable();
//		 shipInformation.setImageFileName(fileName);

      ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(context);

		return shipDbHandler.update(id, fileName);
  }
  


} 

