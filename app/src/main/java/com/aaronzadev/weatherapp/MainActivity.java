package com.aaronzadev.weatherapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronzadev.weatherapp.pojo.DummyObject;
import com.aaronzadev.weatherapp.pojo.MyResponse;
import com.aaronzadev.weatherapp.pojo.Weather;
import com.aaronzadev.weatherapp.recyclerview.DummyObjectAdapter;
import com.aaronzadev.weatherapp.service.WeatherService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int GPS_PERMISSION_REQUEST = 1;
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private LocationListener locListener;
    private LocationManager locManager;

    private TextView txtState;
    private TextView txtMun;
    private TextView txtLocation;
    private TextView txtTemp;
    private ImageView imgIcon;
    private RecyclerView rvItems;

    private Retrofit rf;

    private Drawable curDrawable;

    private static final String API_KEY = "658cb17e420c519a6b337745d69183f3";
    private static final String LANG = "es";
    private static final String UNIT = "metric";
    private static final String IMG_PATH = "https://openweathermap.org/img/w/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        generateDummyData();

        initLocManager();

        initRetrofit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(checkLocationPermission() == PackageManager.PERMISSION_GRANTED){

            showCachedLocation();

        }
        else {

            requestPermision();

        }
    }

    private void generateDummyData() {


        List<DummyObject> items = new ArrayList<>();

        //TODO cadenas de textp harcodeadas, se deben de colocar el el archivo de Strings
        items.add( new DummyObject("Ven a conocer el Centro histórico", "La zona de monumentos históricos de Querétaro, es reconocida como Patrimonio " +
                "Cultural de la Humanidad por la UNESCO gracias a su exquisita arquitectura barroca que se puede apreciar con sólo recorrer las calles del centro. " +
                "El mejor exponente de este estilo es el Patio Barroco un hermoso edificio que hoy es sede de la Facultad de Filosofía de la Universidad. " +
                "La ciudad está llena de agradables parques y zonas de descanso, como la fuente de Neptuno y los jardines Guerrero y Zenea."));

        items.add( new DummyObject("Ven y disfruta del Mercado de la Cruz", "En el Mercado de la Cruz podrás probar las especialidades del estado a precios muy convenientes, " +
                "los platillos tradicionales son la sopa queretana, las gorditas de migajas, " +
                "las enchiladas mineras y muchos otros platillos preparados con nopal y los célebres " +
                "quesos de la región."));

        items.add( new DummyObject("Te invitamos al Palacio de la Corregidora", "La antigua casa de los Corregidores Domínguez "+
                "es hoy el Palacio de Gobierno, puedes visitarlo de manera gratuita todos los días y ver una de las réplicas de la campana de Dolores que ahí se exhibe." +
                "Saliendo a la Plaza de la Corregidora pasa a ver el monumento a Ignacio Pérez, el valiente caudillo que cabalgó de Querétaro a Guanajuato para alertar a Hidalgo sobre el " +
                "descubrimiento de la conspiración de Independencia."));

        items.add( new DummyObject("¿Ya probaste la sopa queretana?", "platillo típico de la ciudad donde fue firmada y que, de acuerdo a las crónicas, " +
                "fue una de las comidas más comunes del ejercito constituyente: la sopa queretana."+
                "Se dice que la sopa queretana, debido a sus ingredientes, es una inigualable fuente de " +
                "energia, por lo que les servía a los congresistas para resistir las largas sesiones a las que se sometían"));

        items.add( new DummyObject("Hotel Mirabel", "El hotel Mirabel es nuestra recomendación si buscas alojamiento con las tres “b” , " +
                "sus habitaciones son muy amplias y modernas. En especial nos encantan las Junior y Master suites que pueden hospedar " +
                "a toda la familia. Sus tarifas convenientes y la varieded de su desayuno bufet, son razones suficientes para " +
                "regresar cada vez que visites Querétaro. El hotel Maribel se encuentra frente a la Alameda Hidalgo, un parque y complejo deportivo lleno de árboles. "));

        items.add( new DummyObject("Conoce el Acueducto de Querétaro", "Este es el emblema de la ciudad con sus 74 arcos y más de un kilómetro de longitud. " +
                "Cuenta la leyenda que un Marqués enamorado en secreto de una monja Capuchina, " +
                "fue el principal benefactor de este proyecto con tal de cumplir la petición realizada por el convento de su amada"));

        items.add( new DummyObject("Disfruta del Restaurante la Mariposa", "El restaurante la Mariposa es famoso por sus postres, nieves y malteadas. " +
                "Nuestra recomendación es probar la tarta de durazno acompañada de buen café o una espumosa malteada de nuez.  " +
                "A un lado de la pastelería, tienen un restaurante familiar en el que sirven ricos platillos para todos los antojos del día."));

        items.add( new DummyObject("Ruta del vino y queso", "Esta ruta se desarrolla alrededor de tres Pueblos Mágicos: " +
                "Tequisquiapan, Cadereyta y Bernal. Dedica medio día o una jornada para visitar Vinos la " +
                "Redonda o la Finca Sala Vivé de Freixenet, donde podrás conocer y disfrutar de vinos de " +
                "calidad de exportación con sello queretano. En los ranchos de ovejas, cabras y vacas " +
                "de esta región se fabrican de manera artesanal quesos mexicanos y de estilo europeo. " +
                "Nuestro lugar favorito para conocer el proceso y ver a los adorables animalitos es Quesos VAI."));

        items.add( new DummyObject("Ecoturismo en la Sierra Gorda", "La región de la Sierra Gorda tiene como protagonista a la Peña de Bernal " +
                "que con sus más de 350 metros de altura, es el tercer monolito más elevado del mundo. " +
                "Las poblaciones y reservas naturales de los alrededores ofrecen la posibilidad de hacer muchas actividades de aventura como exploración en " +
                "cuevas y cascadas, tirolesas, puentes colgantes, cañonismo y ciclismo de montaña."));

        items.add( new DummyObject("Misiones queretanas", "Las misiones franciscanas en la Sierra Gorda, son consideradas " +
                "Patrimonio Cultural de la Humanidad por la UNESCO. " +
                "Estos cinco poblados fundados durante la Conquista por misioneros españoles tienen iglesias con " +
                "singulares fachadas que son muestra de la fusión del trabajo de los misioneros españoles y los artesanos indígenas."));


        /*items.add( new DummyObject("Demo 11", "Esto es una breve descripcion :D"));
        items.add( new DummyObject("Demo 12", "Esto es una breve descripcion :D"));
        items.add( new DummyObject("Demo 13", "Esto es una breve descripcion :D"));
        items.add( new DummyObject("Demo 14", "Esto es una breve descripcion :D"));
        items.add( new DummyObject("Demo 15", "Esto es una breve descripcion :D"));*/

        DummyObjectAdapter adapter = new DummyObjectAdapter(items);
        rvItems.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        locManager.removeUpdates(locListener);
    }

    private void initUI() {

        txtState = findViewById(R.id.txtState);
        txtMun = findViewById(R.id.txtMun);
        txtLocation = findViewById(R.id.txtLocation);
        txtTemp = findViewById(R.id.txtTmp);
        imgIcon = findViewById(R.id.imgIcon);
        rvItems = findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);

    }

    private void initLocManager() {

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //las coordenadas de la ubiacion cambiaron...

                // prueba! ... Log.d("TAG_LOC", "Lat: "+location.getLatitude()+" Long: "+location.getLongitude());
                calculateLocationAddress(location);
                getWeatherForLocation(location);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // el gps se ha cambiado su estado actual..!
            }

            @Override
            public void onProviderEnabled(String provider) {
                //el gps esta activo

            }

            @Override
            public void onProviderDisabled(String provider) {
                //el gps esta desactivado!
                Toast.makeText(getApplicationContext(), "Debe activar el GPS", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void initRetrofit(){

        rf = new Retrofit.Builder().
                baseUrl("https://api.openweathermap.org/data/2.5/").addConverterFactory(GsonConverterFactory.create()).build();

    }

    private void getWeatherForLocation(Location loc){

        if (loc != null) {
            WeatherService service = rf.create(WeatherService.class);
            service.response(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()), LANG, UNIT, API_KEY).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                    if(response.isSuccessful()){

                        //TODO verificar si el objeto response no es nulo
                        getDataFromResponse(response.body());

                    }
                    else {

                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void getDataFromResponse(MyResponse body) {

        String actWeather = "";
        String iconId = "";
        String temp = String.valueOf(body.getMain().getTemp());

        List<Weather> weather = body.getWeather();

        for (Weather w : weather){

            actWeather = w.getDescription();
            iconId = w.getIcon();

        }

        updateUI(actWeather, iconId, temp);
        showNotification(actWeather, temp);

    }


    private void updateUI(String actWeather, String iconId, String actTemp){

        //Toast.makeText(getApplicationContext(), "Estado del Tiempo Actualizado! :D", Toast.LENGTH_SHORT).show();

        txtTemp.setText(actWeather.toUpperCase()+" "+actTemp.toUpperCase()+"°");

        Glide.with(this).
                load(IMG_PATH+iconId+".png").
                apply(RequestOptions.centerCropTransform()).
                apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).
                transition(DrawableTransitionOptions.withCrossFade()).
                into(imgIcon);


    }

    private void showNotification(String actWeather, String temp) {

        //Intent intent = new Intent(this, MainActivity.class); //TODO implementar para abrir la actividad cuando se pulsa la notificacion
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder b;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelName = "Weather App";
            String desc = "El estado actual del clima";

            NotificationChannel channel = new NotificationChannel("1", channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(desc);
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);


            b = new NotificationCompat.Builder(this, "1");


        }
        else {

            b = new NotificationCompat.Builder(this, null);

        }

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.cloud)
                .setContentTitle("Estado Actual del Tiempo")
                .setContentText(actWeather.toUpperCase() + " " + temp.toUpperCase() + "°")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        notificationManager.notify(1, b.build());

    }

    private void showCachedLocation() {

        if (locManager != null && checkLocationPermission() == PackageManager.PERMISSION_GRANTED) {


            Location lastLocation = locManager.getLastKnownLocation(LOCATION_PROVIDER);

            calculateLocationAddress(lastLocation);

            getWeatherForLocation(lastLocation);

            startLocationUpdates();

        }
        else {

            startLocationUpdates();

        }


    }


    private void startLocationUpdates() {

        if(checkLocationPermission() == PackageManager.PERMISSION_GRANTED){

            locManager.requestLocationUpdates(LOCATION_PROVIDER, 3000, 500f, locListener);

        }


    }

    private int checkLocationPermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

    }

    private void requestPermision() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == GPS_PERMISSION_REQUEST){

            if(grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){

                startLocationUpdates();

            }
            else {

                requestPermision();

            }
        }

    }

    private void calculateLocationAddress (Location loc){

        if(loc != null){

            try {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation( loc.getLatitude(), loc.getLongitude(), 1);

                if (!list.isEmpty()) {

                    Address address = list.get(0);

                    txtState.setText(address.getAdminArea());
                    txtMun.setText(address.getLocality());
                    txtLocation.setText(address.getSubLocality());

                    /*String message = "Featured Name: "+address.getFeatureName()
                    +"\nLocality: "+address.getLocality()
                    +"\nSubAdmin Area: "+address.getSubAdminArea()
                    +"\nSubLocality: "+address.getSubLocality()
                    +"\nLocale: "+address.getAdminArea();
                        // TODO Eliminar solo para testing...
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();*/
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
