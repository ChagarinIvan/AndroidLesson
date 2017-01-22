package by.chagarin.androidlesson.rest;


import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = "http://demo.redmine.org", converters = GsonHttpMessageConverter.class)
public interface RestClient {

}
