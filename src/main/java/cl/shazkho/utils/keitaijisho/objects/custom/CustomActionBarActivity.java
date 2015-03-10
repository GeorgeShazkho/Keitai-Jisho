package cl.shazkho.utils.keitaijisho.objects.custom;

import android.support.v7.app.ActionBarActivity;

import cl.shazkho.utils.keitaijisho.objects.ResponseObject;
import cl.shazkho.utils.keitaijisho.tools.StaticHelpers;

/**
 * This module represents a customized representation of the 'ActionBarActivity' class.
 * This class only adds common behaviour to the ActionBarActivity (callback related).
 *
 * @author George Shazkho
 * @version 0.7
 * @since 2015-03-08
 */
public class CustomActionBarActivity extends ActionBarActivity implements StaticHelpers {

    public void requestCallback(ResponseObject response){};

}
