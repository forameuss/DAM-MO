package es.iesnervion.albertonavarro.a10_dadoker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Random;

import es.iesnervion.albertonavarro.a10_dadoker.Models.Dado;

public class VsIA extends AppCompatActivity implements View.OnClickListener{
    public Button btnRoll;
    public Dado dado1, dado2, dado3, dado4, dado5;
    public TextView txtResHum, txtResIA;
    public Dado dadoIA1, dadoIA2, dadoIA3, dadoIA4, dadoIA5;
    public Dado[] dados = new Dado[5];
    public Dado[] dadosIA = new Dado[5];
    public ImageView cor1,cor2,cor3,cor4,cor5;
    public ImageView corIA1,corIA2,corIA3,corIA4,corIA5;
    //private ImageView bannerIA;
    public TextView txtGanIA;
    public ImageView[] corasonesH = new ImageView[5];
    public ImageView[] corasonesIA = new ImageView[5];
    public LinearLayout tableroIA, tableroH;
    public Random ale = new Random();
    public int vidaH = 5;
    public int vidaIA = 5;
    public boolean primeraTirada = true, tirando = false;
    public Animation animDado;
    public Animation animBanner;
    public SoundPool soundPool;
    public MediaPlayer mediaPlayer;
    public int idMusicBackground, idSoundRoll, idMusicVictory, idMusicLose;
    public int colorSel = Color.LTGRAY;
    public int numTutorial = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs_ia);

        btnRoll = (Button) findViewById(R.id.btnRoll);
        btnRoll.setOnClickListener(this);

        txtResHum = (TextView) findViewById(R.id.txtResHum);
        txtResIA = (TextView) findViewById(R.id.txtResIA);
        tableroIA = (LinearLayout) findViewById(R.id.tableroIA);
        tableroH = (LinearLayout) findViewById(R.id.tableroH);

        //Animasion
        animDado = AnimationUtils.loadAnimation(this, R.anim.anim_dado);
        animDado.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                soundPool.stop(idSoundRoll);
                soundPool.play(idSoundRoll, 1, 1, 1, 0, 1);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
               enAnimacionFinal();
            }
        });

        //Sonido
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        idSoundRoll = soundPool.load(this, R.raw.roll, 1);
        idMusicLose = soundPool.load(this, R.raw.lose, 1);
        idMusicVictory = soundPool.load(this, R.raw.victory, 1);

        mediaPlayer = MediaPlayer.create(this, R.raw.background);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


        //Dados del usaurio
        dado1 = (Dado) findViewById(R.id.dado1);
        dado1.setOnClickListener(this);
        dados[0] = dado1;
        dado2 = (Dado) findViewById(R.id.dado2);
        dado2.setOnClickListener(this);
        dados[1] = dado2;
        dado3 = (Dado) findViewById(R.id.dado3);
        dado3.setOnClickListener(this);
        dados[2] = dado3;
        dado4 = (Dado) findViewById(R.id.dado4);
        dado4.setOnClickListener(this);
        dados[3] = dado4;
        dado5 = (Dado) findViewById(R.id.dado5);
        dado5.setOnClickListener(this);
        dados[4] = dado5;

        //Dados del adversario
        dadoIA1 = (Dado) findViewById(R.id.dadoIA1);
        dadosIA[0] = dadoIA1;
        dadoIA2 = (Dado) findViewById(R.id.dadoIA2);
        dadosIA[1] = dadoIA2;
        dadoIA3 = (Dado) findViewById(R.id.dadoIA3);
        dadosIA[2] = dadoIA3;
        dadoIA4 = (Dado) findViewById(R.id.dadoIA4);
        dadosIA[3] = dadoIA4;
        dadoIA5 = (Dado) findViewById(R.id.dadoIA5);
        dadosIA[4] = dadoIA5;

        //Corazones del usuarioo
        cor1 = (ImageView) findViewById(R.id.cor1);
        corasonesH[0] = cor1;
        cor2 = (ImageView) findViewById(R.id.cor2);
        corasonesH[1] = cor2;
        cor3 = (ImageView) findViewById(R.id.cor3);
        corasonesH[2] = cor3;
        cor4 = (ImageView) findViewById(R.id.cor4);
        corasonesH[3] = cor4;
        cor5 = (ImageView) findViewById(R.id.cor5);
        corasonesH[4] = cor5;

        //Corazones del adversario
        corIA1 = (ImageView) findViewById(R.id.corIA1);
        corasonesIA[0] = corIA1;
        corIA2 = (ImageView) findViewById(R.id.corIA2);
        corasonesIA[1] = corIA2;
        corIA3 = (ImageView) findViewById(R.id.corIA3);
        corasonesIA[2] = corIA3;
        corIA4 = (ImageView) findViewById(R.id.corIA4);
        corasonesIA[3] = corIA4;
        corIA5 = (ImageView) findViewById(R.id.corIA5);
        corasonesIA[4] = corIA5;

        //Banners
        txtGanIA = (TextView) findViewById(R.id.txtResultado);

    }

    public void finalizarRonda() {
        if(vidaH==0 || vidaIA==0){
            mediaPlayer.stop();
            String titulo = (vidaH==0)?"¡DERROTA!": "¡VICTORIA!";
            String mensaje = (vidaH==0)?"¡Has perdido!": "¡Has ganado!";
            if(vidaH==0)
                soundPool.play(idMusicLose, 1, 1, 1, 0, 1);
            else
                soundPool.play(idMusicVictory,1, 1, 1, 0, 1);


            new AlertDialog.Builder(this)
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else {
            btnRoll.setText("TIRAR");
            btnRoll.setTextColor(Color.BLACK);
            btnRoll.setEnabled(true);
            primeraTirada = !primeraTirada;
            tirando = false;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRoll:
                //if(!tirando)
                tirarDados();
                break;
            case R.id.dado1:
                if(!primeraTirada) {
                    if (dado1.getColorFilter()==null)
                        dado1.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                    else
                        dado1.clearColorFilter();
                }
                break;
            case R.id.dado2:
                if(!primeraTirada) {
                    if (dado2.getColorFilter()==null)
                        dado2.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                    else
                        dado2.clearColorFilter();
                }
                break;
            case R.id.dado3:
                if(!primeraTirada) {
                    if (dado3.getColorFilter()==null)
                        dado3.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                    else
                        dado3.clearColorFilter();
                }
                break;
            case R.id.dado4:
                if(!primeraTirada) {
                    if (dado4.getColorFilter()==null)
                        dado4.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                    else
                        dado4.clearColorFilter();
                }
                break;
            case R.id.dado5:
                if(!primeraTirada) {
                    if (dado5.getColorFilter()==null)
                        dado5.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                    else
                        dado5.clearColorFilter();
                }
                break;
        }
    }

    public void tirarDados() {
        boolean noMovimientos = true;

        tirando=true;
        btnRoll.setEnabled(false);
        btnRoll.setText("TIRANDO");

        /*soundPool.stop(idSoundRoll);
        soundPool.play(idSoundRoll, 1, 1, 1, 0, 1);*/

        btnRoll.setTextColor(Color.GRAY);

        if(primeraTirada) {
            tableroH.setBackgroundColor(Color.LTGRAY);
            tableroIA.setBackgroundColor(Color.LTGRAY);
        }

        //Usuario
       for (Dado dado : dados) {
           if (primeraTirada || dado.getColorFilter() != null) {
               noMovimientos  = false;
               dado.startAnimation(animDado);
               dado.setValor(ale.nextInt(6) + 2);
               dado.clearColorFilter();
           }
       }


        //IA
        for(Dado dado: dadosIA) {
            if (primeraTirada || dado.getColorFilter() != null) {
                noMovimientos  = false;
                dado.startAnimation(animDado);
                dado.setValor(ale.nextInt(6) + 2);
                dado.clearColorFilter();
            }
        }

        if(noMovimientos)
            enAnimacionFinal();


    }

    public void actualizarVida() {
        int cont = 0;
        for(ImageView cor: corasonesH){
            if(cont < vidaH)
                cor.setImageResource(R.drawable.corasonrojo);
            else
                cor.setImageResource(R.drawable.corasonnegro);
            cont++;
        }

        cont = 0;

        for(ImageView cor: corasonesIA){
            if(cont < vidaIA)
                cor.setImageResource(R.drawable.corasonrojo);
            else
                cor.setImageResource(R.drawable.corasonnegro);
            cont++;
        }

    }

    public void actualizarValores(){
        for(Dado dado : dados){
            switch (dado.getValor()){
                case 7:
                    dado.setImageResource(R.drawable.dado1);
                    break;
                case 2:
                    dado.setImageResource(R.drawable.dado2);
                    break;
                case 3:
                    dado.setImageResource(R.drawable.dado3);
                    break;
                case 4:
                    dado.setImageResource(R.drawable.dado4);
                    break;
                case 5:
                    dado.setImageResource(R.drawable.dado5);
                    break;
                case 6:
                    dado.setImageResource(R.drawable.dado6);
                    break;
            }
        }

        for(Dado dado : dadosIA){
            switch (dado.getValor()){
                case 7:
                    dado.setImageResource(R.drawable.dado1);
                    break;
                case 2:
                    dado.setImageResource(R.drawable.dado2);
                    break;
                case 3:
                    dado.setImageResource(R.drawable.dado3);
                    break;
                case 4:
                    dado.setImageResource(R.drawable.dado4);
                    break;
                case 5:
                    dado.setImageResource(R.drawable.dado5);
                    break;
                case 6:
                    dado.setImageResource(R.drawable.dado6);
                    break;
            }
        }
    }

    public void mostrarResultado() {
        animBanner = AnimationUtils.loadAnimation(this, R.anim.anim_banner);

        int[] manoH = obtenerMano(dados);
        int[] manoIA = obtenerMano(dadosIA);

        txtResHum.setText(obtenerResultado(manoH));
        txtResIA.setText(obtenerResultado(manoIA));

        String res = "resultado";
        switch (humanoGanador(manoH, manoIA)) {
            case 1:
                //res="¡HAS GANADO!";
                if(!primeraTirada) {
                    tableroH.setBackgroundColor(Color.GREEN);
                    tableroIA.setBackgroundColor(Color.RED);
                    vidaIA--;
                }
                break;
            case 2:
                //res="¡HAS PERDIDO! ¡JUAS!";
                if(!primeraTirada) {
                    tableroH.setBackgroundColor(Color.RED);
                    tableroIA.setBackgroundColor(Color.GREEN);
                    vidaH--;
                }
                break;
            case 3:
                //res="¡EMPATE! ¡AW YEAH!";
                if(!primeraTirada)
                    Snackbar.make(findViewById(R.id.activity_vs_ia), "¡EMPATE!", Snackbar.LENGTH_LONG).show();
                break;
        }
        if(!primeraTirada) {
            txtGanIA.setText(res);
            //txtGanIA.startAnimation(animBanner);
        }

    }

    private String obtenerResultado(int[] mano) {
        String res = "resultado";
        switch (mano[0]){
            case 0:
               res = "Vaya mierda...";
                break;
            case 1:
                if(mano[1]==7)
                    res = "Pareja de Ases";
                else
                    res = "Pareja de "+mano[1];

                break;
            case 2:
                res = "Doble Pareja";
                break;
            case 3:
                if(mano[1]==7)
                    res = "Trío de Ases";
                else
                    res = "Trío de "+mano[1];

                //res = "Trío ( ͡° ͜ʖ ͡°)";
                //res = "Trío de "+mano[1];
                break;
            case 4:
                res = "Escalera (1-5)";
                break;
            case 5:
                res = "Escalera (2-6)";
                break;
            case 6:
                res = "Full HD";
                break;
            case 7:
                if(mano[1]==7)
                    res = "¡Póker de Ases!";
                else
                    res = "¡Póker de "+mano[1]+"!";
                break;
            case 8:
                if(mano[1]==7)
                    res = "¡¡¡REPÓKER DE ASES!!!";
                else
                    res = "¡¡¡REPÓKER DE "+mano[1]+"!!!";

                break;
        }
        return res;

    }

    /**
     * @return  1- ganador humano
     *          2- ganador máquina
     *          3- empate
     */
    @Deprecated
    private int humanoGanadorMal(int[] manoH, int[] manoIA) {
        int res = 3;
        if(manoH[0]>manoIA[0])
            res=1;
        else if(manoH[0]<manoIA[0])
            res=2;
        else {
            if((manoH[1]>manoIA[1]&&(manoH[0]!=6&&manoIA[0]!=6))||(manoH[2]==1&&manoH[2]!=manoIA[2]&&(manoH[0]!=6&&manoIA[0]!=6))||(manoH[0]==6&&manoIA[0]==6&&manoH[1]==1&&manoH[1]!=manoIA[1]))
                res=1;
            else if((manoH[1]<manoIA[1]&&(manoH[0]!=6&&manoIA[0]!=6))||(manoIA[2]==1&&manoIA[2]!=manoH[2]&&(manoH[0]!=6&&manoIA[0]!=6))||(manoH[0]==6&&manoIA[0]==6&&manoIA[1]==1&&manoIA[1]!=manoH[1]))
                res=2;
            else {
                if(manoH[2]>manoIA[2])
                    res=1;
                else if(manoH[2]<manoIA[2])
                    res=2;
            }
        }

        return res;
    }


    private int humanoGanador(int[] manoH, int[] manoIA) {
        int res = 3;
        if(manoH[0]>manoIA[0])
            res=1;
        else if(manoH[0]<manoIA[0])
            res=2;
        else {
            if(manoH[1]>manoIA[1])
            res=1;
            else if(manoH[1]<manoIA[1])
                res=2;
            else {
                if(manoH[2]>manoIA[2])
                res=1;
                else if(manoH[2]<manoIA[2])
                    res=2;
            }
        }

        return res;
    }

    /**
     *
     * @return 0-Nada
     *         1-Pareja
     *         2-DoblePareja
     *         3-Trío
     *         4-Escalera(1-5)
     *         5-Escalera(2-6)
     *         6-Full
     *         7-Poker
     *         8-RePoker
     */
    private int[] obtenerMano(Dado[] array){
        int[] res = new int[3];
        int numRep1=0, numRep2=0;
        int cuent1=1, cuent2=1;
        int[] numeros = volcarNumeros(array);
        Arrays.sort(numeros);

        if(esEscalera(numeros)){
            if(numeros[4]==7)
                res[0]=4;
            else
                res[0]=5;

        }else{
            for(int i = 1; i < numeros.length; i++) {
                if(numeros[i] == numeros[i - 1]) {
                    if(numRep1==0) {
                        numRep1=numeros[i];
                        cuent1++;
                    }
                    else if(numRep1==numeros[i]){
                        cuent1++;
                    }else {
                        numRep2 = numeros[i];
                        cuent2++;
                    }
                }
            }
            if(numRep1!=0&&numRep2!=0) {//Dos combinaciones
                if (cuent1==3&&cuent2==2){//Full
                    res[0]=6;res[1]=numRep1;res[2]=numRep2;
                }else if(cuent1==2&&cuent2==3){//Full
                    res[0]=6;res[1]=numRep2;res[2]=numRep1;
                }else if(numRep1>numRep2){//Doble Pareja
                    res[0]=2;
                    res[1]=numRep1;
                    res[2]=numRep2;}
                else{//Pareja
                    res[0]=2;res[1]=numRep2;res[2]=numRep1;}

            }else if(numRep1!=0){
                switch (cuent1){
                    case 2:
                        res[0]=1;res[1]=numRep1;
                        break;
                    case 3:
                        res[0]=3;res[1]=numRep1;
                        break;
                    case 4:
                        res[0]=7;res[1]=numRep1;
                        break;
                    case 5:
                        res[0]=8;res[1]=numRep1;
                        break;
                }
            }
            else
                res[0]=0;
        }
        return res;
    }

    private int[] volcarNumeros(Dado[] array){
        int[] res = new int[5];
        for (int i=0; i<array.length; i++){
            res[i] = array[i].getValor();
        }
        return res;
    }

    private boolean esEscalera(int[] numeros) {
        boolean res = true;
        int[] arrayTemp = Arrays.copyOf(numeros, numeros.length);
        if(arrayTemp[4]==7)
            arrayTemp[4] = 1;
        
        Arrays.sort(arrayTemp);
        
        for (int i = 0; i < arrayTemp.length - 1 && res; i++) {
            if (arrayTemp[i] + 1 != arrayTemp[i + 1])
                res = false;
        }
        return res;
    }


    /**
     *
     * @return 0-Nada
     *         1-Pareja
     *         2-DoblePareja
     *         3-Trío
     *         4-Escalera(1-5)
     *         5-Escalera(2-6)
     *         6-Full
     *         7-Poker
     *         8-RePoker
     */
    public void movimientoIA(){
        int[] mano = obtenerMano(dadosIA);
        switch (mano[0]){
            case 0:
                for(Dado dado:dadosIA)
                    dado.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                break;
            case 1:
            case 3:
            case 7:
            case 8:
                for(Dado dado:dadosIA){
                    if(dado.getValor()!=mano[1])
                        dado.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                }
                break;
            case 2:
            case 6:
                for(Dado dado:dadosIA){
                    if(dado.getValor()!=mano[1]&&dado.getValor()!=mano[2])
                        dado.setColorFilter(colorSel, PorterDuff.Mode.MULTIPLY);
                }
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public void onStop(){
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    public void onPause(){
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        mediaPlayer.start();
    }

    public void enAnimacionFinal(){
        actualizarValores();

        mostrarResultado();
        actualizarVida();

        if(primeraTirada)
            movimientoIA();

        finalizarRonda();
        mostrarTutorial();

    }


    public void mostrarTutorial(){
        int view = R.id.activity_vs_ia;
        switch (numTutorial){
            case 0:
                if(!primeraTirada) {
                    Snackbar.make(findViewById(view), "Selecciona los dados que quieras tirar", Snackbar.LENGTH_LONG).show();
                    numTutorial++;
                }
                break;
            case 1:
                if(primeraTirada) {
                    Snackbar.make(findViewById(view), "Cada ronda consta de 2 tiradas", Snackbar.LENGTH_LONG).show();
                    numTutorial++;
                }
        }
    }
}
