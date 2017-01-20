package by.chagarin.androidlesson.rest;


import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = "https://app.apiary.io/test11933", converters = GsonHttpMessageConverter.class)
public interface RestClient {

}
