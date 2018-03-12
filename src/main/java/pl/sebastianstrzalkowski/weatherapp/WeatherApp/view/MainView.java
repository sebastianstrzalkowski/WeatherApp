package pl.sebastianstrzalkowski.weatherapp.WeatherApp.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sebastianstrzalkowski.weatherapp.WeatherApp.controllers.WeatherService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringUI(path = "")
public class MainView extends UI {
    @Autowired
    private WeatherService weatherService;
    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button showWeather;
    private Label currentLocationTitle;
    private Label currentTemp;
    private Label weatherDescription;
    private Label weatherMin;
    private Label weatherMax;
    private Label pressureLabel;
    private Label humidityLabel;
    private Label windSpeedLabel;
    private Label sunriseLabel;
    private Label sunsetLabel;
    private ExternalResource img;
    private Image iconImage;
    private HorizontalLayout dashBoardTitleLayout;
    private HorizontalLayout dashBoardDescriptionLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout pressureLayout;


    @Override
    protected void init(VaadinRequest request) {
        setUpLayout();
        setHeader();
        setLogo();
        setUpForm();
        dashBoardTitle();
        dashBoardDescription();

        showWeather.addClickListener(event -> {
            if(!    cityTextField.getValue().equals("")){
                try {
                    updateUI();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else  Notification.show("Please eneter city");

        });
    }




    public void setUpLayout(){
        iconImage = new Image();
        weatherDescription = new Label("Description Clear Skies");
        weatherMin = new Label("Min: 15f");
        weatherMax = new Label("Max: 18F");
        pressureLabel = new Label("Pressure: 123pa");
        humidityLabel = new Label("Humidity: 34");
        windSpeedLabel = new Label("Wind speed: 34km/h");
        sunriseLabel = new Label("Sunrise: ");
        sunsetLabel = new Label("Sunset: ");

        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }

    private void setHeader(){
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Weather");
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);

        headerLayout.addComponents(title);

        mainLayout.addComponents(headerLayout);

    }

    private void setLogo() {
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Image icon= new Image(null, new ClassResource("/weather_icon.png"));
        icon.setWidth("125px");
        icon.setHeight("125px");

        logoLayout.addComponents(icon);

        mainLayout.addComponents(logoLayout);
    }

    private void setUpForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);

        //Selection component
        unitSelect = new NativeSelect<>();
        unitSelect.setWidth(("40px"));
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));

        formLayout.addComponents(unitSelect);

        //TextFields
        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponents(cityTextField);

        //Button
        showWeather = new Button();
        showWeather.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponents(showWeather);

        mainLayout.addComponents(formLayout);
    }

    private void dashBoardTitle() {
        dashBoardTitleLayout = new HorizontalLayout();
        dashBoardTitleLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);



        currentLocationTitle = new Label("Curentlu in Spokane");
        currentLocationTitle.addStyleName(ValoTheme.LABEL_H2);
        currentLocationTitle.addStyleName(ValoTheme.LABEL_LIGHT);


        currentTemp = new Label("19F");
        currentTemp.addStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.addStyleName(ValoTheme.LABEL_H1);
        currentTemp.addStyleName(ValoTheme.LABEL_LIGHT);


    }

    private void dashBoardDescription() {
        dashBoardDescriptionLayout = new HorizontalLayout();
        dashBoardDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //Description vertical layout
        descriptionLayout = new VerticalLayout();
        descriptionLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        descriptionLayout.addComponents(weatherDescription);

        descriptionLayout.addComponents(weatherMin);
        descriptionLayout.addComponents(weatherMax);

        pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        pressureLayout.addComponents(pressureLabel);
        pressureLayout.addComponents(humidityLabel);
        pressureLayout.addComponents(windSpeedLabel);
        pressureLayout.addComponents(sunriseLabel);
        pressureLayout.addComponents(sunsetLabel);



    }

    private void updateUI() throws JSONException {
        String city = cityTextField.getValue();
        String defaultUnit;
        String unit;

        if(unitSelect.getValue().equals("F"))
        {
            defaultUnit = "impirial";
            unitSelect.setValue("F");
            unit = "\u00b0" + "F"; //Deagree sign
        }else{
            defaultUnit = "metric";
            unitSelect.setValue("C");
            unit = "\u00b0" + "C"; //Deagree sign
        }

        weatherService.setCityName(city);
        weatherService.setUnit(defaultUnit);

        currentLocationTitle.setValue("Currently in " + city);
        JSONObject mainObject = weatherService.returnMainObject();
        JSONObject windObject = weatherService.returnWindObject();
        JSONObject systemObject = weatherService.returnSunset();

        //Parameters
        double temp = mainObject.getDouble("temp");
        double minTemp = mainObject.getDouble("temp_min");
        double maxTemp = mainObject.getDouble("temp_max");
        int pressure = mainObject.getInt("pressure");
        int humidity = mainObject.getInt("humidity");
        double speed = windObject.getDouble("speed");
        long sunrise = systemObject.getLong("sunrise")*1000;
        long sunset = systemObject.getLong("sunset")*1000;




        currentTemp.setValue(temp + unit );

        //Icon
        String iconeCode = "";
        String description = "";

        JSONArray jsonArray =  weatherService.returnWeatherArray();

        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/w/" + iconeCode + ".png"));
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            iconeCode = weatherObject.getString("icon");
            description = weatherObject.getString("description");
        }
        iconImage.setSource(new ExternalResource("http://openweathermap.org/img/w/" + iconeCode + ".png"));

        dashBoardTitleLayout.addComponents(currentLocationTitle,iconImage,currentTemp);
        mainLayout.addComponents(dashBoardTitleLayout);

        //Update description UI
        weatherDescription.setValue("Cloudiness: " + description);
        weatherMin.setValue("Min: " + String.valueOf(minTemp) + unit);
        weatherMax.setValue("Max: " + String.valueOf(maxTemp) + unit);
        pressureLabel.setValue("Pressure: " + String.valueOf(pressure) + " hpa");
        humidityLabel.setValue("Humidity: " + String.valueOf(humidity) + " %");
        windSpeedLabel.setValue("Speed: " + String.valueOf("speed") + " m/s");
        sunriseLabel.setValue("Sunrise: " + convertTime(sunrise));
        sunsetLabel.setValue("Sunset: " + convertTime(sunset));

        weatherDescription.addStyleName(ValoTheme.LABEL_SUCCESS );
        weatherMin.addStyleName(ValoTheme.LABEL_SUCCESS );
        weatherMax.addStyleName(ValoTheme.LABEL_SUCCESS );
        pressureLabel.addStyleName(ValoTheme.LABEL_SUCCESS );
        humidityLabel.addStyleName(ValoTheme.LABEL_SUCCESS );
        windSpeedLabel.addStyleName(ValoTheme.LABEL_SUCCESS );
        sunriseLabel.addStyleName(ValoTheme.LABEL_SUCCESS );
        sunsetLabel.addStyleName(ValoTheme.LABEL_SUCCESS );

        dashBoardDescriptionLayout.addComponents(descriptionLayout,pressureLayout);
        mainLayout.addComponents(dashBoardDescriptionLayout);
    }

    private String convertTime(Long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh.mm aa");

        return dateFormat.format(new Date(time));
    }

}
