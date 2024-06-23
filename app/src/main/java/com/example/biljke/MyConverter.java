package com.example.biljke;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MyConverter {
    public static class MedKoristConverter {
        @TypeConverter
        public List<MedicinskaKorist> storedStringToEnum(String value) {
            List<String> dbValues = Arrays.asList(value.split("\\s*,\\s*"));
            List<MedicinskaKorist> enums = new ArrayList<>();

            for (String s: dbValues)
                enums.add(MedicinskaKorist.valueOf(s));

            return enums;
        }

        @TypeConverter
        public String enumListToStoredString(List<MedicinskaKorist> cl) {
            String value = "";

            for (MedicinskaKorist lang : cl)
                value += lang.name() + ",";

            return value;
        }
    }

    public static class KlimTipConverter {
        @TypeConverter
        public List<KlimatskiTip> storedStringToEnum(String value) {
            List<String> dbValues = Arrays.asList(value.split("\\s*,\\s*"));
            List<KlimatskiTip> enums = new ArrayList<>();

            for (String s: dbValues)
                if(!s.isEmpty())
                    enums.add(KlimatskiTip.valueOf(s));

            return enums;
        }

        @TypeConverter
        public String enumListToStoredString(List<KlimatskiTip> cl) {
            String value = "";

            for (KlimatskiTip lang : cl)
                value += lang.name() + ",";

            return value;
        }
    }

    public static class ZemljTipConverter {
        @TypeConverter
        public List<Zemljiste> storedStringToEnum(String value) {
            List<String> dbValues = Arrays.asList(value.split("\\s*,\\s*"));
            List<Zemljiste> enums = new ArrayList<>();

            for (String s: dbValues)
                if(!s.isEmpty())
                    enums.add(Zemljiste.valueOf(s));

            return enums;
        }

        @TypeConverter
        public String enumListToStoredString(List<Zemljiste> cl) {
            String value = "";

            for (Zemljiste lang : cl)
                value += lang.name() + ",";

            return value;
        }
    }

    public static class JelaConverter {
        @TypeConverter
        public List<String> stringToList(String value) {
            if (value == null) {
                return null;
            }
            return Arrays.asList(value.split(","));
        }

        @TypeConverter
        public String listToString(List<String> list) {
            if (list == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String item : list) {
                stringBuilder.append(item).append(",");
            }
            return stringBuilder.toString();
        }
    }


    public static class BitmapConverter {
        @TypeConverter
        public String fromBitmap(Bitmap bitmap) {
            Bitmap resizedBitmap = resizeBitmap(bitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        }

        @TypeConverter
        public Bitmap toBitmap(String data) {
            byte[] bytes = Base64.decode(data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        private static Bitmap resizeBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if (width <= 400 && height <= 400) {
                return bitmap;
            }

            float aspectRatio = (float) width / (float) height;
            int newWidth = 400;
            int newHeight = 400;

            if (width > height) {
                newHeight = Math.round(400 / aspectRatio);
            } else if (height > width) {
                newWidth = Math.round(400 * aspectRatio);
            }

            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }
    }
}


