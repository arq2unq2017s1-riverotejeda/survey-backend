package utils;

import unq.api.service.ApiService;

/**
 * Created by mar on 30/04/17.
 */
public  class MainAPIInstance {

    private static Boolean initialized = false;



    public static synchronized void initialize(){
      if (!initialized) {
        initialized =true;
        ApiService.main(null);
      }
    }
}
