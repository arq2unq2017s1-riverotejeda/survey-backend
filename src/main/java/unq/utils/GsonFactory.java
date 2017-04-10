package unq.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by marina.rivero on 19/09/2016.
 */
public class GsonFactory {

	private static Gson instance = new GsonBuilder().serializeNulls() // matter
																		// of
																		// taste,
																		// just
																		// for
																		// output
																		// anyway
			.registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	public static <T> T fromJson(String json, Class<T> classOfObject) {
		return instance.fromJson(json, classOfObject);
	}
	public static <T> T fromJson(String json, Type typeoft) { return instance.fromJson(json, typeoft);}
	public static String toJson(Object model) {
		return instance.toJson(model);
	}
}
