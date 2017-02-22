package com.lvds2000.AcornAPI.exception;

/**
 * Created by lvds2 on 2/17/2017.
 */

public class AcornException extends Exception{
    AcornException(){
        super("Acorn not respond or offline");
    }
}
