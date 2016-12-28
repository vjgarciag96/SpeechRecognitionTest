package quercus.speechrecognitiontest;

import android.Manifest;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SpeechDelegate{

    //Para los permisos
    private static final int REQUEST_PERMISSION = 25;

    //Elementos del diseño relacionados con el speechRecognition
    private ImageButton listenButton;
    private SpeechProgressView progress;

    private EditText[] numFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Speech.init(this);//Iniciar el recognizer
        Speech.getInstance().setLocale(new Locale("es", "ES"));//Cambiar idioma a español

        numFields=new EditText[4];
        numFields[0]=(EditText)findViewById(R.id.numField1);//La utilizaremos
        numFields[1]=(EditText)findViewById(R.id.numField2);//para rellenarla según
        numFields[2]=(EditText)findViewById(R.id.numField3);// lo escuchado
        numFields[3]=(EditText)findViewById(R.id.numField4);

        progress = (SpeechProgressView) findViewById(R.id.progress);
    }

    public void onEditTextClick(View view){
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION);
            try {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                Speech.getInstance().stopTextToSpeech();
                Speech.getInstance().startListening(progress, MainActivity.this);

            } catch (SpeechRecognitionNotAvailable exc) {
                showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
                showEnableGoogleVoiceTyping();
            }
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(MainActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void onStartOfSpeech() {

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {

    }

    @Override
    public void onSpeechResult(String result) {

        if (result.isEmpty()) {
            Speech.getInstance().say(getString(R.string.repeat));

        } else {
            Speech.getInstance().say(result);
            int i=0;
            boolean setted=false;

            while(i<numFields.length && !setted) {
                if(numFields[i].isFocused()) {
                    setted = true;
                    numFields[i].setText(result);
                    numFields[i].setFocusableInTouchMode(false);
                    numFields[i].clearFocus();
                }
                else
                    i++;
            }
        }
    }

}
