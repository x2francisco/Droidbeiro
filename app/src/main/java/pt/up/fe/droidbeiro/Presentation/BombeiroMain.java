package pt.up.fe.droidbeiro.Presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.IOException;

import pt.up.fe.droidbeiro.Communication.Client_Socket;
import pt.up.fe.droidbeiro.Messages.LogoutMessage;
import pt.up.fe.droidbeiro.Messages.PersonalizedMessage;
import pt.up.fe.droidbeiro.Messages.PredefinedMessage;
import pt.up.fe.droidbeiro.R;
import pt.up.fe.droidbeiro.Service.Acelarometro;
import pt.up.fe.droidbeiro.Service.GPS;


public class BombeiroMain extends Activity {

    // Initialize the array
    String[] messages = {   "Afirmativo",
                            "Aguarde",
                            "Assim Farei",
                            "Correto",
                            "Errado",
                            "Informe",
                            "Negativo",
                            "A Caminho",
                            "No local",
                            "No Hospital",
                            "Disponível",
                            "De Regresso",
                            "INOP",
                            "No Quartel",
                            "Necessito de Reforços",
                            "Casa em Perigo",
                            "Preciso de Descansar",
                            "Carro em Perigo",
                            "Descanse",
                            "Fogo a Alastrar",
                        };

    // Declare the UI components
    private ListView lista_mensagens_layout;
    private ArrayAdapter arrayAdapter;
    private Button btn_enviar_msg;
    private Button btn_modo_combate;
    private String mensagem;
    private EditText custom_message;
    private boolean personalization = true;

    Acelarometro acelarometro;
    GPS appLocationManager;

    Client_Socket CS = null;
    boolean CSisBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        //EDITED PART
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            CS = ((Client_Socket.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            CS = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(BombeiroMain.this, Client_Socket.class), mConnection, Context.BIND_AUTO_CREATE);
        CSisBound = true;
        if(CS!=null){
            CS.IsBoundable();
        }
    }

    private void doUnbindService() {
        if (CSisBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            CSisBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bombeiro_main);

        //start service on create
        doBindService();

        //Start APS Service
        startService(new Intent(this, Acelarometro.class));

        // Start GPS Service
        startService(new Intent(this, GPS.class));

        CS.setAfter_login(true);

        lista_mensagens_layout = (ListView) findViewById(R.id.lista_mensagens);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
        lista_mensagens_layout.setAdapter(arrayAdapter);

        custom_message=(EditText)findViewById(R.id.msg_custom);
        btn_enviar_msg = (Button)findViewById(R.id.btn_enviar_msg);
        btn_modo_combate= (Button)findViewById(R.id.btn_modo_combate);

        lista_mensagens_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {//    if (!msg_type) {
                custom_message.setText("");
                mensagem = lista_mensagens_layout.getItemAtPosition(position).toString();
                custom_message.setText(mensagem);
                personalization=false;
            }
        });



        btn_enviar_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mensagem = custom_message.getText().toString().trim();


                if (!(mensagem.isEmpty())) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(BombeiroMain.this);
                    alertDialog.setTitle("Enviar mensagem ?");
                    alertDialog.setMessage(mensagem);

                    //Setting Positive "Sim" Button
                    alertDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //envia a mensagem para o centro de controlo
                            /*if ((mensagem).equals("Preciso de ajuda")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Preciso afastar-me")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Camião com problemas")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Preciso de suporte aéreo")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Fogo a espalhar-se")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("A retirar-me")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Fogo perto de casa")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Casa queimada")){
                                personalization=false;
                            }else{
                                personalization=true;
                            }*/

                            if ((mensagem).equals("Afirmativo")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Aguarde")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Assim Faei")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Correto")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Errado")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Informe")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Negativo")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("A Caminho")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("No local")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("No Hospital")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Disponível")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("De Regresso")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("INOP")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("No Quartel")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Necessito de Reforços")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Casa em Perigo")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Preciso de Descansar")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Carro em Perigo")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Descanse")){
                                personalization=false;
                            }else
                            if ((mensagem).equals("Fogo a Alastrar")){
                                personalization=false;
                            }else{
                                personalization=true;
                            }
                            
                            if (personalization){
                                PersonalizedMessage pers_msg = new PersonalizedMessage(CS.getFirefighter_ID(), mensagem);
                                try {
                                    pers_msg.build_personalizedmessage_packet();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    CS.send_packet(pers_msg.getPersonalizedmessage_packet());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                custom_message.setText("");
                                personalization=true;
                            }else{
                                PredefinedMessage pred_msg = new PredefinedMessage(CS.getFirefighter_ID(), mensagem);
                                try {
                                    pred_msg.build_predefinedmessage_packet();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    CS.send_packet(pred_msg.getPredefinedmessage_packet());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                custom_message.setText("");
                                personalization=true;
                            }

                            custom_message.setText("");
                            personalization=true;
                        }
                    });

                    // Setting Negative "NÃO" Button
                    alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //envia a mensagem para o centro de controlo
                            custom_message.setText("");
                        }
                    });
                    alertDialog.show();
                }else{
                    Toast.makeText(getApplicationContext(), "Nada a enviar", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_modo_combate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Used to test
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(BombeiroMain.this);
                //Setting Dialog Title
                alertDialog.setTitle("Modo de Combate");
                //Setting Dialog Message
                alertDialog.setMessage("Entrar em Combate?");

                //Setting Positive "Sim" Button
                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which) {

                        // Write your code here to invoke SIM event
                        doUnbindService();
                        //Start NewActivity.class
                        Intent myIntent = new Intent(BombeiroMain.this,
                                BombeiroMC.class);
                        startActivity(myIntent);
                    }
                });

                // Setting Negative "NÃO" Button
                alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NÃO event
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });

    }

    public void onResume(){
        super.onResume();
        //acelarometro = new Acelarometro(this);
        startService(new Intent(BombeiroMain.this, Acelarometro.class));
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_lock_power_off).setTitle("Sair")
                .setMessage("Tem a certeza?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            CS.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Não", null).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bombeiro_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.login:

                /****************************************************************/
                LogoutMessage logout_msg = new LogoutMessage(CS.getFirefighter_ID());
                try {
                    logout_msg.build_logout_packet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    CS.send_packet(logout_msg.getLogout_packet());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CS.setAfter_login(false);
                doUnbindService();
                /****************************************************************/

                Intent login_Intent= new Intent(BombeiroMain.this, Login.class);
                login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login_Intent);

                return true;

            case R.id.team:

                Intent team_Intent= new Intent(BombeiroMain.this, ChangeTeam.class);
                team_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(team_Intent);

                return true;

            case R.id.connection:

                /****************************************************************/
                LogoutMessage logout_msg_ = new LogoutMessage(CS.getFirefighter_ID());
                try {
                    logout_msg_.build_logout_packet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    CS.send_packet(logout_msg_.getLogout_packet());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CS.setAfter_login(false);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                try {
                    CS.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doUnbindService();
                /****************************************************************/

                Intent connection_Intent= new Intent(BombeiroMain.this, Connection.class);
                connection_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(connection_Intent);
                return true;

            case R.id.gsm:

                if (CS.getGSM_Status()){
                    CS.setGSM(false);
                    Toast.makeText(getApplicationContext(), "GSM desactivado", Toast.LENGTH_LONG).show();
                }else{
                    CS.setGSM(true);
                    Toast.makeText(getApplicationContext(), "GSM activado", Toast.LENGTH_LONG).show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void finish() {

        acelarometro.stop();
        try {
            CS.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.finish();
    }
}
